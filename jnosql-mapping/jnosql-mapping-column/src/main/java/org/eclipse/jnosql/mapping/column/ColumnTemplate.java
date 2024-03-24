/*
 *
 *  Copyright (c) 2024 Contributors to the Eclipse Foundation
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
 *
 */
package org.eclipse.jnosql.mapping.column;



import org.eclipse.jnosql.mapping.semistructured.SemistructuredTemplate;


/**
 * This interface extends the {@link SemistructuredTemplate} and represents a template for performing
 * column-based operations in a semistructured database.
 * <p>
 * Implementations of this interface provide methods for CRUD (Create, Read, Update, Delete) operations
 * on column-based data structures within a semistructured database.
 * </p>
 * <p>
 * Users can utilize implementations of this interface to interact with column-based data in a semistructured database,
 * abstracting away the complexities of the underlying database operations and providing a unified API for database access.
 * </p>
 *
 * @see SemistructuredTemplate
 */
public interface ColumnTemplate extends SemistructuredTemplate {


}
