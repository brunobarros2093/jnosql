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

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PutProviderTest {

    private final PutQueryConverter provider = new PutQueryConverter();

    @Test
    void shouldReturnErrorWhenStringIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> provider.apply(null));
    }


    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"put {\"Ada\", \"Hunt\"}\n"})
    void shouldReturnParserQuery(String query) {
        PutQuery putQuery = provider.apply(query);
        QueryValue<?> key = putQuery.key();
        QueryValue<?> value = putQuery.value();
        Optional<Duration> ttl = putQuery.ttl();

        assertTrue(key instanceof StringQueryValue);
        assertEquals("Ada", StringQueryValue.class.cast(key).get());
        assertFalse(ttl.isPresent());

        assertTrue(value instanceof StringQueryValue);
        assertEquals("Hunt", StringQueryValue.class.cast(value).get());

        assertFalse(ttl.isPresent());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"put {12, \"Hunt\"}\n"})
    void shouldReturnParserQuery1(String query) {
        PutQuery putQuery = provider.apply(query);
        QueryValue<?> key = putQuery.key();
        QueryValue<?> value = putQuery.value();
        Optional<Duration> ttl = putQuery.ttl();

        assertTrue(key instanceof NumberQueryValue);
        assertEquals(12L, NumberQueryValue.class.cast(key).get());

        assertTrue(value instanceof StringQueryValue);
        assertEquals("Hunt", StringQueryValue.class.cast(value).get());
        assertFalse(ttl.isPresent());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"put {12, 12.12}\n"})
    void shouldReturnParserQuery2(String query) {
        PutQuery putQuery = provider.apply(query);
        QueryValue<?> key = putQuery.key();
        QueryValue<?> value = putQuery.value();
        Optional<Duration> ttl = putQuery.ttl();

        assertTrue(key instanceof NumberQueryValue);
        assertEquals(12L, NumberQueryValue.class.cast(key).get());

        assertTrue(value instanceof NumberQueryValue);
        assertEquals(12.12, NumberQueryValue.class.cast(value).get());
        assertFalse(ttl.isPresent());
    }


    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"put {@name, @value, 10 hour}\n"})
    void shouldReturnParserQuery3(String query) {
        PutQuery putQuery = provider.apply(query);
        QueryValue<?> key = putQuery.key();
        QueryValue<?> value = putQuery.value();
        Optional<Duration> ttl = putQuery.ttl();

        assertTrue(key instanceof DefaultQueryValue);
        assertEquals("name", DefaultQueryValue.class.cast(key).get());

        assertTrue(value instanceof DefaultQueryValue);
        assertEquals("value", DefaultQueryValue.class.cast(value).get());
        assertTrue(ttl.isPresent());
        assertEquals(Duration.ofHours(10L), ttl.get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"put {@name, @value, 10 minute}\n"})
    void shouldReturnParserQuery4(String query) {
        PutQuery putQuery = provider.apply(query);
        QueryValue<?> key = putQuery.key();
        QueryValue<?> value = putQuery.value();
        Optional<Duration> ttl = putQuery.ttl();

        assertTrue(key instanceof DefaultQueryValue);
        assertEquals("name", DefaultQueryValue.class.cast(key).get());

        assertTrue(value instanceof DefaultQueryValue);
        assertEquals("value", DefaultQueryValue.class.cast(value).get());
        assertTrue(ttl.isPresent());
        assertEquals(Duration.ofMinutes(10L), ttl.get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"put {@name, @value, 10 second}\n"})
    void shouldReturnParserQuery5(String query) {
        PutQuery putQuery = provider.apply(query);
        QueryValue<?> key = putQuery.key();
        QueryValue<?> value = putQuery.value();
        Optional<Duration> ttl = putQuery.ttl();

        assertTrue(key instanceof DefaultQueryValue);
        assertEquals("name", DefaultQueryValue.class.cast(key).get());

        assertTrue(value instanceof DefaultQueryValue);
        assertEquals("value", DefaultQueryValue.class.cast(value).get());
        assertTrue(ttl.isPresent());
        assertEquals(Duration.ofSeconds(10L), ttl.get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"put {@name, @value, 10 millisecond}\n"})
    void shouldReturnParserQuery6(String query) {
        PutQuery putQuery = provider.apply(query);
        QueryValue<?> key = putQuery.key();
        QueryValue<?> value = putQuery.value();
        Optional<Duration> ttl = putQuery.ttl();

        assertTrue(key instanceof DefaultQueryValue);
        assertEquals("name", DefaultQueryValue.class.cast(key).get());

        assertTrue(value instanceof DefaultQueryValue);
        assertEquals("value", DefaultQueryValue.class.cast(value).get());
        assertTrue(ttl.isPresent());
        assertEquals(Duration.ofMillis(10L), ttl.get());
    }

    @ParameterizedTest(name = "Should parser the query {0}")
    @ValueSource(strings = {"put {@name, @value, 10 nanosecond}\n"})
    void shouldReturnParserQuery7(String query) {
        PutQuery putQuery = provider.apply(query);
        QueryValue<?> key = putQuery.key();
        QueryValue<?> value = putQuery.value();
        Optional<Duration> ttl = putQuery.ttl();

        assertTrue(key instanceof DefaultQueryValue);
        assertEquals("name", DefaultQueryValue.class.cast(key).get());

        assertTrue(value instanceof DefaultQueryValue);
        assertEquals("value", DefaultQueryValue.class.cast(value).get());
        assertTrue(ttl.isPresent());
        assertEquals(Duration.ofNanos(10L), ttl.get());
    }

}