package ru.babobka.factor.model;

import ru.babobka.factor.task.Params;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetask.model.DataValidators;
import ru.babobka.nodeutils.util.ArrayUtil;

import java.math.BigInteger;

/**
 * Created by 123 on 25.10.2017.
 */
public class EllipticFactorDataValidators extends DataValidators {

    @Override
    protected boolean isValidResponseImpl(NodeResponse response) {
        if (response.getStatus() != ResponseStatus.NORMAL) {
            return false;
        }
        BigInteger factor = response.getDataValue(Params.FACTOR.getValue());
        BigInteger n = response.getDataValue(Params.NUMBER.getValue());
        return !ArrayUtil.isNull(factor, n) && !factor.equals(BigInteger.ONE.negate()) && n.mod(factor).equals(BigInteger.ZERO);
    }

    @Override
    protected boolean isValidRequestImpl(NodeRequest request) {
        BigInteger n = request.getDataValue(Params.NUMBER.getValue());
        return n.compareTo(BigInteger.ZERO) > 0;
    }
}
