package ru.babobka.nodeweb.validation.user.add;

import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodeutils.validation.ValidationRule;
import ru.babobka.nodeutils.validation.Validator;

/**
 * Created by 123 on 15.08.2017.
 */
public class AddUserValidator extends Validator<UserDTO> {

    public AddUserValidator(ValidationRule<UserDTO>... rules) {
        super(rules);
    }
}
