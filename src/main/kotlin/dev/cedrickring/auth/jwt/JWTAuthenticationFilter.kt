package dev.cedrickring.auth.jwt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.cedrickring.auth.loggerFor
import dev.cedrickring.auth.model.LoginUser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.security.PrivateKey
import java.util.*
import java.util.concurrent.TimeUnit
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(private val authManager: AuthenticationManager, private val signingKey: PrivateKey) : UsernamePasswordAuthenticationFilter() {

    private val log = loggerFor<JWTAuthenticationFilter>()

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val loginUser = jacksonObjectMapper()
                .readValue<LoginUser>(request.inputStream)

        log.info("Authenticating user ${loginUser.username}")

        return authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        loginUser.username,
                        loginUser.password,
                        mutableListOf()
                )
        )
    }

    override fun successfulAuthentication(request: HttpServletRequest,
                                          response: HttpServletResponse,
                                          chain: FilterChain,
                                          authResult: Authentication) {

        val username = (authResult.principal as User).username
        val token = Jwts.builder()
                .setSubject(username)
                .setExpiration(Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(10)))
                .signWith(SignatureAlgorithm.RS512, signingKey)
                .compact()

        log.info("Authenticated bearer $username with token $token")

        response.addHeader("Authorization", "Bearer $token")
        response.writer.write("Bearer $token")
    }

}
