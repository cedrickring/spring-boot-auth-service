package dev.cedrickring.auth.controller

import com.ninjasquad.springmockk.MockkBean
import dev.cedrickring.auth.SpringSecurityTestConfiguration
import dev.cedrickring.auth.model.response.UserCreateResponse
import dev.cedrickring.auth.service.UserService
import dev.cedrickring.auth.service.user
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [SpringSecurityTestConfiguration::class])
class RegisterControllerTest {

    @MockkBean
    lateinit var userService: UserService

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun `should create a new user when trying to register`() {
        val user = user("username").copy(password = "password", emailAddress = "emailAddress", fullName = "Full Name")
        every { userService.createUser(any()) } returns Unit
        every { userService.userExists(user) } returns false

        val response = testRestTemplate.postForEntity<UserCreateResponse>("/register", user)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body?.created).isTrue()

        verify {
            userService.createUser(any())
        }
    }

    @Test
    fun `should return 400 when user is already registerd`() {
        val user = user("username").copy(password = "password", emailAddress = "emailAddress", fullName = "Full Name")
        every { userService.userExists(user) } returns true

        val response = testRestTemplate.postForEntity<UserCreateResponse>("/register", user)
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).isNotNull
        assertThat(response.body?.created).isFalse()
    }

}
