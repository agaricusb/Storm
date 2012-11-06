package com.github.StormTeam.Storm;

import org.bukkit.Bukkit;

import java.lang.reflect.Method;
import java.util.HashMap;

public class SyncDelegator {

    public Class delegator;
    public HashMap<String, Method> cache = new HashMap<String, Method>();

    public SyncDelegator(Class clazz) {
        delegator = clazz;
        for (Method m : clazz.getDeclaredMethods())
            if (!cache.containsValue(m))
                cache.put(m.getName(), m);
        for (Method m : clazz.getMethods())
            if (!cache.containsValue(m))
                cache.put(m.getName(), m);
    }

    public Object delegate(final String name, final Object on, final Object... args) {
        final Object[] ret = new Object[1];
        Bukkit.getScheduler().scheduleSyncDelayedTask(Storm.instance, new Runnable() {
            public void run() {
                try {
                    ret[0] = (Object) cache.get(name).invoke(on, args);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0L);

        return ret[0];
    }

    public Object delegate(String name, Object... args) {
        return delegate(name, null, args);
    }
}
