package com.capstone.tala.feature.parent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ParentAccessLogicTest {
    @Test
    fun `math challenge accepts the correct answer`() {
        val challenge = ParentAccessLogic.createMathChallenge()
        val answer = challenge.correctAnswer

        assertTrue(ParentAccessLogic.isMathAnswerCorrect(challenge, answer))
        assertFalse(ParentAccessLogic.isMathAnswerCorrect(challenge, answer + 1))
    }

    @Test
    fun `pin validation accepts the configured pin`() {
        val pin = "2468"

        assertTrue(ParentAccessLogic.isPinValid(pin, "2468"))
        assertFalse(ParentAccessLogic.isPinValid(pin, "2469"))
    }

    @Test
    fun `role choice maps to the expected dashboard path`() {
        assertEquals("teacher", ParentAccessLogic.roleToRoute("Teacher"))
        assertEquals("parent", ParentAccessLogic.roleToRoute("Parent"))
    }
}
