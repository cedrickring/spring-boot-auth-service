package dev.cedrickring.auth.jwt

import dev.cedrickring.auth.loggerFor
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.security.PrivateKey
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(authManager: AuthenticationManager, private val signingKey: PrivateKey) : BasicAuthenticationFilter(authManager) {

    private val log = loggerFor<JWTAuthorizationFilter>()

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header: String? = request.getHeader("Authorization")

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response)
            return
        }

        val authenticationToken = getAuthentication(request)
        authenticationToken?.let {
            SecurityContextHolder.getContext().authentication = it
        }

        chain.doFilter(request, response)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token = request.getHeader("Authorization")

        try {
            val username = Jwts.parser()
                    .setSigningKey(signingKey)
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .body
                    .subject

            if (username != null) {
                log.info("Authorized user $username with token $token")
                return UsernamePasswordAuthenticationToken(username, null, mutableListOf())
            }
        } catch (e: SignatureException) {
            log.error("Couldn't authorize token $token", e)
        } catch (e: ExpiredJwtException) {
            log.error("Token expired: $token")
        }

        return null
    }

}
