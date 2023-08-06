/*
 *
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 *
 */

package org.eclipse.jnosql.communication.reader;


import org.eclipse.jnosql.communication.ValueReader;

/**
 * Class to reads and converts to {@link Long}, first it verify if is Double if yes return itself then verifies if is
 * {@link Number} and use {@link Number#longValue()} otherwise convert to {@link String} and then {@link Long}
 */
public final class LongReader implements ValueReader {

    @Override
    public boolean test(Class<?> type) {
        return Long.class.equals(type) || long.class.equals(type);
    }

    @Override
    public <T> T read(Class<T> type, Object value) {

        if (Long.class.isInstance(value)) {
            return (T) value;
        }
        if (value instanceof Number number) {
            return (T) Long.valueOf(number.longValue());
        } else {
            return (T) Long.valueOf(value.toString());
        }
    }
}
