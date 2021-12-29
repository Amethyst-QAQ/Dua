package top.amethyst.dua.network;

import top.amethyst.dua.api.core.IBlock;
import top.amethyst.dua.api.core.IHash;
import top.amethyst.dua.api.core.ITransaction;
import top.amethyst.dua.api.network.ICompressedData;
import top.amethyst.dua.core.Hash;
import xyz.chlamydomonos.brainfuc.BFChEngine;
import xyz.chlamydomonos.brainfuc.IBFChScript;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FullBlockChain
{
    public static final int SECONDS_IN_A_YEAR = 31557600;
    public static long TRANSACTION_FEE_RATE = 1L << 10;

    private final HashMap<IHash, ArrayList<ICompressedData<? extends IBlock>>> blocks;
    private final ArrayList<HashMap<IHash, ArrayList<ICompressedData<? extends IBlock>>>> blocksSorted;
    private final HashMap<IHash, ArrayList<ICompressedData<? extends ITransaction>>> transactions;
    private final HashMap<IHash, ArrayList<ICompressedData<? extends ITransaction>>> transactionsTo;
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

    private boolean verifyTransactionCore(ITransaction transaction, long[] transactionFee)
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

                ITransaction.IOutput tempOut = tempTx.getOutputs().get(i.getOutput());
                engine.runScript((IBFChScript) i.getInputScript(), i.getInputScript().enqueueInputs(), true, true, true);
                engine.runScript((IBFChScript) tempOut.getOutputScript(), tempOut.getOutputScript().enqueueInputs(), false, false, false);
                if(engine.getOutputs().size() == 1 && engine.getOutputs().get(0).equals(true))
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

    private boolean verifyBlockCore(IBlock block)
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
        transactionFee[0] = lastTransactionFee[0];
        return lastTransactionVerifyResult;
    }

    public FullBlockChain()
    {
        blocks = new HashMap<>();
        blocksSorted = new ArrayList<>();
        transactions = new HashMap<>();
        transactionsTo = new HashMap<>();
        lastVerifiedBlock = null;
        lastBlockVerifyResult = false;
        lastTransactionFee = new long[]{0};
    }

    public boolean verifyTransaction(ITransaction transaction)
    {
        return verifyTransaction(transaction, null);
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
        if(!verifyBlock(block))
            return;

        Hash blockHash = new Hash(block);
        if(blocks.containsKey(blockHash))
        {
            HashMap temp = blocks;
        }
    }
}
