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
package org.eclipse.jnosql.mapping.core.query;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 * Enumeration representing the operations with annotations.
 * Each operation defines a specific behavior when invoked.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public enum AnnotationOperation {

    /**
     * Represents the {@link jakarta.data.repository.Insert} operation.
     */
    INSERT {
        @Override
        public Object invoke(Operation operation) {
            checkParameterNumber(operation);
            Object param = operation.params[0];
            ReturnType returnType = new ReturnType(operation.method);
            if (param instanceof Iterable entities) {
                Iterable<?> result = operation.repository.insertAll(entities);
                return returnInsert(returnType, result);
            } else if (param.getClass().isArray()) {
                Iterable<?> result = operation.repository.insertAll(Arrays.asList((Object[]) param));
                return returnInsert(returnType, result);
            } else {
                var result = operation.repository.insert(param);
                return returnInsert(returnType, result);
            }
        }

        private static Object returnInsert(ReturnType returnType, Object result) {
            if(returnType.isVoid()){
                return Void.TYPE;
            }else if(returnType.isInt()){
                return 1;
            } else if(returnType.isLong()){
                return 1L;
            }else {
                return result;
            }
        }
    },
    /**
     * Represents the {@link jakarta.data.repository.Update} operation.
     */
    UPDATE {
        @Override
        public Object invoke(Operation operation) {
            checkParameterNumber(operation);
            Object param = operation.params[0];
            ReturnType returnType = new ReturnType(operation.method);
            if (param instanceof Iterable entities) {
                return executeIterable(operation, entities, returnType, false, null);
            } else if (param.getClass().isArray()) {
                List<Object> entities = Arrays.asList((Object[]) param);
                return executeIterable(operation, entities, returnType, true, param);
            } else {
                return executeSingleEntity(operation, param, returnType);
            }
        }

        private static Object executeIterable(Operation operation, Iterable entities, ReturnType returnType,
                                              boolean isArray, Object param) {
            int count = operation.repository.updateAll(entities);
            if (returnType.isVoid()) {
                return Void.TYPE;
            } else if (returnType.isBoolean()) {
                return true;
            } else if (returnType.isInt()) {
                return count;
            } else if(returnType.isLong()){
                return (long) count;
            }else if (isArray) {
                return param;
            } else {
                return entities;
            }
        }

        private static Object executeSingleEntity(Operation operation, Object param, ReturnType returnType) {
            boolean result = operation.repository.update(param);
            if (returnType.isVoid()) {
                return Void.TYPE;
            } else if (returnType.isBoolean()) {
                return result;
            } else if (returnType.isInt()) {
                return 1;
            } else if (returnType.isLong()) {
                return 1L;
            } else {
                return param;
            }
        }
    },
    /**
     * Represents the {@link jakarta.data.repository.Delete} operation.
     */
    DELETE {
        @Override
        public Object invoke(Operation operation) {
            checkParameterNumber(operation);
            Object param = operation.params[0];
            ReturnType returnType = new ReturnType(operation.method);
            if (param instanceof Iterable entities) {
                return executeIterable(operation, entities, returnType);
            } else if (param.getClass().isArray()) {
                List<Object> entities = Arrays.asList((Object[]) param);
                return executeIterable(operation, entities, returnType);
            } else {
                return executeSingleEntity(operation, param, returnType);
            }
        }

        private static Object executeIterable(Operation operation, Iterable entities, ReturnType returnType) {

            operation.repository.deleteAll(entities);
            if (returnType.isVoid()) {
                return Void.TYPE;
            } else if (returnType.isBoolean()) {
                return true;
            } else if (returnType.isInt()) {
                return (int) StreamSupport.stream(entities.spliterator(), false).count();
            } else if (returnType.isLong()) {
                return StreamSupport.stream(entities.spliterator(), false).count();
            }
            return null;
        }

        private static Object executeSingleEntity(Operation operation, Object param, ReturnType returnType) {
            operation.repository.delete(param);
            if (returnType.isVoid()) {
                return Void.TYPE;
            } else if (returnType.isBoolean()) {
                return true;
            } else if (returnType.isInt()) {
                return 1;
            }else if (returnType.isLong()) {
                return 1L;
            }
            return null;
        }
    },
    /**
     * Represents the {@link jakarta.data.repository.Save} operation.
     */
    SAVE {
        @Override
        public Object invoke(Operation operation) {
            checkParameterNumber(operation);
            Object param = operation.params[0];
            ReturnType returnType = new ReturnType(operation.method);
            if (param instanceof Iterable entities) {
                Iterable<?> result = operation.repository.saveAll(entities);
                return returnType.isVoid() ? Void.TYPE : result;
            } else if (param.getClass().isArray()) {
                Iterable<?> result = operation.repository.saveAll(Arrays.asList((Object[]) param));
                return returnType.isVoid() ? Void.TYPE : result;
            } else {
                Object result = operation.repository.save(param);
                return returnType.isVoid() ? Void.TYPE : result;
            }
        }
    };

    private static void checkParameterNumber(Operation operation) {
        if (operation.params.length != 1) {
            throw new UnsupportedOperationException("The method operation requires one parameter, please check the method: "
                    + operation.method);
        }
    }

    public abstract Object invoke(Operation operation);

    public record Operation(Method method, Object[] params, AbstractRepository repository) {
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Operation operation = (Operation) o;
            return Objects.equals(method, operation.method)
                    && Arrays.equals(params, operation.params)
                    && Objects.equals(repository, operation.repository);
        }



        @Override
        public int hashCode() {
          return Objects.hash(method, repository) + 31 * Arrays.hashCode(params);
        }

        @Override
        public String toString() {
            return "Operation{" +
                    "method=" + method +
                    ", params=" + Arrays.toString(params) +
                    ", repository=" + repository +
                    '}';
        }
    }


    private record ReturnType(Method method) {

        boolean isVoid() {
            return method.getReturnType().equals(Void.TYPE);
        }

        boolean isBoolean() {
            return method.getReturnType().equals(Boolean.class)
                    || method.getReturnType().equals(Boolean.TYPE);
        }

        boolean isInt() {
            return method.getReturnType().equals(Integer.class)
                    || method.getReturnType().equals(Integer.TYPE);
        }

        public boolean isLong() {
            return method.getReturnType().equals(Long.class)
                    || method.getReturnType().equals(Long.TYPE);
        }
    }
}
