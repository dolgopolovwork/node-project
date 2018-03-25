package ru.babobka.vsjws.model.http;

import net.jodah.expiringmap.ExpiringMap;

import java.io.Serializable;
import java.net.InetAddress;
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
            throw new IllegalArgumentException("Session timeout seconds must be in range [1,MAX_INT]");
        }
        expiringMap = ExpiringMap.builder().expirationPolicy(ExpiringMap.ExpirationPolicy.CREATED)
                .expiration(seconds, TimeUnit.SECONDS).build();
    }

    public Map<String, Serializable> getData(String sessionId) {
        Session session = get(sessionId);
        if (session != null) {
            return session.getData();
        }
        return null;
    }

    public Session get(String sessionId) {
        return expiringMap.get(sessionId);
    }

    public boolean exists(String sessionId) {
        return expiringMap.containsKey(sessionId);
    }

    public void create(String sessionId, HttpRequest request) {
        SessionInfo info = new SessionInfo(request.getAddress());
        Session session = new Session(info, new ConcurrentHashMap<>());
        expiringMap.put(sessionId, session);
    }

    public void clear() {
        expiringMap.clear();
    }

    public static class Session {
        private final SessionInfo info;
        private final Map<String, Serializable> data;

        public Session(SessionInfo info, Map<String, Serializable> data) {
            this.info = info;
            this.data = data;
        }

        public SessionInfo getInfo() {
            return info;
        }

        public Map<String, Serializable> getData() {
            return data;
        }
    }

    public static class SessionInfo {
        private final InetAddress creatorAddress;
        private final long creationTime;

        public SessionInfo(InetAddress creatorAddress) {
            if (creatorAddress == null) {
                throw new IllegalArgumentException("session creator address is null");
            }
            this.creatorAddress = creatorAddress;
            this.creationTime = System.currentTimeMillis();
        }

        public InetAddress getCreatorAddress() {
            return creatorAddress;
        }

        public long getCreationTime() {
            return creationTime;
        }

    }
}
