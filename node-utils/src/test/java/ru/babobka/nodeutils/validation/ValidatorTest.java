package ru.babobka.nodeutils.validation;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 01.09.2017.
 */
public class ValidatorTest {

    private final ValidationRule<Object> validationRule = mock(ValidationRule.class);

    @Test
    public void testValidate() {
        List<ValidationRule<Object>> validationRules = Arrays.asList(validationRule, validationRule, validationRule);
        Validator<Object> validator = new Validator<>(validationRules);
        Object dataObject = new Object();
        validator.validate(dataObject);
        verify(validationRule, times(validationRules.size())).validate(dataObject);
    }
}
