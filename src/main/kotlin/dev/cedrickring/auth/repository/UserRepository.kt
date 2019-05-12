package dev.cedrickring.auth.repository

import dev.cedrickring.auth.model.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository: CrudRepository<User, UUID> {

    fun findUserByUsername(userName: String): List<User>

}
