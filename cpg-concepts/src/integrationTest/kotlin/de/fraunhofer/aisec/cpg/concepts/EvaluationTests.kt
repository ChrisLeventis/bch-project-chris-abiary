/*
 * Copyright (c) 2025, Fraunhofer AISEC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *                    $$$$$$\  $$$$$$$\   $$$$$$\
 *                   $$  __$$\ $$  __$$\ $$  __$$\
 *                   $$ /  \__|$$ |  $$ |$$ /  \__|
 *                   $$ |      $$$$$$$  |$$ |$$$$\
 *                   $$ |      $$  ____/ $$ |\_$$ |
 *                   $$ |  $$\ $$ |      $$ |  $$ |
 *                   \$$$$$   |$$ |      \$$$$$   |
 *                    \______/ \__|       \______/
 *
 */
package de.fraunhofer.aisec.cpg.concepts

import de.fraunhofer.aisec.cpg.frontends.python.PythonLanguage
import de.fraunhofer.aisec.cpg.graph.*
import de.fraunhofer.aisec.cpg.graph.concepts.InputSource
import de.fraunhofer.aisec.cpg.graph.concepts.LoggingOperation
import de.fraunhofer.aisec.cpg.graph.concepts.PersistingOperation
import de.fraunhofer.aisec.cpg.graph.concepts.SendingOperation
import de.fraunhofer.aisec.cpg.graph.concepts.database.DatabasePass
import de.fraunhofer.aisec.cpg.graph.concepts.fileown.FilePass
import de.fraunhofer.aisec.cpg.graph.concepts.flaskinput.request.*
import de.fraunhofer.aisec.cpg.graph.concepts.http.HttpPass
import de.fraunhofer.aisec.cpg.graph.concepts.ownlogging.LoggingPass
import de.fraunhofer.aisec.cpg.graph.concepts.websockets.WebsocketPass
import de.fraunhofer.aisec.cpg.test.analyze
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class EvaluationTest {
    @Test
    fun sending1() {
        val topLevel = File("src/integrationTest/resources/evaluation/sendingEv/project1")
        val result =
            analyze(listOf(topLevel.resolve("websockets.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<WebsocketPass>()
                it.registerPass<HttpPass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths = // This variable was used to debug and check the found violations manually, to
            // cross-reference the manual audit
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is SendingOperation }.fulfilled
            }

        val violationsFoundCount = // this can be used to check the overall violations and
            // represents the actual rule
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is SendingOperation }.fulfilled
                }
                .count()

        assertEquals(
            0,
            violationsFoundCount,
        ) // this mocks a real world analysis of an application, it evaluated to true only if no
        // violations are found in an application
    }

    @Test
    fun sending2() {
        val topLevel = File("src/integrationTest/resources/evaluation/sendingEv/project2")
        val result =
            analyze(
                listOf(topLevel.resolve("api.py"), topLevel.resolve("tempCodeRunnerFile.py")),
                topLevel.toPath(),
                true,
            ) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<WebsocketPass>()
                it.registerPass<HttpPass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is SendingOperation }.fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is SendingOperation }.fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }

    @Test
    fun sending3() {
        val topLevel = File("src/integrationTest/resources/evaluation/sendingEv/project3")
        val result =
            analyze(listOf(topLevel.resolve("run.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<WebsocketPass>()
                it.registerPass<HttpPass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is SendingOperation }.fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is SendingOperation }.fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }

    @Test
    fun sending4() {
        val topLevel = File("src/integrationTest/resources/evaluation/sendingEv/project4")
        val result =
            analyze(listOf(topLevel.resolve("s.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<WebsocketPass>()
                it.registerPass<HttpPass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is SendingOperation }.fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is SendingOperation }.fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }

    @Test
    fun sending5() {
        val topLevel = File("src/integrationTest/resources/evaluation/sendingEv/project5")
        val result =
            analyze(
                listOf(
                    topLevel.resolve("app.py"),
                    topLevel.resolve("env.py"),
                    topLevel.resolve("manage.py"),
                    topLevel.resolve("models.py"),
                ),
                topLevel.toPath(),
                true,
            ) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<WebsocketPass>()
                it.registerPass<HttpPass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is SendingOperation }.fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is SendingOperation }.fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }

    @Test
    fun sending6() {
        val topLevel = File("src/integrationTest/resources/evaluation/sendingEv/project6")
        val result =
            analyze(listOf(topLevel.resolve("rio.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<WebsocketPass>()
                it.registerPass<HttpPass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is SendingOperation }.fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is SendingOperation }.fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }

    @Test
    fun persisting1() {
        val topLevel = File("src/integrationTest/resources/evaluation/persistingEv/project1")
        val result =
            analyze(
                listOf(
                    topLevel.resolve("app"),
                    topLevel.resolve("app.py"),
                    topLevel.resolve("config.py"),
                ),
                topLevel.toPath(),
                true,
            ) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<DatabasePass>()
                it.registerPass<FilePass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            calls.flatMap {
                it.followNextFullDFGEdgesUntilHit { node -> node is PersistingOperation }.fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextFullDFGEdgesUntilHit { node -> node is PersistingOperation }
                        .fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }

    @Test
    fun persisting2() {
        val topLevel = File("src/integrationTest/resources/evaluation/persistingEv/project2")
        val result =
            analyze(
                listOf(
                    topLevel.resolve("api.py"),
                    topLevel.resolve("env.py"),
                    topLevel.resolve("feff5c10d6d5_.py"),
                ),
                topLevel.toPath(),
                true,
            ) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<DatabasePass>()
                it.registerPass<FilePass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is PersistingOperation }.fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is PersistingOperation }.fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }

    @Test
    fun persisting3() {
        val topLevel = File("src/integrationTest/resources/evaluation/persistingEv/project3")
        val result =
            analyze(listOf(topLevel.resolve("app.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<DatabasePass>()
                it.registerPass<FilePass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is PersistingOperation }.fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is PersistingOperation }.fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }

    @Test
    fun persisting4() {
        val topLevel = File("src/integrationTest/resources/evaluation/persistingEv/project4")
        val result =
            analyze(
                listOf(topLevel.resolve("app.py"), topLevel.resolve("models.py")),
                topLevel.toPath(),
                true,
            ) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<DatabasePass>()
                it.registerPass<FilePass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is PersistingOperation }.fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is PersistingOperation }.fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }

    @Test
    fun persisting5() {
        val topLevel = File("src/integrationTest/resources/evaluation/persistingEv/project5")
        val result =
            analyze(
                listOf(topLevel.resolve("flask-backend"), topLevel.resolve("app.py")),
                topLevel.toPath(),
                true,
            ) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<DatabasePass>()
                it.registerPass<FilePass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is PersistingOperation }.fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is PersistingOperation }.fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }

    @Test
    fun persisting6() {
        val topLevel = File("src/integrationTest/resources/evaluation/persistingEv/project6")
        val result =
            analyze(listOf(topLevel.resolve("tn.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<DatabasePass>()
                it.registerPass<FilePass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is PersistingOperation }.fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is PersistingOperation }.fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }

    @Test
    fun logging1() {
        val topLevel = File("src/integrationTest/resources/evaluation/loggingEv/project1")
        val result =
            analyze(
                listOf(
                    topLevel.resolve("tracker.py"),
                    topLevel.resolve("p2p_node.py"),
                    topLevel.resolve("blockchain.py"),
                    topLevel.resolve("app.py"),
                ),
                topLevel.toPath(),
                true,
            ) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<LoggingPass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is LoggingOperation }.fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is LoggingOperation }.fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }

    @Test
    fun logging2() {
        val topLevel = File("src/integrationTest/resources/evaluation/loggingEv/project2")
        val result =
            analyze(
                listOf(
                    topLevel.resolve("bot.py"),
                    topLevel.resolve("loggerbot.py"),
                    topLevel.resolve("web.py"),
                ),
                topLevel.toPath(),
                true,
            ) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<LoggingPass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node ->
                        node is LoggingOperation || node is SendingOperation
                    }
                    .fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is LoggingOperation }.fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }

    @Test
    fun logging3() {
        val topLevel = File("src/integrationTest/resources/evaluation/loggingEv/project3")
        val result =
            analyze(
                listOf(
                    topLevel.resolve("app.py"),
                    topLevel.resolve("fonts.py"),
                    topLevel.resolve("test.py"),
                ),
                topLevel.toPath(),
                true,
            ) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<LoggingPass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is LoggingOperation }.fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is LoggingOperation }.fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }

    @Test
    fun logging4() {
        val topLevel = File("src/integrationTest/resources/evaluation/loggingEv/project4")
        val result =
            analyze(listOf(topLevel.resolve("web.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<LoggingPass>()
                it.registerPass<HttpPass>()
            }

        val inputNodes = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            inputNodes.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is LoggingOperation }.fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is LoggingOperation }.fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }

    @Test
    fun logging5() {
        val topLevel = File("src/integrationTest/resources/evaluation/loggingEv/project5")
        val result =
            analyze(
                listOf(
                    topLevel.resolve("run.py"),
                    topLevel.resolve("test_web.py"),
                    topLevel.resolve("web.py"),
                ),
                topLevel.toPath(),
                true,
            ) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<LoggingPass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node ->
                        node is LoggingOperation || node is SendingOperation
                    }
                    .fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is LoggingOperation }.fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }

    @Test
    fun logging6() {
        val topLevel = File("src/integrationTest/resources/evaluation/loggingEv/project6")
        val result =
            analyze(
                listOf(
                    topLevel.resolve("Token.py"),
                    topLevel.resolve("Dolar API"),
                    topLevel.resolve("Pruebas"),
                    topLevel.resolve("dolarBlue.py"),
                    topLevel.resolve("prueba.py"),
                ),
                topLevel.toPath(),
                true,
            ) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<LoggingPass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<InputSource>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node ->
                        node is LoggingOperation || node is SendingOperation
                    }
                    .fulfilled
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is LoggingOperation }.fulfilled
                }
                .count()

        assertEquals(0, violationsFoundCount)
    }
}
