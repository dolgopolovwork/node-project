package ru.babobka.nodeweb.webcontroller;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodebusiness.dto.UserDTO;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodebusiness.service.NodeUsersService;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeweb.validation.user.add.AddUserValidator;
import ru.babobka.nodeweb.validation.user.update.UpdateUserValidator;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.model.JSONRequest;
import ru.babobka.vsjws.model.JSONResponse;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 13.08.2017.
 */
public class NodeUsersCRUDWebControllerTest {

    private static final NodeUsersService nodeUsersService = mock(NodeUsersService.class);
    private static final AddUserValidator addUserValidator = mock(AddUserValidator.class);
    private static final UpdateUserValidator updateUserValidator = mock(UpdateUserValidator.class);
    private static NodeUsersCRUDWebController nodeUsersCRUDWebController;
    private final PodamFactory podamFactory = new PodamFactoryImpl();

    @BeforeClass
    public static void setUp() {
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(nodeUsersService);
                container.put(addUserValidator);
                container.put(updateUserValidator);
            }
        }.contain(Container.getInstance());
        nodeUsersCRUDWebController = new NodeUsersCRUDWebController();
    }

    @Test
    public void testOnGetUser() {
        UUID uuid = UUID.randomUUID();
        JSONRequest request = mock(JSONRequest.class);
        when(request.getUrlParam("id")).thenReturn(uuid.toString());
        User user = podamFactory.manufacturePojo(User.class);
        when(nodeUsersService.get(uuid)).thenReturn(user);
        JSONResponse response = nodeUsersCRUDWebController.onGet(request);
        assertEquals(response.getResponseCode(), ResponseCode.OK);
        assertEquals(response.getContent(), user);
    }

    @Test
    public void testOnGetUnexistingUser() {
        UUID uuid = UUID.randomUUID();
        JSONRequest request = mock(JSONRequest.class);
        when(request.getUrlParam("id")).thenReturn(uuid.toString());
        when(nodeUsersService.get(uuid)).thenReturn(null);
        JSONResponse response = nodeUsersCRUDWebController.onGet(request);
        assertEquals(response.getResponseCode(), ResponseCode.NOT_FOUND);
    }

    @Test
    public void testOnGetUserList() {
        JSONRequest request = mock(JSONRequest.class);
        User user = podamFactory.manufacturePojo(User.class);
        ArrayList<User> users = new ArrayList<>(Arrays.asList(user, user, user));
        when(request.getUrlParam("id")).thenReturn("");
        when(nodeUsersService.getList()).thenReturn(users);
        JSONResponse response = nodeUsersCRUDWebController.onGet(request);
        assertEquals(response.getResponseCode(), ResponseCode.OK);
        assertEquals(response.getContent(), users);
    }

    @Test
    public void onDeleteNoId() {
        JSONRequest request = mock(JSONRequest.class);
        when(request.getUrlParam("id")).thenReturn("");
        assertEquals(nodeUsersCRUDWebController.onDelete(request).getResponseCode(), ResponseCode.BAD_REQUEST);
    }

    @Test
    public void onDeleteUnexistingUser() {
        UUID uuid = UUID.randomUUID();
        JSONRequest request = mock(JSONRequest.class);
        when(request.getUrlParam("id")).thenReturn(uuid.toString());
        when(nodeUsersService.remove(uuid)).thenReturn(false);
        assertEquals(nodeUsersCRUDWebController.onDelete(request).getResponseCode(), ResponseCode.NOT_FOUND);
    }

    @Test
    public void onDelete() {
        UUID uuid = UUID.randomUUID();
        JSONRequest request = mock(JSONRequest.class);
        when(request.getUrlParam("id")).thenReturn(uuid.toString());
        when(nodeUsersService.remove(uuid)).thenReturn(true);
        assertEquals(nodeUsersCRUDWebController.onDelete(request).getResponseCode(), ResponseCode.OK);
    }

    @Test
    public void onPut() {
        UserDTO userDTO = mock(UserDTO.class);
        JSONRequest request = mock(JSONRequest.class);
        when(request.getBody(UserDTO.class)).thenReturn(userDTO);
        assertEquals(nodeUsersCRUDWebController.onPut(request).getResponseCode(), ResponseCode.OK);
    }

    @Test
    public void onPutBadValidation() {
        UserDTO userDTO = mock(UserDTO.class);
        JSONRequest request = mock(JSONRequest.class);
        when(request.getBody(UserDTO.class)).thenReturn(userDTO);
        doThrow(new IllegalArgumentException()).when(addUserValidator).validate(userDTO);
        assertEquals(nodeUsersCRUDWebController.onPut(request).getResponseCode(), ResponseCode.BAD_REQUEST);
    }

    @Test
    public void testOnPostNoId() {
        UUID uuid = UUID.randomUUID();
        JSONRequest request = mock(JSONRequest.class);
        when(request.getUrlParam("id")).thenReturn("");
        assertEquals(nodeUsersCRUDWebController.onPost(request).getResponseCode(), ResponseCode.BAD_REQUEST);
    }

    @Test
    public void testOnPostBadValidation() {
        UUID uuid = UUID.randomUUID();
        UserDTO userDTO = new UserDTO();
        JSONRequest request = mock(JSONRequest.class);
        when(request.getBody(UserDTO.class)).thenReturn(userDTO);
        when(request.getUrlParam("id")).thenReturn(uuid.toString());
        doThrow(new IllegalArgumentException()).when(updateUserValidator).validate(userDTO);
        assertEquals(nodeUsersCRUDWebController.onPost(request).getResponseCode(), ResponseCode.BAD_REQUEST);
    }

    @Test
    public void testOnPostBadUpdate() {
        UUID uuid = UUID.randomUUID();
        UserDTO userDTO = new UserDTO();
        JSONRequest request = mock(JSONRequest.class);
        when(request.getBody(UserDTO.class)).thenReturn(userDTO);
        when(request.getUrlParam("id")).thenReturn(uuid.toString());
        when(nodeUsersService.update(uuid, userDTO)).thenReturn(false);
        assertEquals(nodeUsersCRUDWebController.onPost(request).getResponseCode(), ResponseCode.BAD_REQUEST);
    }

    @Test
    public void testOnPost() {
        UUID uuid = UUID.randomUUID();
        UserDTO userDTO = new UserDTO();
        JSONRequest request = mock(JSONRequest.class);
        when(request.getBody(UserDTO.class)).thenReturn(userDTO);
        when(request.getUrlParam("id")).thenReturn(uuid.toString());
        when(nodeUsersService.update(uuid, userDTO)).thenReturn(true);
        assertEquals(nodeUsersCRUDWebController.onPost(request).getResponseCode(), ResponseCode.OK);
    }
}
