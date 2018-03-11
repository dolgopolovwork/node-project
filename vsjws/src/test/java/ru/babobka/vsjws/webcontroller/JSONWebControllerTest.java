package ru.babobka.vsjws.webcontroller;

import org.junit.Test;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.model.json.JSONRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by 123 on 15.08.2017.
 */
public class JSONWebControllerTest {

    private JSONWebController jsonWebController = new JSONWebController();

    @Test
    public void testOnGet() throws Exception {
        assertEquals(jsonWebController.onGet(mock(JSONRequest.class)).getResponseCode(), ResponseCode.NOT_IMPLEMENTED);
    }


    @Test
    public void testOnHead() throws Exception {
        assertEquals(jsonWebController.onHead(mock(JSONRequest.class)).getResponseCode(), ResponseCode.NOT_IMPLEMENTED);
    }

    @Test
    public void testOnPost() throws Exception {
        assertEquals(jsonWebController.onPost(mock(JSONRequest.class)).getResponseCode(), ResponseCode.NOT_IMPLEMENTED);
    }

    @Test
    public void testOnPut() throws Exception {
        assertEquals(jsonWebController.onPut(mock(JSONRequest.class)).getResponseCode(), ResponseCode.NOT_IMPLEMENTED);
    }

    @Test
    public void testOnPatch() throws Exception {
        assertEquals(jsonWebController.onPatch(mock(JSONRequest.class)).getResponseCode(), ResponseCode.NOT_IMPLEMENTED);
    }

    @Test
    public void testOnDelete() throws Exception {
        assertEquals(jsonWebController.onDelete(mock(JSONRequest.class)).getResponseCode(), ResponseCode.NOT_IMPLEMENTED);
    }


}
