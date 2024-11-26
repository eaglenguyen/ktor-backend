package com.example.security.hashing


import org.apache.commons.codec.digest.DigestUtils

class SHA256HashingService: HashingService {
    override fun generateHash(value: String, hashLength: Int): Hash {
        // val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
        // val saltAsHex = Hex.encodeHexString(salt)
        val hash = DigestUtils.sha256Hex(value)
        return Hash(
            hash = hash,
            // salt = saltAsHex
        )
    }

    override fun verify(value: String, hash: Hash): Boolean {
        return DigestUtils.sha256Hex(value) == hash.hash
    }
}