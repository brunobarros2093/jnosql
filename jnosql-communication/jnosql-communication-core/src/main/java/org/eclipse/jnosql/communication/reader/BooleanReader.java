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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class to reads and converts to both {@link Boolean} and {@link AtomicBoolean}
 */
public final class BooleanReader implements ValueReader {

    @Override
    public boolean test(Class<?> type) {
        return Boolean.class.equals(type) ||
                AtomicBoolean.class.equals(type) ||
                boolean.class.equals(type);
    }

    @Override
    public <T> T read(Class<T> type, Object value) {

        boolean isAtomicBoolean = AtomicBoolean.class.equals(type);

        if (isAtomicBoolean && AtomicBoolean.class.isInstance(value)) {
            return (T) value;
        }
        Boolean bool = null;
        if (Boolean.class.isInstance(value)) {
            bool = Boolean.class.cast(value);
        } else if (AtomicBoolean.class.isInstance(value)) {
            bool = AtomicBoolean.class.cast(value).get();
        } else if (value instanceof Number number) {
            bool = number.longValue() != 0;
        } else if (String.class.isInstance(value)) {
            bool = Boolean.valueOf(value.toString());
        }

        if (isAtomicBoolean) {
            return (T) new AtomicBoolean(bool);
        }
        return (T) bool;
    }

}
