package ru.babobka.nodemasterserver.webcontroller;

import org.json.JSONException;
import org.json.JSONObject;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodemasterserver.exception.InvalidUserException;
import ru.babobka.nodemasterserver.model.User;
import ru.babobka.nodemasterserver.service.NodeUsersService;

import ru.babobka.vsjws.webcontroller.JSONWebController;

import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.webserver.JSONRequest;
import ru.babobka.vsjws.webserver.JSONResponse;

import java.io.Serializable;


public class NodeUsersCRUDWebController extends JSONWebController {

    private final NodeUsersService nodeUsersService = Container.getInstance().get(NodeUsersService.class);

    @Override
    public JSONResponse onGet(JSONRequest request) throws JSONException {
        String userName = request.getUrlParam("userName");
        if (!userName.isEmpty()) {
            User user = nodeUsersService.get(userName);
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

        String userName = request.getUrlParam("userName");
        if (userName == null) {
            return JSONResponse.badRequest("Parameter 'userName' was not set");
        } else {
            if (nodeUsersService.remove(userName)) {
                return JSONResponse.ok();
            } else {
                return JSONResponse.code(ResponseCode.BAD_REQUEST);
            }
        }

    }

    @Override
    public JSONResponse onPatch(JSONRequest request) {
        try {
            User user = new User(new JSONObject(request.getBody()));
            user.validate();
            if (nodeUsersService.add(user)) {
                return JSONResponse.ok();
            } else {
                return JSONResponse.code(ResponseCode.BAD_REQUEST);
            }
        } catch (InvalidUserException e) {
            return JSONResponse.exception(e, ResponseCode.BAD_REQUEST);
        }

    }

    @Override
    public JSONResponse onPost(JSONRequest request) throws JSONException {
        try {
            String userName = request.getUrlParam("name");
            User user = new User(new JSONObject(request.getBody()));
            user.validate();
            if (nodeUsersService.update(userName, user)) {
                return JSONResponse.ok();
            } else {
                return JSONResponse.code(ResponseCode.INTERNAL_SERVER_ERROR);
            }
        } catch (InvalidUserException e) {
            return JSONResponse.exception(e, ResponseCode.BAD_REQUEST);
        }
    }
}
