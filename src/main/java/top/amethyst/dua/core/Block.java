package top.amethyst.dua.core;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.amethyst.dua.api.core.IBlock;
import top.amethyst.dua.api.core.IHash;
import top.amethyst.dua.api.core.IMerkleTree;
import top.amethyst.dua.api.core.ITransaction;
import top.amethyst.dua.core.utils.JsonUtil;

import java.util.ArrayList;

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
    }

    /**
     * 对{@link IBlock.IBody}接口的实现
     */
    public static class Body implements IBlock.IBody
    {
        private final MerkleTree<ITransaction> transactions;

        public Body(MerkleTree<ITransaction> transactions)
        {
            this.transactions = transactions;
        }

        /**
         * 从Json反序列化
         */
        public Body(JsonObject json)
        {
            class Temp extends MerkleTree<ITransaction>
            {
                public Temp(JsonObject json)
                {
                    super(json);
                }
            }

            transactions = JsonUtil.deserialize(json.getAsJsonObject("transactions"), Temp.class);
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
}
