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
import de.fraunhofer.aisec.cpg.graph.concepts.Operation
import de.fraunhofer.aisec.cpg.graph.concepts.SendingOperation
import de.fraunhofer.aisec.cpg.graph.concepts.database.Database
import de.fraunhofer.aisec.cpg.graph.concepts.database.DatabaseOpAdd
import de.fraunhofer.aisec.cpg.graph.concepts.database.DatabaseOperation
import de.fraunhofer.aisec.cpg.graph.concepts.database.DatabasePass
import de.fraunhofer.aisec.cpg.graph.concepts.fileown.FilePass
import de.fraunhofer.aisec.cpg.graph.concepts.flaskinput.ResourceObjectNode
import de.fraunhofer.aisec.cpg.graph.concepts.flaskinput.ResourceObjectPass
import de.fraunhofer.aisec.cpg.graph.concepts.flaskinput.request.*
import de.fraunhofer.aisec.cpg.graph.concepts.networkcomm.httpTmp.HttpClienttmp
import de.fraunhofer.aisec.cpg.graph.concepts.networkcomm.httpTmp.HttpOp
import de.fraunhofer.aisec.cpg.graph.concepts.networkcomm.httpTmp.HttpPass
import de.fraunhofer.aisec.cpg.graph.concepts.ownlogging.LogOp
import de.fraunhofer.aisec.cpg.graph.concepts.ownlogging.LoggingPass
import de.fraunhofer.aisec.cpg.graph.concepts.websockets.WebsocketOp
import de.fraunhofer.aisec.cpg.graph.concepts.websockets.WebsocketPass
import de.fraunhofer.aisec.cpg.graph.declarations.FunctionDeclaration
import de.fraunhofer.aisec.cpg.graph.statements.expressions.CallExpression
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Literal
import de.fraunhofer.aisec.cpg.test.analyze
import java.io.File
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DatabaseTest {
    @Test
    fun testDBSimple() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("wholeTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<DatabasePass>()
            }

        assertNotNull(result)

        val db = result.conceptNodes.filterIsInstance<Database>().singleOrNull()
        assertNotNull(db)

        val add = result.operationNodes.filterIsInstance<DatabaseOpAdd>().singleOrNull()
        assertNotNull(add)

        val cpgCall = db.underlyingNode
        assertIs<CallExpression>(cpgCall)

        val df = cpgCall.followNextDFGEdgesUntilHit { it is DatabaseOpAdd }.fulfilled.singleOrNull()
        assertNotNull(df)
    }

    @Test
    fun testLog() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("logging.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<LoggingPass>()
            }
        val calls = result.calls
        assertNotNull(result)
    }

    @Test
    fun testPost() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("postHTTP.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<HttpPass>()
            }
        val calls = result.calls
        val post = result.calls.filter { it.name.lastPartsMatch("post") }.firstOrNull()
        val list =
            calls[0].followPrevEOGEdgesUntilHit {
                it.overlays.filterIsInstance<HttpClienttmp>().isNotEmpty()
            }

        val mem = result.mcalls
        assertNotNull(result)
    }

    @Test
    fun testrequest() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("requestObject.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
            }
        val calls = result.calls
        val overlays = result.overlays
        val imports = result.imports

        val mem = result.mcalls
        assertNotNull(result)
    }

    @Test
    fun testrequestDataflow() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("requestDataflow.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<LoggingPass>()
            }
        val calls = result.calls[0]
        val overlay = calls.overlays.first()
        val ful = overlay.followNextDFGEdgesUntilHit { node: Node -> node is LogOp }
        val mem = result.mcalls
        assertNotNull(result)
    }

    @Test
    fun testAnnotation() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("annotationTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
            }
        val calls = result.calls

        assertNotNull(result)
    }

    @Test
    fun testAnnotationForRequest() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("annotationForRequest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
            }
        val calls = result.calls.first().overlays

        assertNotNull(result)
    }

    @Test
    fun testResourceObject() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("annotationForRequest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<ResourceObjectPass>()
            }
        val calls = result.calls
        val overlays = result.overlays
        assertNotNull(result)
    }

    @Test
    fun testWholeDB() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("wholeTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<DatabasePass>()
            }
        val calls = result.calls
        val overlayRO = result.allChildren<Operation>()
        val first = overlayRO.first()
        val paths = first.followNextDFGEdgesUntilHit { node -> node is DatabaseOperation }

        assertNotNull(result)
    }

    @Test
    fun testSmallDB() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("sqlalchemy.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<DatabasePass>()
            }
        val calls = result.calls
        val overlayRO = result.allChildren<Operation>()
        val first = overlayRO.first()
        val paths = first.followNextDFGEdgesUntilHit { node -> node is DatabaseOperation }

        assertNotNull(result)
    }

    @Test
    fun testWholeHTTP() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("wholeTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<HttpPass>()
            }
        val calls = result.calls
        val overlayRO = result.allChildren<Operation>()
        val first = overlayRO.first()
        val paths = first.followNextDFGEdgesUntilHit { node -> node is DatabaseOperation }

        assertNotNull(result)
    }

    @Test
    fun testWholeLogger() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("wholeTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<LoggingPass>()
            }
        val calls = result.calls
        val overlayRO = result.allChildren<Operation>()
        val first = overlayRO.first()
        val paths = first.followNextDFGEdgesUntilHit { node -> node is DatabaseOperation }

        assertNotNull(result)
    }

    @Test
    fun testWholeRequest() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("wholeTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HTTPRequestAccessForm>() }

        val tmp = 11
        assertTrue(calls.isNotEmpty())
    }

    @Test
    fun testWholeRO() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("wholeTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<ResourceObjectPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<ResourceObjectNode>() }

        val tmp = 11
        assertTrue(calls.isNotEmpty())
    }

    @Test
    fun testWholeApplicationDB() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("wholeTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<DatabasePass>()
                it.registerPass<ResourceObjectPass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<ResourceObjectNode>() }
        val first = calls.first()
        val paths = first.followNextDFGEdgesUntilHit { node -> node is DatabaseOperation }

        assertTrue(paths.fulfilled.isNotEmpty())
    }

    @Test
    fun testWholeApplicationLogger() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("wholeTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<LoggingPass>()
                it.registerPass<ResourceObjectPass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<ResourceObjectNode>() }
        val first = calls.first()
        val paths = first.followNextDFGEdgesUntilHit { node -> node is LogOp }

        assertTrue(paths.fulfilled.isNotEmpty())
    }

    @Test
    fun testWholeApplicationHTTP() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("wholeTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<HttpPass>()
                it.registerPass<ResourceObjectPass>()
            }

        val calls = result.calls.flatMap { it.overlays.filterIsInstance<ResourceObjectNode>() }
        val first = calls.first()
        val paths = first.followNextDFGEdgesUntilHit { node -> node is HttpOp }
        val function =
            result.calls
                .filter { it.location!!.region.startLine == 30 }
                .singleOrNull()
                ?.followXUntilHit({ currennode, _, _ -> listOf(currennode.astParent!!) }) {
                    it is FunctionDeclaration
                }
        val tmp = 1
        assertTrue(paths.fulfilled.isNotEmpty())
    }

    @Test
    fun testHttpx() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("httpxPost.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<HttpPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HTTPRequestAccessForm>() }

        val tmp = 11
        assertTrue(calls.isNotEmpty())
    }

    @Test
    fun testHttpxWhole() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("wholeTestHtpx.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<HttpPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HttpOp>() }

        val tmp = 11
        assertTrue(calls.isNotEmpty())
    }

    @Test
    fun testHttpxAndRequests() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("wholeTestHtpx.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<HttpPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HttpOp>() }

        val tmp = 11
        assertTrue(calls.isNotEmpty())
    }

    @Test
    fun testGrequests() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("grequests.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<HttpPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HttpOp>() }

        val tmp = 11
        assertTrue(calls.isNotEmpty())
    }

    @Test
    fun testGrequestsAndGrequets() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("wholeTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<HttpPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HttpOp>() }

        val tmp = 11
        assertTrue(calls.isNotEmpty())
    }

    @Test
    fun testGetJson() {
        val topLevel = File("src/integrationTest/resources/evaluation")
        val result =
            analyze(listOf(topLevel.resolve("evaluation1.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HTTPRequestAccessJson>() }

        val tmp = 11
        assertTrue(calls.isNotEmpty())
    }

    @Test
    fun testGetPath() {
        val topLevel = File("src/integrationTest/resources/evaluation")
        val result =
            analyze(listOf(topLevel.resolve("evaluation2.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<DatabasePass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HTTPRequestAccess>() }
        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is DatabaseOperation }.failed
            }

        val list = 3
        val tmp = 1
    }

    @Test
    fun testGetPath2() {
        val topLevel = File("src/integrationTest/resources/evaluation")
        val result =
            analyze(listOf(topLevel.resolve("evaluation3.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<RequestObjectPass>()
                it.registerPass<DatabasePass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HTTPRequestAccess>() }
        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is DatabaseOperation }.fulfilled
            }
        val tmp = 1
    }

    @Test
    fun testWebsocket() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("websocket3.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<WebsocketPass>()
                it.registerPass<RequestObjectPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HTTPRequestAccess>() }
        val matchingCalls =
            calls.filter {
                (it.key as? Literal<*>)?.value.toString() == "secret" ||
                    (it.key as? Literal<*>)?.value.toString() == "lastname"
            }

        val paths =
            matchingCalls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is WebsocketOp }.fulfilled
            }

        val list = 3
        val tmp = 1
    }

    @Test
    fun testNotSendt() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("wholeTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<WebsocketPass>()
                it.registerPass<HttpPass>()
                it.registerPass<RequestObjectPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HTTPRequestAccess>() }
        val matchingCalls = calls.filter { (it.key as? Literal<*>)?.value.toString() == "secret" }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is DatabaseOperation }.fulfilled
            }
        val list = 3
        val tmp = 1
    }

    @Test
    fun testNotDatabse() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("wholeTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<DatabasePass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HTTPRequestAccess>() }
        val matchingCalls = calls.filter { (it.key as? Literal<*>)?.value.toString() == "secret" }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is DatabaseOperation }.fulfilled
            }
        val list = 3
        val tmp = 1
    }

    // Sending with encrypt sanitization
    @Test
    fun testNoSendWithEncrypt() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("encryptTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<HttpPass>()
                it.registerPass<WebsocketPass>()
                it.registerPass<RequestObjectPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HTTPRequestAccess>() }
        val matchingCalls =
            calls.filter {
                (it.key as? Literal<*>)?.value.toString() == "secret" ||
                    (it.key as? Literal<*>)?.value.toString() == "temperature"
            }

        val paths =
            matchingCalls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is HttpOp || node is WebsocketOp }
                    .fulfilled
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
    fun testNoSendWithEncrypt2() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("encryptTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<HttpPass>()
                it.registerPass<WebsocketPass>()
                it.registerPass<RequestObjectPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HTTPRequestAccess>() }
        val matchingCalls =
            calls.filter {
                (it.key as? Literal<*>)?.value.toString() == "secret" ||
                    (it.key as? Literal<*>)?.value.toString() == "temperature"
            }

        val paths =
            matchingCalls.flatMap {
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
    fun testNoLoggingWithEncrypt() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("encryptTest2.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<LoggingPass>()
                it.registerPass<RequestObjectPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HTTPRequestAccess>() }
        val matchingCalls =
            calls.filter {
                (it.key as? Literal<*>)?.value.toString() == "secret" ||
                    (it.key as? Literal<*>)?.value.toString() == "temperature"
            }

        val paths =
            matchingCalls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is LogOp }.fulfilled
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
    fun testNoSavingWithEncrypt() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("encryptedTest3.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<DatabasePass>()
                it.registerPass<RequestObjectPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HTTPRequestAccess>() }
        val matchingCalls =
            calls.filter {
                (it.key as? Literal<*>)?.value.toString() == "secret" ||
                    (it.key as? Literal<*>)?.value.toString() == "temperature"
            }

        val paths =
            matchingCalls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is DatabaseOperation }.fulfilled
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
    fun testNoSavingEvaluation() {
        val topLevel = File("src/integrationTest/resources/evaluation/textEv")
        val result =
            analyze(
                listOf(
                    topLevel.resolve("something.py"),
                    topLevel.resolve("models.py"),
                    topLevel.resolve("routes.py"),
                ),
                topLevel.toPath(),
                true,
            ) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<DatabasePass>()
                it.registerPass<RequestObjectPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HTTPRequestAccess>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is DatabaseOperation }.fulfilled
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
    fun testNoSavingEvaluation2() {
        val topLevel = File("src/integrationTest/resources/evaluation/testEv2")
        val result =
            analyze(
                listOf(topLevel.resolve("app.py"), topLevel.resolve("gpt.py")),
                topLevel.toPath(),
                true,
            ) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<DatabasePass>()
                it.registerPass<RequestObjectPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HTTPRequestAccess>() }

        val paths =
            calls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is DatabaseOperation }.fulfilled
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
    fun testFile() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("wholeTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<FilePass>()
            }

        val list = 3
        val tmp = 1
    }

    @Test
    fun testDbError() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("sqlalchemyerror.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<DatabasePass>()
            }

        val list = 3
        val tmp = 1
    }

    @Test
    fun testinit() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("carollus.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<DatabasePass>()
                it.registerPass<RequestObjectPass>()
            }
        val calls = result.calls.flatMap { it.overlays.filterIsInstance<HTTPRequestAccess>() }
        val matchingCalls =
            calls.filter {
                (it.key as? Literal<*>)?.value.toString() == "secret" ||
                    (it.key as? Literal<*>)?.value.toString() == "temperature"
            }

        val paths =
            matchingCalls.flatMap {
                it.followNextDFGEdgesUntilHit { node -> node is DatabaseOperation }.fulfilled
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
    fun testDepictionOfRUles() {
        val topLevel = File("src/integrationTest/resources/python")
        val result =
            analyze(listOf(topLevel.resolve("wholeTest.py")), topLevel.toPath(), true) {
                it.registerLanguage<PythonLanguage>()
                it.registerPass<DatabasePass>()
                it.registerPass<HttpPass>()
                it.registerPass<RequestObjectPass>()
            }

        val violationsFoundCount =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<InputSource>() }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is SendingOperation }.fulfilled
                }
                .count()

        val potentialViolationsFound1 =
            result
                .allChildren<Node>()
                .flatMap { it.overlays.filterIsInstance<HTTPRequestAccess>() }
                .filter { (it.key as? Literal<*>)?.value.toString() == "secret" }
                .flatMap {
                    it.followNextDFGEdgesUntilHit { node -> node is SendingOperation }.fulfilled
                }
                .filter { list ->
                    list.none { entry ->
                        entry is CallExpression && entry.name.toString().contains("encrypt")
                    }
                }
                .count()

        val list = 3
        val tmp = 1
    }
}
