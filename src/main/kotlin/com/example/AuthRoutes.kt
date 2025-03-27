package com.example

import com.example.data.request.AuthRequest
import com.example.data.responses.AuthResponse
import com.example.data.user.User
import com.example.data.user.UserDataSource
import com.example.security.hashing.HashingService
import com.example.security.hashing.Hash
import com.example.security.token.TokenClaim
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
) {
     post("signup") {
         val request =  call.receiveNullable<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
         val areFieldsBlank = request.name!!.isBlank() || request.email.isBlank() || request.password.isBlank()
         val isPwTooShort = request.password.length < 8
         if (areFieldsBlank || isPwTooShort) {
             call.respond(HttpStatusCode.Conflict)
             return@post
         }

         // Check if the username is already in use
         val existingUsername = userDataSource.getUserByEmail(request.email)
         if (existingUsername != null) {
             call.respond(HttpStatusCode.Conflict, "Email is already in use")
             return@post
         }

         val hashedPw =  hashingService.generateHash(request.password)
         val user = User(
             name = request.name,
             email = request.email,
             password = hashedPw.hash
             ,
             )
         val wasAcknowledged = userDataSource.insertUser(user)
         if(!wasAcknowledged) {
             call.respond(HttpStatusCode.Conflict)
             return@post
         }
         call.respond(HttpStatusCode.OK)
    }
}




fun Route.signIn(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signin") {
        val request =  call.receiveNullable<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUserByEmail(request.email)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "Incorrect Email")
            return@post
        }

        val isValidPW = hashingService.verify(
            value = request.password,
            hash = Hash(
                hash = user.password,
            )
        )
        if (!isValidPW){
            call.respond(HttpStatusCode.Conflict, "Incorrect Password")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )
        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token,
                name = user.name ?: "Empty",
                email = user.email
            )
        )
    }
}



fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your userID is $userId")
        }
    }
}