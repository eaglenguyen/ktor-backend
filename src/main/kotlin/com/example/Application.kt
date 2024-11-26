package com.example

import com.example.data.user.MongoUserDataSource
import com.example.plugins.configureMonitoring
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.security.hashing.SHA256HashingService
import com.example.security.token.JwtTokenService
import com.example.security.token.TokenConfig
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.server.application.Application


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {

    val mongoPw = System.getenv("MONGO_PW")
    val uri = "mongodb+srv://EagleN:$mongoPw@clouddb.6qyx4.mongodb.net/ktor-auth?retryWrites=true&w=majority&appName=CloudDB"
    val db = MongoClient.create(uri)
        .getDatabase("ktor-auth")
    val userDataSource = MongoUserDataSource(db)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )

    val hashingService = SHA256HashingService()

    configureSecurity(tokenConfig)
    configureRouting(hashingService, userDataSource, tokenService, tokenConfig)
    configureSerialization()
    configureMonitoring()

}
