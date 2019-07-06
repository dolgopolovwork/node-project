package ru.babobka.nodeweb.webcontroller;

import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.Logger;
import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodebusiness.service.NodeUsersService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeweb.validation.user.add.AddUserValidator;
import ru.babobka.nodeweb.validation.user.update.UpdateUserValidator;

import java.io.IOException;
import java.util.UUID;

public class NodeUsersCRUDWebController extends WebController {

    private static final Logger logger = Logger.getLogger(NodeUsersCRUDWebController.class);
    private final AddUserValidator addUserValidator = Container.getInstance().get(AddUserValidator.class);
    private final UpdateUserValidator updateUserValidator = Container.getInstance().get(UpdateUserValidator.class);
    private final NodeUsersService nodeUsersService = Container.getInstance().get(NodeUsersService.class);

    @Override
    public void onGet(HttpExchange httpExchange) throws IOException {
        String uuidParam = getUriParam(httpExchange, "id");
        if (TextUtil.isEmpty(uuidParam)) {
            sendJson(httpExchange, nodeUsersService.getList());
        } else {
            UUID id = UUID.fromString(uuidParam);
            User user = nodeUsersService.get(id);
            if (user == null) {
                sendNotFound(httpExchange, "User not found");
                return;
            }
            sendJson(httpExchange, user);
        }
    }

    @Override
    public void onDelete(HttpExchange httpExchange) throws IOException {
        String uuidParam = getUriParam(httpExchange, "id");
        if (TextUtil.isEmpty(uuidParam)) {
            sendBadRequest(httpExchange, "Parameter 'id' was not set");
            return;
        }
        UUID id = UUID.fromString(uuidParam);
        if (nodeUsersService.remove(id)) {
            sendOk(httpExchange);
        } else {
            sendNotFound(httpExchange, "User with id '" + id + "' doesn't exist");
        }
    }

    @Override
    public void onPut(HttpExchange httpExchange) throws IOException {
        try {
            UserDTO user = readJson(httpExchange, UserDTO.class);
            addUserValidator.validate(user);
            nodeUsersService.add(user);
            sendOk(httpExchange);
        } catch (IllegalArgumentException e) {
            logger.error("cannot create user", e);
            sendBadRequest(httpExchange, "Bad request");
        }
    }

    @Override
    public void onPost(HttpExchange httpExchange) throws IOException {
        try {
            String uuidParam = getUriParam(httpExchange, "id");
            if (TextUtil.isEmpty(uuidParam)) {
                sendBadRequest(httpExchange, "Parameter 'id' was not set");
                return;
            }
            UUID id = UUID.fromString(uuidParam);
            UserDTO user = readJson(httpExchange, UserDTO.class);
            updateUserValidator.validate(user);
            if (nodeUsersService.update(id, user)) {
                sendOk(httpExchange);
            } else {
                sendNotFound(httpExchange, "No user with id '" + id + "' was found");
            }
        } catch (IllegalArgumentException e) {
            logger.error("cannot update user", e);
            sendBadRequest(httpExchange, "Bad request");
        }
    }
}