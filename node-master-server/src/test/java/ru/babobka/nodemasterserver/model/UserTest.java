package ru.babobka.nodemasterserver.model;

import org.junit.Test;

import ru.babobka.nodemasterserver.exception.InvalidUserException;

public class UserTest {

    private static final int TASK_COUNT = 0;

    private static final String EMAIL = "test@test.com";

    private static final String NAME = "test";

    private static final String PASSWORD = "123";

    @Test
    public void testNormalUser() {
	User user = new User();
	user.setEmail(EMAIL);
	user.setName(NAME);
	user.setTaskCount(TASK_COUNT);
	user.setPassword(PASSWORD);
	user.validate();
    }

    @Test(expected = InvalidUserException.class)
    public void testEmptyUser() {
	User user = new User();
	user.validate();
    }

    @Test(expected = InvalidUserException.class)
    public void testInvalidEmailUser() {
	User user = new User();
	user.setEmail("ololo.com");
	user.setName(NAME);
	user.setPassword(PASSWORD);
	user.validate();
    }

    @Test(expected = InvalidUserException.class)
    public void testInvalidPasswordUser() {
	User user = new User();
	user.setEmail(EMAIL);
	user.setName(NAME);
	user.setPassword("");
	user.validate();
    }

    @Test(expected = InvalidUserException.class)
    public void testNullPasswordUser() {
	User user = new User();
	user.setEmail(EMAIL);
	user.setName(NAME);
	user.setPassword(null);
	user.validate();
    }

    @Test(expected = InvalidUserException.class)
    public void testNullEmailUser() {
	User user = new User();
	user.setEmail(null);
	user.setName(NAME);
	user.setPassword(PASSWORD);
	user.validate();
    }

    @Test(expected = InvalidUserException.class)
    public void testNullNameUser() {
	User user = new User();
	user.setEmail(EMAIL);
	user.setName(null);
	user.setPassword(PASSWORD);
	user.validate();
    }

    @Test(expected = InvalidUserException.class)
    public void testNegativeTaskCountUser() {
	User user = new User();
	user.setEmail(EMAIL);
	user.setTaskCount(-1);
	user.setName(NAME);
	user.setPassword(PASSWORD);
	user.validate();
    }

}
