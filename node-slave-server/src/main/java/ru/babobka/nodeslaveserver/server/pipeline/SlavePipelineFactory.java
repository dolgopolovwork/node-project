package ru.babobka.nodeslaveserver.server.pipeline;

import lombok.NonNull;
import ru.babobka.nodeslaveserver.server.pipeline.step.*;
import ru.babobka.nodeutils.func.pipeline.Pipeline;

/**
 * Created by 123 on 08.06.2018.
 */
public class SlavePipelineFactory {

    public Pipeline<PipeContext> create(@NonNull PipeContext pipeContext) {
        return new Pipeline<PipeContext>(() -> pipeContext.getConnection().close())
                .add(new AuthStep())
                .add(new CommonTasksStep())
                .add(new ServerAuthStep())
                .add(new SessionCreatingStep())
                .add(new GetServerTimeStep());
    }
}

