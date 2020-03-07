package dev.cedrickring.auth.service

import dev.cedrickring.auth.loggerFor
import dev.cedrickring.auth.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl : UserDetailsService {

    private val log = loggerFor<UserDetailsServiceImpl>()

    @Autowired
    lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String): User {
        val foundUsers = userRepository.findUserByUsername(username)

        if (foundUsers.isEmpty()) {
            throw UsernameNotFoundException(username)
        }

        log.debug("Found user for username $username")

        val user = foundUsers[0]
        return User(user.username, user.password, mutableListOf())
    }

}
