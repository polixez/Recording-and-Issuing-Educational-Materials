package com.example.security

import org.mindrot.jbcrypt.BCrypt

/**
 * Утилита для работы с bcrypt: хеширование пароля и проверка введённого пароля по сохранённому хешу.
 */
object PasswordService {
    /**
     * Создаёт bcrypt-хеш с фактором 12 и встроенной солью.
     */
    fun hash(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt(12))

    /**
     * Проверяет пароль, используя соль и параметры, встроенные в сохранённый хеш.
     */
    fun verify(password: String, passwordHash: String): Boolean =
        passwordHash.isNotBlank() && runCatching { BCrypt.checkpw(password, passwordHash) }.getOrDefault(false)
}
