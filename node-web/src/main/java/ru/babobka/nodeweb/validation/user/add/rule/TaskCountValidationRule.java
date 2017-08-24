package ru.babobka.nodeweb.validation.user.add.rule;


import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 24.07.2017.
 */
public class TaskCountValidationRule implements ValidationRule<UserDTO> {
    @Override
    public void validate(UserDTO data) {
        if (data.getTaskCount() == null) {
            throw new IllegalArgumentException("task count is null");
        } else if (data.getTaskCount() < 0) {
            throw new IllegalArgumentException("taskCount is negative");
        }
    }
}
