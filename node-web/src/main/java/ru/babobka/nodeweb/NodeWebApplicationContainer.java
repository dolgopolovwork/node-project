package ru.babobka.nodeweb;

import ru.babobka.nodebusiness.service.NodeUsersServiceImpl;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeweb.validation.user.add.AddUserValidator;
import ru.babobka.nodeweb.validation.user.add.rule.EmailValidationRule;
import ru.babobka.nodeweb.validation.user.add.rule.NameValidationRule;
import ru.babobka.nodeweb.validation.user.add.rule.PubKeyValidationRule;
import ru.babobka.nodeweb.validation.user.update.UpdateUserValidator;
import ru.babobka.nodeweb.validation.user.update.rule.IdValidationRule;
import ru.babobka.nodeweb.validation.user.update.rule.NullableEmailValidationRule;
import ru.babobka.nodeweb.validation.user.update.rule.NullableNameValidationRule;
import ru.babobka.nodeweb.validation.user.update.rule.NullablePubKeyValidationRule;

/**
 * Created by 123 on 04.11.2017.
 */
public class NodeWebApplicationContainer extends AbstractApplicationContainer {
    @Override
    protected void containImpl(Container container) {
        container.put(new AddUserValidator(
                new EmailValidationRule(), new NameValidationRule(), new PubKeyValidationRule()));
        container.put(new UpdateUserValidator(
                new NullableEmailValidationRule(),
                new NullableNameValidationRule(),
                new NullablePubKeyValidationRule(),
                new IdValidationRule()));
        container.put(new NodeUsersServiceImpl());
    }
}
