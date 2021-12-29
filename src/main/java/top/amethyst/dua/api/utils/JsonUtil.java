package top.amethyst.dua.api.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import top.amethyst.dua.api.core.IJsonSerializable;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * 一些自定义的Json序列化/反序列化工具
 */
@SuppressWarnings("SameReturnValue")
public interface JsonUtil
{
    /**
     * 获取整个项目使用的全局Gson对象
     * <br>
     * 要使用{@link Gson#toJson(Object)}和{@link Gson#fromJson(Reader, Type)}时，
     * 尽可能使用这个对象。
     */
    Gson getGson();

    /**
     * 从Json反序列化对象
     * @param json 对象序列化产生的Json
     * @param classOfT 对象的类型。注意，该类型必须存在反序列化的构造函数
     * @return 由Json构建出的反序列化对象
     */
    @NotNull
    <T extends IJsonSerializable> T deserialize(@NotNull JsonObject json, @NotNull Class<? extends T> classOfT);
}
