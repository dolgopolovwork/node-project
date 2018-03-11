package ru.babobka.vsjws.webcontroller.it;

import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;
import ru.babobka.vsjws.webcontroller.HttpWebController;

/**
 * Created by dolgopolov.a on 30.12.15.
 */
public class XmlTestController extends HttpWebController {

    public static final String XML_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<note>\n"
            + "\t<to>Tove</to>\n" + "\t<from>Jani</from>\n" + "\t<heading>Reminder</heading>\n"
            + "\t<body>Don't forget me this weekend!</body>\n" + "</note>";

    @Override
    public HttpResponse onGet(HttpRequest request) {
        return ResponseFactory.xml(XML_RESPONSE);

    }

}
