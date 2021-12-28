package top.amethyst.dua.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import top.amethyst.dua.api.core.IJsonSerializable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 一些自定义的Json序列化/反序列化工具
 */
public class JsonUtil
{
    /**
     * 整个项目使用的全局Gson对象
     */
    public static final Gson GSON = new Gson();
    /**
     * 从Json反序列化对象
     * @param json 对象序列化产生的Json
     * @param classOfT 对象的类型。注意，该类型必须存在反序列化的构造函数
     * @return 由Json构建出的反序列化对象
     */
    @NotNull
    public static <T extends IJsonSerializable> T deserialize(@NotNull JsonObject json, @NotNull Class<? extends T> classOfT)
    {
        T temp;

        Constructor<? extends T> constructor;
        try
        {
            constructor = classOfT.getConstructor(JsonObject.class);
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException("Class" + classOfT.getName() + "does not have a deserialize constructor!", e);
        }

        try
        {
            temp = constructor.newInstance(json.getAsJsonObject("datum"));
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            throw new RuntimeException("Create instance failed!" + e);
        }

        return temp;
    }
}
