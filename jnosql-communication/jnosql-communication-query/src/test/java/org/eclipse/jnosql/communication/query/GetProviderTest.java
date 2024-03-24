/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *  You may elect to redistribute this code under either of these licenses.
 *  Contributors:
 *  Otavio Santana
 */

package org.eclipse.jnosql.communication.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetProviderTest {

    private final GetQueryConverter queryProvider = new GetQueryConverter();

    @Test
    void shouldReturnErrorWhenStringIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> queryProvider.apply(null));
    }


    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"get \"Diana\""})
    void shouldReturnParserQuery(String query) {
        GetQuery getQuery = queryProvider.apply(query);
        List<QueryValue<?>> keys = getQuery.keys();
        assertEquals(1, keys.size());
        QueryValue<?> key = keys.get(0);
        assertTrue(key instanceof StringQueryValue);
        assertEquals("Diana", StringQueryValue.class.cast(key).get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"get 12"})
    void shouldReturnParserQuery1(String query) {
        GetQuery getQuery = queryProvider.apply(query);
        List<QueryValue<?>> keys = getQuery.keys();
        assertEquals(1, keys.size());
        QueryValue<?> key = keys.get(0);
        assertTrue(key instanceof NumberQueryValue);
        assertEquals(12L, NumberQueryValue.class.cast(key).get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"get 12.12"})
    void shouldReturnParserQuery2(String query) {
        GetQuery getQuery = queryProvider.apply(query);
        List<QueryValue<?>> keys = getQuery.keys();
        assertEquals(1, keys.size());
        QueryValue<?> key = keys.get(0);
        assertTrue(key instanceof NumberQueryValue);
        assertEquals(12.12, NumberQueryValue.class.cast(key).get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"get -12"})
    void shouldReturnParserQuery3(String query) {
        GetQuery getQuery = queryProvider.apply(query);
        List<QueryValue<?>> keys = getQuery.keys();
        assertEquals(1, keys.size());
        QueryValue<?> key = keys.get(0);
        assertTrue(key instanceof NumberQueryValue);
        assertEquals(-12L, NumberQueryValue.class.cast(key).get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"get -12.12"})
    void shouldReturnParserQuery4(String query) {
        GetQuery getQuery = queryProvider.apply(query);
        List<QueryValue<?>> keys = getQuery.keys();
        assertEquals(1, keys.size());
        QueryValue<?> key = keys.get(0);
        assertTrue(key instanceof NumberQueryValue);
        assertEquals(-12.12, NumberQueryValue.class.cast(key).get());
    }


    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"get {1,12}"})
    void shouldReturnParserQuery5(String query) {
        GetQuery getQuery = queryProvider.apply(query);
        List<QueryValue<?>> keys = getQuery.keys();
        assertEquals(1, keys.size());
        QueryValue<?> key = keys.get(0);
        assertTrue(key instanceof DefaultArrayQueryValue);
        QueryValue<?>[] values = DefaultArrayQueryValue.class.cast(key).get();
        List<Object> ids = Arrays.stream(values).map(QueryValue::get)
                .collect(Collectors.toList());
        assertThat(ids).contains(1L, 12L);
    }


}