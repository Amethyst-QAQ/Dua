package top.amethyst.dua.core.api;

public interface IOutput extends IJsonSerializable
{
    int getValue();
    int getIndex();
    IScript getOutputScript();
}
