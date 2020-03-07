package dev.cedrickring.auth.jwt

import dev.cedrickring.auth.SpringSecurityTestConfiguration
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.test.context.junit4.SpringRunner
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.util.*
import java.util.concurrent.TimeUnit

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [SpringSecurityTestConfiguration::class])
class JWTAuthorizationFilterTest {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    lateinit var signingKey: PrivateKey

    @Test
    fun `should return 403 if no token is provided`() {
        testRestTemplate.restTemplate.interceptors = listOf()
        val response = testRestTemplate.getForEntity<String>("/user")
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `should return 403 if token is expired`() {
        val token = Jwts.builder()
                .setSubject("username")
                .setExpiration(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)))
                .signWith(SignatureAlgorithm.RS512, signingKey)
                .compact()

        testRestTemplate.restTemplate.interceptors = listOf(
                ClientHttpRequestInterceptor { request, body, execution ->
                    request.headers.add("Authorization", "Bearer $token")
                    execution.execute(request, body)
                }
        )

        val response = testRestTemplate.getForEntity<String>("/user")
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `should return 403 if token was signed with another key`() {
        val privateKey = KeyPairGenerator.getInstance("RSA").genKeyPair().private
        val token = Jwts.builder()
                .setSubject("username")
                .setExpiration(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)))
                .signWith(SignatureAlgorithm.RS512, privateKey)
                .compact()

        testRestTemplate.restTemplate.interceptors = listOf(
                ClientHttpRequestInterceptor { request, body, execution ->
                    request.headers.add("Authorization", "Bearer $token")
                    execution.execute(request, body)
                }
        )

        val response = testRestTemplate.getForEntity<String>("/user")
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

}
