package com.example.http

import com.example.UserSession
import com.example.domain.model.UserRole
import com.example.domain.repository.Repositories
import com.example.security.PasswordService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.set
import io.ktor.server.sessions.sessions
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.select
import kotlinx.html.title

private const val TEXT_LOGIN = "Вход в систему"
private const val TEXT_REGISTER = "Регистрация"
private const val TEXT_NAME = "Имя пользователя"
private const val TEXT_PASSWORD = "Пароль"
private const val TEXT_ROLE = "Роль"
private const val TEXT_TEACHER = "Преподаватель"
private const val TEXT_STUDENT = "Студент"
private const val TEXT_SUBMIT_LOGIN = "Войти"
private const val TEXT_SUBMIT_REGISTER = "Зарегистрироваться"
private const val TEXT_HINT_LOGIN = "Введите имя пользователя и пароль. Если учётной записи ещё нет, переключитесь на регистрацию."
private const val TEXT_HINT_REGISTER = "Создайте пользователя и задайте роль. Пароль будет сохранён только в виде хеша (bcrypt)."
private const val TEXT_ERROR_INVALID = "Неверное имя пользователя или пароль."
private const val TEXT_ERROR_EXISTS = "Пользователь с таким именем уже существует."
private const val TEXT_ERROR_NOT_FOUND = "Пользователь не найден. Переключитесь на регистрацию."
private const val TEXT_ERROR_REQUIRED = "Введите имя пользователя и пароль (не менее 6 символов)."
private const val TEXT_ERROR_PASSWORD_LENGTH = "Пароль должен содержать не менее 6 символов."
private const val TEXT_ROLE_HINT = "Роль используется при регистрации и не меняется при повторном входе."
private const val TEXT_REGISTER_LINK = "Нет аккаунта? Зарегистрироваться"
private const val TEXT_LOGIN_LINK = "Уже зарегистрированы? Войти"
private const val MIN_PASSWORD_LENGTH = 6

private enum class AuthMode { LOGIN, REGISTER }

fun Route.authRoutes() {
    val userRepo = Repositories.userRepository

    get("/login") {
        val existingSession = call.sessions.get<UserSession>()
        if (existingSession != null) {
            val user = userRepo.getById(existingSession.userId)
            if (user != null) {
                call.respondRedirect(roleToPath(user.role))
                return@get
            } else {
                call.sessions.clear<UserSession>()
            }
        }

        val isRegister = call.request.queryParameters["register"]?.equals("true", ignoreCase = true) == true
        call.renderAuthPage(
            mode = if (isRegister) AuthMode.REGISTER else AuthMode.LOGIN,
            error = null
        )
    }

    post("/login") {
        val params = call.receiveParameters()
        val name = params["name"]?.trim().orEmpty()
        val password = params["password"]?.trim().orEmpty()
        val roleParam = params["role"]?.trim().orEmpty()
        val role = runCatching { UserRole.valueOf(roleParam) }.getOrNull() ?: UserRole.STUDENT
        val mode = if (params["mode"].equals(AuthMode.REGISTER.name, ignoreCase = true)) {
            AuthMode.REGISTER
        } else {
            AuthMode.LOGIN
        }
        val logger = call.application.environment.log

        if (name.isBlank() || password.isBlank()) {
            call.renderAuthPage(mode, TEXT_ERROR_REQUIRED, name, role, status = HttpStatusCode.BadRequest)
            return@post
        }

        val existingUser = userRepo.getByName(name)
        if (existingUser != null) {
            if (mode == AuthMode.REGISTER) {
                logger.warn("Registration attempt for '$name': user already exists")
                call.renderAuthPage(mode, TEXT_ERROR_EXISTS, name, role, status = HttpStatusCode.Conflict)
                return@post
            }
            if (!PasswordService.verify(password, existingUser.passwordHash)) {
                logger.warn("Login failed for '$name': invalid password")
                call.renderAuthPage(AuthMode.LOGIN, TEXT_ERROR_INVALID, name, existingUser.role, status = HttpStatusCode.Unauthorized)
                return@post
            }
            call.sessions.set(UserSession(userId = existingUser.id))
            logger.info("Login success for '$name' with role ${existingUser.role}")
            call.respondRedirect(roleToPath(existingUser.role))
            return@post
        }

        if (mode == AuthMode.LOGIN) {
            logger.warn("Login failed for '$name': user not found")
            call.renderAuthPage(AuthMode.LOGIN, TEXT_ERROR_NOT_FOUND, name, role, status = HttpStatusCode.NotFound)
            return@post
        }

        if (password.length < MIN_PASSWORD_LENGTH) {
            call.renderAuthPage(AuthMode.REGISTER, TEXT_ERROR_PASSWORD_LENGTH, name, role, status = HttpStatusCode.BadRequest)
            return@post
        }

        val user = try {
            userRepo.create(
                name = name,
                passwordHash = PasswordService.hash(password),
                role = role
            )
        } catch (e: Exception) {
            logger.error("Registration failed for '$name'", e)
            call.renderAuthPage(AuthMode.REGISTER, TEXT_ERROR_EXISTS, name, role, status = HttpStatusCode.Conflict)
            return@post
        }

        call.sessions.set(UserSession(userId = user.id))
        logger.info("User '$name' registered with role ${user.role}")
        call.respondRedirect(roleToPath(user.role))
    }

    get("/logout") {
        call.sessions.clear<UserSession>()
        call.respondRedirect("/login")
    }
}

