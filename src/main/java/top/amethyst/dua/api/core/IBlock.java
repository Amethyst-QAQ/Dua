package top.amethyst.dua.api.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 区块
 * <br>
 * 区块由块头和块身组成，每个区块必须存储块头，但可以不存储块身，区块计算哈希值时，仅计算块头哈希值
 */
public interface IBlock extends IJsonSerializable
{
    /**
     * 块头
     * <br>
     * 块头由序号，上一区块哈希值，时间戳，工作量证明和块身默克尔树根节点的哈希值组成
     */
    interface IHead extends IJsonSerializable
    {
        /**
         * 生成一个工作量证明递增的块头
         * @return 一个新的块头，其他数据与本块头相同，但工作量证明+1
         */
        @NotNull
        IBlock.IHead next();

        /**
         * 获取序号
         */
        int getIndex();

        /**
         * 获取上一个区块的哈希值
         */
        @NotNull
        IHash getPrevHash();

        /**
         * 获取时间戳
         */
        long getTimestamp();

        /**
         * 获取工作量证明
         */
        int getNonce();

        /**
         * 获取块身中默克尔树的根节点哈希值
         */
        @NotNull
        IHash getRootHash();
    }

    /**
     * 块身
     * <br>
     * 块身中以默克尔树的形式存储交易列表
     */
    interface IBody extends IJsonSerializable
    {
        /**
         * 获取交易列表默克尔树
         */
        @NotNull
        IMerkleTree<ITransaction> getTransactions();
    }

    /**
     * 获取块头
     */
    @NotNull
    IHead getHead();

    /**
     * 获取块身
     */
    @Nullable
    IBody getBody();

    /**
     * 判断给定的块身是否有效
     * @param body 给定的块身
     * @return 如果块身默克尔树根节点哈希值与块头的根节点哈希值相同，或者块身为null，返回true
     */
    boolean isBodyValid(@Nullable IBody body);

    /**
     * 设置块身
     * @param body 新的块身，只接受有效的块身或null
     */
    void setBody(@Nullable IBlock.IBody body);
}
