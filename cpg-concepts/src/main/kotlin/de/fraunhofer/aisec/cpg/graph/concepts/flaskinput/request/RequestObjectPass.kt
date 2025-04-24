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
package de.fraunhofer.aisec.cpg.graph.concepts.flaskinput.request

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.graph.*
import de.fraunhofer.aisec.cpg.graph.declarations.FunctionDeclaration
import de.fraunhofer.aisec.cpg.graph.declarations.ImportDeclaration
import de.fraunhofer.aisec.cpg.graph.statements.expressions.CallExpression
import de.fraunhofer.aisec.cpg.graph.statements.expressions.MemberCallExpression
import de.fraunhofer.aisec.cpg.graph.statements.expressions.MemberExpression
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Reference
import de.fraunhofer.aisec.cpg.passes.ComponentPass
import de.fraunhofer.aisec.cpg.passes.configuration.ExecuteLate

@ExecuteLate
class RequestObjectPass(ctx: TranslationContext) : ComponentPass(ctx) {
    override fun cleanup() {
        // nop
    }

    override fun accept(comp: Component) {
        comp.imports
            .filter { it.name.split('.').last() == "request" }
            .forEach { handleRequestObject(it) }

        comp.calls
            .filter { it.name.lastPartsMatch("get") }
            .forEach { handleRequestObjectForm(it, comp.imports) }
        comp.calls
            .filter { it.name.lastPartsMatch("get_json") }
            .forEach { handleRequestObjectJson(it, comp.imports) }
    }

    private fun handleRequestObject(import: ImportDeclaration) {
        newRequestObjectNode(underlyingNode = import)
    }

    private fun handleRequestObjectForm(call: CallExpression, list: List<ImportDeclaration>) {
        when (call) {
            is MemberCallExpression -> {
                val base = call.base
                var flag = false
                if (base is Reference && base.name.toString().contains("request.form")) {
                    val requestOverlay =
                        list.flatMap { it.overlays.filterIsInstance<HTTPInput>() }.firstOrNull()
                    val dec =
                        call.followPrevEOGEdgesUntilHit { node: Node ->
                            node is FunctionDeclaration
                        }
                    val decReal = dec.fulfilled.first().last()
                    if (decReal.annotations.isNotEmpty()) {
                        val containsResource =
                            decReal.annotations.any { it.name.contains("resourceHandler") }
                        if (containsResource) {
                            flag = true
                        }
                    }

                    requestOverlay?.let {
                        newRequestOpForm(call, it, key = call.arguments.firstOrNull(), flag)
                    }
                } else if (
                    base is MemberExpression &&
                        base.name.toString().contains("form") &&
                        base.base.name.toString().contains("request")
                ) {
                    val requestOverlay =
                        list.flatMap { it.overlays.filterIsInstance<HTTPInput>() }.firstOrNull()
                    requestOverlay?.let {
                        newRequestOpForm(call, it, key = call.arguments.firstOrNull(), flag)
                    }
                } else if (base is Reference && base.name.toString().contains("request.args")) {
                    val requestOverlay =
                        list.flatMap { it.overlays.filterIsInstance<HTTPInput>() }.firstOrNull()
                    val dec =
                        call.followPrevEOGEdgesUntilHit { node: Node ->
                            node is FunctionDeclaration
                        }
                    val decReal = dec.fulfilled.first().last()

                    requestOverlay?.let {
                        newRequestOpArgs(call, it, key = call.arguments.firstOrNull())
                    }
                } else if (
                    base is MemberExpression &&
                        base.name.toString().contains("args") &&
                        base.base.name.toString().contains("request")
                ) {
                    val requestOverlay =
                        list.flatMap { it.overlays.filterIsInstance<HTTPInput>() }.firstOrNull()
                    requestOverlay?.let {
                        newRequestOpArgs(call, it, key = call.arguments.firstOrNull())
                    }
                } else if (base is Reference && base.name.toString().contains("request.json")) {
                    val requestOverlay =
                        list.flatMap { it.overlays.filterIsInstance<HTTPInput>() }.firstOrNull()
                    val dec =
                        call.followPrevEOGEdgesUntilHit { node: Node ->
                            node is FunctionDeclaration
                        }
                    val decReal = dec.fulfilled.first().last()

                    requestOverlay?.let {
                        newRequestOpJsonTwo(call, it, key = call.arguments.firstOrNull())
                    }
                } else if (
                    base is MemberExpression &&
                        base.name.toString().contains("json") &&
                        base.base.name.toString().contains("request")
                ) {
                    val requestOverlay =
                        list.flatMap { it.overlays.filterIsInstance<HTTPInput>() }.firstOrNull()
                    requestOverlay?.let {
                        newRequestOpJsonTwo(call, it, key = call.arguments.firstOrNull())
                    }
                }
            }
        }
    }

    private fun handleRequestObjectJson(call: CallExpression, list: List<ImportDeclaration>) {
        if (call.name.contains("request") || call.code.toString().contains("request")) {
            val requestOverlay =
                list.flatMap { it.overlays.filterIsInstance<HTTPInput>() }.firstOrNull()
            requestOverlay?.let { newRequestOpJson(call, it, key = call.arguments.firstOrNull()) }
        }
    }
}
