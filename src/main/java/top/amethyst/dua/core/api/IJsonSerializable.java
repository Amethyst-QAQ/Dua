package top.amethyst.dua.core.api;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * 可被自定义Json序列化的对象
 * <br>
 * 这个接口允许对象被序列化为Json。
 * 注意，在实现此接口时，实现类需要具有一个用于从Json反序列化的构造函数。
 */
public interface IJsonSerializable
{
    @NotNull
    JsonObject serialize();
}
