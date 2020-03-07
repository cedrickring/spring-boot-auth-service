package dev.cedrickring.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@TestConfiguration
class SpringSecurityTestConfiguration {

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder

    @Bean
    @Primary
    fun userDetailsService(): UserDetailsService {
        val basicUser = User("username", passwordEncoder.encode("password"), listOf())

        return InMemoryUserDetailsManager(basicUser)
    }

}
