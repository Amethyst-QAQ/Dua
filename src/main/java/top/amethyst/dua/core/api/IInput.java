package top.amethyst.dua.core.api;

public interface IInput extends IJsonSerializable
{
    String getTxid();
    int getOutput();
    IScript getInputScript();
}
