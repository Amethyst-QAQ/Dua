package top.amethyst.dua.core.api;

/**
 * 自定义哈希对象
 * <br>
 * 这个接口允许对象在被计算哈希值时使用一个其他对象代替自身计算哈希值
 */
public interface ICustomHashObject
{
    /**
     * 返回自定义的哈希值计算所用对象
     */
    Object getHashPart();
}
