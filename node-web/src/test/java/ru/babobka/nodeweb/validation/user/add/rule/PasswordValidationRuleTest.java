package ru.babobka.nodeweb.validation.user.add.rule;

import org.junit.Test;
import ru.babobka.nodebusiness.dto.UserDTO;

/**
 * Created by 123 on 13.08.2017.
 */
public class PasswordValidationRuleTest {

    private PasswordValidationRule passwordValidationRule = new PasswordValidationRule();

    @Test
    public void testValidPassword() {
        UserDTO userDTO = new UserDTO();
        userDTO.setHashedPassword("12345678");
        passwordValidationRule.validate(userDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLittlePassword() {
        UserDTO userDTO = new UserDTO();
        userDTO.setHashedPassword("123");
        passwordValidationRule.validate(userDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPassword() {
        UserDTO userDTO = new UserDTO();
        userDTO.setHashedPassword("");
        passwordValidationRule.validate(userDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPassword() {
        UserDTO userDTO = new UserDTO();
        passwordValidationRule.validate(userDTO);
    }

}
