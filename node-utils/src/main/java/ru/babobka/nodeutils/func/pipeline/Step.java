package ru.babobka.nodeutils.func.pipeline;

/**
 * Created by 123 on 07.06.2018.
 */
public interface Step<C> {

    boolean execute(C context);
}
