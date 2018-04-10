package ru.babobka.nodeutils.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 123 on 24.07.2017.
 */
public class Validator<D> {

    private final List<ValidationRule<D>> rules = new ArrayList<>();

    public Validator(ValidationRule<D> rule) {
        rules.add(rule);
    }

    public Validator(ValidationRule<D>[] rules) {
        this(Arrays.asList(rules));
    }

    public Validator(List<ValidationRule<D>> rules) {
        if (rules != null) {
            this.rules.addAll(rules);
        }
    }

    public void validate(D data) {
        for (ValidationRule<D> rule : rules) {
            rule.validate(data);
        }
    }

}
