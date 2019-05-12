package dev.cedrickring.auth.controller

import dev.cedrickring.auth.loggerFor
import dev.cedrickring.auth.model.User
import dev.cedrickring.auth.model.response.UserCreateResponse
import dev.cedrickring.auth.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class RegisterController {

    private val log = loggerFor<RegisterController>()

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder

    @PostMapping("/register")
    fun register(@RequestBody user: User): ResponseEntity<UserCreateResponse> {
        return if (userService.userExists(user)) {
            log.info("User ${user.username} tried to register twice.")
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(UserCreateResponse(user.username, user.emailAddress, false))
        } else {
            userService.createUser(user.copy(
                    id = UUID.randomUUID(),
                    password = passwordEncoder.encode(user.password),
                    created = System.currentTimeMillis(),
                    passwordLastModified = System.currentTimeMillis()
            ))
            log.info("Created new user ${user.username}")
            ResponseEntity.ok(UserCreateResponse(user.username, user.emailAddress, true))
        }
    }

}
