package ru.babobka.nodeutils.func.pipeline;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 08.06.2018.
 */
public class PipelineTest {

    @Test
    public void testExecute() {
        Pipeline pipeline = new Pipeline();
        Step step = mock(Step.class);
        when(step.execute(any())).thenReturn(true);
        int steps = 10;
        for (int i = 0; i < steps; i++) {
            pipeline.add(step);
        }
        Object object = new Object();
        assertTrue(pipeline.execute(object));
        verify(step, times(steps)).execute(object);
    }

    @Test
    public void testExecuteFailFirst() {
        OnPipelineFailListener onPipelineFailListener = mock(OnPipelineFailListener.class);
        Pipeline pipeline = new Pipeline(onPipelineFailListener);
        Step failStep = mock(Step.class);
        when(failStep.execute(any())).thenReturn(false);
        Step step = mock(Step.class);
        when(step.execute(any())).thenReturn(true);
        pipeline.add(failStep);
        int steps = 10;
        for (int i = 0; i < steps; i++) {
            pipeline.add(step);
        }
        Object object = new Object();
        assertFalse(pipeline.execute(object));
        verify(failStep).execute(object);
        verify(step, never()).execute(object);
        verify(onPipelineFailListener).onPipeFail();
    }

    @Test
    public void testExecuteFailLast() {
        OnPipelineFailListener onPipelineFailListener = mock(OnPipelineFailListener.class);
        Pipeline pipeline = new Pipeline(onPipelineFailListener);
        Step failStep = mock(Step.class);
        when(failStep.execute(any())).thenReturn(false);
        Step step = mock(Step.class);
        when(step.execute(any())).thenReturn(true);
        int steps = 10;
        for (int i = 0; i < steps; i++) {
            pipeline.add(step);
        }
        pipeline.add(failStep);
        Object object = new Object();
        assertFalse(pipeline.execute(object));
        verify(failStep).execute(object);
        verify(step, times(steps)).execute(object);
        verify(onPipelineFailListener).onPipeFail();
    }
}
