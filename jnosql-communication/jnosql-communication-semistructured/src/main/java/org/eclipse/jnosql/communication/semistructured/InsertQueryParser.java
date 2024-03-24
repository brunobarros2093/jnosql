/*
 *  Copyright (c) 2024 Contributors to the Eclipse Foundation
 *   All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 * You may elect to redistribute this code under either of these licenses.
 *
 */
package org.eclipse.jnosql.communication.semistructured;


import org.eclipse.jnosql.communication.Params;
import org.eclipse.jnosql.communication.QueryException;
import org.eclipse.jnosql.communication.query.InsertQuery;
import org.eclipse.jnosql.communication.query.InsertQueryConverter;
import org.eclipse.jnosql.communication.query.JSONQueryValue;
import org.eclipse.jnosql.communication.query.QueryCondition;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

final class InsertQueryParser extends ConditionQueryParser {



    Stream<CommunicationEntity> query(String query, DatabaseManager manager, CommunicationObserverParser observer) {

        InsertQueryConverter converter = new InsertQueryConverter();
        InsertQuery insertQuery = converter.apply(query);

        String columnFamily = insertQuery.entity();
        Params params = Params.newParams();

        CommunicationEntity entity = getEntity(insertQuery, columnFamily, params, observer);

        Optional<Duration> ttl = insertQuery.ttl();
        if (params.isNotEmpty()) {
            throw new QueryException("To run a query with a parameter use a PrepareStatement instead.");
        }
        return ttl.map(duration -> Stream.of(manager.insert(entity, duration)))
                .orElseGet(() -> Stream.of(manager.insert(entity)));
    }


    CommunicationPreparedStatement prepare(String query, DatabaseManager manager,
                                           CommunicationObserverParser observer) {

        InsertQueryConverter converter = new InsertQueryConverter();
        InsertQuery insertQuery = converter.apply(query);

        String columnFamily = observer.fireEntity(insertQuery.entity());
        Params params = Params.newParams();

        Optional<Duration> ttl = insertQuery.ttl();
        CommunicationEntity entity = getEntity(insertQuery, columnFamily, params, observer);

        return CommunicationPreparedStatement.insert(entity, params, query, ttl.orElse(null), manager);

    }

    private CommunicationEntity getEntity(InsertQuery insertQuery, String columnFamily, Params params,
                                          CommunicationObserverParser observer) {
        return getEntity(new InsertQueryConditionSupplier(insertQuery), columnFamily, params, observer);
    }


    private record InsertQueryConditionSupplier(InsertQuery query) implements ConditionQuerySupplier {

        @Override
            public List<QueryCondition> conditions() {
                return query.conditions();
            }

            @Override
            public Optional<JSONQueryValue> value() {
                return query.value();
            }
        }

}
