package ru.babobka.nodeutils.func.pipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 07.06.2018.
 */
public class Pipeline<C> {
    private final List<Step<C>> steps = new ArrayList<>();
    private final OnPipelineFailListener pipelineFailListener;

    public Pipeline(OnPipelineFailListener pipelineFailListener) {
        this.pipelineFailListener = pipelineFailListener;
    }

    public Pipeline() {
        this(null);
    }

    public Pipeline<C> add(Step<C> step) {
        if (step == null) {
            throw new IllegalArgumentException("step is null");
        }
        steps.add(step);
        return this;
    }

    public boolean execute(C context) {
        for (Step<C> step : steps) {
            if (!step.execute(context)) {
                if (pipelineFailListener != null) {
                    pipelineFailListener.onPipeFail();
                }
                return false;
            }
        }
        return true;
    }

}
