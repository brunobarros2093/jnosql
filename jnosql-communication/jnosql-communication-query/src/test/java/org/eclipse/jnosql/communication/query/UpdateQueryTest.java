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

package org.eclipse.jnosql.communication.query;

import org.eclipse.jnosql.communication.QueryException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.eclipse.jnosql.query.grammar.QueryBaseListener;
import org.eclipse.jnosql.query.grammar.QueryLexer;
import org.eclipse.jnosql.query.grammar.QueryParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateQueryTest {

    @ParameterizedTest
    @ArgumentsSource(UpdateQueryArgumentProvider.class)
    void shouldExecuteQuery(String query) {
        testQuery(query);
    }

    @Test
    void shouldIgnoreComments() {
        testQuery("//ignore this line \n update Person (name = \"Ada Lovelace\")");
    }

    @ParameterizedTest
    @ArgumentsSource(WrongUpdateQueryArgumentProvider.class)
    void shouldNotExecute(String query) {
        Assertions.assertThrows(QueryException.class, () -> testQuery(query));
    }

    @Test
    void shouldCreateFromMethodFactory() {
        UpdateQuery query = UpdateQuery.parse("update Person (name = \"Ada Lovelace\")");
        Assertions.assertNotNull(query);
    }

    @Test
    void shouldEquals() {
        String text = "update Person (name = \"Ada Lovelace\")";
        UpdateQuery query = UpdateQuery.parse(text);
        Assertions.assertEquals(query, UpdateQuery.parse(text));
        Assertions.assertEquals(query, query);
        Assertions.assertNotEquals(query, text);
    }

    @Test
    void shouldHashCode() {
        String text = "update Person (name = \"Ada Lovelace\")";
        UpdateQuery query = UpdateQuery.parse(text);
        Assertions.assertEquals(query.hashCode(), UpdateQuery.parse(text).hashCode());
    }

    @Test
    void shouldToString(){
        String text = "update Person (name = \"Ada Lovelace\")";
        UpdateQuery query = UpdateQuery.parse(text);
        assertThat(query.toString())
                .isEqualTo("update Person ([name EQUALS 'Ada Lovelace']) ");
    }

    private void testQuery(String query) {
        CharStream stream = CharStreams.fromString(query);
        QueryLexer lexer = new QueryLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        QueryParser parser = new QueryParser(tokens);
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        lexer.addErrorListener(QueryErrorListener.INSTANCE);
        parser.addErrorListener(QueryErrorListener.INSTANCE);

        ParseTree tree = parser.update();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new QueryBaseListener(), tree);


    }


}
