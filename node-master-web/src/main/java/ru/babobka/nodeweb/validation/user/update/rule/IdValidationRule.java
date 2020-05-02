package ru.babobka.nodeweb.validation.user.update.rule;

import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 23.07.2017.
 */
public class IdValidationRule implements ValidationRule<UserDTO> {

    @Override
    public void validate(UserDTO data) {
        if (TextUtil.isEmpty(data.getId())) {
            throw new IllegalArgumentException("id must be set");
        }
    }
}
