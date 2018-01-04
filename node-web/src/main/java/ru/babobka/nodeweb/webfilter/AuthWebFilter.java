package ru.babobka.nodeweb.webfilter;

import ru.babobka.nodeutils.util.ArrayUtil;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.model.FilterResponse;
import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;
import ru.babobka.vsjws.webcontroller.WebFilter;

public class AuthWebFilter implements WebFilter {

    private final String login;

    private final String hashedPassword;

    public AuthWebFilter(String login, String hashedPassword) {
        if (ArrayUtil.isNull(login, hashedPassword)) {
            throw new IllegalArgumentException("All the values must be set to non null");
        }
        this.login = login;
        this.hashedPassword = hashedPassword;
    }

    @Override
    public void afterFilter(HttpRequest request, HttpResponse response) {
        //Do nothing
    }

    @Override
    public FilterResponse onFilter(HttpRequest request) {
        String loginHeader = request.getHeader("X-Login");
        String hashedPasswordHeader = request.getHeader("X-Password");
        if (!loginHeader.equals(login) || !hashedPassword.equals(hashedPasswordHeader)) {
            return FilterResponse
                    .response(ResponseFactory.text("Bad login/password combination")
                            .setResponseCode(ResponseCode.UNAUTHORIZED));
        } else {
            return FilterResponse.proceed();
        }
    }

}
