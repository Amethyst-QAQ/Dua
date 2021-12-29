package top.amethyst.dua.api.core;

import java.util.ArrayList;

public interface ITransaction extends IJsonSerializable
{
    interface IInput extends IJsonSerializable
    {
        IHash getTransactionId();
        int getOutput();
        IScript getInputScript();
    }

    interface IOutput extends IJsonSerializable
    {
        long getValue();
        int getIndex();
        IScript getOutputScript();
    }

    String getVersion();

    int getLockTime();

    long getTime();

    long getBlockTime();

    IHash getBlockHash();

    ArrayList<IInput> getInputs();

    ArrayList<IOutput> getOutputs();

    void setTime(long time);

    void setBlockTime(long blockTime);

    void setBlockHash(IHash blockHash);
}
