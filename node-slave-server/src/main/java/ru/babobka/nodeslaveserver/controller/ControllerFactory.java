package ru.babobka.nodeslaveserver.controller;

import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodeutils.network.NodeConnection;

@FunctionalInterface
public interface ControllerFactory {

    AbstractSocketController create(NodeConnection connection, TasksStorage tasksStorage);
}
