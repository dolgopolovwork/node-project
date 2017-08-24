package ru.babobka.nodeutils.validation;

/**
 * Created by 123 on 24.07.2017.
 */
public interface ValidationRule<D> {

    void validate(D data);
}
