package dev.cedrickring.auth.service

import com.ninjasquad.springmockk.MockkBean
import dev.cedrickring.auth.repository.UserRepository
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest
@RunWith(SpringRunner::class)
class UserDetailsServiceImplTest {

    @MockkBean
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userDetailsService: UserDetailsServiceImpl

    @Test
    fun `should return a user for a known username`() {
        val user = user("test").copy(password = "password")
        every { userRepository.findUserByUsername("test") } returns listOf(user)

        val userDetails = userDetailsService.loadUserByUsername(user.username)
        assertThat(userDetails.username).isEqualTo(user.username)
        assertThat(userDetails.password).isEqualTo(user.password)
    }

    @Test
    fun `should throw an exception if user is unknown`() {
        every { userRepository.findUserByUsername(any()) } returns listOf()

        assertThrows<UsernameNotFoundException> {
            userDetailsService.loadUserByUsername("unknown-user")
        }
    }

}
