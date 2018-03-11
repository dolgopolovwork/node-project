package ru.babobka.vsjws.validator;

/**
 * Created by 123 on 01.01.2018.
 */
public interface ValidationRule<D> {

    void validate(D data);
}
