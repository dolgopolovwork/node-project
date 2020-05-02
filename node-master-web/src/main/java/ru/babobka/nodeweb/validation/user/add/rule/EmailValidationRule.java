package ru.babobka.nodeweb.validation.user.add.rule;

import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 23.07.2017.
 */
public class EmailValidationRule implements ValidationRule<UserDTO> {

    @Override
    public void validate(UserDTO value) {
        String email = value.getEmail();
        if (value.getEmail() == null || !TextUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("email " + email + " is not valid");
        }
    }
}
