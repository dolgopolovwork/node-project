package ru.babobka.nodemasterserver.listener;

import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.listener.OnExceptionListener;
import ru.babobka.vsjws.webserver.HttpResponse;

public class OnTaskNotFoundExceptionListener implements OnExceptionListener {

    @Override
    public HttpResponse onException(Exception e) {
	return HttpResponse.text("Task was not found", ResponseCode.NOT_FOUND);
    }
}
