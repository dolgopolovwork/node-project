package ru.babobka.nodeweb.validation.user.add.rule;

import org.junit.Test;
import ru.babobka.nodebusiness.dto.UserDTO;

/**
 * Created by 123 on 13.08.2017.
 */
public class NameValidationRuleTest {

    private final NameValidationRule nameValidationRule = new NameValidationRule();

    @Test
    public void testValidName() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("abc");
        nameValidationRule.validate(userDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyName() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("");
        nameValidationRule.validate(userDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullName() {
        UserDTO userDTO = new UserDTO();
        nameValidationRule.validate(userDTO);
    }
}
