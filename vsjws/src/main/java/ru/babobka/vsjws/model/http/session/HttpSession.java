package ru.babobka.vsjws.model.http.session;

import net.jodah.expiringmap.ExpiringMap;
import ru.babobka.vsjws.model.http.HttpRequest;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by dolgopolov.a on 12.01.16.
 */
public class HttpSession {

    private final Map<String, Session> expiringMap;

    public HttpSession(int seconds) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("session timeout seconds must be in range [1,MAX_INT]");
        }
        expiringMap = ExpiringMap.builder().expirationPolicy(
                ExpiringMap.ExpirationPolicy.CREATED).expiration(seconds, TimeUnit.SECONDS).build();
    }

    public synchronized Map<String, Serializable> getData(String sessionId) {
        Session session = get(sessionId);
        if (session != null) {
            return session.getData();
        }
        return null;
    }

    public synchronized Session getOrCreate(String sessionId, HttpRequest request) {
        Session session = get(sessionId);
        if (session == null) {
            session = create(sessionId, request);
        }
        return session;
    }

    private Session get(String sessionId) {
        return expiringMap.get(sessionId);
    }

    synchronized boolean exists(String sessionId) {
        return expiringMap.containsKey(sessionId);
    }

    synchronized Session create(String sessionId, HttpRequest request) {
        Session session = new Session(request.getAddress(), new ConcurrentHashMap<>());
        expiringMap.put(sessionId, session);
        return session;
    }

    public synchronized void clear() {
        expiringMap.clear();
    }


}
