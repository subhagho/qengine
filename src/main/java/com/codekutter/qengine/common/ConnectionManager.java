package com.codekutter.qengine.common;

import com.codekutter.qengine.utils.LogUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.NonNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {
    public enum ConnectionTypes {
        Hibernate
    }

    private Map<String, SessionFactory> hibernateFactory = new HashMap<>();

    private ConnectionManager() {}

    public ConnectionManager addHibernateSession(@NonNull String name, @NonNull SessionFactory sessionFactory) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        hibernateFactory.put(name, sessionFactory);
        return this;
    }

    public Session getHibernateSession(@NonNull String name) {
        SessionFactory sf = hibernateFactory.get(name);
        if (sf != null) {
            LogUtils.debug(getClass(), String.format("Opening new Hibernate session. [name=%s]", name));
            return sf.openSession();
        }
        return null;
    }

    private static final ConnectionManager __instance = new ConnectionManager();

    public static ConnectionManager get() {
        return __instance;
    }
}
