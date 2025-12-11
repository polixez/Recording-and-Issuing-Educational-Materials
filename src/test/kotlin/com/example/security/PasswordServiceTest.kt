package com.example.security

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PasswordServiceTest {

    @Test
    fun `hash produces non-plain text and verifies correctly`() {
        val password = "s3cure!"
        val hash = PasswordService.hash(password)

        assertNotEquals(password, hash, "Хеш не должен совпадать с исходным паролем")
        assertTrue(PasswordService.verify(password, hash), "Верный пароль должен проходить проверку")
        assertFalse(PasswordService.verify("wrong", hash), "Неверный пароль не должен проходить проверку")
    }
}
