package ru.babobka.nodeweb.validation.user.update.rule;


import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodeutils.validation.ValidationRule;
import ru.babobka.nodeweb.validation.user.add.rule.TaskCountValidationRule;

/**
 * Created by 123 on 24.07.2017.
 */
public class NullableTaskCountValidationRule implements ValidationRule<UserDTO> {

    private final TaskCountValidationRule taskCountValidationRule = new TaskCountValidationRule();

    @Override
    public void validate(UserDTO data) {
        if (data.getTaskCount() != null) {
            taskCountValidationRule.validate(data);
        }
    }
}
