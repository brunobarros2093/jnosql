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
package org.eclipse.jnosql.mapping.graph;

import jakarta.inject.Inject;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.eclipse.jnosql.mapping.Converters;
import org.eclipse.jnosql.mapping.graph.spi.GraphExtension;
import org.eclipse.jnosql.mapping.reflection.EntityMetadataExtension;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@EnableAutoWeld
@AddPackages(value = {Converters.class, Transactional.class})
@AddPackages(BookRepository.class)
@AddExtensions({EntityMetadataExtension.class, GraphExtension.class})
public class GraphTemplateProducerTest {

    @Inject
    private GraphTemplateProducer producer;

    @Test
    public void shouldReturnErrorWhenManagerNull() {
        assertThrows(NullPointerException.class, () -> producer.get((Graph) null));
        assertThrows(NullPointerException.class, () -> producer.get((GraphTraversalSourceSupplier) null));
    }

    @Test
    public void shouldReturnGraphTemplateWhenGetGraph() {
        Graph graph = Mockito.mock(Graph.class);
        GraphTemplate template = producer.get(graph);
        assertNotNull(template);
    }


    @Test
    public void shouldReturnGraphTemplateWhenGetGraphTraversalSourceSupplier() {
        GraphTraversalSourceSupplier supplier = Mockito.mock(GraphTraversalSourceSupplier.class);
        GraphTemplate template = producer.get(supplier);
        assertNotNull(template);
    }
}