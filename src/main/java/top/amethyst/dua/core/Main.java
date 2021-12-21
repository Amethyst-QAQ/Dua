package top.amethyst.dua.core;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import top.amethyst.dua.core.api.IMerkleTree;
import top.amethyst.dua.core.utils.Log4jInitializer;

import java.util.ArrayList;

public class Main
{
    static
    {
        Log4jInitializer.init();
    }

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args)
    {
        try
        {
            ArrayList<Block> test = new ArrayList<>();
            for (int i = 0; i < 10; i++)
                test.add(new Block(new Block.Head(0, new Hash(), 0, i, new Hash()), null));

            MerkleTree<Block> testTree = new MerkleTree<Block>(test){};

            if(testTree.contains(test.get(3)))
            {
                IMerkleTree.@NotNull IMerkleProof<Block> proof = testTree.getMerkleProof(test.get(3));
                LOGGER.debug(proof.valid(testTree.getRootHash()));
            }

            JsonObject testJson = testTree.serialize();
            LOGGER.debug(testJson);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
