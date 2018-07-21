package ru.babobka.dlp.listener;

import ru.babobka.nodeutils.func.done.DoneFunc;
import ru.babobka.dlp.mapper.PollardDistResultMapper;
import ru.babobka.dlp.model.dist.PollardDistResult;
import ru.babobka.dlp.task.Params;
import ru.babobka.nodeclient.listener.ListenerResult;
import ru.babobka.nodeclient.listener.OnResponseListener;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

/**
 * Created by 123 on 15.07.2018.
 */
public class DlpDistResponseListener implements OnResponseListener {

    private final BlockingQueue<PollardDistResult> results;
    private final DoneFunc doneFunc;
    private final PollardDistResultMapper pollardDistResultMapper;

    public DlpDistResponseListener(BlockingQueue<PollardDistResult> results,
                                   DoneFunc doneFunc,
                                   PollardDistResultMapper pollardDistResultMapper) {
        if (results == null) {
            throw new IllegalArgumentException("results is null");
        } else if (doneFunc == null) {
            throw new IllegalArgumentException("doneFunc is null");
        } else if (pollardDistResultMapper == null) {
            throw new IllegalArgumentException("pollardDistResultMapper is null");
        }
        this.results = results;
        this.doneFunc = doneFunc;
        this.pollardDistResultMapper = pollardDistResultMapper;
    }

    @Override
    public ListenerResult onResponse(NodeResponse response) {
        if (response.getStatus() != ResponseStatus.NORMAL) {
            return ListenerResult.PROCEED;
        }
        PollardDistResult result = pollardDistResultMapper.reverseMap(response.getData());
        try {
            results.put(result);
        } catch (InterruptedException e) {
            if (!doneFunc.isDone()) {
                e.printStackTrace();
            }
        }
        if (result.hasResult()) {
            BigInteger exp = result.getExp();
            BigInteger x = response.getData().get(Params.X.getValue());
            BigInteger y = response.getData().get(Params.Y.getValue());
            BigInteger mod = response.getData().get(Params.MOD.getValue());
            if (x.modPow(exp, mod).equals(y)) {
                doneFunc.setDone();
            }
            return ListenerResult.STOP;
        }
        return ListenerResult.PROCEED;
    }
}
