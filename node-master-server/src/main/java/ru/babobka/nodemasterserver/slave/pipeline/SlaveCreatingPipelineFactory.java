package ru.babobka.nodemasterserver.slave.pipeline;

import ru.babobka.nodemasterserver.slave.pipeline.step.CommonTasksStep;
import ru.babobka.nodemasterserver.slave.pipeline.step.SlaveAuthStep;
import ru.babobka.nodemasterserver.slave.pipeline.step.SlaveCreateStep;
import ru.babobka.nodemasterserver.slave.pipeline.step.SlaveRunStep;
import ru.babobka.nodeutils.func.pipeline.Pipeline;

/**
 * Created by 123 on 08.06.2018.
 */
public class SlaveCreatingPipelineFactory {

    public Pipeline<PipeContext> create(PipeContext pipeContext) {
        return new Pipeline<PipeContext>(() -> pipeContext.getConnection().close())
                .add(new SlaveAuthStep())
                .add(new CommonTasksStep())
                .add(new SlaveCreateStep())
                .add(new SlaveRunStep());
    }
}
