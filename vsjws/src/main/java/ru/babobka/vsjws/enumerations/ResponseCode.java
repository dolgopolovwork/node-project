package ru.babobka.vsjws.enumerations;

/**
 * Created by 123 on 02.04.2017.
 */
public enum ResponseCode {

    OK(200, "Ok"), ACCEPTED(202, "Accepted"), NO_CONTENT(204, "No content"),

    MOVED_PERMANENTLY(301, "Moved permanently"), MOVED_TEMPORARILY(302, "Moved temporarily"), SEE_OTHER(303, "See other"),

    NOT_FOUND(404, "Not found"), REQUEST_TIMEOUT(408, "Request Timeout"), UNAUTHORIZED(401, "Unauthorized"), METHOD_NOT_ALLOWED(405, "Method not allowed"), BAD_REQUEST(400, "Bad request"), FORBIDDEN(403, "Forbidden"), LENGTH_REQUIRED(411, "Length required"),

    INTERNAL_SERVER_ERROR(500, "Internal server error"), NOT_IMPLEMENTED(501, "Not implemented"), SERVICE_UNAVAILABLE(503, "Service unavailable"), HTTP_VERSION_NOT_SUPPORTED(505, "HTTP version not supported");

    private final String text;
    private final int code;

    ResponseCode(int code, String text) {
        this.text = text;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return code + " " + text;
    }

}