package com.test.utils

import android.util.Base64
import com.test.network.BaseEndpoint
import java.util.Random
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class SignUtils {

    fun appSign(apiKey: String, secret: String, currtTime: Long, expireTime: Long): String {
        try {
            val rdm = Math.abs(Random().nextInt())

            val plainText = String.format("a=%s&b=%d&c=%d&d=%d", apiKey, expireTime, currtTime, rdm)
            val hmacDigest = HmacSha1(plainText, secret)
            val signContent = ByteArray(hmacDigest.size + plainText.toByteArray().size)
            System.arraycopy(hmacDigest, 0, signContent, 0, hmacDigest.size)
            System.arraycopy(
                    plainText.toByteArray(), 0, signContent, hmacDigest.size,
                    plainText.toByteArray().size
            )
            return Base64Encode(signContent).replace("[\\s*\t\n\r]".toRegex(), "")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun Base64Encode(binaryData: ByteArray): String {
        return Base64.encodeToString(binaryData, Base64.DEFAULT)
    }

    @Throws(Exception::class)
    fun HmacSha1(binaryData: ByteArray, key: String): ByteArray {
        val mac = Mac.getInstance("HmacSHA1")
        val secretKey = SecretKeySpec(key.toByteArray(), "HmacSHA1")
        mac.init(secretKey)
        return mac.doFinal(binaryData)
    }

    @Throws(Exception::class)
    fun HmacSha1(plainText: String, key: String): ByteArray {
        return HmacSha1(plainText.toByteArray(), key)
    }

    fun getVersion(): String {
        return "hmac_sha1"
    }

    fun getSign(): String {
        val currtTime = System.currentTimeMillis() / 1000
        val expireTime = (System.currentTimeMillis() + 60 * 60 * 100) / 1000
        val sign = SignUtils().appSign(BaseEndpoint.faceIDApiKey, BaseEndpoint.faceIDAppSecret, currtTime, expireTime)
        return sign
    }

}