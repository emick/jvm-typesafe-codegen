package com.github.emick.codegen.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Reflection utilities for this library's usage.
 * <p>
 * WARNING: these are not usable in general. E.g. these methods do not handle fields in parent classes.
 */
@SuppressWarnings("unused") // Methods are used from generated classes.
public class FieldGenReflectionUtil {

    public static void setFieldValue(Object object, String fieldName, Object value) {
        Field field = getDeclaredField(object, fieldName);
        doPreservingAccessible(field, object, () -> {
            field.set(object, value);
            return null;
        });
    }

    public static Object getFieldValue(Object object, String fieldName) {
        Field field = getDeclaredField(object, fieldName);
        return doPreservingAccessible(field, object, () -> field.get(object));
    }

    private static <T> T doPreservingAccessible(Field field, Object targetObject, ThrowingSupplier<T> runnable) {
        Object accessTarget = Modifier.isStatic(field.getModifiers()) ? null : targetObject;
        boolean mustOpen = !field.canAccess(accessTarget);

        try {
            if (mustOpen) {
                field.setAccessible(true);
            }
            return runnable.get();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        } finally {
            if (mustOpen) {
                field.setAccessible(false);
            }
        }
    }

    private static Field getDeclaredField(Object obj, String fieldName) {
        Class<?> current = obj.getClass();

        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (final NoSuchFieldException ignored) {
                // Keep searching in parent types and interfaces
            }

            for (Class<?> iface : current.getInterfaces()) {
                try {
                    return iface.getDeclaredField(fieldName);
                } catch (NoSuchFieldException ignored) {
                    // Keep searching
                }
            }

            current = current.getSuperclass();
        }

        throw new RuntimeException(new NoSuchFieldException(fieldName));
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws ReflectiveOperationException;
    }
}
