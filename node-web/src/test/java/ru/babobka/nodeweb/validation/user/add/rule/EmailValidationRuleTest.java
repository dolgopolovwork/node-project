package ru.babobka.nodeweb.validation.user.add.rule;

import org.junit.Test;
import ru.babobka.nodebusiness.dto.UserDTO;

/**
 * Created by 123 on 13.08.2017.
 */
public class EmailValidationRuleTest {

    private EmailValidationRule emailValidationRule = new EmailValidationRule();

    @Test
    public void testValidEmail() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("babobka@bk.ru");
        emailValidationRule.validate(userDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEmail() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("babobka@bk");
        emailValidationRule.validate(userDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullEmail() {
        UserDTO userDTO = new UserDTO();
        emailValidationRule.validate(userDTO);
    }

}
