package dev.cedrickring.auth.config

import org.junit.AfterClass
import org.junit.Assert.assertArrayEquals
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyPairGenerator
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

@SpringBootTest
@RunWith(SpringRunner::class)
class AppConfigTest {

    companion object {
        private val privateKeyPath = Paths.get("privatekey.pem")
        private val privateKey = KeyPairGenerator.getInstance("RSA").genKeyPair().private

        @BeforeClass
        @JvmStatic
        fun setup() {
            val keySpec = PKCS8EncodedKeySpec(privateKey.encoded)

            val pem = """
            -----BEGIN PRIVATE KEY-----
            ${Base64.getEncoder().encodeToString(keySpec.encoded)}
            -----END PRIVATE KEY-----
        """.trimIndent()

            Files.write(privateKeyPath, pem.toByteArray())
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            Files.delete(privateKeyPath)
        }

    }

    @Autowired
    lateinit var appConfig: AppConfig

    @Test
    fun `should use provided private key if file exists`() {
        assertArrayEquals(appConfig.getSigningKey().encoded, privateKey.encoded)
    }

}
