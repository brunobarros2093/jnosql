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

import jakarta.enterprise.inject.spi.AnnotatedMember;
import jakarta.enterprise.inject.spi.ProcessProducer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DatabasesTest {

    @Test
    public void shouldAddDatabaseValidDatabaseTypeAddsMetadataToSet() {
        ProcessProducer processProducer = mock(ProcessProducer.class);
        AnnotatedMember annotatedMember = mock(AnnotatedMember.class);
        DatabaseType type = DatabaseType.DOCUMENT;
        Set<DatabaseMetadata> databases = new HashSet<>();

        // Creating a Database annotation with a matching type
        Database databaseAnnotation = mock(Database.class);
        when(databaseAnnotation.value()).thenReturn(DatabaseType.DOCUMENT);

        // Mocking the annotations set returned by the processProducer
        Set<Annotation> annotations = new HashSet<>();
        annotations.add(databaseAnnotation);
        when(processProducer.getAnnotatedMember()).thenReturn(annotatedMember);
        when(annotatedMember.getAnnotations()).thenReturn(annotations);

        Databases.addDatabase(processProducer, type, databases);

        assertEquals(1, databases.size());
        assertTrue(databases.contains(DatabaseMetadata.of(databaseAnnotation)));
    }

    @Test
    public void shouldAddDatabase_NoDatabaseAnnotation_NoMetadataAdded() {
        ProcessProducer processProducer = mock(ProcessProducer.class);
        DatabaseType type = DatabaseType.DOCUMENT;
        AnnotatedMember annotatedMember = mock(AnnotatedMember.class);
        Set<DatabaseMetadata> databases = new HashSet<>();

        // Mocking the annotations set returned by the processProducer (no Database annotation)
        Set<Annotation> annotations = new HashSet<>();
        when(processProducer.getAnnotatedMember()).thenReturn(annotatedMember);
        when(annotatedMember.getAnnotations()).thenReturn(annotations);

        Databases.addDatabase(processProducer, type, databases);

        assertEquals(0, databases.size());
    }

}