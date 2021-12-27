package top.amethyst.dua.core;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import top.amethyst.dua.api.core.IOutput;
import top.amethyst.dua.api.core.IScript;
import top.amethyst.dua.core.utils.JsonUtil;

public class Output implements IOutput
{
    private final int value;
    private final int index;
    private final IScript outputScript;

    public Output(JsonObject json)
    {
        value = json.get("value").getAsInt();
        index = json.get("index").getAsInt();
        outputScript = (IScript) JsonUtil.deserialize(json.getAsJsonObject("outputScript"), Script.class);
    }

    public Output(int value, int index, IScript outputScript)
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
