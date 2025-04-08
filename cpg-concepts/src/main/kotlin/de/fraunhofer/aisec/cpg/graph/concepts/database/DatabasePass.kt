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
package de.fraunhofer.aisec.cpg.graph.concepts.database

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.graph.Component
import de.fraunhofer.aisec.cpg.graph.calls
import de.fraunhofer.aisec.cpg.graph.followPrevDFGEdgesUntilHit
import de.fraunhofer.aisec.cpg.graph.statements.expressions.CallExpression
import de.fraunhofer.aisec.cpg.graph.statements.expressions.MemberCallExpression
import de.fraunhofer.aisec.cpg.graph.statements.expressions.MemberExpression
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Reference
import de.fraunhofer.aisec.cpg.passes.ComponentPass
import de.fraunhofer.aisec.cpg.passes.configuration.ExecuteLate

@ExecuteLate
class DatabasePass(ctx: TranslationContext) : ComponentPass(ctx) {
    override fun cleanup() {
        // nop
    }

    override fun accept(comp: Component) {
        comp.calls.filter { it.name.lastPartsMatch("SQLAlchemy") }.forEach { handleSQLAlchemy(it) }

        comp.calls.filter { it.name.lastPartsMatch("add") }.forEach { handleAdd(it, comp.calls) }
    }

    private fun handleAdd(addCall: CallExpression, list: List<CallExpression>) {
        when (addCall) {
            is MemberCallExpression -> {
                val base = addCall.base
                when (base) {
                    is MemberExpression -> {
                        val baseBase = base.base
                        val db =
                            baseBase
                                .followPrevDFGEdgesUntilHit {
                                    it.overlays.filterIsInstance<Database>().isNotEmpty()
                                }
                                .fulfilled
                                .singleOrNull()
                                ?.last()
                                ?.overlays // it's overlay nodes
                                ?.filterIsInstance<Database>()
                                ?.singleOrNull()
                        db?.let {
                            newDatabaseAdd(addCall, it, what = addCall.arguments.firstOrNull())
                        }
                    }

                    is Reference -> {
                        if (base.name.toString().contains("session")) {
                            val db =
                                list
                                    .flatMap { it.overlays.filterIsInstance<Database>() }
                                    .firstOrNull()
                            // richtige
                            // findet functioniert solange den edge case mit 2
                            // dbs nicht hat
                            db?.let {
                                newDatabaseAdd(addCall, it, what = addCall.arguments.firstOrNull())
                            }
                            val tmp = 1
                        }
                    }
                }
            }
        }
    }

    private fun handleSQLAlchemy(db: CallExpression) {
        newDatabase(underlyingNode = db)
    }
}
