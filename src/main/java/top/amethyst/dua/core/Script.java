package top.amethyst.dua.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import top.amethyst.dua.api.core.IScript;
import xyz.chlamydomonos.brainfuc.BFChScript;

import java.util.*;

public class Script extends BFChScript implements IScript
{
    private final ArrayList<IInputWrapper<?>> inputs;

    public Script(@NotNull String data, @NotNull List<IInputWrapper<?>> inputs)
    {
        super(data);
        this.inputs = new ArrayList<>(inputs);
    }

    public Script(@NotNull JsonObject json)
    {
        super(json.get("data").getAsString());
        this.inputs = new ArrayList<>();
        JsonArray inputArray = json.getAsJsonArray("inputs");
        for(JsonElement i : inputArray)
            inputs.add(ScriptInputWrapper.deserialize(i.getAsJsonObject()));
    }

    @Override
    public @NotNull Queue<Object> enqueueInputs()
    {
        Queue<Object> out = new LinkedList<>();
        for(IInputWrapper<?> i : inputs)
            out.add(i.get());
        return out;
    }

    @Override
    public @NotNull JsonObject serialize()
    {
        JsonObject json = new JsonObject();
        ArrayList<Character> temp = getData();
        StringBuilder builder = new StringBuilder();
        for(char i : temp)
            builder.append(i);
        json.addProperty("data", builder.toString());

        JsonArray inputArray = new JsonArray();
        for(IInputWrapper<?> i : inputs)
            inputArray.add(i.serialize());

        json.add("inputs", inputArray);

        return json;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Script)) return false;
        if (!super.equals(o)) return false;
        Script script = (Script) o;
        return Objects.equals(inputs, script.inputs);
    }
}
