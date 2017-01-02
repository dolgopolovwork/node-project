package ru.babobka.nodemasterserver.listener;

import ru.babobka.vsjws.listener.OnExceptionListener;
import ru.babobka.vsjws.model.HttpResponse;

public class OnJSONExceptionListener implements OnExceptionListener {

	@Override
	public HttpResponse onException(Exception e) {
		return HttpResponse.textResponse("Invalid JSON input", HttpResponse.ResponseCode.BAD_REQUEST);
	}

}
