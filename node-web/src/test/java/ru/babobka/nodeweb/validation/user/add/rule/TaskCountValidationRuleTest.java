package ru.babobka.nodeweb.validation.user.add.rule;

import org.junit.Test;
import ru.babobka.nodebusiness.dto.UserDTO;

/**
 * Created by 123 on 13.08.2017.
 */
public class TaskCountValidationRuleTest {

    private TaskCountValidationRule taskCountValidationRule = new TaskCountValidationRule();

    @Test
    public void testValidTaskCount() {
        UserDTO userDTO = new UserDTO();
        userDTO.setTaskCount(123);
        taskCountValidationRule.validate(userDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullTaskCount() {
        UserDTO userDTO = new UserDTO();
        taskCountValidationRule.validate(userDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeTaskCount() {
        UserDTO userDTO = new UserDTO();
        userDTO.setTaskCount(-123);
        taskCountValidationRule.validate(userDTO);
    }

}
