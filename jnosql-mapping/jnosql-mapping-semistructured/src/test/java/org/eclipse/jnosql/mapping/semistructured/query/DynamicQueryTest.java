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
package org.eclipse.jnosql.mapping.semistructured.query;

import jakarta.data.Limit;
import jakarta.data.page.PageRequest;
import jakarta.data.Sort;
import org.eclipse.jnosql.communication.semistructured.SelectQuery;
import org.eclipse.jnosql.mapping.core.repository.SpecialParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class DynamicQueryTest {

    @Mock
    private SpecialParameters special;

    @Mock
    private SelectQuery query;

    @Mock
    private Limit limit;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateDynamicQuery() {
        when(special.isEmpty()).thenReturn(true);
        when(query.condition()).thenReturn(Optional.empty());
        when(query.name()).thenReturn("sampleQuery");

        DynamicQuery dynamicQuery = DynamicQuery.of(new Object[]{}, query);

        assertEquals(query, dynamicQuery.get());
    }

    @Test
    void shouldCreateDynamicQueryWithSortsAndLimit() {
        when(special.isEmpty()).thenReturn(false);
        when(special.hasOnlySort()).thenReturn(true);
        when(special.sorts()).thenReturn(List.of(mock(Sort.class)));
        when(limit.startAt()).thenReturn(1L);
        when(special.limit()).thenReturn(Optional.of(limit));
        when(query.condition()).thenReturn(Optional.empty());
        when(query.name()).thenReturn("sampleQuery");
        when(query.sorts()).thenReturn(List.of(mock(Sort.class)));
        when(query.skip()).thenReturn(0L);
        when(query.limit()).thenReturn(10L);

        DynamicQuery dynamicQuery = DynamicQuery.of(new Object[]{}, query);

        assertEquals("sampleQuery", dynamicQuery.get().name());
        assertEquals(0, dynamicQuery.get().skip());
        assertEquals(10, dynamicQuery.get().limit());
        assertEquals(1, dynamicQuery.get().sorts().size());
    }

    @Test
    void shouldCreateDynamicQueryWithLimit() {
        when(special.isEmpty()).thenReturn(false);
        when(special.hasOnlySort()).thenReturn(false);
        when(limit.startAt()).thenReturn(1L);
        when(special.limit()).thenReturn(Optional.of(limit));
        when(query.condition()).thenReturn(Optional.empty());
        when(query.name()).thenReturn("sampleQuery");
        when(query.sorts()).thenReturn(List.of(mock(Sort.class)));
        when(query.skip()).thenReturn(0L);
        when(query.limit()).thenReturn(10L);

        DynamicQuery dynamicQuery = DynamicQuery.of(new Object[]{}, query);

        assertEquals("sampleQuery", dynamicQuery.get().name());
        assertEquals(0, dynamicQuery.get().skip());
        assertEquals(10, dynamicQuery.get().limit());
        assertEquals(1, dynamicQuery.get().sorts().size());
    }

    @Test
    void shouldCreateDynamicQueryWithPageRequest() {
        when(special.isEmpty()).thenReturn(false);
        when(special.pageRequest()).thenReturn(Optional.of(mock(PageRequest.class)));
        when(special.sorts()).thenReturn(List.of(mock(Sort.class)));
        when(query.condition()).thenReturn(Optional.empty());
        when(query.name()).thenReturn("sampleQuery");
        when(query.sorts()).thenReturn(List.of(mock(Sort.class)));
        when(query.skip()).thenReturn(0L);
        when(query.limit()).thenReturn(10L);

        DynamicQuery dynamicQuery = DynamicQuery.of(new Object[]{}, query);

        assertEquals("sampleQuery", dynamicQuery.get().name());
        assertEquals(0, dynamicQuery.get().skip());
        assertEquals(10, dynamicQuery.get().limit());
        assertEquals(1, dynamicQuery.get().sorts().size());
    }

    @Test
    void shouldReturnWhenThereIsLimitAndSort(){
        when(special.isEmpty()).thenReturn(false);
        when(special.pageRequest()).thenReturn(Optional.of(mock(PageRequest.class)));
        when(query.condition()).thenReturn(Optional.empty());
        when(query.name()).thenReturn("sampleQuery");
        when(query.sorts()).thenReturn(Collections.emptyList());
        when(query.skip()).thenReturn(0L);
        when(query.limit()).thenReturn(10L);

        DynamicQuery dynamicQuery = DynamicQuery.of(new Object[]{Sort.asc("name"), Limit.of(20)}
        , query);

        SelectQuery columnQuery = dynamicQuery.get();
        assertEquals("sampleQuery", columnQuery.name());
        assertEquals(0, columnQuery.skip());
        assertEquals(20, columnQuery.limit());
        assertEquals(1, columnQuery.sorts().size());
    }
}