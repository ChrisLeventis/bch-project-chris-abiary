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
import de.fraunhofer.aisec.cpg.graph.concepts.SendingOperation
import de.fraunhofer.aisec.cpg.graph.concepts.flaskinput.request.*
import de.fraunhofer.aisec.cpg.graph.concepts.networkcomm.httpTmp.HttpPass
import de.fraunhofer.aisec.cpg.graph.concepts.websockets.WebsocketPass
import de.fraunhofer.aisec.cpg.graph.statements.expressions.CallExpression
import de.fraunhofer.aisec.cpg.test.analyze
import java.io.File
import kotlin.test.Test

class EvaluationTest {
    @Test
    fun sending1() {
        val topLevel = File("src/integrationTest/resources/evaluation/sendingEv/project4")
        val result =
            analyze(listOf(topLevel.resolve("websockets.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<WebsocketPass>()
                it.registerPass<HttpPass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<RequestOp>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is SendingOperation }.fulfilled
            }
        val violations =
            paths.filter { list ->
                list.none { entry ->
                    entry is CallExpression && entry.name.toString().contains("encrypt")
                }
            }

        val list = 3
        val tmp = 1
    }

    @Test
    fun sending2() {
        val topLevel = File("src/integrationTest/resources/evaluation/sendingEv/project4")
        val result =
            analyze(listOf(topLevel.resolve("websockets.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<WebsocketPass>()
                it.registerPass<HttpPass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<RequestOp>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is SendingOperation }.fulfilled
            }
        val violations =
            paths.filter { list ->
                list.none { entry ->
                    entry is CallExpression && entry.name.toString().contains("encrypt")
                }
            }

        val list = 3
        val tmp = 1
    }
}
