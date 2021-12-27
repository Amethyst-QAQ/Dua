package top.amethyst.dua.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.amethyst.dua.api.core.IHash;
import top.amethyst.dua.api.core.IInput;
import top.amethyst.dua.api.core.IOutput;
import top.amethyst.dua.api.core.ITransaction;
import top.amethyst.dua.core.utils.JsonUtil;

import java.util.ArrayList;

public class Transaction implements ITransaction
{
    private final String version;
    private final int lockTime;
    private long time;
    private long blockTime;
    private IHash blockHash;
    private int confirmations;
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
            inputs.add(JsonUtil.deserialize(i.getAsJsonObject(), Input.class));
        }

        JsonArray outputArray = json.getAsJsonArray("outputs");
        outputs = new ArrayList<>();
        for(JsonElement i : outputArray)
        {
            outputs.add(JsonUtil.deserialize(i.getAsJsonObject(), Output.class));
        }
        time = json.get("time").getAsLong();
        blockTime = json.get("blockTime").getAsLong();
        if(json.has("blockHash"))
            blockHash = JsonUtil.deserialize(json.getAsJsonObject("blockHash"), Hash.class);
        confirmations = json.get("confirmations").getAsInt();
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
        json.addProperty("confirmations", confirmations);

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


    public String getVersion()
    {
        return version;
    }

    public int getLockTime()
    {
        return lockTime;
    }

    public long getTime()
    {
        return time;
    }

    public long getBlockTime()
    {
        return blockTime;
    }

    public IHash getBlockHash()
    {
        return blockHash;
    }

    public int getConfirmations()
    {
        return confirmations;
    }

    public ArrayList<IInput> getInputs()
    {
        return inputs;
    }

    public ArrayList<IOutput> getOutputs()
    {
        return outputs;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    public void setBlockTime(long blockTime)
    {
        this.blockTime = blockTime;
    }

    public void setBlockHash(IHash blockHash)
    {
        this.blockHash = blockHash;
    }

    public void setConfirmations(int confirmations)
    {
        this.confirmations = confirmations;
    }
}
