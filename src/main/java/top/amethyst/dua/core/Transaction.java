package top.amethyst.dua.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.amethyst.dua.api.core.IInput;
import top.amethyst.dua.api.core.IOutput;
import top.amethyst.dua.api.core.ITransaction;

import java.util.ArrayList;

public class Transaction implements ITransaction
{
    private String version;
    private int lockTime;
    private long time;
    private long blockTime;
    private String blockHash;
    private int confirmations;
    private ArrayList<IInput> inputs;
    private ArrayList<IOutput> outputs;

    public Transaction(String version, int lockTime, ArrayList<IInput> inputs)
    {
        this.version = version;
        this.lockTime = lockTime;
        this.inputs = inputs;
    }

    public Transaction(JsonObject json)
    {

    }

    @Override
    public @NotNull JsonObject serialize()
    {
        JsonObject json = new JsonObject();
        if(blockHash != null)
            json.addProperty("blockHash", blockHash);

        json.addProperty("version", version);
        json.addProperty("lockTime", lockTime);
        json.addProperty("time", time);
        json.addProperty("blockTime", blockTime);
        json.addProperty("confirmations", confirmations);

        JsonArray inputArray = new JsonArray();
        for(IInput i : inputs)
            inputArray.add(i.serialize());
        json.add("inputs", inputArray);

        if(outputs != null)
        {
            JsonArray outputArray = new JsonArray();
            for(IOutput i : outputs)
                outputArray.add(i.serialize());
            json.add("outputs", outputArray);
        }

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
        list.add("outputs");

        return list;
    }
}
