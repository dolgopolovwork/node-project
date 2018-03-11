package ru.babobka.vsjws.webcontroller.it;

import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;
import ru.babobka.vsjws.webcontroller.HttpWebController;

/**
 * Created by dolgopolov.a on 30.12.15.
 */
public class JsonTestController extends HttpWebController {

    public static final String JSON_RESPONSE = "{\"id\": 1, \"name\": \"A green door\",  \"price\": 12.50, \"tags\": [\"home\", \"green\"]}";

    @Override
    public HttpResponse onGet(HttpRequest request) {
        return ResponseFactory.json(JSON_RESPONSE);
    }

}
