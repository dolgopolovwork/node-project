package ru.babobka.nodeweb.webcontroller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.log4j.Logger;

import java.io.IOException;

public class WebController extends WebControllerUtil implements HttpHandler {

    private static final Logger logger = Logger.getLogger(WebController.class);

    @Override
    public final void handle(HttpExchange httpExchange) throws IOException {
        try {
            switch (httpExchange.getRequestMethod()) {
                case "GET": {
                    onGet(httpExchange);
                    return;
                }
                case "POST": {
                    onPost(httpExchange);
                    return;
                }
                case "PUT": {
                    onPut(httpExchange);
                    return;
                }
                case "PATCH": {
                    onPatch(httpExchange);
                    return;
                }
                case "DELETE": {
                    onDelete(httpExchange);
                    return;
                }
                case "HEAD": {
                    onHead(httpExchange);
                    return;
                }
                default: {
                    notAllowed(httpExchange);
                }
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            logger.error("cannot handle HTTP request", e);
            sendServerError(httpExchange, "Server error");
        }
    }

    protected void onGet(HttpExchange httpExchange) throws IOException {
        notAllowed(httpExchange);
    }

    protected void onPost(HttpExchange httpExchange) throws IOException {
        notAllowed(httpExchange);
    }

    protected void onPut(HttpExchange httpExchange) throws IOException {
        notAllowed(httpExchange);
    }

    protected void onPatch(HttpExchange httpExchange) throws IOException {
        notAllowed(httpExchange);
    }

    protected void onDelete(HttpExchange httpExchange) throws IOException {
        notAllowed(httpExchange);
    }

    protected void onHead(HttpExchange httpExchange) throws IOException {
        notAllowed(httpExchange);
    }

}
