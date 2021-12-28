package top.amethyst.dua.api.core;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Queue;

public interface IScript extends IJsonSerializable
{
    interface IInputWrapper <T> extends IJsonSerializable
    {
        T get();
    }

    ArrayList<Character> getData();
    ArrayList<Integer> getBracketInfo();
    @NotNull
    Queue<?> enqueueInputs();
}
