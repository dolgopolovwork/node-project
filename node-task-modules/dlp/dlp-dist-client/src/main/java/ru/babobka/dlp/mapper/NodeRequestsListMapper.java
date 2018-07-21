package ru.babobka.dlp.mapper;

import ru.babobka.dlp.model.dist.DlpTaskDist;
import ru.babobka.dlp.task.Params;
import ru.babobka.dlp.task.dist.PollardDistDlpTask;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.data.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 123 on 15.07.2018.
 */
public class NodeRequestsListMapper {

    private static final String TASK_NAME = PollardDistDlpTask.class.getCanonicalName();

    public List<NodeRequest> map(DlpTaskDist taskDist, int nodes) {
        if (taskDist == null) {
            throw new IllegalArgumentException("taskDist is null");
        } else if (nodes < 1) {
            throw new IllegalArgumentException("there must be at least one node to execute task");
        }
        List<NodeRequest> requests = new ArrayList<>(nodes);
        Data data = new Data();
        data.put(Params.X.getValue(), taskDist.getGen().getNumber());
        data.put(Params.Y.getValue(), taskDist.getY().getNumber());
        data.put(Params.MOD.getValue(), taskDist.getGen().getMod());
        data.put(Params.LOOPS.getValue(), taskDist.getLoops());
        for (int i = 0; i < nodes; i++) {
            requests.add(NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data));
        }
        return requests;
    }


}
