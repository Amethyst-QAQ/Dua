package top.amethyst.dua.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import top.amethyst.dua.api.core.IBlock;
import top.amethyst.dua.api.core.IHash;
import top.amethyst.dua.api.core.IScript;
import top.amethyst.dua.api.core.ITransaction;
import top.amethyst.dua.api.network.ICompressedData;
import top.amethyst.dua.core.Block;
import top.amethyst.dua.core.Hash;
import top.amethyst.dua.utils.JsonUtil;
import xyz.chlamydomonos.brainfuc.BFChEngine;
import xyz.chlamydomonos.brainfuc.IBFChScript;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FullBlockChain
{
    public static final int SECONDS_IN_A_YEAR = 31557600;
    public static final long TRANSACTION_FEE_RATE = 1L << 10;
    public static final int UPDATE_DIFFICULTY_INTERVAL = 2000;
    public static final String INITIAL_DIFFICULTY = "{\"value\": \"8000000000000000000000000000000000000000000000000000000000000000\"}";

    private final HashMap<IHash, ArrayList<ICompressedData<? extends IBlock>>> blocks;
    private final ArrayList<HashMap<IHash, ArrayList<ICompressedData<? extends IBlock>>>> blocksSorted;
    private final HashMap<IHash, ArrayList<ICompressedData<? extends ITransaction>>> transactions;
    private final HashMap<ITransaction, ArrayList<ITransaction>> transactionsTo;

    private Hash currentDifficulty;

    private IBlock lastVerifiedBlock;
    private boolean lastBlockVerifyResult;
    private ITransaction lastVerifiedTransaction;
    private boolean lastTransactionVerifyResult;
    private final long[] lastTransactionFee;

    private long getBlockReward()
    {
        if(blocksSorted.isEmpty())
            throw new RuntimeException("Trying to get block reward from an uninitialized node!");
        int tenMinutesInFourYears = (SECONDS_IN_A_YEAR * 4) / (60 * 10);
        long initialBlockReward = (Long.MAX_VALUE / 2) / tenMinutesInFourYears;
        int logInitialBlockReward = (int) Math.floor(Math.log(initialBlockReward) / Math.log(2));
        ICompressedData<? extends IBlock> genesisBlock = blocksSorted.get(0).get(new ArrayList<>(blocksSorted.get(0).keySet()).get(0)).get(0);
        long fourYears = (new Date().getTime() - genesisBlock.getData().getHead().getTimestamp()) / (SECONDS_IN_A_YEAR * 4);
        long logBlockReward = logInitialBlockReward - fourYears;
        return 1L << logBlockReward;
    }

    private boolean verifyTransactionCore(@NotNull ITransaction transaction, long[] transactionFee)
    {
        if(transaction.getInputs().isEmpty())
            return false;

        BFChEngine engine = new BFChEngine();

        long totalInput = 0;
        for (ITransaction.IInput i : transaction.getInputs())
        {
            IHash transactionId = i.getTransactionId();
            if(!transactions.containsKey(transactionId))
                return false;
            boolean temp = false;
            ArrayList<ICompressedData<? extends ITransaction>> withId = transactions.get(transactionId);
            for (ICompressedData<? extends ITransaction> j : withId)
            {
                ITransaction tempTx = j.getData();
                if(tempTx.getOutputs().size() <= i.getOutput())
                    continue;

                ITransaction toTx = transactionsTo.get(tempTx).get(i.getOutput());
                if(toTx != null)
                {
                    ArrayList<IBlock> toBlocks = findPossibleBlocksForTransaction(toTx);
                    boolean toBlocksLegal = true;
                    for(IBlock toBlock : toBlocks)
                        if(toBlock.getHead().getIndex() != blocksSorted.size() - 1)
                            toBlocksLegal = false;

                    if(!toBlocksLegal)
                        continue;
                }

                ITransaction.IOutput tempOut = tempTx.getOutputs().get(i.getOutput());
                engine.runScript((IBFChScript) i.getInputScript(), i.getInputScript().enqueueInputs(), true, true, true);
                engine.runScript((IBFChScript) tempOut.getOutputScript(), tempOut.getOutputScript().enqueueInputs(), false, false, false);
                if(engine.getOutputs().size() == 1 && engine.getOutputs().get(0).equals(Boolean.TRUE))
                {
                    temp = true;
                    totalInput += tempOut.getValue();
                    break;
                }
            }

            if(!temp)
                return false;
        }

        long totalOutput = 0;
        for(ITransaction.IOutput i : transaction.getOutputs())
            totalOutput += i.getValue();

        if(transactionFee != null)
        {
            if(totalOutput != 0)
                transactionFee[0] = totalOutput / TRANSACTION_FEE_RATE;
            else
                transactionFee[0] = totalInput / (TRANSACTION_FEE_RATE + 1);
        }

        return totalInput >= totalOutput + totalOutput / TRANSACTION_FEE_RATE;
    }

    private boolean verifyBlockCore(@NotNull IBlock block)
    {
        if (block.getBody() == null)
            return false;

        if(block.getHead().getIndex() > blocksSorted.size())
            return false;

        if(!block.getHead().getRootHash().equals(new Hash(block.getBody().getTransactions().getRootHash())))
            return false;

        if(currentDifficulty.compareTo(new Hash(block)) > 0)
            return false;

        ITransaction createCoinTransaction = null;
        long totalTransactionFee = 0;
        long[] transactionFee = new long[]{0};
        for(ITransaction i : block.getBody().getTransactions())
        {
            if (!verifyTransaction(i, transactionFee))
            {
                if(createCoinTransaction != null)
                    return false;

                if(i.getOutputs().size() != 1)
                    return false;

                createCoinTransaction = i;
            }
            totalTransactionFee += transactionFee[0];
        }

        if(createCoinTransaction == null)
            return false;

        if(createCoinTransaction.getOutputs().get(0).getValue() > totalTransactionFee + getBlockReward())
            return false;

        return blocksSorted.get(block.getHead().getIndex() - 1).containsKey(block.getHead().getPrevHash());
    }

    private boolean verifyTransaction(ITransaction transaction, long[] transactionFee)
    {
        if(!lastVerifiedTransaction.equals(transaction))
            lastTransactionVerifyResult = verifyTransactionCore(transaction, lastTransactionFee);

        lastVerifiedTransaction = transaction;
        if(transactionFee != null)
            transactionFee[0] = lastTransactionFee[0];
        return lastTransactionVerifyResult;
    }

    private boolean canFindAsParent(IBlock child, IBlock parent)
    {
        ArrayList<ICompressedData<? extends IBlock>> possibleParents = blocks.get(child.getHead().getPrevHash());
        boolean result = false;
        for (ICompressedData<? extends IBlock> i : possibleParents)
        {
            if (i.getData().equals(parent))
                return true;
            else if (i.getData().getHead().getIndex() <= child.getHead().getIndex())
                return false;
            else if(canFindAsParent(i.getData(), parent))
                result = true;
        }
        return result;
    }

    private void addTransaction(ITransaction transaction)
    {
        BFChEngine bfChEngine = new BFChEngine();

        ICompressedData<ITransaction> compressedTransaction = new CompressedData<ITransaction>(transaction){};
        Hash transactionHash = new Hash(transaction);

        if(!transactions.containsKey(transactionHash))
            transactions.put(transactionHash, new ArrayList<>());
        transactions.get(transactionHash).add(compressedTransaction);

        ArrayList<ITransaction> temp = new ArrayList<>();
        for(int i = 0; i < transaction.getOutputs().size(); i++)
            temp.add(null);
        transactionsTo.put(transaction, temp);

        for(ITransaction.IInput i : transaction.getInputs())
        {
            ArrayList<ICompressedData<? extends ITransaction>> tempList = transactions.get(i.getTransactionId());
            if(tempList.size() == 1)
                transactionsTo.get((ITransaction) tempList.get(0)).set(i.getOutput(), transaction);
            else
            {
                for(ICompressedData<? extends ITransaction> compressedData : tempList)
                {

                    ITransaction tempTransaction = compressedData.getData();
                    if(tempTransaction.getOutputs().size() <= i.getOutput())
                        continue;

                    IScript inputScript = i.getInputScript();
                    IScript outputSctipt = tempTransaction.getOutputs().get(i.getOutput()).getOutputScript();
                    bfChEngine.runScript((IBFChScript) inputScript, inputScript.enqueueInputs(), true, true, true);
                    bfChEngine.runScript((IBFChScript) outputSctipt, outputSctipt.enqueueInputs(), false, false, false);
                    if(bfChEngine.getOutputs().size() == 1 && bfChEngine.getOutputs().get(0).equals(Boolean.TRUE))
                        transactionsTo.get(tempTransaction).set(i.getOutput(), transaction);
                }
            }
        }
    }

    private void unsafeAddBlock(IBlock block)
    {
        ICompressedData<IBlock> compressedBlock = new CompressedData<IBlock>(block){};
        Hash blockHash = new Hash(block);
        if(!blocks.containsKey(blockHash))
            blocks.put(blockHash, new ArrayList<>());

        blocks.get(blockHash).add(compressedBlock);

        while (blocksSorted.size() <= block.getHead().getIndex())
            blocksSorted.add(new HashMap<>());

        if(!blocksSorted.get(block.getHead().getIndex()).containsKey(blockHash))
            blocksSorted.get(block.getHead().getIndex()).put(blockHash, new ArrayList<>());

        blocksSorted.get(block.getHead().getIndex()).get(blockHash).add(compressedBlock);
    }

    public FullBlockChain()
    {
        blocks = new HashMap<>();
        blocksSorted = new ArrayList<>();
        transactions = new HashMap<>();
        lastVerifiedBlock = null;
        lastBlockVerifyResult = false;
        lastTransactionFee = new long[]{0};
        transactionsTo = new HashMap<>();
        currentDifficulty = new Hash(JsonUtil.GSON.fromJson(INITIAL_DIFFICULTY, JsonObject.class));
    }

    public void updateDifficulty()
    {

    }

    public boolean verifyTransaction(ITransaction transaction)
    {
        return verifyTransaction(transaction, null);
    }

    public boolean onLongestLegalChain(IBlock block)
    {
        if(!verifyBlock(block))
            return false;

        HashMap<IHash, ArrayList<ICompressedData<? extends IBlock>>> latestBlocks = blocksSorted.get(blocksSorted.size() - 1);
        for (IHash tempHashI : latestBlocks.keySet())
        {
            for (ICompressedData<? extends IBlock> i : latestBlocks.get(tempHashI))
            {
                if(canFindAsParent(i.getData(), block))
                    return true;
            }
        }
        return false;
    }

    public ArrayList<IBlock> findPossibleBlocksForTransaction(ITransaction transaction)
    {
        ArrayList<IBlock> out = new ArrayList<>();
        if(!verifyTransaction(transaction))
            return out;

        ArrayList<ICompressedData<? extends IBlock>> blockList = blocks.get(transaction.getBlockHash());
        for(ICompressedData<? extends IBlock> i : blockList)
        {
            IBlock block = i.getData();
            if(block.getBody() == null)
                throw new RuntimeException("What happened??");

            if(block.getBody().getTransactions().contains(transaction))
                out.add(block);
        }
        return out;
    }

    public boolean verifyBlock(IBlock block)
    {
        if(!lastVerifiedBlock.equals(block))
            lastBlockVerifyResult = verifyBlockCore(block);

        lastVerifiedBlock = block;
        return lastBlockVerifyResult;
    }

    public void addBlock(IBlock block)
    {
        if (block.getBody() == null)
            throw new RuntimeException("Cannot add an incomplete block to a full blockchain!");

        if(!(blocksSorted.isEmpty() && block.getHead().getIndex() == 0))
        {
            if (!verifyBlock(block))
                return;

            if (block.getHead().getIndex() % UPDATE_DIFFICULTY_INTERVAL == 0)
                updateDifficulty();
        }

        unsafeAddBlock(block);

        for(ITransaction i : block.getBody().getTransactions())
            addTransaction(i);
    }

    public void saveToPath(String filePath)
    {
        JsonObject json1 = new JsonObject();
        json1.addProperty("chainLength", blocksSorted.size());
        JsonUtil.saveToFile(json1, filePath + "/" + "info.json");
        for(int i = 0; i < blocksSorted.size(); i++)
        {
            JsonObject json2 = new JsonObject();
            for(Map.Entry<IHash, ArrayList<ICompressedData<? extends IBlock>>> j : blocksSorted.get(i).entrySet())
            {
                json2.addProperty(j.getKey().toString(), j.getValue().size());
                for(int k = 0; k < j.getValue().size(); k++)
                {
                    ICompressedData<IBlock> temp = new CompressedData<IBlock>(j.getValue().get(k).getData()){};
                    temp.saveToFile(filePath + "/" + i + "/" + j.getKey().toString() + "/" + k + ".json.gz");
                }
            }
            JsonUtil.saveToFile(json2, filePath + "/" + i + "/info.json");
        }
    }

    public void loadFromPath(String filePath)
    {
        JsonObject json1 = JsonUtil.loadFromFile(filePath + "/" + "info.json");
        int chainLength = json1.get("chainLength").getAsInt();
        for(int i = 0; i < chainLength; i++)
        {
            JsonObject json2 = JsonUtil.loadFromFile(filePath + "/" + i + "/info.json");
            for(Map.Entry<String, JsonElement> j : json2.entrySet())
            {
                int size = j.getValue().getAsInt();
                for(int k = 0; k < size; k++)
                {
                    ICompressedData<? extends IBlock> temp = new CompressedData<Block>(
                            filePath + "/" + i + "/" + j.getKey() + "/" + k + ".json.gz") {};
                    addBlock(temp.getData());
                }
            }
        }
    }
}
