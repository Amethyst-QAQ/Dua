package top.amethyst.dua.core;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import top.amethyst.dua.api.core.IJsonSerializable;
import xyz.chlamydomonos.brainfuc.BFChScript;

import java.util.ArrayList;

public class Script extends BFChScript implements IJsonSerializable
{
    public Script(@NotNull String data)
    {
        super(data);
    }

    public Script(@NotNull JsonObject json)
    {
        this(json.get("data").getAsString());
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
        return json;
    }
}
