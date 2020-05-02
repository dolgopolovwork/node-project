package ru.babobka.nodeweb.validation.user.update.rule;

import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodeutils.validation.ValidationRule;
import ru.babobka.nodeweb.validation.user.add.rule.NameValidationRule;


/**
 * Created by 123 on 24.07.2017.
 */
public class NullableNameValidationRule implements ValidationRule<UserDTO> {

    private final NameValidationRule nameValidationRule = new NameValidationRule();

    @Override
    public void validate(UserDTO data) {
        if (data.getName() != null) {
            nameValidationRule.validate(data);
        }
    }
}
