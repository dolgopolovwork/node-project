package ru.babobka.nodemasterserver.listener;

import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.vsjws.listener.OnExceptionListener;
import ru.babobka.vsjws.model.HttpResponse;

public class OnIllegalArgumentExceptionListener implements OnExceptionListener {

	@Override
	public HttpResponse onException(Exception e) {
		return HttpResponse.textResponse(TextUtil.notNull(e.getMessage()), HttpResponse.ResponseCode.BAD_REQUEST);
	}

}
