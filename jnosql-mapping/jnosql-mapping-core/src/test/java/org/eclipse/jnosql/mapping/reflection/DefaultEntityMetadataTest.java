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

import jakarta.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.eclipse.jnosql.mapping.Convert;
import org.eclipse.jnosql.mapping.VetedConverter;
import org.eclipse.jnosql.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.mapping.metadata.FieldMetadata;
import org.eclipse.jnosql.mapping.test.entities.Person;
import org.eclipse.jnosql.mapping.test.entities.inheritance.Notification;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@EnableAutoWeld
@AddPackages(value = Convert.class)
@AddPackages(value = VetedConverter.class)
@AddExtensions(EntityMetadataExtension.class)
class DefaultEntityMetadataTest {

    @Inject
    private ClassConverter converter;


    @Test
    public void shouldToString(){
        EntityMetadata entityMetadata = converter.create(Person.class);
        assertThat(entityMetadata.toString()).isNotBlank();
    }

    @Test
    public void shouldCreateInstance(){
        EntityMetadata entityMetadata = converter.create(Person.class);
        Object person = entityMetadata.newInstance();
        assertThat(person).isNotNull().isInstanceOf(Person.class);
    }

    @Test
    public void shouldGetId(){
        EntityMetadata entityMetadata = converter.create(Person.class);
        Optional<FieldMetadata> id = entityMetadata.id();
        assertThat(id).isNotEmpty();
        FieldMetadata fieldMetadata = id.orElseThrow();
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(fieldMetadata.isId()).isTrue();
            soft.assertThat(fieldMetadata.name()).isEqualTo("_id");
            soft.assertThat(fieldMetadata.fieldName()).isEqualTo("id");
        });
    }

    @Test
    public void shouldIsInheritance(){
        EntityMetadata entityMetadata = converter.create(Person.class);
       assertThat(entityMetadata.isInheritance()).isFalse();

        EntityMetadata entityMetadata2 = converter.create(Notification.class);
        assertThat(entityMetadata2.isInheritance()).isTrue();
    }

    @Test
    public void shouldGroupByName(){
        EntityMetadata entityMetadata = converter.create(Person.class);
        Map<String, FieldMetadata> map = entityMetadata.fieldsGroupByName();

        assertThat(map).isNotEmpty()
                .hasSize(4)
                .containsKeys("_id", "name", "age", "phones");
    }
}