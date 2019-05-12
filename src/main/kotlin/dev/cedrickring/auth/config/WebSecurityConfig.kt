package dev.cedrickring.auth.config

import dev.cedrickring.auth.jwt.JWTAuthenticationFilter
import dev.cedrickring.auth.jwt.JWTAuthorizationFilter
import dev.cedrickring.auth.service.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.security.PrivateKey

@EnableWebSecurity
class WebSecurityConfig: WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var signingKey: PrivateKey

    @Autowired
    lateinit var userDetailsService: UserDetailsServiceImpl

    @Autowired
    lateinit var passwordEncoder: BCryptPasswordEncoder

    override fun configure(http: HttpSecurity) {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, "/register").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(JWTAuthenticationFilter(authenticationManager(), signingKey))
                .addFilter(JWTAuthorizationFilter(authenticationManager(), signingKey))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    override fun configure(authBuilder: AuthenticationManagerBuilder) {
        authBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder)
    }

}
