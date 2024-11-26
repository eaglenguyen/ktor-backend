package com.example.plugins

import com.example.authenticate
import com.example.data.user.UserDataSource
import com.example.getSecretInfo
import com.example.security.hashing.HashingService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import com.example.signIn
import com.example.signUp
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        signUp(hashingService, userDataSource)
        signIn(hashingService,userDataSource,tokenService,tokenConfig)
        authenticate()
        getSecretInfo()
    }
}
