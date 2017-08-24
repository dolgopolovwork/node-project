package ru.babobka.nodeweb;

import ru.babobka.nodebusiness.service.NodeUsersServiceImpl;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeweb.validation.user.add.AddUserValidator;
import ru.babobka.nodeweb.validation.user.add.rule.EmailValidationRule;
import ru.babobka.nodeweb.validation.user.add.rule.NameValidationRule;
import ru.babobka.nodeweb.validation.user.add.rule.PasswordValidationRule;
import ru.babobka.nodeweb.validation.user.add.rule.TaskCountValidationRule;
import ru.babobka.nodeweb.validation.user.update.UpdateUserValidator;
import ru.babobka.nodeweb.validation.user.update.rule.NullableEmailValidationRule;
import ru.babobka.nodeweb.validation.user.update.rule.NullableNameValidationRule;
import ru.babobka.nodeweb.validation.user.update.rule.NullablePasswordValidationRule;
import ru.babobka.nodeweb.validation.user.update.rule.NullableTaskCountValidationRule;

/**
 * Created by 123 on 04.11.2017.
 */
public class NodeWebApplicationContainer implements ApplicationContainer {
    @Override
    public void contain(Container container) {
        container.put(new AddUserValidator(new EmailValidationRule(), new NameValidationRule(), new PasswordValidationRule(), new TaskCountValidationRule()));
        container.put(new UpdateUserValidator(new NullableEmailValidationRule(), new NullableNameValidationRule(), new NullablePasswordValidationRule(), new NullableTaskCountValidationRule()));
        container.put(new NodeUsersServiceImpl());
    }
}
