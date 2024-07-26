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
package org.eclipse.jnosql.mapping.semistructured;

import jakarta.data.exceptions.NonUniqueResultException;
import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;
import jakarta.data.page.impl.CursoredPageRecord;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.eclipse.jnosql.mapping.PreparedStatement;
import org.assertj.core.api.SoftAssertions;
import org.eclipse.jnosql.communication.semistructured.CommunicationEntity;
import org.eclipse.jnosql.communication.semistructured.CriteriaCondition;
import org.eclipse.jnosql.communication.semistructured.DatabaseManager;
import org.eclipse.jnosql.communication.semistructured.DeleteQuery;
import org.eclipse.jnosql.communication.semistructured.Element;
import org.eclipse.jnosql.communication.semistructured.SelectQuery;
import org.eclipse.jnosql.mapping.core.Converters;
import org.eclipse.jnosql.mapping.IdNotFoundException;
import org.eclipse.jnosql.mapping.semistructured.entities.Job;
import org.eclipse.jnosql.mapping.semistructured.entities.Person;
import org.eclipse.jnosql.mapping.metadata.EntitiesMetadata;
import org.eclipse.jnosql.mapping.reflection.Reflections;
import org.eclipse.jnosql.mapping.core.spi.EntityMetadataExtension;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.eclipse.jnosql.communication.semistructured.DeleteQuery.delete;
import static org.eclipse.jnosql.communication.semistructured.SelectQuery.select;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@EnableAutoWeld
@AddPackages(value = {Converters.class, EntityConverter.class})
@AddPackages(MockProducer.class)
@AddPackages(Reflections.class)
@AddExtensions({EntityMetadataExtension.class})
class DefaultSemiStructuredTemplateTest {

    private final Person person = Person.builder().
            withAge().
            withPhones(Arrays.asList("234", "432")).
            withName("Name")
            .withId(19)
            .withIgnore().build();

    private final Element[] columns = new Element[]{
            Element.of("age", 10),
            Element.of("phones", Arrays.asList("234", "432")),
            Element.of("name", "Name"),
            Element.of("id", 19L),
    };

    @Inject
    private EntityConverter converter;

    @Inject
    private EntitiesMetadata entities;

    @Inject
    private Converters converters;

    private DatabaseManager managerMock;

    private DefaultSemiStructuredTemplate template;

    private ArgumentCaptor<CommunicationEntity> captor;

