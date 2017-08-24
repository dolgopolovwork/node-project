package ru.babobka.nodeweb.validation.user.update.rule;

import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodeutils.validation.ValidationRule;
import ru.babobka.nodeweb.validation.user.add.rule.EmailValidationRule;

/**
 * Created by 123 on 23.07.2017.
 */
public class NullableEmailValidationRule implements ValidationRule<UserDTO> {

    private EmailValidationRule emailValidationRule = new EmailValidationRule();

    @Override
    public void validate(UserDTO data) {
        if (data.getEmail() != null) {
            emailValidationRule.validate(data);
        }
    }
}
