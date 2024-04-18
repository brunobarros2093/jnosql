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

import org.eclipse.jnosql.communication.CommunicationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DatabaseConfigurationTest {



    @Test
    void shouldGetErrorConfiguration(){
        assertThatThrownBy(DatabaseConfiguration::getConfiguration)
                .isInstanceOf(CommunicationException.class);
    }

    @Test
    void shouldGetErrorConfigurationClass(){
        assertThatThrownBy(() -> DatabaseConfiguration.getConfiguration(DatabaseMock.class))
                .isInstanceOf(CommunicationException.class);
    }
}