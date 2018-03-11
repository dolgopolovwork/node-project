package ru.babobka.vsjws.model.http;

import net.jodah.expiringmap.ExpiringMap;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by dolgopolov.a on 12.01.16.
 */
public class HttpSession {

    private final Map<String, ConcurrentHashMap<String, Serializable>> expiringMap;

    public HttpSession(int seconds) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("Session timeout seconds must be in range [1,MAX_INT]");
        }
        expiringMap = ExpiringMap.builder().expirationPolicy(ExpiringMap.ExpirationPolicy.CREATED)
                .expiration(seconds, TimeUnit.SECONDS).build();
    }

    public Map<String, Serializable> get(String sessionId) {
        return expiringMap.get(sessionId);
    }

    public boolean exists(String sessionId) {
        return expiringMap.containsKey(sessionId);
    }

    public void create(String sessionId) {
        expiringMap.put(sessionId, new ConcurrentHashMap<>());

    }

    public void clear() {
        expiringMap.clear();
    }

}
