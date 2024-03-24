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

import java.time.temporal.Temporal;


/**
 * A {@code ValueWriter} implementation for handling types that implement the {@code Temporal} interface.
 * This class is responsible for converting {@code Temporal} instances to their string representations.
 */
public class TemporalValueWriter implements ValueWriter<Temporal, String> {

    @Override
    public boolean test(Class<?> type) {
        return Temporal.class.isAssignableFrom(type);
    }

    @Override
    public String write(Temporal object) {
        return object.toString();
    }
}