    private EventPersistManager eventPersistManager;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        managerMock = Mockito.mock(DatabaseManager.class);
        eventPersistManager = Mockito.mock(EventPersistManager.class);
        captor = ArgumentCaptor.forClass(CommunicationEntity.class);
        Instance<DatabaseManager> instance = Mockito.mock(Instance.class);
        Mockito.when(instance.get()).thenReturn(managerMock);
        this.template = new DefaultSemiStructuredTemplate(converter, instance,
                eventPersistManager, entities, converters);
    }

    @Test
    void shouldInsert() {
        CommunicationEntity columnEntity = CommunicationEntity.of("Person");
        columnEntity.addAll(Stream.of(columns).collect(Collectors.toList()));

        Mockito.when(managerMock
                .insert(any(CommunicationEntity.class)))
                .thenReturn(columnEntity);

        template.insert(this.person);
        verify(managerMock).insert(captor.capture());
        verify(eventPersistManager).firePostEntity(any(Person.class));
        verify(eventPersistManager).firePreEntity(any(Person.class));
        CommunicationEntity value = captor.getValue();
        assertEquals("Person", value.name());
        assertEquals(4, value.elements().size());
    }


    @Test
    void shouldMergeOnInsert() {
        CommunicationEntity columnEntity = CommunicationEntity.of("Person");
        columnEntity.addAll(Stream.of(columns).collect(Collectors.toList()));

        Mockito.when(managerMock
                .insert(any(CommunicationEntity.class)))
                .thenReturn(columnEntity);

        Person person = Person.builder().build();
        Person result = template.insert(person);
        verify(managerMock).insert(captor.capture());
        verify(eventPersistManager).firePostEntity(any(Person.class));
        verify(eventPersistManager).firePreEntity(any(Person.class));
        assertSame(person, result);
       assertEquals(10, person.getAge());

    }




    @Test
    void shouldInsertTTL() {
        CommunicationEntity columnEntity = CommunicationEntity.of("Person");
        columnEntity.addAll(Stream.of(columns).collect(Collectors.toList()));

        Mockito.when(managerMock
                .insert(any(CommunicationEntity.class),
                        any(Duration.class)))
                .thenReturn(columnEntity);

        template.insert(this.person, Duration.ofHours(2));
        verify(managerMock).insert(captor.capture(), Mockito.eq(Duration.ofHours(2)));
        verify(eventPersistManager).firePostEntity(any(Person.class));
        verify(eventPersistManager).firePreEntity(any(Person.class));
        CommunicationEntity value = captor.getValue();
        assertEquals("Person", value.name());
        assertEquals(4, value.elements().size());
    }

    @Test
    void shouldUpdate() {
        CommunicationEntity columnEntity = CommunicationEntity.of("Person");
        columnEntity.addAll(Stream.of(columns).collect(Collectors.toList()));

        Mockito.when(managerMock
                .update(any(CommunicationEntity.class)))
                .thenReturn(columnEntity);

        template.update(this.person);
        verify(managerMock).update(captor.capture());
        verify(eventPersistManager).firePostEntity(any(Person.class));
        verify(eventPersistManager).firePreEntity(any(Person.class));
        CommunicationEntity value = captor.getValue();
        assertEquals("Person", value.name());
        assertEquals(4, value.elements().size());
    }

    @Test
    void shouldMergeOnUpdate() {
        CommunicationEntity columnEntity = CommunicationEntity.of("Person");
        columnEntity.addAll(Stream.of(columns).collect(Collectors.toList()));

        Mockito.when(managerMock
                .update(any(CommunicationEntity.class)))
                .thenReturn(columnEntity);

        Person person = Person.builder().build();
        Person result = template.update(person);
        verify(managerMock).update(captor.capture());
        verify(eventPersistManager).firePostEntity(any(Person.class));
        verify(eventPersistManager).firePreEntity(any(Person.class));
        assertSame(person, result);
        assertEquals(10, person.getAge());

    }

    @Test
    void shouldInsertEntitiesTTL() {
        CommunicationEntity columnEntity = CommunicationEntity.of("Person");
        columnEntity.addAll(Stream.of(columns).collect(Collectors.toList()));
        Duration duration = Duration.ofHours(2);

        Mockito.when(managerMock
                .insert(any(CommunicationEntity.class), Mockito.eq(duration)))
                .thenReturn(columnEntity);

        template.insert(Arrays.asList(person, person), duration);
        verify(managerMock, times(2)).insert(any(CommunicationEntity.class), any(Duration.class));
    }

    @Test
    void shouldInsertEntities() {
        CommunicationEntity columnEntity = CommunicationEntity.of("Person");
        columnEntity.addAll(Stream.of(columns).collect(Collectors.toList()));

        Mockito.when(managerMock
                .insert(any(CommunicationEntity.class)))
                .thenReturn(columnEntity);

        template.insert(Arrays.asList(person, person));
        verify(managerMock, times(2)).insert(any(CommunicationEntity.class));
    }

    @Test
    void shouldUpdateEntities() {
        CommunicationEntity columnEntity = CommunicationEntity.of("Person");
        columnEntity.addAll(Stream.of(columns).collect(Collectors.toList()));

        Mockito.when(managerMock
                .update(any(CommunicationEntity.class)))
                .thenReturn(columnEntity);

        template.update(Arrays.asList(person, person));
        verify(managerMock, times(2)).update(any(CommunicationEntity.class));
    }

    @Test
    void shouldDelete() {

        DeleteQuery query = delete().from("delete").build();
        template.delete(query);
        verify(managerMock).delete(query);
    }

    @Test
    void shouldSelect() {
        SelectQuery query = select().from("person").build();
        template.select(query);
        verify(managerMock).select(query);
    }

    @Test
    void shouldCountBy() {
        SelectQuery query = select().from("person").build();
        template.count(query);
        verify(managerMock).count(query);
    }

    @Test
    void shouldExist() {
        SelectQuery query = select().from("person").build();
        template.exists(query);
        verify(managerMock).exists(query);
    }

    @Test
    void shouldReturnSingleResult() {
        CommunicationEntity columnEntity = CommunicationEntity.of("Person");
        columnEntity.addAll(Stream.of(columns).collect(Collectors.toList()));

        Mockito.when(managerMock
                .select(any(SelectQuery.class)))
                .thenReturn(Stream.of(columnEntity));

        SelectQuery query = select().from("person").build();

        Optional<Person> result = template.singleResult(query);
        assertTrue(result.isPresent());
    }

    @Test
    void shouldReturnSingleResultIsEmpty() {
        Mockito.when(managerMock
                .select(any(SelectQuery.class)))
                .thenReturn(Stream.empty());

        SelectQuery query = select().from("person").build();

        Optional<Person> result = template.singleResult(query);
        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnSingleResultQuery() {
        CommunicationEntity columnEntity = CommunicationEntity.of("Person");
        columnEntity.addAll(Stream.of(columns).collect(Collectors.toList()));

        Mockito.when(managerMock
                        .select(any(SelectQuery.class)))
                .thenReturn(Stream.of(columnEntity));

        Optional<Person> result = template.singleResult("from Person");
        assertTrue(result.isPresent());
    }

    @Test
    void shouldReturnSingleResultQueryIsEmpty() {
        Mockito.when(managerMock
                        .select(any(SelectQuery.class)))
                .thenReturn(Stream.empty());

        Optional<Person> result = template.singleResult("from Person");
        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnErrorWhenThereMoreThanASingleResult() {
        Assertions.assertThrows(NonUniqueResultException.class, () -> {
            CommunicationEntity columnEntity = CommunicationEntity.of("Person");
            columnEntity.addAll(Stream.of(columns).collect(Collectors.toList()));

            Mockito.when(managerMock
                    .select(any(SelectQuery.class)))
                    .thenReturn(Stream.of(columnEntity, columnEntity));

            SelectQuery query = select().from("person").build();

            template.singleResult(query);
        });
    }


    @Test
    void shouldReturnErrorWhenFindIdHasIdNull() {
        Assertions.assertThrows(NullPointerException.class, () -> template.find(Person.class, null));
    }

    @Test
    void shouldReturnErrorWhenFindIdHasClassNull() {
        Assertions.assertThrows(NullPointerException.class, () -> template.find(null, "10"));
    }

    @Test
    void shouldReturnErrorWhenThereIsNotIdInFind() {
        Assertions.assertThrows(IdNotFoundException.class, () -> template.find(Job.class, "10"));
    }

    @Test
    void shouldReturnFind() {
        template.find(Person.class, "10");
        ArgumentCaptor<SelectQuery> queryCaptor = ArgumentCaptor.forClass(SelectQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        SelectQuery query = queryCaptor.getValue();
        CriteriaCondition condition = query.condition().get();

        assertEquals("Person", query.name());
        assertEquals(CriteriaCondition.eq(Element.of("_id", 10L)), condition);
    }

    @Test
    void shouldDeleteEntity() {
        template.delete(Person.class, "10");
        ArgumentCaptor<DeleteQuery> queryCaptor = ArgumentCaptor.forClass(DeleteQuery.class);
        verify(managerMock).delete(queryCaptor.capture());

        DeleteQuery query = queryCaptor.getValue();

        CriteriaCondition condition = query.condition().get();

        assertEquals("Person", query.name());
        assertEquals(CriteriaCondition.eq(Element.of("_id", 10L)), condition);
    }

    @Test
    void shouldExecuteQuery() {
        template.query("FROM Person");
        ArgumentCaptor<SelectQuery> queryCaptor = ArgumentCaptor.forClass(SelectQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        SelectQuery query = queryCaptor.getValue();
        assertEquals("Person", query.name());
    }

    @Test
    void shouldExecuteQueryEntity() {
        template.query("FROM Person", "Person");
        ArgumentCaptor<SelectQuery> queryCaptor = ArgumentCaptor.forClass(SelectQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        SelectQuery query = queryCaptor.getValue();
        assertEquals("Person", query.name());
    }


    @Test
    void shouldConvertEntity() {
        template.query("FROM Movie");
        ArgumentCaptor<SelectQuery> queryCaptor = ArgumentCaptor.forClass(SelectQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        SelectQuery query = queryCaptor.getValue();
        assertEquals("movie", query.name());
    }

    @Test
    void shouldConvertEntityName() {
        template.query("SELECT name FROM download");
        ArgumentCaptor<SelectQuery> queryCaptor = ArgumentCaptor.forClass(SelectQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        SelectQuery query = queryCaptor.getValue();
        assertEquals("download", query.name());
    }
    @Test
    void shouldConvertEntityNameClassName() {
        template.query("FROM " + Person.class.getSimpleName());
        ArgumentCaptor<SelectQuery> queryCaptor = ArgumentCaptor.forClass(SelectQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        SelectQuery query = queryCaptor.getValue();
        assertEquals("Person", query.name());
    }

    @Test
    void shouldConvertConvertFromAnnotationEntity(){
        template.query("FROM Vendor" );
        ArgumentCaptor<SelectQuery> queryCaptor = ArgumentCaptor.forClass(SelectQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        SelectQuery query = queryCaptor.getValue();
        assertEquals("vendors", query.name());
    }

    @Test
    void shouldPreparedStatement() {
        PreparedStatement preparedStatement = template.prepare("FROM Person WHERE name = :name");
        preparedStatement.bind("name", "Ada");
        preparedStatement.result();
        ArgumentCaptor<SelectQuery> queryCaptor = ArgumentCaptor.forClass(SelectQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        SelectQuery query = queryCaptor.getValue();
        assertEquals("Person", query.name());
    }

    @Test
    void shouldPreparedStatementEntity() {
        PreparedStatement preparedStatement = template.prepare("FROM Person WHERE name = :name", "Person");
        preparedStatement.bind("name", "Ada");
        preparedStatement.result();
        ArgumentCaptor<SelectQuery> queryCaptor = ArgumentCaptor.forClass(SelectQuery.class);
        verify(managerMock).select(queryCaptor.capture());
        SelectQuery query = queryCaptor.getValue();
        assertEquals("Person", query.name());
    }

    @Test
    void shouldCount() {
        template.count("Person");
        verify(managerMock).count("Person");
    }

    @Test
    void shouldCountFromEntityClass() {
        template.count(Person.class);
        var captor = ArgumentCaptor.forClass(SelectQuery.class);
        verify(managerMock).count(captor.capture());
        var query = captor.getValue();
        SoftAssertions.assertSoftly(soft ->{
            soft.assertThat(query.condition()).isEmpty();
        });
    }


    @Test
    void shouldFindAll(){
        template.findAll(Person.class);
        verify(managerMock).select(select().from("Person").build());
    }

    @Test
    void shouldDeleteAll(){
        template.deleteAll(Person.class);
        verify(managerMock).delete(delete().from("Person").build());
    }

    @Test
    void shouldSelectCursor(){
        PageRequest request = PageRequest.ofSize(2);

        PageRequest afterKey = PageRequest.afterCursor(PageRequest.Cursor.forKey("Ada"), 1, 2, false);
        SelectQuery query = select().from("Person").orderBy("name").asc().build();

        Mockito.when(managerMock.selectCursor(query, request))
                .thenReturn(new CursoredPageRecord<>(content(),
                        Collections.emptyList(), -1, request, afterKey, null));

        PageRequest personRequest = PageRequest.ofSize(2);
        CursoredPage<Person> result = template.selectCursor(query, personRequest);

        SoftAssertions.assertSoftly(soft ->{
            soft.assertThat(result).isNotNull();
            soft.assertThat(result.content()).hasSize(1);
            soft.assertThat(result.hasNext()).isTrue();
            Person person = result.stream().findFirst().orElseThrow();

            soft.assertThat(person.getAge()).isEqualTo(10);
            soft.assertThat(person.getName()).isEqualTo("Name");
            soft.assertThat(person.getPhones()).containsExactly("234", "432");
        });

    }


    private List<CommunicationEntity> content(){
        CommunicationEntity columnEntity = CommunicationEntity.of("Person");
        columnEntity.addAll(Stream.of(columns).collect(Collectors.toList()));
        return List.of(columnEntity);
    }
}
