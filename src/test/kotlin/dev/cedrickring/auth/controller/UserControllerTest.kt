package dev.cedrickring.auth.controller

import com.ninjasquad.springmockk.MockkBean
import dev.cedrickring.auth.SpringSecurityTestConfiguration
import dev.cedrickring.auth.model.LoginUser
import dev.cedrickring.auth.model.UserInfo
import dev.cedrickring.auth.model.request.ChangePasswordRequest
import dev.cedrickring.auth.model.response.ChangePasswordResponse
import dev.cedrickring.auth.service.UserService
import dev.cedrickring.auth.service.user
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [SpringSecurityTestConfiguration::class])
class UserControllerTest {

    @MockkBean
    lateinit var userService: UserService

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Before
    fun setup() {
        testRestTemplate.postForEntity<String>("/login", LoginUser("username", "password"))
                .body?.let {
            testRestTemplate.restTemplate.interceptors = listOf(
                    ClientHttpRequestInterceptor { request, body, execution ->
                        request.headers.add("Authorization", it)
                        execution.execute(request, body)
                    }
            )
        }
    }

    @Test
    fun `should display correct user info`() {
        val user = user("username").copy(password = "password", emailAddress = "email@address.com")
        every { userService.findUser("username") } returns user

        val userInfo = testRestTemplate.getForEntity<UserInfo>("/user")
        assertThat(userInfo.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(userInfo.body).isNotNull
        assertThat(userInfo.body?.username).isEqualTo(user.username)
        assertThat(userInfo.body?.emailAddress).isEqualTo(user.emailAddress)
    }


    @Test
    fun `should change password for user`() {
        val user = user("username").copy(password = passwordEncoder.encode("password"), emailAddress = "email@address.com")
        every { userService.findUser("username") } returns user
        every { userService.updatePassword(user, any()) } returns Unit

        val request = ChangePasswordRequest("password", "newPassword")
        val response = testRestTemplate.postForEntity<ChangePasswordResponse>("/user/changepassword", request)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body?.success).isTrue()

        verify {
            userService.updatePassword(user, any())
        }
    }

    @Test
    fun `should return 401 if old password doesn't match`() {
        val user = user("username").copy(password = passwordEncoder.encode("password"), emailAddress = "email@address.com")
        every { userService.findUser("username") } returns user

        val request = ChangePasswordRequest("wrongPassword", "newPassword")
        val response = testRestTemplate.postForEntity<ChangePasswordResponse>("/user/changepassword", request)
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

}
