package top.amethyst.dua.core;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import top.amethyst.dua.api.core.IJsonSerializable;

public class Script implements IJsonSerializable
{
    @Override
    public @NotNull JsonObject serialize()
    {
        return new JsonObject();
    }
}
