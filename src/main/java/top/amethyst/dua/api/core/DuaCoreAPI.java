package top.amethyst.dua.api.core;

import com.google.gson.JsonObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface DuaCoreAPI
{
    IHash createHash(IJsonSerializable obj);
    IHash deserializeHash(JsonObject json);

    IBlock createBlock(IBlock.IHead head, IBlock.IBody body);
    IBlock deserializeBlock(JsonObject json);

    IBlock.IHead createBlockHead(int index, IHash prevHash, long timestamp, int nonce, IHash rootHash);
    IBlock.IHead deserializeBlockHead(JsonObject json);

    IBlock.IBody createBlockBody(IMerkleTree<ITransaction> transactions);
    IBlock.IBody deserializeBlockBody(JsonObject json);

    <T extends IJsonSerializable> IMerkleTree<T> createMerkleTree(Collection< ? extends T> data);
    <T extends IJsonSerializable> IMerkleTree<T> deserializeMerkleTree(JsonObject json);

    IScript createScript(String data, List<IScript.IInputWrapper<?>> inputs);
    IScript deserializeScript(JsonObject json);

    IScript.IInputWrapper<Integer> createIntegerInputWrapper(int value);
    IScript.IInputWrapper<Method> createMethodInputWrapper(Method value);
    IScript.IInputWrapper<?> deserializeInputWrapper(JsonObject json);

    ITransaction createTransaction(String version, int lockTime, ArrayList<ITransaction.IInput> inputs, ArrayList<ITransaction.IOutput> outputs);
    ITransaction deserializeTransaction(JsonObject json);
}
