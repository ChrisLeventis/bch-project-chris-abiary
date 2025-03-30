/*
 * Copyright (c) 2024, Fraunhofer AISEC. All rights reserved.
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
package de.fraunhofer.aisec.cpg.frontends.python

import de.fraunhofer.aisec.cpg.graph.*
import de.fraunhofer.aisec.cpg.graph.statements.expressions.*
import de.fraunhofer.aisec.cpg.test.analyze
import de.fraunhofer.aisec.cpg.test.assertLiteralValue
import de.fraunhofer.aisec.cpg.test.assertLocalName
import java.nio.file.Path
import kotlin.test.*
import org.junit.jupiter.api.Test

class privateTests {
    @Test
    fun testListComprehensions() {
        val topLevel = Path.of("src", "test", "resources", "python")
        val result =
            analyze(listOf(topLevel.resolve("comprehension.py").toFile()), topLevel, true) {
                it.registerLanguage<PythonLanguage>()
            }
        assertNotNull(result)
        val listComp = result.functions["listComp"]
        assertNotNull(listComp)

        val body = listComp.body
        assertIs<Block>(body)
        val singleWithIfAssignment = body.statements[0]
        assertIs<AssignExpression>(singleWithIfAssignment)
        val singleWithIf = singleWithIfAssignment.rhs[0]
        assertIs<CollectionComprehension>(singleWithIf)
        assertIs<CallExpression>(singleWithIf.statement)
        assertEquals(1, singleWithIf.comprehensionExpressions.size)
        assertLocalName("i", singleWithIf.comprehensionExpressions[0].variable)
        assertIs<Reference>(singleWithIf.comprehensionExpressions[0].iterable)
        assertLocalName("x", singleWithIf.comprehensionExpressions[0].iterable)
        val ifPredicate = singleWithIf.comprehensionExpressions[0].predicate
        assertIs<BinaryOperator>(ifPredicate)
        assertEquals("==", ifPredicate.operatorCode)

        val singleWithoutIfAssignment = body.statements[1]
        assertIs<AssignExpression>(singleWithoutIfAssignment)
        val singleWithoutIf = singleWithoutIfAssignment.rhs[0]
        assertIs<CollectionComprehension>(singleWithoutIf)
        assertIs<CallExpression>(singleWithoutIf.statement)
        assertEquals(1, singleWithoutIf.comprehensionExpressions.size)
        assertLocalName("i", singleWithoutIf.comprehensionExpressions[0].variable)
        assertIs<Reference>(singleWithoutIf.comprehensionExpressions[0].iterable)
        assertLocalName("x", singleWithoutIf.comprehensionExpressions[0].iterable)
        assertNull(singleWithoutIf.comprehensionExpressions[0].predicate)

        val singleWithDoubleIfAssignment = body.statements[2]
        assertIs<AssignExpression>(singleWithDoubleIfAssignment)
        val singleWithDoubleIf = singleWithDoubleIfAssignment.rhs[0]
        assertIs<CollectionComprehension>(singleWithDoubleIf)
        assertIs<CallExpression>(singleWithDoubleIf.statement)
        assertEquals(1, singleWithDoubleIf.comprehensionExpressions.size)
        assertLocalName("i", singleWithDoubleIf.comprehensionExpressions[0].variable)
        assertIs<Reference>(singleWithDoubleIf.comprehensionExpressions[0].iterable)
        assertLocalName("x", singleWithDoubleIf.comprehensionExpressions[0].iterable)
        val doubleIfPredicate = singleWithDoubleIf.comprehensionExpressions[0].predicate
        assertIs<BinaryOperator>(doubleIfPredicate)
        assertEquals("and", doubleIfPredicate.operatorCode)

        val doubleAssignment = body.statements[3] as? AssignExpression
        assertIs<AssignExpression>(doubleAssignment)
        val double = doubleAssignment.rhs[0] as? CollectionComprehension
        assertNotNull(double)
        assertIs<CallExpression>(double.statement)
        assertEquals(2, double.comprehensionExpressions.size)
        // TODO: Add tests on the comprehension expressions
    }

    @Test
    fun testSetComprehensions() {
        val topLevel = Path.of("src", "test", "resources", "python")
        val result =
            analyze(listOf(topLevel.resolve("comprehension.py").toFile()), topLevel, true) {
                it.registerLanguage<PythonLanguage>()
            }
        assertNotNull(result)
        val listComp = result.functions["setComp"]
        assertNotNull(listComp)

        val body = listComp.body as? Block
        assertNotNull(body)
        val singleWithIfAssignment = body.statements[0]
        assertIs<AssignExpression>(singleWithIfAssignment)
        val singleWithIf = singleWithIfAssignment.rhs[0]
        assertIs<CollectionComprehension>(singleWithIf)
        assertIs<CallExpression>(singleWithIf.statement)
        assertEquals(1, singleWithIf.comprehensionExpressions.size)
        assertLocalName("i", singleWithIf.comprehensionExpressions[0].variable)
        assertIs<Reference>(singleWithIf.comprehensionExpressions[0].iterable)
        assertLocalName("x", singleWithIf.comprehensionExpressions[0].iterable)
        val ifPredicate = singleWithIf.comprehensionExpressions[0].predicate
        assertIs<BinaryOperator>(ifPredicate)
        assertEquals("==", ifPredicate.operatorCode)

        val singleWithoutIfAssignment = body.statements[1]
        assertIs<AssignExpression>(singleWithoutIfAssignment)
        val singleWithoutIf = singleWithoutIfAssignment.rhs[0]
        assertIs<CollectionComprehension>(singleWithoutIf)
        assertIs<CallExpression>(singleWithoutIf.statement)
        assertEquals(1, singleWithoutIf.comprehensionExpressions.size)
        assertLocalName("i", singleWithoutIf.comprehensionExpressions[0].variable)
        assertIs<Reference>(singleWithoutIf.comprehensionExpressions[0].iterable)
        assertLocalName("x", singleWithoutIf.comprehensionExpressions[0].iterable)
        assertNull(singleWithoutIf.comprehensionExpressions[0].predicate)

        val singleWithDoubleIfAssignment = body.statements[2]
        assertIs<AssignExpression>(singleWithDoubleIfAssignment)
        val singleWithDoubleIf = singleWithDoubleIfAssignment.rhs[0]
        assertIs<CollectionComprehension>(singleWithDoubleIf)
        assertIs<CallExpression>(singleWithDoubleIf.statement)
        assertEquals(1, singleWithDoubleIf.comprehensionExpressions.size)
        assertLocalName("i", singleWithDoubleIf.comprehensionExpressions[0].variable)
        assertIs<Reference>(singleWithDoubleIf.comprehensionExpressions[0].iterable)
        assertLocalName("x", singleWithDoubleIf.comprehensionExpressions[0].iterable)
        val doubleIfPredicate = singleWithDoubleIf.comprehensionExpressions[0].predicate
        assertIs<BinaryOperator>(doubleIfPredicate)
        assertEquals("and", doubleIfPredicate.operatorCode)

        val doubleAssignment = body.statements[3]
        assertIs<AssignExpression>(doubleAssignment)
        val double = doubleAssignment.rhs[0]
        assertIs<CollectionComprehension>(double)
        assertIs<CallExpression>(double.statement)
        assertEquals(2, double.comprehensionExpressions.size)
    }

    @Test
    fun testDictComprehensions() {
        val topLevel = Path.of("src", "test", "resources", "python")
        val result =
            analyze(listOf(topLevel.resolve("comprehension.py").toFile()), topLevel, true) {
                it.registerLanguage<PythonLanguage>()
            }
        assertNotNull(result)
        val listComp = result.functions["dictComp"]
        assertNotNull(listComp)

        val body = listComp.body as? Block
        assertNotNull(body)
        val singleWithIfAssignment = body.statements[0]
        assertIs<AssignExpression>(singleWithIfAssignment)
        val singleWithIf = singleWithIfAssignment.rhs[0]
        assertIs<CollectionComprehension>(singleWithIf)
        var statement = singleWithIf.statement
        assertIs<KeyValueExpression>(statement)
        assertIs<Reference>(statement.key)
        assertLocalName("i", statement.key)
        assertIs<CallExpression>(statement.value)
        assertEquals(1, singleWithIf.comprehensionExpressions.size)
        assertLocalName("i", singleWithIf.comprehensionExpressions[0].variable)
        assertIs<Reference>(singleWithIf.comprehensionExpressions[0].iterable)
        assertLocalName("x", singleWithIf.comprehensionExpressions[0].iterable)
        val ifPredicate = singleWithIf.comprehensionExpressions[0].predicate
        assertIs<BinaryOperator>(ifPredicate)
        assertEquals("==", ifPredicate.operatorCode)

        val singleWithoutIfAssignment = body.statements[1]
        assertIs<AssignExpression>(singleWithoutIfAssignment)
        val singleWithoutIf = singleWithoutIfAssignment.rhs[0]
        assertIs<CollectionComprehension>(singleWithoutIf)
        statement = singleWithIf.statement
        assertIs<KeyValueExpression>(statement)
        assertIs<Reference>(statement.key)
        assertLocalName("i", statement.key)
        assertIs<CallExpression>(statement.value)
        assertEquals(1, singleWithoutIf.comprehensionExpressions.size)
        assertLocalName("i", singleWithoutIf.comprehensionExpressions[0].variable)
        assertIs<Reference>(singleWithoutIf.comprehensionExpressions[0].iterable)
        assertLocalName("x", singleWithoutIf.comprehensionExpressions[0].iterable)
        assertNull(singleWithoutIf.comprehensionExpressions[0].predicate)

        val singleWithDoubleIfAssignment = body.statements[2]
        assertIs<AssignExpression>(singleWithDoubleIfAssignment)
        val singleWithDoubleIf = singleWithDoubleIfAssignment.rhs[0]
        assertIs<CollectionComprehension>(singleWithDoubleIf)
        statement = singleWithIf.statement
        assertIs<KeyValueExpression>(statement)
        assertIs<Reference>(statement.key)
        assertLocalName("i", statement.key)
        assertIs<CallExpression>(statement.value)
        assertEquals(1, singleWithDoubleIf.comprehensionExpressions.size)
        assertLocalName("i", singleWithDoubleIf.comprehensionExpressions[0].variable)
        assertIs<Reference>(singleWithDoubleIf.comprehensionExpressions[0].iterable)
        assertLocalName("x", singleWithDoubleIf.comprehensionExpressions[0].iterable)
        val doubleIfPredicate = singleWithDoubleIf.comprehensionExpressions[0].predicate
        assertIs<BinaryOperator>(doubleIfPredicate)
        assertEquals("and", doubleIfPredicate.operatorCode)

        val doubleAssignment = body.statements[3] as? AssignExpression
        assertIs<AssignExpression>(doubleAssignment)
        val double = doubleAssignment.rhs[0] as? CollectionComprehension
        assertNotNull(double)
        statement = singleWithIf.statement
        assertIs<KeyValueExpression>(statement)
        assertIs<Reference>(statement.key)
        assertLocalName("i", statement.key)
        assertIs<CallExpression>(statement.value)
        assertEquals(2, double.comprehensionExpressions.size)
    }

    @Test
    fun testGeneratorExpr() {
        val topLevel = Path.of("src", "test", "resources", "python")
        val result =
            analyze(listOf(topLevel.resolve("comprehension.py").toFile()), topLevel, true) {
                it.registerLanguage<PythonLanguage>()
            }
        assertNotNull(result)
        val listComp = result.functions["generator"]
        assertNotNull(listComp)

        val body = listComp.body as? Block
        assertNotNull(body)
        val singleWithIfAssignment = body.statements[0]
        assertIs<AssignExpression>(singleWithIfAssignment)
        val singleWithIf = singleWithIfAssignment.rhs[0]
        assertIs<CollectionComprehension>(singleWithIf)
        assertIs<BinaryOperator>(singleWithIf.statement)
        assertEquals(1, singleWithIf.comprehensionExpressions.size)
        assertLocalName("i", singleWithIf.comprehensionExpressions[0].variable)
        assertIs<CallExpression>(singleWithIf.comprehensionExpressions[0].iterable)
        assertLocalName("range", singleWithIf.comprehensionExpressions[0].iterable)
        val ifPredicate = singleWithIf.comprehensionExpressions[0].predicate
        assertIs<BinaryOperator>(ifPredicate)
        assertEquals("==", ifPredicate.operatorCode)

        val singleWithoutIfAssignment = body.statements[1]
        assertIs<AssignExpression>(singleWithoutIfAssignment)
        val singleWithoutIf = singleWithoutIfAssignment.rhs[0]
        assertIs<CollectionComprehension>(singleWithoutIf)
        assertIs<BinaryOperator>(singleWithoutIf.statement)
        assertEquals(1, singleWithoutIf.comprehensionExpressions.size)
        assertLocalName("i", singleWithoutIf.comprehensionExpressions[0].variable)
        assertIs<CallExpression>(singleWithIf.comprehensionExpressions[0].iterable)
        assertLocalName("range", singleWithIf.comprehensionExpressions[0].iterable)
        assertNull(singleWithoutIf.comprehensionExpressions[0].predicate)
    }

    @Test
    fun testBoolOps() {
        val topLevel = Path.of("src", "test", "resources", "python")
        val result =
            analyze(listOf(topLevel.resolve("boolop.py").toFile()), topLevel, true) {
                it.registerLanguage<PythonLanguage>()
            }
        assertNotNull(result)

        val twoBoolOpCondition = result.functions["twoBoolOp"]?.ifs?.singleOrNull()?.condition
        assertIs<BinaryOperator>(twoBoolOpCondition)
        assertEquals("and", twoBoolOpCondition.operatorCode)
        assertLocalName("a", twoBoolOpCondition.lhs)
        assertLiteralValue(true, twoBoolOpCondition.rhs)

        // We expect that lhs comes first in the EOG and then the rhs.
        assertContains(twoBoolOpCondition.lhs.nextEOG, twoBoolOpCondition.rhs)

        val threeBoolOpCondition = result.functions["threeBoolOp"]?.ifs?.singleOrNull()?.condition
        assertIs<BinaryOperator>(threeBoolOpCondition)
        assertEquals("and", threeBoolOpCondition.operatorCode)
        assertLocalName("a", threeBoolOpCondition.lhs)
        val threeBoolOpConditionRhs = threeBoolOpCondition.rhs
        assertIs<BinaryOperator>(threeBoolOpConditionRhs)
        assertEquals("and", threeBoolOpConditionRhs.operatorCode)
        assertLiteralValue(true, threeBoolOpConditionRhs.lhs)
        assertLocalName("b", threeBoolOpConditionRhs.rhs)

        val threeBoolOpNoBoolCondition =
            result.functions["threeBoolOpNoBool"]?.ifs?.singleOrNull()?.condition
        assertIs<BinaryOperator>(threeBoolOpNoBoolCondition)
        assertEquals("and", threeBoolOpNoBoolCondition.operatorCode)
        assertLocalName("a", threeBoolOpNoBoolCondition.lhs)
        val threeBoolOpNoBoolConditionRhs = threeBoolOpNoBoolCondition.rhs
        assertIs<BinaryOperator>(threeBoolOpNoBoolConditionRhs)
        assertEquals("and", threeBoolOpNoBoolConditionRhs.operatorCode)
        assertLiteralValue(true, threeBoolOpNoBoolConditionRhs.lhs)
        assertLiteralValue("foo", threeBoolOpNoBoolConditionRhs.rhs)

        // We expect that lhs comes first in the EOG and then the lhs of the rhs and last the rhs of
        // the rhs.
        assertContains(threeBoolOpNoBoolCondition.lhs.nextEOG, threeBoolOpNoBoolConditionRhs.lhs)
        assertContains(threeBoolOpNoBoolConditionRhs.lhs.nextEOG, threeBoolOpNoBoolConditionRhs.rhs)

        val nestedBoolOpDifferentOp =
            result.functions["nestedBoolOpDifferentOp"]?.ifs?.singleOrNull()?.condition

        assertIs<BinaryOperator>(nestedBoolOpDifferentOp)
        assertEquals("or", nestedBoolOpDifferentOp.operatorCode)
        assertLocalName("b", nestedBoolOpDifferentOp.rhs)
        val nestedBoolOpDifferentOpLhs = nestedBoolOpDifferentOp.lhs
        assertIs<BinaryOperator>(nestedBoolOpDifferentOpLhs)
        assertEquals("and", nestedBoolOpDifferentOpLhs.operatorCode)
        assertLiteralValue(true, nestedBoolOpDifferentOpLhs.rhs)
        assertLocalName("a", nestedBoolOpDifferentOpLhs.lhs)

        // We expect that lhs of the "and" comes first in the EOG and then the rhs of the "and",
        // then we evaluate the whole "and" and last the rhs of the "or".
        assertContains(nestedBoolOpDifferentOpLhs.lhs.nextEOG, nestedBoolOpDifferentOpLhs.rhs)
        assertContains(nestedBoolOpDifferentOpLhs.rhs.nextEOG, nestedBoolOpDifferentOpLhs)
        assertContains(nestedBoolOpDifferentOpLhs.nextEOG, nestedBoolOpDifferentOp.rhs)

        val nestedBoolOpDifferentOp2 =
            result.functions["nestedBoolOpDifferentOp2"]?.ifs?.singleOrNull()?.condition
        assertIs<BinaryOperator>(nestedBoolOpDifferentOp2)
        assertEquals("or", nestedBoolOpDifferentOp2.operatorCode)
        assertLocalName("a", nestedBoolOpDifferentOp2.lhs)
        val nestedBoolOpDifferentOp2Rhs = nestedBoolOpDifferentOp2.rhs
        assertIs<BinaryOperator>(nestedBoolOpDifferentOp2Rhs)
        assertEquals("and", nestedBoolOpDifferentOp2Rhs.operatorCode)
        assertLiteralValue(true, nestedBoolOpDifferentOp2Rhs.lhs)
        assertLocalName("b", nestedBoolOpDifferentOp2Rhs.rhs)

        // We expect that lhs comes first in the EOG and then the lhs of the rhs and last the rhs of
        // the rhs.
        assertContains(nestedBoolOpDifferentOp2.lhs.nextEOG, nestedBoolOpDifferentOp2Rhs.lhs)
        assertContains(nestedBoolOpDifferentOp2Rhs.lhs.nextEOG, nestedBoolOpDifferentOp2Rhs.rhs)
    }

    @Test
    fun justTrying() {
        val topLevel = Path.of("privateTestFiles")
        val result =
            analyze(listOf(topLevel.resolve("persistingFile.py").toFile()), topLevel, true) {
                it.registerLanguage<PythonLanguage>()
            }
        val listComp = result.functions["showForm"] // direkter filter für funktionsnamen
        val annotationsIn = result.annotations // warum keine annotations zurück
        val assigns = result.assignments
        // result.dfgFrom<>()
        result.printDFG()

        val skajdflk = 3
    }

    @Test
    fun justTrying2() {
        val topLevel = Path.of("privateTestFiles")
        val result =
            analyze(listOf(topLevel.resolve("persistingFile.py").toFile()), topLevel, true) {
                it.registerLanguage<PythonLanguage>()
            }

        // find secret variable
        val tryFunction = result.allChildren<CallExpression>() { it.name.localName == "get" }
        val secretNode = tryFunction[0].nextDFG

        // find file persist funcrion
        val tryFindOpen = result.allChildren<CallExpression>() { it.name.localName == "open" }
        val allNexts =
            tryFindOpen[0].followNextEOGEdgesUntilHit { node: Node ->
                node.name.localName == "write"
            }
        // assertTrue(allNexts.fulfilled.isNotEmpty())
        val attt = 15
        // find Path between secret variable and the open fundtion
        val isTherePath =
            secretNode.firstOrNull()?.followNextFullDFGEdgesUntilHit { node: Node ->
                node.name.localName == "open"
            }
        // assertNotNull(isTherePath?.fulfilled?.isNotEmpty())
        // val allNexts = tryFindOpen[0].followNextFullDFGEdgesUntilHit { node: Node -> node. }
        val tmp = 3
    }

    @Test
    fun justTrying3() {
        val topLevel = Path.of("privateTestFiles")
        val result =
            analyze(listOf(topLevel.resolve("persistingFile.py").toFile()), topLevel, true) {
                it.registerLanguage<PythonLanguage>()
            }

        // dataflow stream start
        val tryFunction = result.allChildren<CallExpression>() { it.name.localName == "get" }

        // find end
        val end = result.allChildren<CallExpression> { it.name.localName == "open" }
        val pathToWrite =
            end[0].followNextEOGEdgesUntilHit { node: Node -> node.name.localName == "write" }
        assertFalse(pathToWrite.fulfilled.isEmpty())

        val writeNode = result.allChildren<CallExpression> { it.name.localName == "write" }
        // check forbidden Path
        val isPath =
            tryFunction[0].followNextFullDFGEdgesUntilHit { node: Node ->
                node.equals(writeNode[0])
            }
        assertFalse(isPath.fulfilled.isEmpty())
        val tmp = 3
    }

    @Test
    fun dfgTest() {
        val topLevel = Path.of("privateTestFiles")
        val result =
            analyze(listOf(topLevel.resolve("SQLAlchemy.py").toFile()), topLevel, true) {
                it.registerLanguage<PythonLanguage>()
            }

        val end = result.allChildren<CallExpression> { it.name.localName.toString() == "add" }
        val call = end[0]
        assertTrue(
            call.name.localName.toString() == "add" &&
                call.prevDFG.any { it.name.localName.toString() == "session" }
        )
        val calle = end[0].callee
        // val base = (calle.base as Reference).refersTo
        val prev = end[0].prevDFG
        val tmp = 1
    }

    @Test
    fun sqlAlchemyDecTest() {
        val topLevel = Path.of("privateTestFiles")
        val result =
            analyze(listOf(topLevel.resolve("SQLAlchemy.py").toFile()), topLevel, true) {
                it.registerLanguage<PythonLanguage>()
            }

        val end = result.allChildren<CallExpression> { it.name.localName.toString() == "add" }
        val memberTest = end[0]
        val callee = memberTest.callee
        val otherTest = memberTest.invokes

        val tmp = 1
    }

    @Test
    fun sqlAlchemyAllTest() {
        val topLevel = Path.of("privateTestFiles")
        val result =
            analyze(listOf(topLevel.resolve("SQLAlchemyDec.py").toFile()), topLevel, true) {
                it.registerLanguage<PythonLanguage>()
            }

        val end =
            result.allChildren<CallExpression> { it.name.localName.toString() == "SQLAlchemy" }
        val test = end.find { it.name.localName.toString() == "SQLAlchemy" }
        val tmp = 1
    }

    @Test
    fun calleeTest() {
        val topLevel = Path.of("privateTestFiles")
        val result =
            analyze(listOf(topLevel.resolve("logging.py").toFile()), topLevel, true) {
                it.registerLanguage<PythonLanguage>()
            }

        val end = result.allChildren<CallExpression> { it.name.localName.toString() == "add" }
        val tmp = 1
    }
}
