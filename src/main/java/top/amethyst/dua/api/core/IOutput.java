package top.amethyst.dua.api.core;

public interface IOutput extends IJsonSerializable
{
    int getValue();
    int getIndex();
    IScript getOutputScript();
}
