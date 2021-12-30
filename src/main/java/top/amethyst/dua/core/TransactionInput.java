package top.amethyst.dua.core;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import top.amethyst.dua.api.core.IHash;
import top.amethyst.dua.api.core.IScript;
import top.amethyst.dua.api.core.ITransaction;
import top.amethyst.dua.utils.JsonUtil;

import java.util.Objects;

public class TransactionInput implements ITransaction.IInput
{
    private final IHash transactionId;
    private final int output;
    private final IScript inputScript;

    public TransactionInput(JsonObject json)
    {
        transactionId = JsonUtil.deserialize(json.getAsJsonObject("transactionId"), Hash.class);
        output = json.get("output").getAsInt();
        inputScript = JsonUtil.deserialize(json.getAsJsonObject("inputScript"), Script.class);
    }

    public TransactionInput(IHash transactionId, int output, IScript inputScript)
    {
        this.transactionId = transactionId;
        this.output = output;
        this.inputScript = inputScript;
    }

    @Override
    public @NotNull JsonObject serialize()
    {
        JsonObject json = new JsonObject();
        json.add("transactionId", transactionId.serialize());
        json.addProperty("output", output);
        json.add("inputScript", inputScript.serialize());
        return json;
    }

    @Override
    public IHash getTransactionId()
    {
        return transactionId;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof TransactionInput)) return false;
        TransactionInput that = (TransactionInput) o;
        return output == that.output && Objects.equals(transactionId, that.transactionId) && Objects.equals(inputScript, that.inputScript);
    }
}
