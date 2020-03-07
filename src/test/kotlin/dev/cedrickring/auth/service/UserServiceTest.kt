package dev.cedrickring.auth.service

import com.ninjasquad.springmockk.SpykBean
import dev.cedrickring.auth.model.User
import dev.cedrickring.auth.repository.UserRepository
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@SpringBootTest
@RunWith(SpringRunner::class)
class UserServiceTest {

    @SpykBean
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userService: UserService

    @Test
    fun `should find a user with a given username`() {
        val username = "test"
        val validUser = user(username)
        val unknownUser = "unknown"
        every { userRepository.findUserByUsername(username) }.returns(listOf(validUser))
        every { userRepository.findUserByUsername(unknownUser) }.returns(listOf())

        assertThat(userService.findUser(username)).isEqualTo(validUser)
        assertThat(userService.findUser(unknownUser)).isNull()
    }

    @Test
    fun `should add a new user on save`() {
        val user = user("test")
        every { userRepository.save(any<User>()) } returns user
        userService.createUser(user)

        verify {
            userRepository.save(any<User>())
        }
    }

    @Test
    fun `should update password for given user`() {
        val user: User = user("test").copy(password = "test")
        val newUser = user("test").copy(password = "newPassword")
        every { userRepository.delete(any()) } returns Unit
        every { userRepository.save(any<User>()) } returns newUser

        userService.updatePassword(user, "newPassword")

        verify {
            userRepository.delete(user)
            userRepository.save(any<User>())
        }
    }

    @Test
    fun `should return true if user exists`() {
        val user = user("test")
        every { userRepository.findUserByUsername("test") } returns listOf(user)
        every { userRepository.findUserByUsername("some-other-user") } returns listOf()

        assertThat(userService.userExists(user)).isTrue()
        assertThat(userService.userExists(user("some-other-user"))).isFalse()
    }

}

fun user(username: String) =
        User(UUID.randomUUID(), "test", username, "", "", 0, 0)
