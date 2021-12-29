package top.amethyst.dua.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.amethyst.dua.api.core.IHash;
import top.amethyst.dua.api.core.ITransaction;
import top.amethyst.dua.network.utils.JsonUtil;

import java.util.ArrayList;
import java.util.Objects;

public class Transaction implements ITransaction
{
    private final String version;
    private final int lockTime;
    private long time;
    private long blockTime;
    private IHash blockHash;
    private final ArrayList<IInput> inputs;
    private final ArrayList<IOutput> outputs;

    public Transaction(String version, int lockTime, ArrayList<IInput> inputs, ArrayList<IOutput> outputs)
    {
        this.version = version;
        this.lockTime = lockTime;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public Transaction(JsonObject json)
    {
        version = json.get("version").getAsString();
        lockTime = json.get("lockTime").getAsInt();
        JsonArray inputArray = json.getAsJsonArray("inputs");
        inputs = new ArrayList<>();
        for(JsonElement i : inputArray)
        {
            inputs.add(JsonUtil.deserialize(i.getAsJsonObject(), TransactionInput.class));
        }

        JsonArray outputArray = json.getAsJsonArray("outputs");
        outputs = new ArrayList<>();
        for(JsonElement i : outputArray)
        {
            outputs.add(JsonUtil.deserialize(i.getAsJsonObject(), TransactionOutput.class));
        }
        time = json.get("time").getAsLong();
        blockTime = json.get("blockTime").getAsLong();
        if(json.has("blockHash"))
            blockHash = JsonUtil.deserialize(json.getAsJsonObject("blockHash"), Hash.class);
    }

    @Override
    public @NotNull JsonObject serialize()
    {
        JsonObject json = new JsonObject();
        if (blockHash != null)
            json.add("blockHash", blockHash.serialize());

        json.addProperty("version", version);
        json.addProperty("lockTime", lockTime);
        json.addProperty("time", time);
        json.addProperty("blockTime", blockTime);

        JsonArray inputArray = new JsonArray();
        for (IInput i : inputs)
            inputArray.add(i.serialize());
        json.add("inputs", inputArray);

        JsonArray outputArray = new JsonArray();
        for (IOutput i : outputs)
            outputArray.add(i.serialize());
        json.add("outputs", outputArray);

        return json;
    }

    @Override
    public @Nullable ArrayList<String> getHashExcludedFields()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add("time");
        list.add("blockTime");
        list.add("blockHash");
        list.add("confirmations");

        return list;
    }

    @Override
    public String getVersion()
    {
        return version;
    }

    @Override
    public int getLockTime()
    {
        return lockTime;
    }

    @Override
    public long getTime()
    {
        return time;
    }

    @Override
    public long getBlockTime()
    {
        return blockTime;
    }

    @Override
    public IHash getBlockHash()
    {
        return blockHash;
    }

    @Override
    public ArrayList<IInput> getInputs()
    {
        return inputs;
    }

    @Override
    public ArrayList<IOutput> getOutputs()
    {
        return outputs;
    }

    @Override
    public void setTime(long time)
    {
        this.time = time;
    }

    @Override
    public void setBlockTime(long blockTime)
    {
        this.blockTime = blockTime;
    }

    @Override
    public void setBlockHash(IHash blockHash)
    {
        this.blockHash = blockHash;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return lockTime == that.lockTime && Objects.equals(version, that.version) && Objects.equals(inputs, that.inputs) && Objects.equals(outputs, that.outputs);
    }
}
