package ru.babobka.vsjws.webcontroller.it;

import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;
import ru.babobka.vsjws.webcontroller.HttpWebController;

/**
 * Created by 123 on 22.04.2017.
 */
public class CookieTimeController extends HttpWebController {

    @Override
    public HttpResponse onGet(HttpRequest request) {
        String key = "time";
        String time = request.getCookies().get(key);
        if (time == null) {
            time = String.valueOf(System.currentTimeMillis());
        }
        return ResponseFactory.text(time).addCookie(key, time);
    }
}
