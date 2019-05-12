package dev.cedrickring.auth.service

import dev.cedrickring.auth.model.User
import dev.cedrickring.auth.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service("userService")
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    fun findUser(userName: String): User? {
        val foundUsers = userRepository.findUserByUsername(userName)
        return if (foundUsers.isNotEmpty()) foundUsers[0] else null
    }

    fun createUser(user: User) {
        userRepository.save(user)
    }

    fun updatePassword(user: User, newPassword: String) {
        val newUser = user.copy(password = newPassword, passwordLastModified = System.currentTimeMillis())
        userRepository.delete(user)
        userRepository.save(newUser)
    }

    fun userExists(user: User): Boolean {
        return userRepository.findUserByUsername(user.username).isNotEmpty()
    }

}
