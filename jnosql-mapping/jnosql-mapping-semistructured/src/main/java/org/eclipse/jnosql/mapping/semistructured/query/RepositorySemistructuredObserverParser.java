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

import org.eclipse.jnosql.communication.semistructured.CommunicationObserverParser;
import org.eclipse.jnosql.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.mapping.core.repository.RepositoryObserverParser;

import java.util.Objects;

/**
 * The {@link CommunicationObserverParser} to {@link RepositoryObserverParser}
 */
public final class RepositorySemistructuredObserverParser implements CommunicationObserverParser {

    private final RepositoryObserverParser parser;

    RepositorySemistructuredObserverParser(EntityMetadata entityMetadata) {
        this.parser = RepositoryObserverParser.of(entityMetadata);
    }

    @Override
    public String fireEntity(String entity) {
        return parser.name();
    }

    @Override
    public String fireField(String entity, String field) {
        return parser.field(field);
    }

    /**
     * @return RepositoryColumnObserverParser
     * throws NullPointerException if entityMetadata is null
     */
    public static CommunicationObserverParser of(EntityMetadata entityMetadata) {
        Objects.requireNonNull(entityMetadata, "entityMetadata is required");
        return new RepositorySemistructuredObserverParser(entityMetadata);
    }
}
