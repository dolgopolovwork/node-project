package ru.babobka.nodetask.model;

import org.apache.log4j.Logger;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;

/**
 * Created by 123 on 22.10.2017.
 */
public abstract class DataValidators {

    private static final Logger logger = Logger.getLogger(DataValidators.class);

    public boolean isValidResponse(NodeResponse response) {
        if (response == null) {
            return false;
        }
        try {
            return isValidResponseImpl(response);
        } catch (RuntimeException e) {
            logger.error("exception thrown", e);
            return false;
        }
    }

    public boolean isValidRequest(NodeRequest request) {
        if (request == null) {
            return false;
        }
        try {
            return isValidRequestImpl(request);
        } catch (RuntimeException e) {
            logger.error("exception thrown", e);
            return false;
        }
    }

    protected abstract boolean isValidResponseImpl(NodeResponse response);

    protected abstract boolean isValidRequestImpl(NodeRequest request);

}
