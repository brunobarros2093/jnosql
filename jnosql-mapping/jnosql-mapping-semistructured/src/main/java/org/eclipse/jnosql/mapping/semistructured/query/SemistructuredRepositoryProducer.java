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
package org.eclipse.jnosql.mapping.semistructured.query;

import jakarta.data.repository.BasicRepository;
import org.eclipse.jnosql.communication.semistructured.DatabaseManager;
import org.eclipse.jnosql.mapping.core.Converters;
import org.eclipse.jnosql.mapping.semistructured.ColumnTemplateProducer;
import org.eclipse.jnosql.mapping.semistructured.SemistructuredTemplate;
import org.eclipse.jnosql.mapping.metadata.EntitiesMetadata;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * The producer of Repository
 */
@ApplicationScoped
public class SemistructuredRepositoryProducer {

    @Inject
    private EntitiesMetadata entities;

    @Inject
    private Converters converters;

    @Inject
    private ColumnTemplateProducer producer;

    /**
     * Produces a Repository class from repository class and {@link DatabaseManager}
     *
     * @param repositoryClass the repository class
     * @param manager         the manager
     * @param <T>             the entity of repository
     * @param <K>             the K of the entity
     * @param <R>             the repository type
     * @return a Repository interface
     * @throws NullPointerException when there is null parameter
     */
    public <T, K, R extends BasicRepository<T, K>> R get(Class<R> repositoryClass, DatabaseManager manager) {
        Objects.requireNonNull(repositoryClass, "repository class is required");
        Objects.requireNonNull(manager, "manager class is required");
        SemistructuredTemplate template = producer.apply(manager);
        return get(repositoryClass, template);
    }

    /**
     * Produces a Repository class from repository class and {@link DatabaseManager}
     *
     * @param repositoryClass the repository class
     * @param template        the template
     * @param <T>             the entity of repository
     * @param <K>             the K of the entity
     * @param <R>             the repository type
     * @return a Repository interface
     * @throws NullPointerException when there is null parameter
     */
    @SuppressWarnings("unchecked")
    public <T, K, R extends BasicRepository<T, K>> R get(Class<R> repositoryClass, SemistructuredTemplate template) {
        Objects.requireNonNull(repositoryClass, "repository class is required");
        Objects.requireNonNull(template, "template class is required");

        SemistructuredRepositoryProxy<T, K> handler = new SemistructuredRepositoryProxy<>(template,
                entities, repositoryClass, converters);
        return (R) Proxy.newProxyInstance(repositoryClass.getClassLoader(),
                new Class[]{repositoryClass},
                handler);
    }
}
