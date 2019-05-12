package dev.cedrickring.auth.controller

import dev.cedrickring.auth.loggerFor
import dev.cedrickring.auth.model.User
import dev.cedrickring.auth.model.UserInfo
import dev.cedrickring.auth.model.request.ChangePasswordRequest
import dev.cedrickring.auth.model.response.ChangePasswordResponse
import dev.cedrickring.auth.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.security.core.userdetails.User as UserDetails

@RestController
@RequestMapping("/user")
class UserController {

    private val log = loggerFor<UserController>()

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder

    @GetMapping
    fun getUserInfo(): ResponseEntity<UserInfo> {
        val foundUser = getCurrentUser()

        log.info("Requested UserInfo for user ${foundUser?.username}")

        return when (foundUser) {
            null -> ResponseEntity.notFound().build()
            else -> {
                val userInfo = UserInfo(foundUser.username, foundUser.fullName, foundUser.emailAddress)
                ResponseEntity.ok(userInfo)
            }
        }
    }

    @PostMapping("/changepassword")
    fun changePassword(@RequestBody request: ChangePasswordRequest): ResponseEntity<ChangePasswordResponse> {
        val foundUser = getCurrentUser()
        return when (foundUser) {
            null -> ResponseEntity.badRequest().body(ChangePasswordResponse(false))
            else -> {
                log.info("Changing password for user ${foundUser.username}")
                if (passwordEncoder.matches(request.oldPassword, foundUser.password)) {
                    userService.updatePassword(foundUser, passwordEncoder.encode(request.newPassword))
                    return ResponseEntity.ok(ChangePasswordResponse(true))
                }
                return ResponseEntity.status(401).build()
            }
        }
    }

    private fun getCurrentUser(): User? {
        val principal = SecurityContextHolder.getContext().authentication.principal as UserDetails
        return userService.findUser(principal.username)
    }

}
