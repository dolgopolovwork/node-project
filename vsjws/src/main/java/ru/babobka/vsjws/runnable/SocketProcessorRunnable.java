package ru.babobka.vsjws.runnable;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.vsjws.enumerations.HttpMethod;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.exception.BadProtocolSpecifiedException;
import ru.babobka.vsjws.exception.InvalidContentLengthException;
import ru.babobka.vsjws.model.Request;
import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.RawHttpRequest;
import ru.babobka.vsjws.model.http.ResponseFactory;
import ru.babobka.vsjws.model.http.session.HttpSession;
import ru.babobka.vsjws.model.http.session.Session;
import ru.babobka.vsjws.util.HttpUtil;
import ru.babobka.vsjws.validator.request.RequestValidator;
import ru.babobka.vsjws.webcontroller.HttpWebController;
import ru.babobka.vsjws.webcontroller.StaticResourcesController;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * Created by dolgopolov.a on 25.12.15.
 */
public class SocketProcessorRunnable implements Runnable {

    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final RequestValidator requestValidator = Container.getInstance().get(RequestValidator.class);
    private final Socket socket;
    private final HttpSession httpSession;
    private final Map<String, HttpWebController> controllerMap;
    private final StaticResourcesController staticResourcesController = new StaticResourcesController();

    public SocketProcessorRunnable(HttpSession httpSession, Socket socket, Map<String, HttpWebController> controllerMap) {
        this.socket = socket;
        this.httpSession = httpSession;
        this.controllerMap = controllerMap;
    }

    @Override
    public void run() {
        HttpResponse response = ResponseFactory.code(ResponseCode.NOT_FOUND);
        boolean noContent = false;
        try {
            RawHttpRequest rawHttpRequest = new RawHttpRequest(socket.getInputStream());
            requestValidator.validate(rawHttpRequest);
            HttpRequest request = new HttpRequest(httpSession, socket.getInetAddress(), rawHttpRequest);
            if (request.getMethod() == HttpMethod.HEAD)
                noContent = true;
            String uri = HttpUtil.cleanUri(request.getUri());
            if (uri == null) {
                return;
            }
            String sessionId = request.getCookies().get(Request.SESSION_ID_HEADER);
            if (sessionId == null) {
                sessionId = HttpUtil.generateSessionId();
                response.addCookie(Request.SESSION_ID_HEADER, sessionId);
            }
            Session session = httpSession.getOrCreate(sessionId, request);
            if (!session.getAddress().equals(request.getAddress())) {
                nodeLogger.warning("Possible session hacking. Session id " + sessionId + "; ip address " + request.getAddress());
                response = ResponseFactory.code(ResponseCode.FORBIDDEN);
                return;
            }
            HttpWebController httpWebController;
            if (uri.startsWith("/web-content")) {
                response = staticResourcesController.onGet(request);
            } else if ((httpWebController = controllerMap.get(uri)) != null) {
                response = httpWebController.control(request);
            }
        } catch (BadProtocolSpecifiedException e) {
            response = ResponseFactory.exception(e).setResponseCode(ResponseCode.HTTP_VERSION_NOT_SUPPORTED);
        } catch (InvalidContentLengthException e) {
            response = ResponseFactory.exception(e).setResponseCode(ResponseCode.LENGTH_REQUIRED);
        } catch (IllegalArgumentException e) {
            response = ResponseFactory.exception(e).setResponseCode(ResponseCode.BAD_REQUEST);
        } catch (SocketTimeoutException e) {
            response = ResponseFactory.exception(e).setResponseCode(ResponseCode.REQUEST_TIMEOUT);
        } catch (Exception e) {
            nodeLogger.error(e);
            response = ResponseFactory.exception(e);
        } finally {
            try {
                HttpUtil.writeResponse(socket.getOutputStream(), response, noContent);
            } catch (IOException e1) {
                nodeLogger.error(e1);
            }
            try {
                socket.close();
            } catch (IOException e) {
                nodeLogger.error(e);
            }
        }
    }
}
