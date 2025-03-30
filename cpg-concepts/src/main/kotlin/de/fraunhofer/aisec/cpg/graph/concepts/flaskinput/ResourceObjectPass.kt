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
package de.fraunhofer.aisec.cpg.graph.concepts.flaskinput

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.graph.*
import de.fraunhofer.aisec.cpg.graph.concepts.Operation
import de.fraunhofer.aisec.cpg.graph.concepts.flaskinput.request.RequestOp
import de.fraunhofer.aisec.cpg.graph.declarations.FunctionDeclaration
import de.fraunhofer.aisec.cpg.graph.statements.expressions.CallExpression
import de.fraunhofer.aisec.cpg.passes.ComponentPass
import de.fraunhofer.aisec.cpg.passes.configuration.ExecuteLate

@ExecuteLate
class ResourceObjectPass(ctx: TranslationContext) : ComponentPass(ctx) {
    override fun cleanup() {
        // nop
    }

    override fun accept(comp: Component) {

        comp.calls.forEach { call ->
            call.overlays.forEach {
                when (it) {
                    is RequestOp -> {
                        handleResourceObject(it, call)
                    }
                }
            }
        }
    }

    private fun handleResourceObject(overlay: Operation, underlyingNode: CallExpression) {
        val paths =
            underlyingNode.followPrevEOGEdgesUntilHit { node: Node -> node is FunctionDeclaration }
        val decReal = paths.fulfilled.firstOrNull()?.last()
        if ((decReal?.annotations?.isNotEmpty() ?: false)) {
            val containsResource = decReal?.annotations?.any { it.name.contains("resourceHandler") }
            if (containsResource ?: false) {
                (overlay.underlyingNode as? CallExpression)?.let { callExpr ->
                    overlay.underlyingNode?.let {
                        newResourceObject(it, overlay.concept, callExpr.arguments.firstOrNull())
                    }
                }
            }
        }
    }
}
