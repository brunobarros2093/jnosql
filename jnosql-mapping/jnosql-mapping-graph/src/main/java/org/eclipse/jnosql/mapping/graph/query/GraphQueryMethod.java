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
package org.eclipse.jnosql.mapping.graph.query;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.jnosql.communication.query.BooleanQueryValue;
import org.eclipse.jnosql.communication.query.QueryValue;
import org.eclipse.jnosql.mapping.Converters;
import org.eclipse.jnosql.mapping.DynamicQueryException;
import org.eclipse.jnosql.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.mapping.util.ConverterUtil;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Collections.singletonList;

final class GraphQueryMethod {

    private final EntityMetadata mapping;
    private final GraphTraversal<Vertex, Vertex> traversal;
    private final Object[] args;
    private final Converters converters;
    private final Method method;
    private int counter = 0;

    GraphQueryMethod(EntityMetadata mapping,
                     GraphTraversal<Vertex, Vertex> traversal,
                     Converters converters, Method method, Object[] args) {
        this.mapping = mapping;
        this.traversal = traversal;
        this.args = args;
        this.converters = converters;
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public String getEntityName() {
        return mapping.name();
    }

    public EntityMetadata getMapping() {
        return mapping;
    }

    public GraphTraversal<Vertex, Vertex> getTraversal() {
        return traversal;
    }

    public Object getValue(String name, QueryValue<?> value) {
        if (value instanceof BooleanQueryValue) {
            return BooleanQueryValue.class.cast(value).get();
        }
        return ConverterUtil.getValue(getValue(), mapping, name, converters);
    }

    public Collection<?> getInValue(String name) {
        Object value = getValue();
        if (value instanceof Iterable<?>) {
            return (Collection<?>) StreamSupport.stream(((Iterable) value).spliterator(), false)
                    .map(v -> ConverterUtil.getValue(v, mapping, name, converters))
                    .collect(Collectors.toList());
        }
        return singletonList(ConverterUtil.getValue(value, mapping, name, converters));
    }

    private Object getValue() {
        if ((counter + 1) > args.length) {
            throw new DynamicQueryException(String.format("There is a missed argument in the method %s",
                    method));
        }
        Object value = args[counter];
        counter++;
        return value;
    }


}
