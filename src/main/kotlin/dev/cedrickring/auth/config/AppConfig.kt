package dev.cedrickring.auth.config

import dev.cedrickring.auth.loggerFor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

@Configuration
class AppConfig {

    private val keyFactory = KeyFactory.getInstance("RSA")
    private val log = loggerFor<AppConfig>()

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun getSigningKey(): PrivateKey {
        val keyPath = Paths.get("privatekey.pem")

        if (Files.notExists(keyPath)) {
            log.info("Generating a new SigningKey...")
            return KeyPairGenerator.getInstance("RSA").genKeyPair().private
        }

        log.info("Using provided PrivateKey for JWT signing")

        val keyPem = String(Files.readAllBytes(keyPath))
        val key = Base64.getDecoder().decode(removeRSAIdentifiers(keyPem))
        val keySpec = PKCS8EncodedKeySpec(key)

        return keyFactory.generatePrivate(keySpec)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
        return source
    }

    private fun removeRSAIdentifiers(pem: String): String {
        return pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("\\s".toRegex(), "")
    }

}
