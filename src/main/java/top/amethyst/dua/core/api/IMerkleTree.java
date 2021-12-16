package top.amethyst.dua.core.api;

import org.jetbrains.annotations.NotNull;

/**
 * 默克尔树，用于存储交易信息
 * @param <T> 默克尔树存储的数据类型
 */
public interface IMerkleTree <T>
{
    /**
     * 默克尔证据
     * <br>
     * 默克尔证据由默克尔树产生，用来判断指定数据在默克尔树中的合法性
     */
    public interface IMerkleProof <T>
    {
        /**
         * 获得默克尔证据对应的数据
         */
        @NotNull
        T getDatum();

        /**
         * 验证默克尔证据的有效性
         * @param rootHash 对应的默克尔树根节点哈希值
         * @return 如果默克尔证据有效，返回true
         */
        boolean valid(@NotNull IHash rootHash);
    }

    /**
     * 获得根节点的哈希值
     */
    @NotNull
    IHash getRootHash();

    /**
     * 判断指定数据是否在默克尔树中
     * @param value 要判断的数据
     * @return 如果数据在默克尔树中，返回true，否则返回false
     */
    boolean contains(@NotNull T value);

    /**
     * 获取默克尔证据
     * <br>
     * 注意，调用本方法前需要先调用{@link #contains(T)}确定要验证的数据在默克尔树中
     * @param datum 要验证的数据
     * @return 用于验证数据的默克尔证据
     *
     */
    @NotNull
    IMerkleProof<T> getMerkleProof(@NotNull T datum);
}
