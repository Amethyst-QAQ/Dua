package top.amethyst.dua.api.core;

public interface IInput extends IJsonSerializable
{
    String getTxid();
    int getOutput();
    IScript getInputScript();
}
