package top.amethyst.dua.core;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import top.amethyst.dua.api.core.IScript;
import top.amethyst.dua.api.core.ITransaction;
import top.amethyst.dua.utils.JsonUtil;

public class TransactionOutput implements ITransaction.IOutput
{
    private final int value;
    private final int index;
    private final IScript outputScript;

    public TransactionOutput(JsonObject json)
    {
        value = json.get("value").getAsInt();
        index = json.get("index").getAsInt();
        outputScript = JsonUtil.deserialize(json.getAsJsonObject("outputScript"), Script.class);
    }

    public TransactionOutput(int value, int index, IScript outputScript)
    {
        this.value = value;
        this.index = index;
        this.outputScript = outputScript;
    }

    @Override
    public @NotNull JsonObject serialize()
    {
        JsonObject json = new JsonObject();
        json.addProperty("value", value);
        json.addProperty("index", index);
        json.add("outputScript", outputScript.serialize());
        return json;
    }

    @Override
    public int getValue()
    {
        return value;
    }

    @Override
    public int getIndex()
    {
        return index;
    }

    @Override
    public IScript getOutputScript()
    {
        return outputScript;
    }
}
