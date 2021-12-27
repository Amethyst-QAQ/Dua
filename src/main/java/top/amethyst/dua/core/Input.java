package top.amethyst.dua.core;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import top.amethyst.dua.api.core.IInput;
import top.amethyst.dua.api.core.IScript;
import top.amethyst.dua.core.utils.JsonUtil;

public class Input implements IInput
{
    private final String txid;
    private final int output;
    private final IScript inputScript;

    public Input(JsonObject json)
    {
        txid = json.get("txid").getAsString();
        output = json.get("output").getAsInt();
        inputScript = (IScript) JsonUtil.deserialize(json.getAsJsonObject("inputScript"), Script.class);
    }

    public Input(String txid, int output, IScript inputScript)
    {
        this.txid = txid;
        this.output = output;
        this.inputScript = inputScript;
    }

    @Override
    public @NotNull JsonObject serialize()
    {
        JsonObject json = new JsonObject();
        json.addProperty("txid", txid);
        json.addProperty("output", output);
        json.add("inputScript", inputScript.serialize());
        return json;
    }

    @Override
    public String getTxid()
    {
        return txid;
    }

    @Override
    public int getOutput()
    {
        return output;
    }

    @Override
    public IScript getInputScript()
    {
        return inputScript;
    }
}
