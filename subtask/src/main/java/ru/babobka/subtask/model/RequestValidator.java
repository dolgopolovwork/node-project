package ru.babobka.subtask.model;

import ru.babobka.nodeserials.NodeRequest;

/**
 * Created by 123 on 20.06.2017.
 */
public interface RequestValidator {

    ValidationResult validateRequest(NodeRequest request);


}
