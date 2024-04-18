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
package org.eclipse.jnosql.mapping.core.query;

import jakarta.data.Order;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.CrudRepository;
import jakarta.nosql.Template;
import org.eclipse.jnosql.mapping.NoSQLRepository;
import org.eclipse.jnosql.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.mapping.metadata.FieldMetadata;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.eclipse.jnosql.mapping.IdNotFoundException.KEY_NOT_FOUND_EXCEPTION_SUPPLIER;

/**
 * An abstract template class providing a base implementation for repositories managing entities
 * through Jakarta Data's {@link NoSQLRepository} and {@link CrudRepository} interfaces.
 * This class encapsulates common CRUD (Create, Read, Update, Delete) operations and supports pagination
 * for a specific entity type. Subclasses are required to implement certain abstract methods to customize
 * behavior for a particular database or data model.
 *
 * @param <T> The entity type managed by this repository.
 * @param <K> The type of the entity's primary key.
 */
public abstract class AbstractRepository<T, K> implements NoSQLRepository<T, K> {

    /**
     * Retrieves the template associated with this repository.
     *
     * @return The template used for database operations.
     */
    protected abstract Template template();

    /**
     * Retrieves the metadata information about the entity managed by this repository.
     *
     * @return The entity metadata information.
     */
    protected abstract EntityMetadata entityMetadata();

    /**
     * Retrieves the Class object representing the entity type managed by this repository.
     *
     * @return The Class object of the entity type.
     */
    @SuppressWarnings("unchecked")
    protected Class<T> type() {
        return (Class<T>) entityMetadata().type();
    }

    /**
     * Converts an entity ID to a Stream of entities, wrapping it in an Optional.
     *
     * @return A function that converts an entity ID to a Stream of entities.
     */
    protected Function<K, Stream<T>> optionalToStream() {
        return id -> {
            Optional<T> entity = this.findById(id);
            return entity.stream();
        };
    }

    /**
     * Retrieves the metadata information about the ID field of the entity.
     *
     * @return The metadata information about the ID field.
     * @throws org.eclipse.jnosql.mapping.IdNotFoundException if the ID field metadata is not found.
     */
    protected FieldMetadata getIdField() {
        return entityMetadata().id().orElseThrow(KEY_NOT_FOUND_EXCEPTION_SUPPLIER);
    }

    /**
     * Retrieves the error message template for unsupported repository methods.
     *
     * @return The error message template.
     */
    protected String getErrorMessage(){
        return "The AbstractRepository does not support %s method";
    }

    @Override
    public <S extends T> S save(S entity) {
        Objects.requireNonNull(entity, "Entity is required");
        Object id = getIdField().read(entity);
        if (nonNull(id) && existsById((K) id)) {
            return template().update(entity);
        } else {
            return template().insert(entity);
        }
    }

    @Override
    public <S extends T> List<S> saveAll(List<S> entities) {
        requireNonNull(entities, "entities is required");
        return entities.stream().map(this::save).collect(toList());
    }


    @Override
    public void deleteById(K id) {
        requireNonNull(id, "is is required");
        template().delete(type(), id);
    }

    @Override
    public void deleteByIdIn(Iterable<K> ids) {
        requireNonNull(ids, "ids is required");
        ids.forEach(this::deleteById);
    }

    @Override
    public Optional<T> findById(K id) {
        requireNonNull(id, "id is required");
        return template().find(type(), id);
    }


    @Override
    public Stream<T> findByIdIn(Iterable<K> ids) {
        requireNonNull(ids, "ids is required");
        return stream(ids.spliterator(), false)
                .flatMap(optionalToStream());
    }

    @Override
    public boolean existsById(K id) {
        return findById(id).isPresent();
    }

    @Override
    public void delete(T entity) {
        Objects.requireNonNull(entity, "entity is required");
        EntityMetadata metadata = entityMetadata();
        FieldMetadata id = metadata.id().orElseThrow(KEY_NOT_FOUND_EXCEPTION_SUPPLIER);
        template().delete(metadata.type(), id.read(entity));
    }

    @Override
    public void deleteAll(List<? extends T>  entities) {
        Objects.requireNonNull(entities, "entities is required");
        entities.forEach(this::delete);
    }

    @Override
    public <S extends T> S insert(S entity) {
        Objects.requireNonNull(entity, "entity is required");
        return template().insert(entity);
    }

    @Override
    public <S extends T> List<S> insertAll(List<S> entities) {
        Objects.requireNonNull(entities, "entities is required");
        return stream(template().insert(entities).spliterator(), false)
                .toList();
    }

    @Override
    public <S extends T> S update(S entity) {
        Objects.requireNonNull(entity, "entity is required");
        return template().update(entity);
    }

    @Override
    public <S extends T> List<S> updateAll(List<S> entities) {
        Objects.requireNonNull(entities, "entities is required");
        return stream(template().update(entities).spliterator(), false)
                .toList();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException(String.format(getErrorMessage(), "deleteAll"));
    }

    @Override
    public long countBy() {
        throw new UnsupportedOperationException(String.format(getErrorMessage(), "count"));
    }

    @Override
    public Page<T> findAll(PageRequest pageRequest, Order<T> order) {
        throw new UnsupportedOperationException(String.format(getErrorMessage(), "findAll"));
    }

    @Override
    public Stream<T> findAll() {
        throw new UnsupportedOperationException(String.format(getErrorMessage(), "findAll"));
    }


}