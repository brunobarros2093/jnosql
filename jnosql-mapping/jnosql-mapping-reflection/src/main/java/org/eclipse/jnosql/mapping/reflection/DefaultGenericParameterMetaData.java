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
package org.eclipse.jnosql.mapping.reflection;

import org.eclipse.jnosql.communication.TypeSupplier;
import jakarta.nosql.AttributeConverter;
import org.eclipse.jnosql.mapping.metadata.CollectionSupplier;
import org.eclipse.jnosql.mapping.metadata.GenericParameterMetaData;
import org.eclipse.jnosql.mapping.metadata.MappingType;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.ServiceLoader;

class DefaultGenericParameterMetaData  extends DefaultParameterMetaData implements GenericParameterMetaData {


    private final TypeSupplier<?> typeSupplier;

    DefaultGenericParameterMetaData(String name, Class<?> type, boolean id,
                             Class<? extends AttributeConverter<?, ?>> converter,
                             MappingType mappingType, TypeSupplier<?> typeSupplier) {
        super(name, type, id, converter, mappingType);
        this.typeSupplier = typeSupplier;
    }

    public Class<?> elementType() {
        return (Class<?>) ((ParameterizedType) typeSupplier.get()).getActualTypeArguments()[0];
    }

    public Collection<?> collectionInstance() {
        Class<?> type =  type();
        final CollectionSupplier supplier = ServiceLoader.load(CollectionSupplier.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .map(CollectionSupplier.class::cast)
                .filter(c -> c.test(type))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("This collection is not supported yet: " + type));
        return (Collection<?>) supplier.get();
    }

}
