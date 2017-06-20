package ru.babobka.subtask.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 14.06.2017.
 */
public class ValidationResultTest {

    @Test
    public void testOk() {
        ValidationResult validationResult = ValidationResult.ok();
        assertTrue(validationResult.isValid());
    }

    @Test
    public void testFail() {
        ValidationResult validationResult = ValidationResult.fail(new Exception());
        assertFalse(validationResult.isValid());
    }

    @Test
    public void testFailMessage() {
        ValidationResult validationResult = ValidationResult.fail("Fail message");
        assertFalse(validationResult.isValid());
        assertEquals(validationResult.getMessage(), "Fail message");
    }
}

