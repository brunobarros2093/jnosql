/*
 *  Copyright (c) 2023 Contributors to the Eclipse Foundation
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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class StringQueryValueTest {

    @Test
    void shouldReturnType() {
        StringQueryValue string = new StringQueryValue("text");
        Assertions.assertThat(string).isNotNull()
                .extracting(StringQueryValue::type)
                .isEqualTo(ValueType.STRING);
    }


    @Test
    void shouldReturnValue() {
        StringQueryValue string = new StringQueryValue("text");
        Assertions.assertThat(string).isNotNull()
                .extracting(StringQueryValue::get)
                .isEqualTo("text");
    }

    @Test
    void shouldEquals() {
        StringQueryValue queryValue = new StringQueryValue("text");
        assertEquals(queryValue, queryValue);
        assertEquals(new StringQueryValue("text"), new StringQueryValue("text"));
        assertNotEquals(new StringQueryValue("text"), new StringQueryValue("text2"));
        assertNotEquals(new StringQueryValue("text"), "text2");
    }

    @Test
    void shouldHasCode() {
        assertEquals(new StringQueryValue("text").hashCode()
                , new StringQueryValue("text").hashCode());

    }

    @Test
    void shouldReturnToString() {
        assertEquals("'text'", new StringQueryValue("text").toString());
    }
}