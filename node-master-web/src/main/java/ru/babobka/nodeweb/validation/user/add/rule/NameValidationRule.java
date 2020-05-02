package ru.babobka.nodeweb.validation.user.add.rule;

import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;


/**
 * Created by 123 on 24.07.2017.
 */
public class NameValidationRule implements ValidationRule<UserDTO> {
    @Override
    public void validate(UserDTO data) {
        if (TextUtil.isEmpty(data.getName())) {
            throw new IllegalArgumentException("name must be set");
        }
    }
}
