package top.amethyst.dua.core;

import com.google.gson.JsonObject;
import top.amethyst.dua.api.core.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class APIImpl implements DuaCoreAPI
{
    public static final APIImpl INSTANCE = new APIImpl();

    @Override
    public IHash createHash(IJsonSerializable obj)
    {
        return new Hash(obj);
    }

    @Override
    public IHash deserializeHash(JsonObject json)
    {
        return new Hash(json);
    }

    @Override
    public IBlock createBlock(IBlock.IHead head, IBlock.IBody body)
    {
        return new Block(head, body);
    }

    @Override
    public IBlock deserializeBlock(JsonObject json)
    {
        return new Block(json);
    }

    @Override
    public IBlock.IHead createBlockHead(int index, IHash prevHash, long timestamp, int nonce, IHash rootHash)
    {
        return new Block.Head(index, prevHash, timestamp, nonce, rootHash);
    }

    @Override
    public IBlock.IHead deserializeBlockHead(JsonObject json)
    {
        return new Block.Head(json);
    }

    @Override
    public IBlock.IBody createBlockBody(IMerkleTree<ITransaction> transactions)
    {
        return new Block.Body(transactions);
    }

    @Override
    public IBlock.IBody deserializeBlockBody(JsonObject json)
    {
        return new Block.Body(json);
    }

    @Override
    public <T extends IJsonSerializable> IMerkleTree<T> createMerkleTree(Collection<? extends T> data)
    {
        return new MerkleTree<T>(data) {};
    }

    @Override
    public <T extends IJsonSerializable> IMerkleTree<T> deserializeMerkleTree(JsonObject json)
    {
        return new MerkleTree<T>(json) {};
    }

    @Override
    public IScript createScript(String data, List<IScript.IInputWrapper<?>> inputs)
    {
        return new Script(data, inputs);
    }

    @Override
    public IScript deserializeScript(JsonObject json)
    {
        return new Script(json);
    }

    @Override
    public IScript.IInputWrapper<Integer> createIntegerInputWrapper(int value)
    {
        return new ScriptInputWrapper.IntegerInputWrapper(value);
    }

    @Override
    public IScript.IInputWrapper<Method> createMethodInputWrapper(Method value)
    {
        return new ScriptInputWrapper.MethodInputWrapper(value);
    }

    @Override
    public IScript.IInputWrapper<?> deserializeInputWrapper(JsonObject json)
    {
        return ScriptInputWrapper.deserialize(json);
    }

    @Override
    public ITransaction createTransaction(String version, int lockTime, ArrayList<ITransaction.IInput> inputs, ArrayList<ITransaction.IOutput> outputs)
    {
        return new Transaction(version, lockTime, inputs, outputs);
    }

    @Override
    public ITransaction deserializeTransaction(JsonObject json)
    {
        return new Transaction(json);
    }
}
