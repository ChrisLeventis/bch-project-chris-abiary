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
package de.fraunhofer.aisec.cpg.graph.concepts.logging

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.graph.Component
import de.fraunhofer.aisec.cpg.graph.calls
import de.fraunhofer.aisec.cpg.graph.memberExpressions
import de.fraunhofer.aisec.cpg.graph.statements.expressions.CallExpression
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Expression
import de.fraunhofer.aisec.cpg.graph.statements.expressions.MemberCallExpression
import de.fraunhofer.aisec.cpg.graph.statements.expressions.MemberExpression
import de.fraunhofer.aisec.cpg.passes.ComponentPass
import de.fraunhofer.aisec.cpg.passes.configuration.ExecuteLate

@ExecuteLate
class LoggingPass(ctx: TranslationContext) : ComponentPass(ctx) {
    override fun cleanup() {
        // nop
    }

    override fun accept(comp: Component) {
        comp.memberExpressions
            .filter { it.name.lastPartsMatch("logger") }
            .forEach { handleLogger(it) }
        """
        comp.calls.filter { it.name.lastPartsMatch("error") }.forEach { handleLogOpError(it) }

        comp.calls.filter { it.name.lastPartsMatch("info") }.forEach { handleLogOpInfo(it) }

        comp.calls.filter { it.name.lastPartsMatch("debug") }.forEach { handleLogOpDebug(it) }

        comp.calls.filter { it.name.lastPartsMatch("critical") }.forEach { handleLogOpCritical(it) }

        comp.calls.filter { it.name.lastPartsMatch("warning") }.forEach { handleLogOpWarning(it) }
        """
        comp.calls.forEach { call ->
            when {
                call.name.lastPartsMatch("error") -> handleLogOpError(call)
                call.name.lastPartsMatch("info") -> handleLogOpInfo(call)
                call.name.lastPartsMatch("debug") -> handleLogOpDebug(call)
                call.name.lastPartsMatch("critical") -> handleLogOpCritical(call)
                call.name.lastPartsMatch("warning") -> handleLogOpWarning(call)
            }
        }
    }

    private fun handleLogOpError(addCall: CallExpression) {
        when (addCall) {
            is MemberCallExpression -> {
                val base = addCall.base
                when (base) {
                    is MemberExpression ->
                        if (base.name.toString().contains("logger")) {
                            base.overlays.filterIsInstance<LoggerNode>().singleOrNull()?.let { lN ->
                                newLogOpError(addCall, lN, addCall.arguments.firstOrNull())
                            }
                        }
                }
            }
        }
    }

    private fun handleLogOpInfo(addCall: CallExpression) {
        when (addCall) {
            is MemberCallExpression -> {
                val base = addCall.base
                when (base) {
                    is MemberExpression ->
                        if (base.name.toString().contains("logger")) {
                            base.overlays.filterIsInstance<LoggerNode>().singleOrNull()?.let { lN ->
                                newLogOpInfo(addCall, lN, addCall.arguments.firstOrNull())
                            }
                        }
                }
            }
        }
    }

    private fun handleLogOpDebug(addCall: CallExpression) {
        when (addCall) {
            is MemberCallExpression -> {
                val base = addCall.base
                when (base) {
                    is MemberExpression ->
                        if (base.name.toString().contains("logger")) {
                            base.overlays.filterIsInstance<LoggerNode>().singleOrNull()?.let { lN ->
                                newLogOpDebug(addCall, lN, addCall.arguments.firstOrNull())
                            }
                        }
                }
            }
        }
    }

    private fun handleLogOpCritical(addCall: CallExpression) {
        when (addCall) {
            is MemberCallExpression -> {
                val base = addCall.base
                when (base) {
                    is MemberExpression ->
                        if (base.name.toString().contains("logger")) {
                            base.overlays.filterIsInstance<LoggerNode>().singleOrNull()?.let { lN ->
                                newLogOpCritical(addCall, lN, addCall.arguments.firstOrNull())
                            }
                        }
                }
            }
        }
    }

    private fun handleLogOpWarning(addCall: CallExpression) {
        when (addCall) {
            is MemberCallExpression -> {
                val base = addCall.base
                when (base) {
                    is MemberExpression ->
                        if (base.name.toString().contains("logger")) {
                            base.overlays.filterIsInstance<LoggerNode>().singleOrNull()?.let { lN
                                -> // wird nicht funktionieren nach änderung
                                newLogOpWarning(addCall, lN, addCall.arguments.firstOrNull())
                            }
                        }
                }
            }
        }
    }

    private fun handleLogger(logger: Expression) {
        newLoggerNode(underlyingNode = logger)
    }
}
