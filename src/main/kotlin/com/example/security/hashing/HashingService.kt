package com.example.security.hashing

interface HashingService {
    fun generateHash(value : String, hashLength: Int = 32) : Hash
    fun verify(value: String, hash: Hash) : Boolean
}