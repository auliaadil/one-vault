package com.adilstudio.project.onevault.core.security

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CryptoServiceInstrumentedTest {

    private lateinit var cryptoService: CryptoService

    @Before
    fun setup() {
        // This runs on Android device/emulator where Android Keystore is available
        cryptoService = CryptoService()
    }

    @Test
    fun testEncryptDecrypt_simplePassword() {
        val originalPassword = "myPassword123"

        val encrypted = cryptoService.encrypt(originalPassword)
        val decrypted = cryptoService.decrypt(encrypted)

        assertNotEquals(originalPassword, encrypted)
        assertEquals(originalPassword, decrypted)
    }

    @Test
    fun testEncryptDecrypt_complexPassword() {
        val originalPassword = "P@ssw0rd!#$%^&*()_+-=[]{}|;':\",./<>?"

        val encrypted = cryptoService.encrypt(originalPassword)
        val decrypted = cryptoService.decrypt(encrypted)

        assertNotEquals(originalPassword, encrypted)
        assertEquals(originalPassword, decrypted)
    }

    @Test
    fun testEncryptDecrypt_emptyPassword() {
        val originalPassword = ""

        val encrypted = cryptoService.encrypt(originalPassword)
        val decrypted = cryptoService.decrypt(encrypted)

        assertEquals(originalPassword, decrypted)
    }

    @Test
    fun testEncryptDecrypt_unicodePassword() {
        val originalPassword = "パスワード密码🔒"

        val encrypted = cryptoService.encrypt(originalPassword)
        val decrypted = cryptoService.decrypt(encrypted)

        assertNotEquals(originalPassword, encrypted)
        assertEquals(originalPassword, decrypted)
    }

    @Test
    fun testEncryption_producesUniqueResults() {
        val password = "samePassword"

        val encrypted1 = cryptoService.encrypt(password)
        val encrypted2 = cryptoService.encrypt(password)

        // Due to randomized IV, two encryptions of the same data should be different
        assertNotEquals(encrypted1, encrypted2)

        // But both should decrypt to the same value
        assertEquals(password, cryptoService.decrypt(encrypted1))
        assertEquals(password, cryptoService.decrypt(encrypted2))
    }

    @Test
    fun testKeyPersistence() {
        // Test that the key persists across multiple CryptoService instances
        val password = "testPersistence"

        val encrypted = cryptoService.encrypt(password)

        // Create a new instance
        val newCryptoService = CryptoService()
        val decrypted = newCryptoService.decrypt(encrypted)

        assertEquals(password, decrypted)
    }

    @Test(expected = CryptoException::class)
    fun testDecrypt_invalidFormat() {
        cryptoService.decrypt("invalidformat")
    }

    @Test(expected = CryptoException::class)
    fun testDecrypt_invalidBase64() {
        cryptoService.decrypt("invalid:base64data!")
    }

    @Test
    fun testEncryptedData_containsIvSeparator() {
        val password = "testPassword"
        val encrypted = cryptoService.encrypt(password)

        assertTrue("Encrypted data should contain IV separator", encrypted.contains(":"))

        val parts = encrypted.split(":")
        assertEquals("Encrypted data should have exactly 2 parts", 2, parts.size)
        assertTrue("IV part should not be empty", parts[0].isNotEmpty())
        assertTrue("Encrypted part should not be empty", parts[1].isNotEmpty())
    }

    @Test
    fun testEncryptedData_isBase64() {
        val password = "testPassword"
        val encrypted = cryptoService.encrypt(password)

        val parts = encrypted.split(":")

        // Verify both parts are valid Base64
        try {
            android.util.Base64.decode(parts[0], android.util.Base64.DEFAULT)
            android.util.Base64.decode(parts[1], android.util.Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {
            fail("Encrypted data parts should be valid Base64: ${e.message}")
        }
    }
}
