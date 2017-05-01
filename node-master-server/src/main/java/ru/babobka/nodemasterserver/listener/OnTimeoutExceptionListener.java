package ru.babobka.nodemasterserver.listener;

import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.listener.OnExceptionListener;
import ru.babobka.vsjws.webserver.HttpResponse;

public class OnTimeoutExceptionListener implements OnExceptionListener {

    @Override
    public HttpResponse onException(Exception e) {
	return HttpResponse.text("Request timeout", ResponseCode.REQUEST_TIMEOUT);
    }
}
