package ru.babobka.nodeweb.webcontroller;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import org.apache.log4j.Logger;
import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.mapper.UserEntityToDTOMapper;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodebusiness.service.NodeUsersService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeweb.validation.user.add.AddUserValidator;
import ru.babobka.nodeweb.validation.user.update.UpdateUserValidator;

import java.util.UUID;
import java.util.stream.Collectors;

public class NodeUsersCRUDWebController {

    private static final Logger logger = Logger.getLogger(NodeUsersCRUDWebController.class);
    private final AddUserValidator addUserValidator = Container.getInstance().get(AddUserValidator.class);
    private final UpdateUserValidator updateUserValidator = Container.getInstance().get(UpdateUserValidator.class);
    private final NodeUsersService nodeUsersService = Container.getInstance().get(NodeUsersService.class);
    private final UserEntityToDTOMapper userEntityToDTOMapper = Container.getInstance().get(UserEntityToDTOMapper.class);

    @OpenApi(
            path = "/users/all",
            method = HttpMethod.GET,
            summary = "Get all users",
            operationId = "getAllUsers",
            tags = {"Users"},
            responses = {@OpenApiResponse(status = "200", content = {@OpenApiContent(from = UserDTO[].class)})}
    )
    public void getAllUsers(Context context) {
        context.json(nodeUsersService.getList()
                .stream()
                .map(userEntityToDTOMapper::map)
                .collect(Collectors.toList()));
    }

    @OpenApi(
            pathParams = {@OpenApiParam(name = "id", description = "The user ID")},
            method = HttpMethod.GET,
            path = "/users/:id",
            summary = "Get a user",
            operationId = "getUser",
            tags = {"Users"},
            responses = {@OpenApiResponse(status = "200", content = {@OpenApiContent(from = UserDTO.class)})}
    )
    public void getUser(Context context) {
        UUID id;
        try {
            id = UUID.fromString(context.pathParam("id"));
        } catch (IllegalArgumentException ex) {
            context.result("Invalid id");
            context.status(400);
            return;
        }
        User user = nodeUsersService.get(id);
        if (user == null) {
            context.result("User not found");
            context.status(404);
            return;
        }
        context.json(userEntityToDTOMapper.map(user));
    }

    @OpenApi(
            pathParams = {@OpenApiParam(name = "id", description = "The user ID")},
            method = HttpMethod.DELETE,
            path = "/users/:id",
            summary = "Remove a user",
            operationId = "removeUser",
            tags = {"Users"},
            responses = {@OpenApiResponse(status = "200")}
    )
    public void deleteUser(Context context) {
        UUID id;
        try {
            id = UUID.fromString(context.pathParam("id"));
        } catch (IllegalArgumentException ex) {
            context.result("Invalid id");
            context.status(400);
            return;
        }
        if (nodeUsersService.remove(id)) {
            context.result("Ok");
        } else {
            context.result("User with id '" + id + "' doesn't exist");
            context.status(404);
        }
    }

    @OpenApi(
            path = "/users",
            method = HttpMethod.PUT,
            summary = "Create a user",
            operationId = "createUser",
            tags = {"Users"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = UserDTO.class)}),
            responses = {@OpenApiResponse(status = "200")}
    )
    public void createUser(Context context) {
        UserDTO user = context.bodyAsClass(UserDTO.class);
        try {
            addUserValidator.validate(user);
        } catch (IllegalArgumentException e) {
            logger.error("cannot create user", e);
            context.result("Bad request");
            context.status(400);
            return;
        }
        nodeUsersService.add(user);
        context.result("Ok");
    }

    @OpenApi(
            path = "/users",
            method = HttpMethod.POST,
            summary = "Update a user",
            operationId = "updateUser",
            tags = {"Users"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = UserDTO.class)}),
            responses = {@OpenApiResponse(status = "200")}
    )
    public void updateUser(Context context) {
        UserDTO user = context.bodyAsClass(UserDTO.class);
        try {
            updateUserValidator.validate(user);
        } catch (IllegalArgumentException e) {
            logger.error("cannot update user", e);
            context.result("Bad request");
            context.status(400);
            return;
        }
        if (nodeUsersService.update(user)) {
            context.result("Ok");
        } else {
            context.result("No user with id '" + user.getId() + "' was found");
            context.status(404);
        }
    }
}