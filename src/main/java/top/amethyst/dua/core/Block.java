package top.amethyst.dua.core;

public class Block
{
    public static class Head
    {
        private static final String version = "Dua-1.0";
        private final int index;
        private final Hash prevHash;
        private final long timestamp;
        private final int nonce;
        private final Hash rootHash;

        public Head(int index, Hash prevHash, long timestamp, int nonce, Hash rootHash)
        {
            this.index = index;
            this.prevHash = prevHash;
            this.timestamp = timestamp;
            this.nonce = nonce;
            this.rootHash = rootHash;
        }

        public Head next()
        {
            return new Head(index, prevHash, timestamp, nonce + 1, rootHash);
        }

        public int getIndex()
        {
            return index;
        }

        public Hash getPrevHash()
        {
            return prevHash;
        }

        public long getTimestamp()
        {
            return timestamp;
        }

        public int getNonce()
        {
            return nonce;
        }

        public Hash getRootHash()
        {
            return rootHash;
        }
    }

    public static class Body
    {
        private MerkleTree<Transaction> transactions;
    }
}
