package top.amethyst.dua.core;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import top.amethyst.dua.api.core.IScript;
import top.amethyst.dua.api.core.ITransaction;
import top.amethyst.dua.network.utils.JsonUtil;

import java.util.Objects;

public class TransactionOutput implements ITransaction.IOutput
{
    private final long value;
    private final int index;
    private final IScript outputScript;

    public TransactionOutput(JsonObject json)
    {
        value = json.get("value").getAsLong();
        index = json.get("index").getAsInt();
        outputScript = JsonUtil.deserialize(json.getAsJsonObject("outputScript"), Script.class);
    }

    public TransactionOutput(long value, int index, IScript outputScript)
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
    public long getValue()
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof TransactionOutput)) return false;
        TransactionOutput that = (TransactionOutput) o;
        return value == that.value && index == that.index && Objects.equals(outputScript, that.outputScript);
    }
}
