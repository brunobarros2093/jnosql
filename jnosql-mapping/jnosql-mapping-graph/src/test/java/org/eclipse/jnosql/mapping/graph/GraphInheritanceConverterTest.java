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
package org.eclipse.jnosql.mapping.graph;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.jnosql.mapping.graph.entities.inheritance.EmailNotification;
import org.eclipse.jnosql.mapping.graph.entities.inheritance.LargeProject;
import org.eclipse.jnosql.mapping.graph.entities.inheritance.Project;
import org.eclipse.jnosql.mapping.graph.entities.inheritance.SmallProject;
import org.eclipse.jnosql.mapping.graph.entities.inheritance.SmsNotification;
import org.eclipse.jnosql.mapping.graph.entities.inheritance.SocialMediaNotification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.smallrye.common.constraint.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class GraphInheritanceConverterTest {

    protected abstract Graph getGraph();

    protected abstract GraphConverter getConverter();

    @BeforeEach
    public void setUp() {
        getGraph().traversal().V().toList().forEach(Vertex::remove);
        getGraph().traversal().E().toList().forEach(Edge::remove);
    }

    @Test
    public void shouldConvertProjectToSmallProject() {
        Vertex vertex = getGraph().addVertex(T.label, "Project",
                "investor", "Otavio Santana",
                "size", "Small", "name",
                "Small Project");
        Project project = this.getConverter().toEntity(vertex);
        assertEquals("Small Project", project.getName());
        assertEquals(SmallProject.class, project.getClass());
        SmallProject smallProject = SmallProject.class.cast(project);
        assertEquals("Otavio Santana", smallProject.getInvestor());
    }

    @Test
    public void shouldConvertProjectToLargeProject() {
        Vertex vertex = getGraph().addVertex(T.label, "Project",
                "investor", "Otavio Santana",
                "size", "Large",
                "name", "Large Project",
                "budget", 10D);

        Project project = this.getConverter().toEntity(vertex);
        assertEquals("Large Project", project.getName());
        assertEquals(LargeProject.class, project.getClass());
        LargeProject smallProject = LargeProject.class.cast(project);
        assertEquals(10D, smallProject.getBudget());
    }

    @Test
    public void shouldConvertLargeProjectToCommunicationEntity() {
        LargeProject project = new LargeProject();
        project.setName("Large Project");
        project.setBudget(10D);
        Vertex entity = this.getConverter().toVertex(project);
        assertNotNull(entity);
        assertEquals("Project", entity.label());
        assertEquals(project.getName(), entity.property("name").value());
        assertEquals(project.getBudget(), entity.property("budget").value());
        assertEquals("Large", entity.property("size").value());
    }

    @Test
    public void shouldConvertSmallProjectToCommunicationEntity() {
        SmallProject project = new SmallProject();
        project.setName("Small Project");
        project.setInvestor("Otavio Santana");
        Vertex entity = this.getConverter().toVertex(project);
        assertNotNull(entity);
        assertEquals("Project", entity.label());
        assertEquals(project.getName(), entity.property("name").value());
        assertEquals(project.getInvestor(), entity.property("investor").value());
        assertEquals("Small", entity.property("size").value());
    }

    @Test
    public void shouldConvertDocumentEntityToSocialMedia(){
        LocalDate date = LocalDate.now();
        Vertex vertex = getGraph().addVertex(T.label, "Notification",
                "name", "Social Media",
                "nickname", "otaviojava",
                "dtype", SocialMediaNotification.class.getSimpleName(),
                "createdOn",date);

        SocialMediaNotification notification = this.getConverter().toEntity(vertex);
        Assertions.assertNotNull(notification.getId());
        assertEquals("Social Media", notification.getName());
        assertEquals("otaviojava", notification.getNickname());
        assertEquals(date, notification.getCreatedOn());
    }

    @Test
    public void shouldConvertDocumentEntityToSms(){
        LocalDate date = LocalDate.now();

        Vertex vertex = getGraph().addVertex(T.label, "Notification",
                "name", "SMS Notification",
                "phone", "+351987654123",
                "dtype", "SMS",
                "createdOn",date);

        SmsNotification notification = this.getConverter().toEntity(vertex);
        Assertions.assertNotNull(notification.getId());
        Assertions.assertEquals("SMS Notification", notification.getName());
        Assertions.assertEquals("+351987654123", notification.getPhone());
        assertEquals(date, notification.getCreatedOn());
    }

    @Test
    public void shouldConvertDocumentEntityToEmail(){
        LocalDate date = LocalDate.now();

        Vertex vertex = getGraph().addVertex(T.label, "Notification",
                "name", "SMS Notification",
                "email", "otavio@otavio.test",
                "dtype", "Email",
                "createdOn",date);

        EmailNotification notification = this.getConverter().toEntity(vertex);
        Assertions.assertNotNull(notification.getId());
        Assertions.assertEquals("SMS Notification", notification.getName());
        Assertions.assertEquals("otavio@otavio.test", notification.getEmail());
        assertEquals(date, notification.getCreatedOn());
    }

    @Test
    public void shouldConvertSocialMediaToCommunicationEntity(){
        SocialMediaNotification notification = new SocialMediaNotification();
        notification.setName("Social Media");
        notification.setCreatedOn(LocalDate.now());
        notification.setNickname("otaviojava");
        Vertex entity = this.getConverter().toVertex(notification);
        assertNotNull(entity);
        assertEquals("Notification", entity.label());
        assertEquals(notification.getName(), entity.property("name").value());
        assertEquals(notification.getNickname(),entity.property("nickname").value());
        assertEquals(notification.getCreatedOn(), entity.property("createdOn").value());
    }

    @Test
    public void shouldConvertSmsToCommunicationEntity(){
        SmsNotification notification = new SmsNotification();
        notification.setName("SMS");
        notification.setCreatedOn(LocalDate.now());
        notification.setPhone("+351123456987");
        Vertex entity = this.getConverter().toVertex(notification);
        assertNotNull(entity);
        assertEquals("Notification", entity.label());
        assertEquals(notification.getName(), entity.property("name").value());
        assertEquals(notification.getPhone(), entity.property("phone").value());
        assertEquals(notification.getCreatedOn(), entity.property("createdOn").value());
    }

    @Test
    public void shouldConvertEmailToCommunicationEntity(){
        EmailNotification notification = new EmailNotification();
        notification.setName("Email Media");
        notification.setCreatedOn(LocalDate.now());
        notification.setEmail("otavio@otavio.test.com");
        Vertex entity = this.getConverter().toVertex(notification);
        assertNotNull(entity);
        assertEquals("Notification", entity.label());
        assertEquals(notification.getName(),  entity.property("name").value());
        assertEquals(notification.getEmail(),  entity.property("email").value());
        assertEquals(notification.getCreatedOn(),  entity.property("createdOn").value());
    }

}
