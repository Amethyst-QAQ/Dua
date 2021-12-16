package top.amethyst.dua.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.amethyst.dua.core.api.*;

/**
 * 对{@link IBlock}接口的实现
 */
public class Block implements IBlock
{
    /**
     * 对{@link top.amethyst.dua.core.api.IBlock.IHead}接口的实现
     */
    public static class Head implements IBlock.IHead
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

        @NotNull
        @Override
        public IBlock.IHead next()
        {
            return new Head(index, prevHash, timestamp, nonce + 1, rootHash);
        }

        @Override
        public int getIndex()
        {
            return index;
        }

        @NotNull
        @Override
        public IHash getPrevHash()
        {
            return prevHash;
        }

        @Override
        public long getTimestamp()
        {
            return timestamp;
        }

        @Override
        public int getNonce()
        {
            return nonce;
        }

        @NotNull
        @Override
        public IHash getRootHash()
        {
            return rootHash;
        }
    }

    /**
     * 对{@link top.amethyst.dua.core.api.IBlock.IBody}接口的实现
     */
    public static class Body implements IBlock.IBody
    {
        private final MerkleTree<ITransaction> transactions;

        public Body(MerkleTree<ITransaction> transactions)
        {
            this.transactions = transactions;
        }

        @NotNull
        @Override
        public IMerkleTree<ITransaction> getTransactions()
        {
            return transactions;
        }
    }

    private final IBlock.IHead head;
    private IBlock.IBody body;

    public Block(@NotNull IBlock.IHead head, @Nullable IBlock.IBody body)
    {
        this.head = head;
        this.body = body;
    }

    @NotNull
    @Override
    public IBlock.IHead getHead()
    {
        return head;
    }

    @Nullable
    @Override
    public IBlock.IBody getBody()
    {
        return body;
    }

    @Override
    public boolean isBodyValid(@Nullable IBlock.IBody body)
    {
        if(body == null)
            return true;
        return body.getTransactions().getRootHash().equals(head.getRootHash());
    }

    @Override
    public void setBody(@Nullable IBlock.IBody body)
    {
        if(!isBodyValid(body))
            throw new IllegalArgumentException("Cannot set an illegal body");
        this.body = body;
    }
}