private fun roleToPath(role: UserRole): String =
    if (role == UserRole.TEACHER) "/teacher" else "/student"

private suspend fun io.ktor.server.application.ApplicationCall.renderAuthPage(
    mode: AuthMode,
    error: String?,
    username: String = "",
    role: UserRole = UserRole.STUDENT,
    status: HttpStatusCode = HttpStatusCode.OK
) {
    respondHtml(status = status) {
        head {
            commonMetaAndStyles()
            title { +(if (mode == AuthMode.REGISTER) TEXT_REGISTER else TEXT_LOGIN) }
        }
        body {
            div(classes = "page") {
                div(classes = "card") {
                    h1 { +(if (mode == AuthMode.REGISTER) TEXT_REGISTER else TEXT_LOGIN) }
                    p(classes = "muted") {
                        +(if (mode == AuthMode.REGISTER) TEXT_HINT_REGISTER else TEXT_HINT_LOGIN)
                    }
                    error?.let {
                        div(classes = "alert") { +it }
                    }
                    form(action = "/login", method = FormMethod.post) {
                        input(type = InputType.hidden, name = "mode") {
                            value = mode.name
                        }
                        p {
                            label { +"$TEXT_NAME:" }
                            input {
                                name = "name"
                                placeholder = "Уникальное имя пользователя"
                                value = username
                                required = true
                            }
                        }
                        p {
                            label { +"$TEXT_PASSWORD:" }
                            input {
                                name = "password"
                                type = InputType.password
                                placeholder = "Не менее $MIN_PASSWORD_LENGTH символов"
                                required = true
                                attributes["autocomplete"] = if (mode == AuthMode.REGISTER) "new-password" else "current-password"
                            }
                        }
                        p {
                            label { +"$TEXT_ROLE:" }
                            select {
                                name = "role"
                                option {
                                    value = UserRole.TEACHER.name
                                    if (role == UserRole.TEACHER) selected = true
                                    +TEXT_TEACHER
                                }
                                option {
                                    value = UserRole.STUDENT.name
                                    if (role == UserRole.STUDENT) selected = true
                                    +TEXT_STUDENT
                                }
                            }
                        }
                        p(classes = "muted") { +TEXT_ROLE_HINT }
                        button {
                            +(if (mode == AuthMode.REGISTER) TEXT_SUBMIT_REGISTER else TEXT_SUBMIT_LOGIN)
                        }
                    }
                    p {
                        if (mode == AuthMode.REGISTER) {
                            a(href = "/login") { +TEXT_LOGIN_LINK }
                        } else {
                            a(href = "/login?register=true") { +TEXT_REGISTER_LINK }
                        }
                    }
                }
            }
        }
    }
}
