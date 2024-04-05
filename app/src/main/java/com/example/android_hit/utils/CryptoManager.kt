package com.example.android_hit.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class CryptoManager {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }
    fun encrypt(data: String): String {
        val key = getKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val byteData = data.toByteArray(StandardCharsets.UTF_8)
        val encryptedData = cipher.doFinal(byteData)
        val iv = cipher.iv

        // Combine IV and encrypted data for transmission/storage (optional)
        val combinedData = ByteArrayOutputStream().apply {
            write(iv)
            write(encryptedData)
        }.toByteArray()

        // Encode bytes to Base64 for easier handling (optional)
        return Base64.encodeToString(combinedData, Base64.NO_WRAP)
    }

    fun decrypt(data: String): String {
        val combinedData = Base64.decode(data, Base64.NO_WRAP)

        val inputStream = ByteArrayInputStream(combinedData)
        val iv = ByteArray(IV_LENGTH)
        inputStream.read(iv)
        val encryptedData = inputStream.readBytes()

        val key = getKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)

        val decryptedData = cipher.doFinal(encryptedData)

        return String(decryptedData, StandardCharsets.UTF_8)
    }
    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry("secret", null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    "secret",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        private const val IV_LENGTH = 16
    }
}