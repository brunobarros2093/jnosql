/*
 *  Copyright (c) 2023 Contributors to the Eclipse Foundation
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
 */
package org.eclipse.jnosql.mapping;

/**
 * A class that implements this interface can be used to convert entity attribute state into database column
 * representation and back again. Note that the X and Y types may be the same Java type.
 * @param <X> the type of the entity attribute
 * @param <Y> the type of the database column
 */
public interface AttributeConverter<X, Y> {

    /**
     * Converts the value stored in the entity attribute into the data representation to be stored in the database.
     *
     * @param attribute attribute the entity attribute value to be converted
     * @return the converted data to be stored in the database column
     */
    Y convertToDatabaseColumn(X attribute);

    /**
     * Converts the data stored in the database column into the value to be stored in the entity attribute.
     * Note that it is the responsibility of the converter writer to specify the correct dbData type for the
     * corresponding column for use by the JDBC driver: i.e., persistence providers are not expected to do
     * such type conversion.
     *
     * @param dbData the data from the database column to be converted
     * @return the converted value to be stored in the entity attribute
     */
    X convertToEntityAttribute(Y dbData);
}
