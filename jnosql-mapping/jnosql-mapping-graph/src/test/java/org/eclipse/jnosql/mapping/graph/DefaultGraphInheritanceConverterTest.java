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

@EnableAutoWeld
@AddPackages(value = {Converters.class, Transactional.class})
@AddPackages(BookRepository.class)
@AddExtensions({EntityMetadataExtension.class, GraphExtension.class})
class DefaultGraphInheritanceConverterTest extends GraphInheritanceConverterTest {

    @Inject
    private GraphConverter converter;

    @Inject
    private Graph graph;

    @Override
    protected Graph getGraph() {
        return graph;
    }

    @Override
    protected GraphConverter getConverter() {
        return converter;
    }
}
