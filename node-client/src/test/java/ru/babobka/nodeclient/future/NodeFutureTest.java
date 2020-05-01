package ru.babobka.nodeclient.future;

import org.junit.Test;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.func.done.DoneFunc;
import ru.babobka.nodeutils.network.NodeConnection;

import java.util.concurrent.Future;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NodeFutureTest {

    @Test
    public void testCancel() {
        NodeConnection connection = mock(NodeConnection.class);
        DoneFunc doneFunc = mock(DoneFunc.class);
        Future<NodeResponse> future = mock(Future.class);
        NodeFuture<NodeResponse> nodeFuture = new NodeFuture<>(
                connection, future, doneFunc);
        nodeFuture.cancel(true);
        verify(doneFunc).setDone();
        verify(connection).close();
        verify(future).cancel(true);

    }
}
