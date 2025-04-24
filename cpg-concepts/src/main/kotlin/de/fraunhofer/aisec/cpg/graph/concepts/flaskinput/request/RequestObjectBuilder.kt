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

import de.fraunhofer.aisec.cpg.graph.*
import de.fraunhofer.aisec.cpg.graph.statements.expressions.CallExpression

fun MetadataProvider.newRequestObjectNode(underlyingNode: Node): HTTPInput {
    val node = HTTPInput(underlyingNode = underlyingNode)
    node.codeAndLocationFrom(underlyingNode)
    node.name = Name("HTTPInput")
    NodeBuilder.log(node)
    return node
}

fun MetadataProvider.newRequestOpForm(
    underlyingNode: Node,
    requestOb: HTTPInput,
    key: Node?,
    isResourceHandlerr: Boolean,
): HTTPRequestAccessForm {
    val node =
        HTTPRequestAccessForm(
            underlyingNode = underlyingNode,
            concept = requestOb,
            key = key,
            isResourceHandler = isResourceHandlerr,
        )
    node.codeAndLocationFrom(underlyingNode)
    node.name = Name("HTTPAccess[" + underlyingNode.name.toString() + "]")

    (underlyingNode as? CallExpression)?.let { node.nextDFG = it.nextDFG.toMutableSet() }

    NodeBuilder.log(node)
    return node
}

fun MetadataProvider.newRequestOpJson(
    underlyingNode: Node,
    requestOb: HTTPInput,
    key: Node?,
): HTTPRequestAccessJson {
    val node =
        HTTPRequestAccessJson(underlyingNode = underlyingNode, concept = requestOb, key = key)
    node.codeAndLocationFrom(underlyingNode)
    node.name = Name("HTTPAccess[" + underlyingNode.name.toString() + "]")

    (underlyingNode as? CallExpression)?.let { node.nextDFG = it.nextDFG.toMutableSet() }

    NodeBuilder.log(node)
    return node
}

fun MetadataProvider.newRequestOpArgs(
    underlyingNode: Node,
    requestOb: HTTPInput,
    key: Node?,
): HTTPRequestAccessArgs {
    val node =
        HTTPRequestAccessArgs(underlyingNode = underlyingNode, concept = requestOb, key = key)
    node.codeAndLocationFrom(underlyingNode)
    node.name = Name("HTTPAccess[" + underlyingNode.name.toString() + "]")

    (underlyingNode as? CallExpression)?.let { node.nextDFG = it.nextDFG.toMutableSet() }

    NodeBuilder.log(node)
    return node
}

fun MetadataProvider.newRequestOpJsonTwo(
    underlyingNode: Node,
    requestOb: HTTPInput,
    key: Node?,
): HTTPRequestAccessJsonTwo {
    val node =
        HTTPRequestAccessJsonTwo(underlyingNode = underlyingNode, concept = requestOb, key = key)
    node.codeAndLocationFrom(underlyingNode)
    node.name = Name("HTTPAccess[" + underlyingNode.name.toString() + "]")

    (underlyingNode as? CallExpression)?.let { node.nextDFG = it.nextDFG.toMutableSet() }

    NodeBuilder.log(node)
    return node
}
