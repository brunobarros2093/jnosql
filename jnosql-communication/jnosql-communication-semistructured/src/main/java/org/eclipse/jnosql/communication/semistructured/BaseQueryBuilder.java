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


import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

abstract class BaseQueryBuilder {

    protected String name;

    protected boolean negate;

    protected boolean and;

    protected CriteriaCondition condition;

    protected <T> void eqImpl(T value) {
        requireNonNull(value, "value is required");
        CriteriaCondition newCondition = CriteriaCondition.eq(Element.of(name, value));
        appendCondition(newCondition);
    }

    protected <T> void gtImpl(T value) {
        requireNonNull(value, "value is required");
        CriteriaCondition newCondition = CriteriaCondition.gt(Element.of(name, value));
        appendCondition(newCondition);
    }

    protected void likeImpl(String value) {
        requireNonNull(value, "value is required");
        CriteriaCondition newCondition = CriteriaCondition.like(Element.of(name, value));
        appendCondition(newCondition);
    }

    protected <T> void ltImpl(T value) {
        requireNonNull(value, "value is required");
        CriteriaCondition newCondition = CriteriaCondition.lt(Element.of(name, value));
        appendCondition(newCondition);
    }

    protected <T> void lteImpl(T value) {
        requireNonNull(value, "value is required");
        CriteriaCondition newCondition = CriteriaCondition.lte(Element.of(name, value));
        appendCondition(newCondition);
    }

    protected <T> void gteImpl(T value) {
        requireNonNull(value, "value is required");
        CriteriaCondition newCondition = CriteriaCondition.gte(Element.of(name, value));
        appendCondition(newCondition);
    }

    protected <T> void betweenImpl(T valueA, T valueB) {
        requireNonNull(valueA, "valueA is required");
        requireNonNull(valueB, "valueB is required");
        CriteriaCondition newCondition = CriteriaCondition.between(Element.of(name, asList(valueA, valueB)));
        appendCondition(newCondition);
    }

    protected <T> void inImpl(Iterable<T> values) {
        requireNonNull(values, "values is required");
        CriteriaCondition newCondition = CriteriaCondition.in(Element.of(name, values));
        appendCondition(newCondition);
    }


    protected void appendCondition(CriteriaCondition newCondition) {

        CriteriaCondition criteriaCondition = getColumnCondition(newCondition);

        if (nonNull(condition)) {
            if (and) {
                this.condition = condition.and(criteriaCondition);
            } else {
                this.condition = condition.or(criteriaCondition);
            }
        } else {
            this.condition = criteriaCondition;
        }
        this.negate = false;
        this.name = null;
    }

    private CriteriaCondition getColumnCondition(CriteriaCondition newCondition) {
        if (negate) {
            return newCondition.negate();
        } else {
            return newCondition;
        }
    }
}
