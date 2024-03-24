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

package org.eclipse.jnosql.communication.writer;


import org.eclipse.jnosql.communication.ValueWriter;

import java.util.Optional;

/**
 * A {@code ValueWriter} implementation for handling {@code Optional} types.
 * This class is responsible for converting {@code Optional<T>} instances to their
 * underlying values.
 *
 * @param <T> the type of the elements in the Optional
 */
public final class OptionalValueWriter<T> implements ValueWriter<Optional<T>, T> {

    @Override
    public boolean test(Class<?> type) {
        return Optional.class.equals(type);
    }

    @Override
    public T write(Optional<T> optional) {
        return optional.orElse(null);
    }
}
