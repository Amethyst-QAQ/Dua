package top.amethyst.dua.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < 10; i++)
            list.add(i);

        IMerkleTree<Integer> test = new MerkleTree<>(list);

        IMerkleTree.IMerkleProof<Integer> proof = test.getMerkleProof(5);

        LOGGER.debug(proof.getDatum());
        LOGGER.debug(proof.valid(test.getRootHash()));
    }
}
