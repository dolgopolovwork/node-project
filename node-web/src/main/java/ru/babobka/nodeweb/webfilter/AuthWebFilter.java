package ru.babobka.nodeweb.webfilter;

import ru.babobka.nodeutils.util.HashUtil;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.model.FilterResponse;
import ru.babobka.vsjws.model.HttpRequest;
import ru.babobka.vsjws.model.HttpResponse;
import ru.babobka.vsjws.webcontroller.WebFilter;

import java.util.Arrays;

public class AuthWebFilter implements WebFilter {

    private final String login;

    private final byte[] hashedPassword;

    public AuthWebFilter(String login, byte[] hashedPassword) {
        this.login = login;
        this.hashedPassword = hashedPassword.clone();
    }

    @Override
    public void afterFilter(HttpRequest request, HttpResponse response) {
        //Do nothing
    }

    @Override
    public FilterResponse onFilter(HttpRequest request) {
        String loginHeader = request.getHeader("X-Login");
        String passwordHeader = request.getHeader("X-Password");
        if (!loginHeader.equals(login) || !Arrays.equals(hashedPassword, HashUtil.sha2(passwordHeader))) {
            return FilterResponse
                    .response(HttpResponse.text("Bad login/password combination", ResponseCode.UNAUTHORIZED));
        } else {
            return FilterResponse.proceed();
        }
    }

}
