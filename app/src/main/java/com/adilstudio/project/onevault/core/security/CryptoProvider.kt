package com.adilstudio.project.onevault.core.security

interface CryptoProvider {
    fun encrypt(plainText: String): String
    fun decrypt(encryptedData: String): String
}

class CryptoException(message: String, cause: Throwable? = null) : Exception(message, cause)
