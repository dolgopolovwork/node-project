package ru.babobka.nodemasterserver.applyer;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.func.Applyer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by 123 on 11.09.2017.
 */
//TODO написать тесты
public class GroupTaskApplyer extends Applyer<NodeRequest> {

    private final Map<String, List<NodeRequest>> groupedTasks = new HashMap<>();

    @Override
    protected void applyImpl(NodeRequest request) {
        if (!groupedTasks.containsKey(request.getTaskName())) {
            groupedTasks.put(request.getTaskName(), new LinkedList<>());
        }
        groupedTasks.get(request.getTaskName()).add(request);
    }

    public Map<String, List<NodeRequest>> getGroupedTasks() {
        return groupedTasks;
    }
}
