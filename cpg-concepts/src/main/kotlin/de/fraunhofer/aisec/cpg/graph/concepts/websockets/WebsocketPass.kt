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
package de.fraunhofer.aisec.cpg.graph.concepts.websockets

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.graph.Component
import de.fraunhofer.aisec.cpg.graph.calls
import de.fraunhofer.aisec.cpg.graph.statements.expressions.CallExpression
import de.fraunhofer.aisec.cpg.graph.statements.expressions.MemberCallExpression
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Reference
import de.fraunhofer.aisec.cpg.passes.ComponentPass
import de.fraunhofer.aisec.cpg.passes.configuration.ExecuteLate

@ExecuteLate
class WebsocketPass(ctx: TranslationContext) : ComponentPass(ctx) {
    override fun cleanup() {}

    override fun accept(comp: Component) {

        comp.calls.filter { it.name.lastPartsMatch("WebSocket") }.forEach { handleWSClient(it) }

        comp.calls
            .filter { it.name.lastPartsMatch("send") }
            .forEach { handleWSOperation(it, comp.calls) }
    }

    private fun handleWSClient(call: CallExpression) {
        val tmp = 1
        newWebsocketClientNode(underlyingNode = call)
    }

    private fun handleWSOperation(call: CallExpression, list: List<CallExpression>) {
        when (call) {
            is MemberCallExpression -> {
                val base = call.base
                when (base) {
                    /*
                    is Reference -> {
                        val ws =
                            base
                                .followPrevDFGEdgesUntilHit {
                                    it.overlays.filterIsInstance<WebsocketClient>().isNotEmpty()
                                }
                                .fulfilled
                                .singleOrNull()
                                ?.last()
                                ?.overlays
                                ?.filterIsInstance<WebsocketClient>()
                                ?.singleOrNull()
                        ws?.let {
                            newWebsocketOpSendNode(call, it, what = call.arguments.firstOrNull())
                        }
                    }

                     */

                    is Reference -> {
                        val ws =
                            list
                                .flatMap { it.overlays.filterIsInstance<WebsocketClient>() }
                                .firstOrNull()
                        if (
                            base.name
                                .toString()
                                .contains(ws?.underlyingNode?.nextDFG?.first()?.name.toString())
                        ) {
                            ws?.let {
                                newWebsocketOpSendNode(
                                    call,
                                    it,
                                    what = call.arguments.firstOrNull(),
                                )
                            }
                        }
                        val tmp = 1
                    }
                }
            }
        }
    }
}
