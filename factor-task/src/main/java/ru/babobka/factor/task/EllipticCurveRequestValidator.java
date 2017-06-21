package ru.babobka.factor.task;

import ru.babobka.factor.util.MathUtil;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.subtask.model.RequestValidator;
import ru.babobka.subtask.model.ValidationResult;

import java.math.BigInteger;

/**
 * Created by 123 on 20.06.2017.
 */
public class EllipticCurveRequestValidator implements RequestValidator {

    @Override
    public ValidationResult validateRequest(NodeRequest request) {
        if (request == null) {
            return ValidationResult.fail("Empty request");
        }
        try {
            BigInteger number = new BigInteger(request.getStringDataValue(Params.NUMBER.getValue()));
            if (number.compareTo(BigInteger.valueOf(3)) <= 0) {
                return ValidationResult.fail("number must be greater than 3");
            } else if (MathUtil.isPrime(number)) {
                return ValidationResult.fail("number is not composite");
            }

        } catch (RuntimeException e) {
            return ValidationResult.fail(e);
        }

        return ValidationResult.ok();
    }
}
