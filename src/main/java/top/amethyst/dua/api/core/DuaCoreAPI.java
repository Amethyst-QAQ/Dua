package top.amethyst.dua.api.core;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Dua核心API，负责创建区块链相关对象
 */
public interface DuaCoreAPI
{
    /**
     * 创建指定对象的哈希值
     * @param objects 指定的对象，可以为null
     * @see IHash
     */
    IHash createHash(IJsonSerializable... objects);

    /**
     * 创建空对象的哈希值
     * @see IHash
     */
    IHash createHash();

    /**
     * 从Json反序列化哈希值
     * @see IHash
     */
    IHash deserializeHash(JsonObject json);

    /**
     * 由给定的块头和块身创建区块
     * @see IBlock
     */
    IBlock createBlock(@NotNull IBlock.IHead head, @Nullable IBlock.IBody body);

    /**
     * 从Json反序列化区块
     * @see IBlock
     */
    IBlock deserializeBlock(JsonObject json);

    /**
     * 由指定数据创建块头
     * @see IBlock.IHead
     */
    IBlock.IHead createBlockHead(int index, IHash prevHash, long timestamp, int nonce, IHash rootHash);

    /**
     * 从Json反序列化块头
     * @see IBlock.IHead
     */
    IBlock.IHead deserializeBlockHead(JsonObject json);

    /**
     * 由指定数据创建块身
     * @see IBlock.IBody
     */
    IBlock.IBody createBlockBody(IMerkleTree<ITransaction> transactions);

    /**
     * 从Json反序列化块身
     * @see IBlock.IBody
     */
    IBlock.IBody deserializeBlockBody(JsonObject json);

    /**
     * 从指定数据创建默克尔树
     * @see IMerkleTree
     */
    <T extends IJsonSerializable> IMerkleTree<T> createMerkleTree(Collection< ? extends T> data);

    /**
     * 从Json反序列化默克尔树
     * @see IMerkleTree
     */
    <T extends IJsonSerializable> IMerkleTree<T> deserializeMerkleTree(JsonObject json);

    /**
     * 从指定数据创建交易脚本
     * @see IScript
     */
    IScript createScript(String data, List<IScript.IInputWrapper<?>> inputs);

    /**
     * 从Json反序列化交易脚本
     * @see IScript
     */
    IScript deserializeScript(JsonObject json);

    /**
     * 从指定数据创建交易脚本输入
     * @see IScript.IInputWrapper
     */
    IScript.IInputWrapper<Integer> createIntegerInputWrapper(int value);

    /**
     * 从指定数据创建交易脚本输入
     * @see IScript.IInputWrapper
     */
    IScript.IInputWrapper<Method> createMethodInputWrapper(Method value);

    /**
     * 从Json反序列化交易脚本输入
     * @see IScript.IInputWrapper
     */
    IScript.IInputWrapper<?> deserializeInputWrapper(JsonObject json);

    /**
     * 从指定数据创建交易
     * @see ITransaction
     */
    ITransaction createTransaction(String version, int lockTime, ArrayList<ITransaction.IInput> inputs, ArrayList<ITransaction.IOutput> outputs);

    /**
     * 从Json反序列化交易
     * @see ITransaction
     */
    ITransaction deserializeTransaction(JsonObject json);
}
