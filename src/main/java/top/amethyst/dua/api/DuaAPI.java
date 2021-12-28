package top.amethyst.dua.api;

import top.amethyst.dua.api.core.DuaCoreAPI;

public abstract class DuaAPI
{
    private static final DuaAPI instance;

    static
    {
        try
        {
            instance = (DuaAPI) Class.forName("top.amethyst.dua.APIImpl").getDeclaredField("INSTANCE").get(null);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cannot find dua");
        }
    }

    public static DuaAPI getInstance()
    {
        return instance;
    }

    public abstract DuaCoreAPI getCoreAPI();
}
