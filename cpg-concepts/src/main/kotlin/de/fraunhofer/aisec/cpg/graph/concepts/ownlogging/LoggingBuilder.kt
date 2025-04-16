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

import de.fraunhofer.aisec.cpg.graph.*
import de.fraunhofer.aisec.cpg.graph.statements.expressions.CallExpression

fun MetadataProvider.newLoggerNode(underlyingNode: Node): LoggerNode {
    val node = LoggerNode(underlyingNode = underlyingNode)
    node.codeAndLocationFrom(underlyingNode)

    node.name = Name("Logger")

    NodeBuilder.log(node)
    return node
}

fun MetadataProvider.newLogOpError(
    underlyingNode: Node,
    logger: LoggerNode,
    what: Node?,
): LogOpError {
    val node = LogOpError(underlyingNode = underlyingNode, concept = logger, what = what)
    node.codeAndLocationFrom(underlyingNode)

    node.name = Name("LogOperation[" + underlyingNode.name.toString() + "]")

    (underlyingNode as? CallExpression)?.let { it.arguments.forEach { arg -> arg.nextDFG += node } }

    NodeBuilder.log(node)
    return node
}

fun MetadataProvider.newLogOpInfo(
    underlyingNode: Node,
    logger: LoggerNode,
    what: Node?,
): LogOpInfo {
    val node = LogOpInfo(underlyingNode = underlyingNode, concept = logger, what = what)
    node.codeAndLocationFrom(underlyingNode)

    node.name = Name("LogOperation[" + underlyingNode.name.toString() + "]")

    (underlyingNode as? CallExpression)?.let { it.arguments.forEach { arg -> arg.nextDFG += node } }

    NodeBuilder.log(node)
    return node
}

fun MetadataProvider.newLogOpDebug(
    underlyingNode: Node,
    logger: LoggerNode,
    what: Node?,
): LogOpDebug {
    val node = LogOpDebug(underlyingNode = underlyingNode, concept = logger, what = what)
    node.codeAndLocationFrom(underlyingNode)

    node.name = Name("LogOperation[" + underlyingNode.name.toString() + "]")

    (underlyingNode as? CallExpression)?.let { it.arguments.forEach { arg -> arg.nextDFG += node } }

    NodeBuilder.log(node)
    return node
}

fun MetadataProvider.newLogOpCritical(
    underlyingNode: Node,
    logger: LoggerNode,
    what: Node?,
): LogOpCritical {
    val node = LogOpCritical(underlyingNode = underlyingNode, concept = logger, what = what)
    node.codeAndLocationFrom(underlyingNode)

    node.name = Name("LogOperation[" + underlyingNode.name.toString() + "]")

    (underlyingNode as? CallExpression)?.let { it.arguments.forEach { arg -> arg.nextDFG += node } }

    NodeBuilder.log(node)
    return node
}

fun MetadataProvider.newLogOpWarning(
    underlyingNode: Node,
    logger: LoggerNode,
    what: Node?,
): LogOpWarning {
    val node = LogOpWarning(underlyingNode = underlyingNode, concept = logger, what = what)
    node.codeAndLocationFrom(underlyingNode)

    node.name = Name("LogOperation[" + underlyingNode.name.toString() + "]")

    (underlyingNode as? CallExpression)?.let { it.arguments.forEach { arg -> arg.nextDFG += node } }

    NodeBuilder.log(node)
    return node
}
