package com.example.sso.session;

import com.example.sso.security.RealmResolver;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RealmSessionService {

    private final RealmResolver realmResolver;
    private final SessionRepository<? extends Session> sessionRepository;

    public RealmSessionService(RealmResolver realmResolver,
                               SessionRepository<? extends Session> sessionRepository) {
        this.realmResolver = realmResolver;
        this.sessionRepository = sessionRepository;
    }

    /**
     * Check if current realm session exists (cookie + Geode store)
     */
    public boolean currentRealmSessionExists(HttpServletRequest request) {

        String realm = normalize(realmResolver.resolveRealm(request));
        String cookieName = "SSOSESSION_" + realm;

        String sessionId = getSessionIdFromCookie(request, cookieName);

        if (!StringUtils.hasText(sessionId)) {
            return false;
        }

        Session session = sessionRepository.findById(sessionId);

        return session != null;
    }

    private String getSessionIdFromCookie(HttpServletRequest request, String cookieName) {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie c : cookies) {
            if (cookieName.equals(c.getName())
                    && StringUtils.hasText(c.getValue())) {
                return c.getValue();
            }
        }
        return null;
    }

    private String normalize(String realm) {
        if (!StringUtils.hasText(realm)) return "DEFAULT";
        return realm.trim().toUpperCase();
    }
}
