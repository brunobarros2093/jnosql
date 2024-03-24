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

import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RemoveProviderTest {

    private final DelQueryConverter provider = new DelQueryConverter();

    @Test
    void shouldReturnErrorWhenStringIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> provider.apply(null));
    }


    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"del \"Diana\""})
    void shouldReturnParserQuery(String query) {
        DelQuery delQuery = provider.apply(query);
        List<QueryValue<?>> keys = delQuery.keys();
        assertEquals(1, keys.size());
        QueryValue<?> key = keys.get(0);
        assertTrue(key instanceof StringQueryValue);
        assertEquals("Diana", StringQueryValue.class.cast(key).get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"del 12"})
    void shouldReturnParserQuery1(String query) {
        DelQuery delQuery = provider.apply(query);
        List<QueryValue<?>> keys = delQuery.keys();
        assertEquals(1, keys.size());
        QueryValue<?> key = keys.get(0);
        assertTrue(key instanceof NumberQueryValue);
        assertEquals(12L, NumberQueryValue.class.cast(key).get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"del 12.12"})
    void shouldReturnParserQuery2(String query) {
        DelQuery delQuery = provider.apply(query);
        List<QueryValue<?>> keys = delQuery.keys();
        assertEquals(1, keys.size());
        QueryValue<?> key = keys.get(0);
        assertTrue(key instanceof NumberQueryValue);
        assertEquals(12.12, NumberQueryValue.class.cast(key).get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"del -12"})
    void shouldReturnParserQuery3(String query) {
        DelQuery delQuery = provider.apply(query);
        List<QueryValue<?>> keys = delQuery.keys();
        assertEquals(1, keys.size());
        QueryValue<?> key = keys.get(0);
        assertTrue(key instanceof NumberQueryValue);
        assertEquals(-12L, NumberQueryValue.class.cast(key).get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"del -12.12"})
    void shouldReturnParserQuery4(String query) {
        DelQuery delQuery = provider.apply(query);
        List<QueryValue<?>> keys = delQuery.keys();
        assertEquals(1, keys.size());
        QueryValue<?> key = keys.get(0);
        assertTrue(key instanceof NumberQueryValue);
        assertEquals(-12.12, NumberQueryValue.class.cast(key).get());
    }


    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"del {1,12}"})
    void shouldReturnParserQuery5(String query) {
        DelQuery delQuery = provider.apply(query);
        List<QueryValue<?>> keys = delQuery.keys();
        assertEquals(1, keys.size());
        QueryValue<?> key = keys.get(0);
        assertTrue(key instanceof DefaultArrayQueryValue);
        QueryValue<?>[] values = DefaultArrayQueryValue.class.cast(key).get();
        List<Long> ids = stream(values).map(QueryValue::get)
                .map(Long.class::cast)
                .collect(toList());
        assertThat(ids).hasSize(2).contains(1L, 12L);
    }


}