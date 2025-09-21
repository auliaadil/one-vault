package com.adilstudio.project.onevault.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import android.util.Base64

class CryptoService : CryptoProvider {

    companion object {
        private const val KEY_ALIAS = "OneVaultPasswordKey"
        private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val IV_SEPARATOR = ":"
    }

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }
    }

    private val secretKey: SecretKey by lazy {
        generateOrGetSecretKey()
    }

    private fun generateOrGetSecretKey(): SecretKey {
        return if (keyStore.containsAlias(KEY_ALIAS)) {
            keyStore.getKey(KEY_ALIAS, null) as SecretKey
        } else {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(false)
                .setRandomizedEncryptionRequired(true)
                .build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    override fun encrypt(plainText: String): String {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            val ivString = Base64.encodeToString(iv, Base64.DEFAULT)
            val encryptedString = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)

            return "$ivString$IV_SEPARATOR$encryptedString"
        } catch (e: Exception) {
            throw CryptoException("Failed to encrypt data", e)
        }
    }

    override fun decrypt(encryptedData: String): String {
        try {
            val parts = encryptedData.split(IV_SEPARATOR)
            if (parts.size != 2) {
                throw CryptoException("Invalid encrypted data format")
            }

            val iv = Base64.decode(parts[0], Base64.DEFAULT)
            val encryptedBytes = Base64.decode(parts[1], Base64.DEFAULT)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            throw CryptoException("Failed to decrypt data", e)
        }
    }
}
