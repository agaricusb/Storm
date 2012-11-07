package com.github.StormTeam.Storm;

import java.lang.reflect.Method;
import java.util.HashMap;

public class SyncDelegator {

    public Class delegator;
    public HashMap<String, Method> cache = new HashMap<String, Method>();

    public SyncDelegator(Class clazz) {
        delegator = clazz;
        for (Method m : clazz.getMethods())
            cache.put(m.getName(), m);
    }

    public Object delegateDynamic(String name, Object on, Object... args) {
        Verbose.log("(SyncDelegator) Delegating " + name + " in class " + on.getClass() + " with args " + args + ".");
        Object ret = null;
        synchronized (on) {
            try {
                ret = cache.get(name).invoke(on, args);
            } catch (Exception e) {
              //  ErrorLogger.generateErrorLog(e);
            }
        }

        Verbose.log("(SyncDelegator) Delegation " + name + " in class " + on.getClass() + " returned " + ret);
        return ret;
    }

    public Object delegateStatic(String name, Object... args) {
        return delegateDynamic(name, null, args);
    }
}
