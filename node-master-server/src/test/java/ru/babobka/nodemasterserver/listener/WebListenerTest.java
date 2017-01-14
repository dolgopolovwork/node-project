package ru.babobka.nodemasterserver.listener;

import static org.junit.Assert.*;

import java.util.concurrent.TimeoutException;

import org.junit.Test;

import ru.babobka.nodemasterserver.exception.TaskNotFoundException;
import ru.babobka.vsjws.model.HttpResponse;

public class WebListenerTest {

	@Test
	public void onIllegalArgumentExceptionListenerTest() {
		HttpResponse response = new OnIllegalArgumentExceptionListener().onException(new IllegalArgumentException());
		assertEquals(response.getResponseCode(), HttpResponse.ResponseCode.BAD_REQUEST);
	}

	@Test
	public void onIllegalStateExceptionListenerTest() {
		HttpResponse response = new OnIllegalStateExceptionListener().onException(new IllegalStateException());
		assertEquals(response.getResponseCode(), HttpResponse.ResponseCode.INTERNAL_SERVER_ERROR);
	}


	@Test
	public void onTaskNotFoundExceptionListenerTest() {
		HttpResponse response = new OnTaskNotFoundExceptionListener().onException(new TaskNotFoundException());
		assertEquals(response.getResponseCode(), HttpResponse.ResponseCode.NOT_FOUND);
	}

	@Test
	public void onTimeoutExceptionListenerTest() {
		HttpResponse response = new OnTimeoutExceptionListener().onException(new TimeoutException());
		assertEquals(response.getResponseCode(), HttpResponse.ResponseCode.REQUEST_TIMEOUT);
	}

}
