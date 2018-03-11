package ru.babobka.vsjws.enumerations;

/**
 * Created by dolgopolov.a on 30.12.15.
 */

public enum HttpMethod {
    GET, POST, PUT, DELETE, HEAD, PATCH;

    public static boolean isValidMethod(String inputMethod) {
        if (inputMethod == null || inputMethod.isEmpty()) {
            return false;
        }
        for (HttpMethod method : HttpMethod.values()) {
            if (method.toString().equals(inputMethod)) {
                return true;
            }
        }
        return false;
    }
}
