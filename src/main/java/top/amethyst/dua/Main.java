package top.amethyst.dua;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.amethyst.dua.api.core.ITransaction;
import top.amethyst.dua.core.*;
import top.amethyst.dua.network.FullBlockChain;
import top.amethyst.dua.utils.JsonUtil;
import top.amethyst.dua.utils.Log4jInitializer;
import xyz.chlamydomonos.brainfuc.BFChEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class Main
{
    static
    {
        Log4jInitializer.init();
    }

    private static final Logger LOGGER = LogManager.getLogger();

    public static StringBuilder create()
    {
        return new StringBuilder();
    }

    public static void test1()
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/dua/test/testScript.json"))));
        StringBuilder builder = new StringBuilder();
        String line;
        try
        {
            line = reader.readLine();
            while (line != null)
            {
                builder.append(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e)
        {
            e.printStackTrace();org.apache.logging.log4j.LogManager.getLogger().error(e.getMessage(), e);
        }
        LOGGER.debug(int.class.getName());
        JsonObject jsonObject = JsonUtil.GSON.fromJson(builder.toString(), JsonObject.class);
        Script script = new Script(jsonObject);
        BFChEngine bfChEngine = new BFChEngine();
        bfChEngine.runScript(script, script.enqueueInputs(), true, true, true);
        for (Object i : bfChEngine.getOutputs())
            LOGGER.debug(i);
    }

    public static void test2()
    {
        FullBlockChain testChain1 = new FullBlockChain();

        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/dua/test/testOutputScript.json"))));
        StringBuilder builder = new StringBuilder();
        String line;
        try
        {
            line = reader.readLine();
            while (line != null)
            {
                builder.append(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e)
        {
            e.printStackTrace();org.apache.logging.log4j.LogManager.getLogger().error(e.getMessage(), e);
        }
        JsonObject jsonObject = JsonUtil.GSON.fromJson(builder.toString(), JsonObject.class);
        Script script = new Script(jsonObject);

        TransactionOutput output = new TransactionOutput(100, 0, script);

        Transaction testCreateCoinTransaction = new Transaction(APIImpl.DUA_VERSION, 0, new ArrayList<>(), new ArrayList<>());
        testCreateCoinTransaction.getOutputs().add(output);

        ArrayList<Transaction> transactionArrayList = new ArrayList<>();
        transactionArrayList.add(testCreateCoinTransaction);

        MerkleTree<ITransaction> transactions = new MerkleTree<ITransaction>(transactionArrayList) {};
        Block.Body testBody = new Block.Body(transactions);
        Block.Head testHead = new Block.Head(0, new Hash(), 0L, 0, testBody.getTransactions().getRootHash());

        Block testGenesisBlock = new Block(testHead, testBody);
        testCreateCoinTransaction.setBlockHash(new Hash(testGenesisBlock));
        testCreateCoinTransaction.setTime(0L);
        testCreateCoinTransaction.setBlockTime(0L);

        testChain1.addBlock(testGenesisBlock);

        LOGGER.debug("Block size is" + testGenesisBlock.serialize().toString().getBytes(StandardCharsets.UTF_8).length);
        LOGGER.debug(1 << 6);

        testChain1.saveToPath("H:/test");
        FullBlockChain testChain2 = new FullBlockChain();
        testChain2.loadFromPath("H:/test");
    }

    public static void main(String[] args)
    {
        try
        {
            test1();
            test2();
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
