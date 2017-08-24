package ru.babobka.nodeweb.webcontroller;

import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodebusiness.service.NodeUsersService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeweb.validation.user.add.AddUserValidator;
import ru.babobka.nodeweb.validation.user.update.UpdateUserValidator;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.model.JSONRequest;
import ru.babobka.vsjws.model.JSONResponse;
import ru.babobka.vsjws.webcontroller.JSONWebController;

import java.io.Serializable;
import java.util.UUID;

public class NodeUsersCRUDWebController extends JSONWebController {

    private final AddUserValidator addUserValidator = Container.getInstance().get(AddUserValidator.class);

    private final UpdateUserValidator updateUserValidator = Container.getInstance().get(UpdateUserValidator.class);

    private final NodeUsersService nodeUsersService = Container.getInstance().get(NodeUsersService.class);

    @Override
    public JSONResponse onGet(JSONRequest request) {
        String uidParam = request.getUrlParam("id");
        if (!uidParam.isEmpty()) {
            UUID id = UUID.fromString(uidParam);
            User user = nodeUsersService.get(id);
            if (user == null) {
                return JSONResponse.code(ResponseCode.NOT_FOUND);
            } else {
                return JSONResponse.ok(user);
            }
        } else {
            return JSONResponse.ok((Serializable) nodeUsersService.getList());
        }
    }

    @Override
    public JSONResponse onDelete(JSONRequest request) {
        String uidParam = request.getUrlParam("id");
        if (uidParam.isEmpty()) {
            return JSONResponse.badRequest("Parameter 'id' was not set");
        } else {
            UUID id = UUID.fromString(uidParam);
            if (nodeUsersService.remove(id)) {
                return JSONResponse.ok();
            } else {
                return JSONResponse.code(ResponseCode.NOT_FOUND);
            }
        }
    }

    @Override
    public JSONResponse onPut(JSONRequest request) {
        try {
            UserDTO user = request.getBody(UserDTO.class);
            addUserValidator.validate(user);
            nodeUsersService.add(user);
            return JSONResponse.ok();
        } catch (IllegalArgumentException e) {
            return JSONResponse.exception(e, ResponseCode.BAD_REQUEST);
        }
    }

    @Override
    public JSONResponse onPost(JSONRequest request) {
        try {
            String uidParam = request.getUrlParam("id");
            if (uidParam.isEmpty())
                return JSONResponse.badRequest("Parameter 'id' was not set");
            UUID id = UUID.fromString(uidParam);
            UserDTO user = request.getBody(UserDTO.class);
            updateUserValidator.validate(user);
            if (nodeUsersService.update(id, user)) {
                return JSONResponse.ok();
            } else {
                return JSONResponse.badRequest("No user with id " + id + " was found");
            }
        } catch (IllegalArgumentException e) {
            return JSONResponse.exception(e, ResponseCode.BAD_REQUEST);
        }
    }
}