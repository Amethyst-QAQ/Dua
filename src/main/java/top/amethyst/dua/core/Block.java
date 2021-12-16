package top.amethyst.dua.core;

public class Block
{
    public class Head
    {
        private static final String version = "Dua-1.0";
        private int index;
        private Hash prevHash;
        private long timestamp;
        private int nonce;
        private Hash rootHash;
    }

    public class Body
    {
        private MerkleTree<Transaction> transactions;
    }
}
