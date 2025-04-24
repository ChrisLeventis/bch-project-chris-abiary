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
package de.fraunhofer.aisec.cpg.graph.concepts.ownlogging

import de.fraunhofer.aisec.cpg.TranslationContext
import de.fraunhofer.aisec.cpg.graph.Component
import de.fraunhofer.aisec.cpg.graph.calls
import de.fraunhofer.aisec.cpg.graph.statements.expressions.*
import de.fraunhofer.aisec.cpg.passes.ComponentPass
import de.fraunhofer.aisec.cpg.passes.configuration.ExecuteLate

@ExecuteLate
class LoggingPass(ctx: TranslationContext) : ComponentPass(ctx) {
    override fun cleanup() {
        // nop
    }

    override fun accept(comp: Component) {
        comp.calls
            .filter { it.name.lastPartsMatch("Flask") }
            .forEach { handleLogger(it) } // inferredLogger

        comp.calls.forEach { call ->
            when {
                call.name.lastPartsMatch("error") -> handleLogOpError(call, comp.calls)
                call.name.lastPartsMatch("info") -> handleLogOpInfo(call, comp.calls)
                call.name.lastPartsMatch("debug") -> handleLogOpDebug(call, comp.calls)
                call.name.lastPartsMatch("critical") -> handleLogOpCritical(call, comp.calls)
                call.name.lastPartsMatch("warning") -> handleLogOpWarning(call, comp.calls)
            }
        }
    }

    private fun handleLogOpError(addCall: CallExpression, list: List<CallExpression>) {
        when (addCall) {
            is MemberCallExpression -> {
                val base = addCall.base
                when (base) {
                    is MemberExpression ->
                        if (base.name.toString().contains("logger")) {
                            val logger =
                                list
                                    .flatMap { it.overlays.filterIsInstance<LoggerNode>() }
                                    .firstOrNull()

                            if (logger != null) {
                                newLogOpError(addCall, logger, addCall.arguments.firstOrNull())
                            }
                        }
                    is Reference ->
                        if (base.name.toString().contains("app.logger")) {
                            val logger =
                                list
                                    .flatMap { it.overlays.filterIsInstance<LoggerNode>() }
                                    .firstOrNull()

                            if (logger != null) {
                                newLogOpError(addCall, logger, addCall.arguments.firstOrNull())
                            }
                        }
                }
            }
        }
    }

    private fun handleLogOpInfo(addCall: CallExpression, list: List<CallExpression>) {
        when (addCall) {
            is MemberCallExpression -> {
                val base = addCall.base
                when (base) {
                    is MemberExpression ->
                        if (base.name.toString().contains("logger")) {
                            val logger =
                                list
                                    .flatMap { it.overlays.filterIsInstance<LoggerNode>() }
                                    .firstOrNull()

                            if (logger != null) {
                                newLogOpInfo(addCall, logger, addCall.arguments.firstOrNull())
                            }
                        }
                    is Reference ->
                        if (base.name.toString().contains("app.logger")) {
                            val logger =
                                list
                                    .flatMap { it.overlays.filterIsInstance<LoggerNode>() }
                                    .firstOrNull()

                            if (logger != null) {
                                newLogOpInfo(addCall, logger, addCall.arguments.firstOrNull())
                            }
                        }
                }
            }
        }
    }

    private fun handleLogOpDebug(addCall: CallExpression, list: List<CallExpression>) {
        when (addCall) {
            is MemberCallExpression -> {
                val base = addCall.base
                when (base) {
                    is MemberExpression ->
                        if (base.name.toString().contains("logger")) {
                            val logger =
                                list
                                    .flatMap { it.overlays.filterIsInstance<LoggerNode>() }
                                    .firstOrNull()

                            if (logger != null) {
                                newLogOpDebug(addCall, logger, addCall.arguments.firstOrNull())
                            }
                        }
                    is Reference ->
                        if (base.name.toString().contains("app.logger")) {
                            val logger =
                                list
                                    .flatMap { it.overlays.filterIsInstance<LoggerNode>() }
                                    .firstOrNull()

                            if (logger != null) {
                                newLogOpDebug(addCall, logger, addCall.arguments.firstOrNull())
                            }
                        }
                }
            }
        }
    }

    private fun handleLogOpCritical(addCall: CallExpression, list: List<CallExpression>) {
        when (addCall) {
            is MemberCallExpression -> {
                val base = addCall.base
                when (base) {
                    is MemberExpression ->
                        if (base.name.toString().contains("logger")) {
                            val logger =
                                list
                                    .flatMap { it.overlays.filterIsInstance<LoggerNode>() }
                                    .firstOrNull()

                            if (logger != null) {
                                newLogOpCritical(addCall, logger, addCall.arguments.firstOrNull())
                            }
                        }
                    is Reference ->
                        if (base.name.toString().contains("app.logger")) {
                            val logger =
                                list
                                    .flatMap { it.overlays.filterIsInstance<LoggerNode>() }
                                    .firstOrNull()

                            if (logger != null) {
                                newLogOpCritical(addCall, logger, addCall.arguments.firstOrNull())
                            }
                        }
                }
            }
        }
    }

    private fun handleLogOpWarning(addCall: CallExpression, list: List<CallExpression>) {
        when (addCall) {
            is MemberCallExpression -> {
                val base = addCall.base
                when (base) {
                    is MemberExpression ->
                        if (base.name.toString().contains("logger")) {
                            val logger =
                                list
                                    .flatMap { it.overlays.filterIsInstance<LoggerNode>() }
                                    .firstOrNull()

                            if (logger != null) {
                                newLogOpWarning(addCall, logger, addCall.arguments.firstOrNull())
                            }
                        }
                    is Reference ->
                        if (base.name.toString().contains("app.logger")) {
                            val logger =
                                list
                                    .flatMap { it.overlays.filterIsInstance<LoggerNode>() }
                                    .firstOrNull()

                            if (logger != null) {
                                newLogOpWarning(addCall, logger, addCall.arguments.firstOrNull())
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
