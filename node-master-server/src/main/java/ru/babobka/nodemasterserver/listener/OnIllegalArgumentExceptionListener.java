package ru.babobka.nodemasterserver.listener;

import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.listener.OnExceptionListener;
import ru.babobka.vsjws.webserver.HttpResponse;

public class OnIllegalArgumentExceptionListener implements OnExceptionListener {

    @Override
    public HttpResponse onException(Exception e) {
	return HttpResponse.text(TextUtil.notNull(e.getMessage()), ResponseCode.BAD_REQUEST);
    }

}
