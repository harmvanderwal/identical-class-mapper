package com.codedependency.classmapper;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * @author Harm van der Wal <h.t.vd.wal@gmail.com>
 */
@Slf4j
public class IdenticalClassMapper {

    /**
     * For mapping classes that have recursively identical fields,
     * all fields can be literally copied into the destination class.
     * @param source
     * @param destType
     * @param <T>
     * @param <U>
     * @return Destination Class filled with source Class values.
     */
    public static <T, U> U map(T source, Class<U> destType) {
        try {
            Constructor<U> destConstructor = destType.getDeclaredConstructor();
            destConstructor.setAccessible(true);
            U dest = destConstructor.newInstance();
            Map<String, Field> sourceFields = Arrays.stream(source.getClass().getDeclaredFields())
                    .peek(x -> x.setAccessible(true))
                    .filter(x -> {
                        try {
                            return x.get(source) != null;
                        } catch (IllegalAccessException e) {
                            // This cannot happen, because accessible is set to true.
                            log.error(e.getMessage(), e);
                            return false;
                        }
                    })
                    .collect(toMap(Field::getName, entry -> entry));
            Field[] destFields = dest.getClass().getDeclaredFields();
            for (Field destField : destFields) {
                if (Modifier.isStatic(destField.getModifiers())) {
                    continue;
                }
                Field sourceField = sourceFields.get(destField.getName());
                if (sourceField != null) {
                    Object sourceFieldValue = sourceField.get(source);
                    destField.setAccessible(true);
                    if (sourceFieldValue instanceof List) {
                        destField.set(dest, getDestList(destField, sourceFieldValue));
                    } else if (sourceFieldValue instanceof Map) {
                        destField.set(dest, getDestMap(destField, sourceFieldValue));
                    } else if (sourceFieldValue instanceof Set) {
                        destField.set(dest, getDestSet(destField, sourceFieldValue));
                    } else if (destField.getType().equals(sourceField.getType())) {
                        destField.set(dest, sourceFieldValue);
                    } else {
                        destField.set(dest, map(sourceFieldValue, destField.getType()));
                    }
                }
            }
            return dest;
        } catch (ReflectiveOperationException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private static <T> List<?> getDestList(Field destField, Object sourceList) throws ClassNotFoundException {
        Class<?> listGenericTypeClass = getGenericParameterClass(destField.getGenericType(), 0);
        return ((List<?>) sourceList)
                .stream()
                .map(x -> x instanceof String || x instanceof Number ? x : map(x, listGenericTypeClass))
                .collect(toList());
    }

    private static Map<?, ?> getDestMap(Field destField, Object sourceMap) throws ClassNotFoundException {
        Class<?> mapFirstArgumentTypeClass = getGenericParameterClass(destField.getGenericType(), 0);
        Class<?> mapSecondArgumentTypeClass = getGenericParameterClass(destField.getGenericType(), 1);
        return ((Map<?, ?>) sourceMap).entrySet()
                .stream()
                .map(x -> new AbstractMap.SimpleEntry<>(x.getKey() instanceof String || x.getKey() instanceof Number ? x.getKey() : map(x.getKey(), mapFirstArgumentTypeClass),
                        x.getValue() instanceof String || x.getValue() instanceof Number ? x.getValue() : map(x.getValue(), mapSecondArgumentTypeClass)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Set<?> getDestSet(Field destField, Object sourceSet) throws ClassNotFoundException {
        Class<?> setGenericTypeClass = getGenericParameterClass(destField.getGenericType(), 0);
        return ((Set<?>) sourceSet)
                .stream()
                .map(x -> x instanceof String || x instanceof Number? x : map(x, setGenericTypeClass))
                .collect(toSet());
    }

    private static Class<?> getGenericParameterClass(Type genericType, int argNumber) throws ClassNotFoundException {
        if (!(genericType instanceof ParameterizedType)) {
            return Object.class;
        }
        ParameterizedType paramType = (ParameterizedType) genericType;
        Type[] argTypes = paramType.getActualTypeArguments();
        return argTypes.length > argNumber ? Class.forName(argTypes[argNumber].getTypeName()) : Object.class;
    }
}
