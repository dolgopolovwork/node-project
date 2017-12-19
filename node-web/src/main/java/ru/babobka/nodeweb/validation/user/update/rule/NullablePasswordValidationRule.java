package ru.babobka.nodeweb.validation.user.update.rule;

import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodeutils.validation.ValidationRule;
import ru.babobka.nodeweb.validation.user.add.rule.PasswordValidationRule;

/**
 * Created by 123 on 24.07.2017.
 */
public class NullablePasswordValidationRule implements ValidationRule<UserDTO> {

    private final PasswordValidationRule passwordValidationRule = new PasswordValidationRule();

    @Override
    public void validate(UserDTO data) {
        if (data.getHashedPassword() != null) {
            passwordValidationRule.validate(data);
        }
    }
}
