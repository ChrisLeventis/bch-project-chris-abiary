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
package de.fraunhofer.aisec.cpg.graph.concepts.fileown

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.graph.Component
import de.fraunhofer.aisec.cpg.graph.calls
import de.fraunhofer.aisec.cpg.graph.followPrevDFGEdgesUntilHit
import de.fraunhofer.aisec.cpg.graph.statements.expressions.CallExpression
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Literal
import de.fraunhofer.aisec.cpg.graph.statements.expressions.MemberCallExpression
import de.fraunhofer.aisec.cpg.passes.ComponentPass
import de.fraunhofer.aisec.cpg.passes.configuration.ExecuteLate

@ExecuteLate
class FilePass(ctx: TranslationContext) : ComponentPass(ctx) {
    override fun cleanup() {}

    override fun accept(comp: Component) {

        comp.calls.filter { it.name.lastPartsMatch("open") }.forEach { handleFileHandler(it) }

        comp.calls.filter { it.name.lastPartsMatch("write") }.forEach { handleFileOpWrite(it) }
    }

    private fun handleFileHandler(call: CallExpression) {
        if (call.arguments.any { it is Literal<*> && it.value.toString().equals("w") }) {
            newFileHandler(underlyingNode = call)
        }
    }

    private fun handleFileOpWrite(call: CallExpression) {

        when (call) {
            is MemberCallExpression -> {
                val base = call.base

                val file =
                    base
                        ?.followPrevDFGEdgesUntilHit {
                            it.overlays.filterIsInstance<FileHandler>().isNotEmpty()
                        }
                        ?.fulfilled
                        ?.singleOrNull()
                        ?.last()
                        ?.overlays
                        ?.filterIsInstance<FileHandler>()
                        ?.singleOrNull()

                file?.let { newFileOpWrite(call, it, what = call.arguments.firstOrNull()) }
            }
        }
    }
}
