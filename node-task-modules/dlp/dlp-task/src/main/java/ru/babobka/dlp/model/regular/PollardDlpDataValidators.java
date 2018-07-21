package ru.babobka.dlp.model.regular;

import ru.babobka.dlp.task.Params;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetask.model.DataValidators;
import ru.babobka.nodeutils.util.ArrayUtil;

import java.math.BigInteger;

/**
 * Created by 123 on 25.10.2017.
 */
public class PollardDlpDataValidators extends DataValidators {

    @Override
    protected boolean isValidResponseImpl(NodeResponse response) {
        if (response.getStatus() != ResponseStatus.NORMAL) {
            return false;
        }
        BigInteger exp = response.getDataValue(Params.EXP.getValue());
        BigInteger x = response.getDataValue(Params.X.getValue());
        BigInteger y = response.getDataValue(Params.Y.getValue());
        BigInteger mod = response.getDataValue(Params.MOD.getValue());
        return !ArrayUtil.isNull(exp, x, y, mod) && x.modPow(exp, mod).equals(y.mod(mod));
    }

    @Override
    protected boolean isValidRequestImpl(NodeRequest request) {
        BigInteger x = request.getDataValue(Params.X.getValue());
        BigInteger y = request.getDataValue(Params.Y.getValue());
        BigInteger mod = request.getDataValue(Params.MOD.getValue());
        return !ArrayUtil.isNull(x, y, mod) && !x.equals(BigInteger.ZERO) &&
                !y.equals(BigInteger.ZERO) && mod.compareTo(BigInteger.ONE) > 0;
    }
}
