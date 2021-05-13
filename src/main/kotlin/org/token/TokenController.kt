package org.token

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.javalin.http.Context
import javalinjwt.JWTGenerator
import javalinjwt.JWTProvider
import javalinjwt.JavalinJWT
import org.model.User

class InvalidToken(): Exception("Invalid token")


class TokenController {
    private val algorithm: Algorithm = Algorithm.HMAC256("very_secret")
    private val generator: JWTGenerator<User> = JWTGenerator<User> { user: User, alg: Algorithm? ->
        val token: JWTCreator.Builder = JWT.create()
            .withClaim("id", user.id)
        token.sign(alg)
    }
    private val verifier: JWTVerifier = JWT.require(algorithm).build()
    private val provider = JWTProvider(algorithm, generator, verifier)

    fun generate(user: User) : String = provider.generateToken(user)

    fun validate(token: String) : String {
        val jwt = provider.validateToken(token)
        if (jwt.isPresent && jwt.get().claims.containsKey("id")) {
            return jwt.get().getClaim("id").asString()
        }
        else {
            throw InvalidToken()
        }
    }
}