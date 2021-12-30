package top.amethyst.dua.core;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.amethyst.dua.api.core.IBlock;
import top.amethyst.dua.api.core.IHash;
import top.amethyst.dua.api.core.IMerkleTree;
import top.amethyst.dua.api.core.ITransaction;
import top.amethyst.dua.utils.JsonUtil;

import java.util.ArrayList;
import java.util.Objects;

/**
 * 对{@link IBlock}接口的实现
 */
public class Block implements IBlock
{
    /**
     * 对{@link IBlock.IHead}接口的实现
     */
    public static class Head implements IBlock.IHead
    {
        private final String version;
        private final int index;
        private final IHash prevHash;
        private final long timestamp;
        private final int nonce;
        private final IHash rootHash;

        public Head(int index, IHash prevHash, long timestamp, int nonce, IHash rootHash)
        {
            this.index = index;
            this.prevHash = prevHash;
            this.timestamp = timestamp;
            this.nonce = nonce;
            this.rootHash = rootHash;
            version = "Dua-1.0";
        }

        /**
         * 从Json反序列化
         */
        public Head(JsonObject json)
        {
            index = json.get("index").getAsInt();
            prevHash = new Hash(json.get("prevHash").getAsJsonObject());
            timestamp = json.get("timestamp").getAsLong();
            nonce = json.get("nonce").getAsInt();
            rootHash = new Hash(json.get("rootHash").getAsJsonObject());
            version = "Dua-1.0";
        }

        @NotNull
        @Override
        public IBlock.IHead next()
        {
            return new Head(index, prevHash, timestamp, nonce + 1, rootHash);
        }

        @Override
        public String getVersion()
        {
            return version;
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

        @Override
        public @NotNull JsonObject serialize()
        {
            JsonObject json = new JsonObject();
            json.addProperty("index", index);
            json.add("prevHash", prevHash.serialize());
            json.addProperty("timestamp", timestamp);
            json.addProperty("nonce", nonce);
            json.add("rootHash", rootHash.serialize());
            return json;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof Head)) return false;
            Head head = (Head) o;
            return index == head.index && timestamp == head.timestamp && nonce == head.nonce
                   && Objects.equals(version, head.version) && Objects.equals(prevHash, head.prevHash)
                   && Objects.equals(rootHash, head.rootHash);
        }
    }

    /**
     * 对{@link IBlock.IBody}接口的实现
     */
    public static class Body implements IBlock.IBody
    {
        public static class Temp extends MerkleTree<Transaction>
        {
            public Temp(JsonObject json)
            {
                super(json);
            }
        }

        private final IMerkleTree<ITransaction> transactions;

        public Body(IMerkleTree<ITransaction> transactions)
        {
            this.transactions = transactions;
        }

        /**
         * 从Json反序列化
         */
        public Body(JsonObject json)
        {
            Temp temp = JsonUtil.deserialize(json.getAsJsonObject("transactions"), Temp.class);

            ArrayList<ITransaction> tempList = new ArrayList<>();
            for(ITransaction i : temp)
                tempList.add(i);

            transactions = new MerkleTree<ITransaction>(tempList) {};
        }

        @NotNull
        @Override
        public IMerkleTree<ITransaction> getTransactions()
        {
            return transactions;
        }

        @Override
        public @NotNull JsonObject serialize()
        {
            JsonObject json = new JsonObject();
            json.add("transactions", transactions.serialize());
            return json;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof Body)) return false;
            Body body = (Body) o;
            return Objects.equals(transactions, body.transactions);
        }
    }

    private final IBlock.IHead head;
    private IBlock.IBody body;

    public Block(@NotNull IBlock.IHead head, @Nullable IBlock.IBody body)
    {
        this.head = head;
        this.body = body;
    }

    /**
     * 从Json反序列化
     */
    public Block(@NotNull JsonObject json)
    {
        head = JsonUtil.deserialize(json.getAsJsonObject("head"), Head.class);
        if(json.has("body"))
            body = JsonUtil.deserialize(json.getAsJsonObject("body"), Body.class);
        else
            body = null;
    }

    @Override
    public @NotNull JsonObject serialize()
    {
        JsonObject json = new JsonObject();
        json.add("head", head.serialize());
        if(body != null)
            json.add("body", body.serialize());
        return json;
    }

    @Override
    public @Nullable ArrayList<String> getHashExcludedFields()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add("body");
        return list;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Block)) return false;
        Block block = (Block) o;
        return Objects.equals(head, block.head);
    }
}
