package top.amethyst.dua;

import top.amethyst.dua.api.DuaAPI;
import top.amethyst.dua.api.core.DuaCoreAPI;
import top.amethyst.dua.api.utils.DuaUtilsAPI;

public class APIImpl extends DuaAPI
{
    public static final APIImpl INSTANCE = new APIImpl();

    @Override
    public DuaCoreAPI getCoreAPI()
    {
        return top.amethyst.dua.core.APIImpl.INSTANCE;
    }

    @Override
    public DuaUtilsAPI getUtilsAPI()
    {
        return top.amethyst.dua.utils.APIImpl.INSTANCE;
    }
}
