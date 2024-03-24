/*
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
 */
package org.eclipse.jnosql.mapping.core;

import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseMetadata;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DatabaseMetadataTest {

    @Test
    void shouldReturnErrorWhenDatabaseIsNull() {
        assertThrows(NullPointerException.class, () -> DatabaseMetadata.of(null));
    }

    @Test
    void shouldReturnMetadata() {
        Database database = Mockito.mock(Database.class);
        Mockito.when(database.value()).thenReturn(DatabaseType.COLUMN);
        Mockito.when(database.provider()).thenReturn("column");
        DatabaseMetadata metadata = DatabaseMetadata.of(database);
        assertEquals(DatabaseType.COLUMN, metadata.getType());
        assertEquals("column", metadata.getProvider());
    }

    @Test
    void shouldReturnToString() {
        Database database = Mockito.mock(Database.class);
        Mockito.when(database.value()).thenReturn(DatabaseType.COLUMN);
        Mockito.when(database.provider()).thenReturn("column");
        DatabaseMetadata metadata = DatabaseMetadata.of(database);
        assertEquals("COLUMN@column", metadata.toString());
    }

    @Test
    void shouldReturnToString2() {
        Database database = Mockito.mock(Database.class);
        Mockito.when(database.value()).thenReturn(DatabaseType.COLUMN);
        DatabaseMetadata metadata = DatabaseMetadata.of(database);
        assertEquals("COLUMN", metadata.toString());
    }
}