package com.example.security

import org.mindrot.jbcrypt.BCrypt

object PasswordService {
    fun hash(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt(12))

    fun verify(password: String, passwordHash: String): Boolean =
        passwordHash.isNotBlank() && runCatching { BCrypt.checkpw(password, passwordHash) }.getOrDefault(false)
}
