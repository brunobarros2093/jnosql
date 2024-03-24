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
package org.eclipse.jnosql.mapping.keyvalue.configuration;

import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.Value;
import org.eclipse.jnosql.communication.keyvalue.BucketManager;
import org.eclipse.jnosql.communication.keyvalue.BucketManagerFactory;
import org.eclipse.jnosql.communication.keyvalue.KeyValueConfiguration;
import org.eclipse.jnosql.communication.keyvalue.KeyValueEntity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class KeyValueConfigurationMock implements KeyValueConfiguration {


    @Override
    public BucketManagerFactory apply(Settings settings) {
        return new BucketManagerFactoryMock(settings);
    }


    public record BucketManagerFactoryMock(Settings settings) implements BucketManagerFactory {

        @Override
        public BucketManagerMock apply(String bucketName) {
            return new BucketManagerMock(bucketName);
        }

        @Override
        public <T> List<T> getList(String bucketName, Class<T> type) {
            return new ArrayList<>();
        }

        @Override
        public <T> Set<T> getSet(String bucketName, Class<T> type) {
            return new HashSet<>();
        }

        @Override
        public <T> Queue<T> getQueue(String bucketName, Class<T> type) {
            return new LinkedList<>();
        }

        @Override
        public <K, V> Map<K, V> getMap(String bucketName, Class<K> keyValue, Class<V> valueValue) {
            return new HashMap<>();
        }

        @Override
        public void close() {

        }
    }

    public record BucketManagerMock(String name) implements BucketManager {
        @Override
        public <K, V> void put(K key, V value) {

        }

        @Override
        public void put(KeyValueEntity entity) {

        }

        @Override
        public void put(KeyValueEntity entity, Duration ttl) {

        }

        @Override
        public void put(Iterable<KeyValueEntity> entities) {

        }

        @Override
        public void put(Iterable<KeyValueEntity> entities, Duration ttl) {

        }

        @Override
        public <K> Optional<Value> get(K key) {
            return Optional.empty();
        }

        @Override
        public <K> Iterable<Value> get(Iterable<K> keys) {
            return null;
        }

        @Override
        public <K> void delete(K key) {

        }

        @Override
        public <K> void delete(Iterable<K> keys) {

        }

        @Override
        public void close() {

        }
    }
}
