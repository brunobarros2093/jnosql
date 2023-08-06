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
package org.eclipse.jnosql.mapping.spi;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.PassivationCapable;
import org.eclipse.jnosql.mapping.Convert;
import org.eclipse.jnosql.mapping.VetedConverter;
import org.eclipse.jnosql.mapping.reflection.EntityMetadataExtension;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AbstractBeanTest {



    @Test
    public void shouldGetInjectionPoints() {
        // Create the AbstractBean instance
        AbstractBean<Object> abstractBean = getInstance();

        // Call getInjectionPoints
        Set<InjectionPoint> injectionPoints = abstractBean.getInjectionPoints();

        // Verify that the returned injectionPoints set is empty
        assertEquals(Collections.emptySet(), injectionPoints);
    }

    @Test
    public void shouldReturnScope() {
        // Create the AbstractBean instance
        AbstractBean<Object> abstractBean = getInstance();

        // Call getScope
        Class<? extends Annotation> scope = abstractBean.getScope();

        // Verify that the returned scope is ApplicationScoped.class
        assertEquals(ApplicationScoped.class, scope);
    }

    @Test
    public void shouldReturnNameAsNull() {
        // Create the AbstractBean instance
        AbstractBean<Object> abstractBean = getInstance();

        // Call getName
        String name = abstractBean.getName();

        // Verify that the returned name is null
        assertEquals(null, name);
    }

    @Test
    public void shouldReturnEmptyStereotypes() {
        // Create the AbstractBean instance
        AbstractBean<Object> abstractBean = getInstance();

        // Call getStereotypes
        Set<Class<? extends Annotation>> stereotypes = abstractBean.getStereotypes();

        // Verify that the returned set of stereotypes is empty
        assertEquals(Collections.emptySet(), stereotypes);
    }

    @Test
    public void shouldReturnIsAlternativeAsFalse() {
        // Create the AbstractBean instance
        AbstractBean<Object> abstractBean = getInstance();

        // Call isAlternative
        boolean isAlternative = abstractBean.isAlternative();

        // Verify that the returned isAlternative is false
        assertEquals(false, isAlternative);
    }


    private AbstractBean<Object> getInstance(){
        return new AbstractBean<Object>() {
            @Override
            public Class<?> getBeanClass() {
                return null;
            }

            @Override
            public Object create(CreationalContext<Object> creationalContext) {
                return null;
            }

            @Override
            public Set<Type> getTypes() {
                return null;
            }

            @Override
            public Set<Annotation> getQualifiers() {
                return null;
            }

            @Override
            public String getId() {
                return null;
            }
        };
    }
}
