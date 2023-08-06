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
package org.eclipse.jnosql.mapping.reflection;


import jakarta.nosql.Column;
import org.assertj.core.api.Assertions;
import org.eclipse.jnosql.mapping.Convert;
import org.eclipse.jnosql.mapping.Embeddable;
import org.eclipse.jnosql.mapping.VetedConverter;
import org.eclipse.jnosql.mapping.metadata.EntityMetadata;
import org.eclipse.jnosql.mapping.metadata.FieldMetadata;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.eclipse.jnosql.mapping.metadata.MappingType.COLLECTION;
import static org.eclipse.jnosql.mapping.metadata.MappingType.DEFAULT;
import static org.eclipse.jnosql.mapping.metadata.MappingType.EMBEDDED;
import static org.eclipse.jnosql.mapping.metadata.MappingType.MAP;
import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableAutoWeld
@AddPackages(value = Convert.class)
@AddPackages(value = VetedConverter.class)
@AddExtensions(EntityMetadataExtension.class)
public class FieldMetadataTest {


    @Inject
    private ClassConverter classConverter;

    @Test
    public void shouldReadDefaultField() {
        EntityMetadata entityMetadata = classConverter.create(ForClass.class);
        List<FieldMetadata> fields = entityMetadata.fields();

        FieldMetadata field = fields.stream()
                .filter(f -> "string".equals(f.fieldName())).findFirst().get();

        assertEquals("string", field.fieldName());
        assertEquals("stringTypeAnnotation", field.name());
        assertEquals(DEFAULT, field.mappingType());

    }

    @Test
    public void shouldReadCollectionField() {
        EntityMetadata entityMetadata = classConverter.create(ForClass.class);
        List<FieldMetadata> fields = entityMetadata.fields();
        FieldMetadata field = fields.stream()
                .filter(f -> "list".equals(f.fieldName())).findFirst().get();

        assertEquals("list", field.fieldName());
        assertEquals("listAnnotation", field.name());
        assertEquals(COLLECTION, field.mappingType());
    }

    @Test
    public void shouldReadMapField() {
        EntityMetadata entityMetadata = classConverter.create(ForClass.class);
        List<FieldMetadata> fields = entityMetadata.fields();
        FieldMetadata field = fields.stream()
                .filter(f -> "map".equals(f.fieldName())).findFirst().get();

        assertEquals("map", field.fieldName());
        assertEquals("mapAnnotation", field.name());
        assertEquals(MAP, field.mappingType());

    }

    @Test
    public void shouldReadEmbeddableField() {
        EntityMetadata entityMetadata = classConverter.create(ForClass.class);
        List<FieldMetadata> fields = entityMetadata.fields();
        FieldMetadata field = fields.stream()
                .filter(f -> "barClass".equals(f.fieldName())).findFirst().get();

        assertEquals("barClass", field.fieldName());
        assertEquals("barClass", field.name());
        assertEquals(EMBEDDED, field.mappingType());
    }

    @Test
    public void shouldUserFieldReader() {
        ForClass forClass = new ForClass();
        forClass.string = "text";
        forClass.list = Collections.singletonList("text");
        forClass.map = Collections.singletonMap("key", "value");
        forClass.barClass = new BarClass();
        forClass.barClass.integer = 10;

        EntityMetadata entityMetadata = classConverter.create(ForClass.class);

        FieldMetadata string = entityMetadata.fieldMapping("string").get();
        FieldMetadata list = entityMetadata.fieldMapping("list").get();
        FieldMetadata map = entityMetadata.fieldMapping("map").get();
        FieldMetadata barClass = entityMetadata.fieldMapping("barClass").get();

        assertEquals("text", string.read(forClass));
        assertEquals(forClass.list, list.read(forClass));
        assertEquals(forClass.map, map.read(forClass));
        assertEquals(forClass.barClass, barClass.read(forClass));

    }

    @Test
    public void shouldUserFieldWriter() {
        ForClass forClass = new ForClass();
        BarClass value = new BarClass();
        value.integer = 10;

        EntityMetadata entityMetadata = classConverter.create(ForClass.class);

        FieldMetadata string = entityMetadata.fieldMapping("string").get();
        FieldMetadata list = entityMetadata.fieldMapping("list").get();
        FieldMetadata map = entityMetadata.fieldMapping("map").get();
        FieldMetadata barClass = entityMetadata.fieldMapping("barClass").get();

        string.write(forClass, "text");
        list.write(forClass, Collections.singletonList("text"));
        map.write(forClass, Collections.singletonMap("key", "value"));
        barClass.write(forClass, value);

        assertEquals("text", string.read(forClass));
        assertEquals(forClass.list, list.read(forClass));
        assertEquals(forClass.map, map.read(forClass));
        assertEquals(forClass.barClass, barClass.read(forClass));
    }

    @Test
    public void shouldReadFromAnnotation(){
        EntityMetadata entityMetadata = classConverter.create(ForClass.class);
        List<FieldMetadata> fields = entityMetadata.fields();

        FieldMetadata field = fields.stream()
                .filter(f -> "string".equals(f.fieldName())).findFirst().get();

        var value = field.value(Custom.class);
        Assertions.assertThat(value)
                .isNotEmpty()
                .get().isEqualTo("customAnnotationValue");
    }

    @Test
    public void shouldReturnEmptyWhenThereIsNotAnnotation(){
        EntityMetadata entityMetadata = classConverter.create(ForClass.class);
        List<FieldMetadata> fields = entityMetadata.fields();

        FieldMetadata field = fields.stream()
                .filter(f -> "list".equals(f.fieldName())).findFirst().get();

        var value = field.value(Custom.class);
        Assertions.assertThat(value)
                .isEmpty();
    }

    @Test
    public void shouldReturnEmptyWhenThereIsValueMethod(){
        EntityMetadata entityMetadata = classConverter.create(ForClass.class);
        List<FieldMetadata> fields = entityMetadata.fields();

        FieldMetadata field = fields.stream()
                .filter(f -> "list".equals(f.fieldName())).findFirst().get();

        var value = field.value(Custom2.class);
        Assertions.assertThat(value)
                .isEmpty();
    }


    @Test
    public void shouldReturnEmptyWhenThereIsValueMethod2(){
        EntityMetadata entityMetadata = classConverter.create(ForClass.class);
        List<FieldMetadata> fields = entityMetadata.fields();

        FieldMetadata field = fields.stream()
                .filter(f -> "map".equals(f.fieldName())).findFirst().get();

        var value = field.value(Custom3.class);
        Assertions.assertThat(value)
                .isEmpty();
    }


    public static class ForClass {

        @Column("stringTypeAnnotation")
        @Custom("customAnnotationValue")
        private String string;

        @Column("listAnnotation")
        @Custom2
        private List<String> list;

        @Column("mapAnnotation")
        @Custom3
        private Map<String, String> map;


        @Column
        private BarClass barClass;
    }

    @Embeddable
    public static class BarClass {

        @Column("integerAnnotation")
        private Integer integer;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    public @interface Custom{
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    public @interface Custom2 {
    }

    public @interface Custom3 {
    }

}
