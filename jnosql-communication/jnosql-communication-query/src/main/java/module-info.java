/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *  You may elect to redistribute this code under either of these licenses.
 *  Contributors:
 *  Otavio Santana
 */
module org.eclipse.jnosql.communication.query {
    requires org.antlr.antlr4.runtime;
    requires jakarta.json;
    requires org.eclipse.jnosql.communication.core;
    requires jakarta.data;

    exports org.eclipse.jnosql.communication.query.method;
    exports org.eclipse.jnosql.communication.query;

    opens org.eclipse.jnosql.communication.query;
    opens org.eclipse.jnosql.communication.query.method;


}