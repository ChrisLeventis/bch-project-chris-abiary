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
package de.fraunhofer.aisec.cpg.graph.concepts.networkcomm.httpTmp

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.graph.*
import de.fraunhofer.aisec.cpg.graph.Component
import de.fraunhofer.aisec.cpg.graph.declarations.ImportDeclaration
import de.fraunhofer.aisec.cpg.graph.statements.expressions.CallExpression
import de.fraunhofer.aisec.cpg.passes.ComponentPass
import de.fraunhofer.aisec.cpg.passes.configuration.ExecuteLate

@ExecuteLate
class HttpPass(ctx: TranslationContext) : ComponentPass(ctx) {
    override fun cleanup() {}

    override fun accept(comp: Component) {
        comp.imports
            .filter {
                it.name.lastPartsMatch("requests") ||
                    it.name.lastPartsMatch("httpx") ||
                    it.name.lastPartsMatch("grequests")
            }
            .forEach { handleHttpClient(it) }

        comp.calls
            .filter { it.name.lastPartsMatch("post") }
            .forEach { handleHttpPostOp(it, comp.imports) }
        comp.calls
            .filter { it.name.lastPartsMatch("put") }
            .forEach { handleHttpPutOp(it, comp.imports) }
    }

    private fun handleHttpClient(import: ImportDeclaration) {
        newHttpClientNode(underlyingNode = import)
    }

    private fun handleHttpPostOp(call: CallExpression, list: List<ImportDeclaration>) {

        if (call.name.toString().equals("requests.post")) {
            val httpClientOverlay =
                list
                    .flatMap { it.overlays.filterIsInstance<HttpClienttmp>() }
                    .firstOrNull { it.name.toString().contains("requests") }

            httpClientOverlay?.let {
                newHttpOpPostNode(call, it, what = call.arguments.firstOrNull())
            }
        } else if (call.name.toString().contains("httpx")) {
            val httpClientOverlay =
                list
                    .flatMap { it.overlays.filterIsInstance<HttpClienttmp>() }
                    .firstOrNull { it.name.toString().contains("httpx") }

            httpClientOverlay?.let {
                newHttpOpPostNode(call, it, what = call.arguments.firstOrNull())
            }
        } else if (call.name.toString().equals("grequests.post")) {
            val httpClientOverlay =
                list
                    .flatMap { it.overlays.filterIsInstance<HttpClienttmp>() }
                    .firstOrNull { it.name.toString().contains("grequests") }

            httpClientOverlay?.let {
                newHttpOpPostNode(call, it, what = call.arguments.firstOrNull())
            }
        }
    }

    private fun handleHttpPutOp(call: CallExpression, list: List<ImportDeclaration>) {

        if (call.name.toString().equals("requests.put")) {
            val httpClientOverlay =
                list
                    .flatMap { it.overlays.filterIsInstance<HttpClienttmp>() }
                    .firstOrNull { it.name.toString().contains("requests") }

            httpClientOverlay?.let {
                newHttpOpPutNode(call, it, what = call.arguments.firstOrNull())
            }
        } else if (call.name.toString().contains("httpx")) {
            val httpClientOverlay =
                list
                    .flatMap { it.overlays.filterIsInstance<HttpClienttmp>() }
                    .firstOrNull { it.name.toString().contains("httpx") }

            httpClientOverlay?.let {
                newHttpOpPutNode(call, it, what = call.arguments.firstOrNull())
            }
        } else if (call.name.toString().equals("grequests.put")) {
            val httpClientOverlay =
                list
                    .flatMap { it.overlays.filterIsInstance<HttpClienttmp>() }
                    .firstOrNull { it.name.toString().contains("grequests") }

            httpClientOverlay?.let {
                newHttpOpPutNode(call, it, what = call.arguments.firstOrNull())
            }
        }
    }
}
