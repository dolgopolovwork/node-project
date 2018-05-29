package ru.babobka.vsjws.enumerations;

import ru.babobka.nodeutils.util.TextUtil;

/**
 * Created by dolgopolov.a on 30.12.15.
 */

public enum HttpMethod {
    GET, POST, PUT, DELETE, HEAD, PATCH;

    public static boolean isValidMethod(String inputMethod) {
        if (TextUtil.isEmpty(inputMethod)) {
            return false;
        }
        for (HttpMethod method : HttpMethod.values()) {
            if (method.toString().equals(inputMethod)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMethodWithContent(String method) {
        return method.equals(HttpMethod.PATCH.toString()) || method.equals(HttpMethod.POST.toString()) || method.equals(HttpMethod.PUT.toString());
    }
}
