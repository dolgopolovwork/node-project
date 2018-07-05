package ru.babobka.nodeweb.webcontroller;

import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodebusiness.service.NodeUsersService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeweb.validation.user.add.AddUserValidator;
import ru.babobka.nodeweb.validation.user.update.UpdateUserValidator;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;
import ru.babobka.vsjws.model.json.JSONRequest;
import ru.babobka.vsjws.webcontroller.JSONWebController;

import java.util.UUID;

public class NodeUsersCRUDWebController extends JSONWebController {

    private final AddUserValidator addUserValidator = Container.getInstance().get(AddUserValidator.class);
    private final UpdateUserValidator updateUserValidator = Container.getInstance().get(UpdateUserValidator.class);
    private final NodeUsersService nodeUsersService = Container.getInstance().get(NodeUsersService.class);

    @Override
    public HttpResponse onGet(JSONRequest request) {
        String uidParam = request.getUrlParam("id");
        if (uidParam.isEmpty()) {
            return ResponseFactory.json(nodeUsersService.getList());
        }
        UUID id = UUID.fromString(uidParam);
        User user = nodeUsersService.get(id);
        if (user == null) {
            return ResponseFactory.code(ResponseCode.NOT_FOUND);
        }
        return ResponseFactory.json(user);
    }

    @Override
    public HttpResponse onDelete(JSONRequest request) {
        String uidParam = request.getUrlParam("id");
        if (uidParam.isEmpty()) {
            return ResponseFactory.text("Parameter 'id' was not set")
                    .setResponseCode(ResponseCode.BAD_REQUEST);
        }
        UUID id = UUID.fromString(uidParam);
        if (nodeUsersService.remove(id)) {
            return ResponseFactory.ok();
        } else {
            return ResponseFactory.code(ResponseCode.NOT_FOUND);
        }
    }

    @Override
    public HttpResponse onPut(JSONRequest request) {
        try {
            UserDTO user = request.getBody(UserDTO.class);
            addUserValidator.validate(user);
            nodeUsersService.add(user);
            return ResponseFactory.ok();
        } catch (IllegalArgumentException e) {
            return ResponseFactory.exception(e).setResponseCode(ResponseCode.BAD_REQUEST);
        }
    }

    @Override
    public HttpResponse onPost(JSONRequest request) {
        try {
            String uidParam = request.getUrlParam("id");
            if (uidParam.isEmpty())
                return ResponseFactory.text("Parameter 'id' was not set")
                        .setResponseCode(ResponseCode.BAD_REQUEST);
            UUID id = UUID.fromString(uidParam);
            UserDTO user = request.getBody(UserDTO.class);
            updateUserValidator.validate(user);
            if (nodeUsersService.update(id, user)) {
                return ResponseFactory.ok();
            } else {
                return ResponseFactory.text("No user with id " + id + " was found")
                        .setResponseCode(ResponseCode.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            return ResponseFactory.exception(e).setResponseCode(ResponseCode.BAD_REQUEST);
        }
    }
}