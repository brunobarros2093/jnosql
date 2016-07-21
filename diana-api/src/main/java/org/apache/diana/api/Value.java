package org.apache.diana.api;


import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public interface Value extends Serializable {


    Object get();

    /**
     * Converts Value to specified class
     * @param clazz the new class
     * @param <T> the new instance type
     * @return a new instance converted to informed class
     * @throws NullPointerException when the class is null
     * @throws UnsupportedOperationException when the type is unsupported
     * @see ReaderField
     */
    <T> T get(Class<T> clazz)throws  NullPointerException, UnsupportedOperationException;

    <T> List<T> getList(Class<T> clazz);

    <T> Stream<T> getStream(Class<T> clazz);

    <T> Set<T> getSet(Class<T> clazz);

    <K, V > Map<K, V> getSet(Class<K> keyClass, Class<V> valueClass);

    static Value of(Object value) {
        return DefaultValue.of(value);
    }

}
