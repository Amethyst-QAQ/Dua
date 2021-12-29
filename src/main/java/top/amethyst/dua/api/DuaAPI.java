package top.amethyst.dua.api;

import top.amethyst.dua.api.core.DuaCoreAPI;
import top.amethyst.dua.api.utils.DuaUtilsAPI;

/**
 * Dua API主接口，用于调用一切Dua API
 */
@SuppressWarnings("SameReturnValue")
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

    /**
     * 获取主接口实例
     */
    public static DuaAPI getInstance()
    {
        return instance;
    }

    /**
     * 获取当前API使用的Dua协议版本
     */
    public abstract String getDuaVersion();

    /**
     * 获取Dua核心API接口
     * @see DuaCoreAPI
     */
    public abstract DuaCoreAPI getCoreAPI();

    /**
     * 获取Dua其他工具API接口
     * @see DuaUtilsAPI
     */
    public abstract DuaUtilsAPI getUtilsAPI();
}
