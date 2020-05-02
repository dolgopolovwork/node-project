package ru.babobka.nodeweb.validation.user.update;

import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodeutils.validation.ValidationRule;
import ru.babobka.nodeutils.validation.Validator;

/**
 * Created by 123 on 17.08.2017.
 */
public class UpdateUserValidator extends Validator<UserDTO> {

    public UpdateUserValidator(ValidationRule<UserDTO>... rules) {
        super(rules);
    }
}
