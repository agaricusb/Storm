/*
 * This file is part of Storm.
 *
 * Storm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Storm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Storm.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.github.StormTeam.Storm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectionHelper {
    public static <T> FieldContainer<T> field(String name) {
        return new FieldContainer<T>(name);
    }

    public static <T> MethodContainer<T> method(String name) {
        return new MethodContainer<T>(name);
    }

    public static ConstructorContainer constructor() {
        return new ConstructorContainer();
    }
}

class Container {
    public Container(String name) {
        this.name = name;
    }

    public Container() {
    }

    protected String name;
    protected Object in;
    protected Class target;
}

class MethodContainer<T> extends Container {
    private Class[] param;

    public MethodContainer(String name) {
        super(name);
    }

    public <T> MethodContainer<T> withReturnType(Class<T> clazz) {
        MethodContainer<T> clone = new MethodContainer<T>(name);
        clone.param = param;
        clone.target = target;
        clone.in = in;
        return clone;
    }

    public MethodContainer in(T object) {
        in = object;
        target = object.getClass();
        return this;
    }

    public MethodContainer<T> in(Class clazz) {
        target = clazz;
        return this;
    }

    public MethodContainer<T> withParameters(Class... args) {
        param = args;
        return this;
    }

    public MethodContainer<T> withParameters(Object... args) {
        Class[] classes = new Class[args.length];
        for (int i = 0; i < args.length; ++i) classes[i] = args[i].getClass();
        param = classes;
        return this;
    }

    public T invoke(Object... args) {
        try {
            return (T) getRaw().invoke(in, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Method getRaw() {
        try {
            Method raw = param.length > 0 ? target.getDeclaredMethod(name, param) : target.getDeclaredMethod(name);
            raw.setAccessible(true);
            return raw;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

class ConstructorContainer extends Container {
    private Class[] param;

    public ConstructorContainer withParameters(Class... args) {
        param = args;
        return this;
    }

    public ConstructorContainer withParameters(Object... args) {
        Class[] classes = new Class[args.length];
        for (int i = 0; i < args.length; ++i) classes[i] = args[i].getClass();
        return withParameters(classes);
    }

    public ConstructorContainer in(Class clazz) {
        target = clazz;
        return this;
    }

    public Object newInstance(Object... args) {
        try {
            return getRaw().newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Constructor getRaw() {
        try {
            Constructor raw = param.length > 0 ? target.getDeclaredConstructor(param) : target.getDeclaredConstructor();
            raw.setAccessible(true);
            return raw;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

class FieldContainer<T> extends Container {
    public FieldContainer(String name) {
        super(name);
    }

    public <T> FieldContainer<T> ofType(Class<T> clazz) {
        FieldContainer<T> clone = new FieldContainer<T>(name);
        clone.target = target;
        clone.in = in;
        return clone;
    }

    public FieldContainer<T> in(Object object) {
        in((in = object).getClass());
        return this;
    }

    public FieldContainer<T> in(Class clazz) {
        target = clazz;
        return this;
    }

    public void set(Object object) {
        try {
            Field field = target.getDeclaredField(name);
            access(field);
            field.set(in, object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public T get() {
        try {
            return (T) getRaw().get(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Field getRaw() {
        try {
            Field raw = target.getDeclaredField(name);
            access(raw);
            return raw;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void access(Field field) throws IllegalAccessException {
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.setAccessible(true);
    }

    protected static Field modifiersField;

    static {
        try {
            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
