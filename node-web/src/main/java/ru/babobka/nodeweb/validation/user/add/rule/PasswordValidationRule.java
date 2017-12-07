package ru.babobka.nodeweb.validation.user.add.rule;


import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 24.07.2017.
 */
public class PasswordValidationRule implements ValidationRule<UserDTO> {

    private static final int MIN_PASSWORD_LENGTH = 8;

    @Override
    public void validate(UserDTO data) {
        if (TextUtil.isEmpty(data.getHashedPassword())) {
            throw new IllegalArgumentException("password must be set");
        } else if (data.getHashedPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("min password length is " + MIN_PASSWORD_LENGTH);
        }
    }
}
