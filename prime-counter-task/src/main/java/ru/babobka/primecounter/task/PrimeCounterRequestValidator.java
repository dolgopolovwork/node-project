package ru.babobka.primecounter.task;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.subtask.model.RequestValidator;
import ru.babobka.subtask.model.ValidationResult;

/**
 * Created by 123 on 20.06.2017.
 */
public class PrimeCounterRequestValidator implements RequestValidator {

    @Override
    public ValidationResult validateRequest(NodeRequest request) {
        if (request == null) {
            return ValidationResult.fail("Request is empty");
        } else {
            try {
                long begin = Long.parseLong(request.getStringDataValue(Params.BEGIN.getValue()));
                long end = Long.parseLong(request.getStringDataValue(Params.END.getValue()));
                if (begin < 0 || end < 0 || begin > end) {
                    return ValidationResult.fail("begin is more than end");
                }
            } catch (NumberFormatException e) {
                return ValidationResult.fail(e);
            }
        }
        return ValidationResult.ok();
    }
}
