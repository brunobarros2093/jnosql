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
 * Class reader for {@link Number}, this converter first verify if the object is a Number instance,
 * if it will return itself, otherwise convert to String and then to {@link Double}
 */
@SuppressWarnings("unchecked")
public final class NumberReader implements ValueReader {

    @Override
    public boolean test(Class<?> type) {
        return Number.class.equals(type);
    }

    @Override
    public <T> T read(Class<T> type, Object value) {
        if (value instanceof Number) {
            return (T) value;
        } else {
            return (T) Double.valueOf(value.toString());
        }
    }


}
