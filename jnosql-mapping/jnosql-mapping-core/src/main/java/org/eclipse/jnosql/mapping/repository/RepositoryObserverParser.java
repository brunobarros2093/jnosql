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
package org.eclipse.jnosql.mapping.repository;

import org.eclipse.jnosql.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.mapping.metadata.FieldMetadata;

import java.util.Objects;
import java.util.Optional;

public final class RepositoryObserverParser {

    private static final String CAMEL_CASE = "(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])";

    private final EntityMetadata metadata;

    private RepositoryObserverParser(EntityMetadata metadata) {
        this.metadata = metadata;
    }


    /**
     * Returns name of the entity
     *
     * @return the field result
     */
    public String name() {
        return metadata.name();
    }

    /**
     * Given a Java field, it will convert it to native if it does exist,
     * otherwise, it will return the same value
     *
     * @param field the field
     * @return the field result
     */
    public String field(String field) {
        if (metadata.fieldMapping(field).isPresent()) {
            return metadata.columnField(field);
        } else {
            String currentField = "";
            String[] fields = field.split(CAMEL_CASE);
            for (int index = 0; index < fields.length; index++) {
                if (currentField.isEmpty()) {
                    currentField = capitalize(fields[index], false);
                } else {
                    currentField = currentField + capitalize(fields[index], true);
                }
                Optional<FieldMetadata> mapping = metadata.fieldMapping(currentField);
                if (mapping.isPresent()) {
                    String name = mapping.map(FieldMetadata::name).orElseThrow();
                    return name + concat(index, fields);
                }
            }
        }
        return field;
    }

    private String concat(int index, String[] fields) {
        if (index > (fields.length - 1)) {
            return "";
        }
        StringBuilder column = new StringBuilder(".");
        column.append(capitalize(fields[index + 1], false));
        if (index + 2 > (fields.length - 1)) {
            return column.toString();
        }
        for (int newIndex = index + 2; newIndex < fields.length; newIndex++) {
            column.append(fields[newIndex]);
        }
        return column.toString();
    }

    private static String capitalize(String text, boolean upperCase) {
        if (text.isBlank()) {
            return text;
        }

        char baseChar = text.charAt(0);
        char updatedChar;
        if (upperCase) {
            updatedChar = Character.toUpperCase(baseChar);
        } else {
            updatedChar = Character.toLowerCase(baseChar);
        }
        if (baseChar == updatedChar) {
            return text;
        }

        char[] chars = text.toCharArray();
        chars[0] = updatedChar;
        return new String(chars, 0, chars.length);
    }

    public static RepositoryObserverParser of(EntityMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata is required");
        return new RepositoryObserverParser(metadata);
    }
}
