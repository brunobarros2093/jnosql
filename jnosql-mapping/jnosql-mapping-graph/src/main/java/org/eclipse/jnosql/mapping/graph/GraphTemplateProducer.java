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

import org.eclipse.jnosql.mapping.Converters;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.eclipse.jnosql.mapping.metadata.EntitiesMetadata;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.util.TypeLiteral;
import jakarta.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Iterator;

import static java.util.Objects.requireNonNull;

/**
 * The producer of {@link GraphTemplate}
 */
@ApplicationScoped
public class GraphTemplateProducer {

    @Inject
    private EntitiesMetadata entities;

    @Inject
    private Converters converters;

    @Inject
    private GraphEventPersistManager persistManager;

    @Inject
    private GraphEventPersistManager eventManager;


    /**
     * creates a {@link GraphTemplate}
     *
     * @param graph the graph
     * @return a new instance
     * @throws NullPointerException when collectionManager is null
     */
    public GraphTemplate get(Graph graph) {
        requireNonNull(graph, "graph is required");

        SingleInstance<Graph> instance = new SingleInstance<>(graph);

        GraphConverter converter = new DefaultGraphConverter(entities,
                converters,instance, eventManager);
        return new DefaultGraphTemplate(instance, entities, converter, persistManager, converters);
    }

    /**
     * creates a {@link GraphTemplate}
     *
     * @param supplier the supplier
     * @return a new instance
     * @throws NullPointerException when supplier is null
     */
    public GraphTemplate get(GraphTraversalSourceSupplier supplier) {
        requireNonNull(supplier, "supplier is required");

        SingleInstance<GraphTraversalSourceSupplier> instance = new SingleInstance<>(supplier);

        GraphConverter converter = new DefaultGraphTraversalSourceConverter(entities,
                converters, instance, eventManager);
        return new DefaultGraphTraversalSourceTemplate(instance, entities, converter, persistManager, converters);
    }

    static class SingleInstance<T> implements Instance<T> {

        private final T instance;

        SingleInstance(T instance) {
            this.instance = instance;
        }


        @Override
        public Instance<T> select(Annotation... annotations) {
           throw new UnsupportedOperationException("this method is not support");
        }

        @Override
        public <U extends T> Instance<U> select(Class<U> aClass, Annotation... annotations) {
            throw new UnsupportedOperationException("this method is not support");
        }

        @Override
        public <U extends T> Instance<U> select(TypeLiteral<U> typeLiteral, Annotation... annotations) {
            throw new UnsupportedOperationException("this method is not support");
        }

        @Override
        public boolean isUnsatisfied() {
            return false;
        }

        @Override
        public boolean isAmbiguous() {
            return false;
        }

        @Override
        public void destroy(T t) {
        }

        @Override
        public Handle<T> getHandle() {
            throw new UnsupportedOperationException("this method is not support");
        }

        @Override
        public Iterable<? extends Handle<T>> handles() {
            throw new UnsupportedOperationException("this method is not support");
        }

        @Override
        public Iterator<T> iterator() {
         return Collections.singletonList(instance).iterator();
        }

        @Override
        public T get() {
            return instance;
        }
    }
}
