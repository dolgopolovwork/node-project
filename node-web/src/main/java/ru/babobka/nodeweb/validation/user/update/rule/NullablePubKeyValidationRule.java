package ru.babobka.nodeweb.validation.user.update.rule;

import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodeutils.validation.ValidationRule;
import ru.babobka.nodeweb.validation.user.add.rule.PubKeyValidationRule;

public class NullablePubKeyValidationRule implements ValidationRule<UserDTO> {

    private final PubKeyValidationRule pubKeyValidationRule = new PubKeyValidationRule();

    @Override
    public void validate(UserDTO userDTO) {
        if (userDTO.getBase64PubKey() != null) {
            pubKeyValidationRule.validate(userDTO);
        }
    }
}
