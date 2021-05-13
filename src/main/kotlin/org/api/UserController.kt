package org.api

import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import jdk.nashorn.internal.parser.Token
import org.model.MovieSystem
import org.model.NotFound
import org.model.RepeatedException
import org.model.User
import org.token.TokenController

data class RegisterUserDTO(var email: String, var password: String)
class UserDTO(user: User){
    val id = user.id
    val email = user.email
}


class UserController(val system: MovieSystem) {

    val tokenController = TokenController()

    fun register(ctx: Context){
        val body = ctx.body<RegisterUserDTO>()
        try {
            val user = system.register(body.email,body.password)
            ctx.header("Authorization", tokenController.generate(user))
            ctx.json(UserDTO(user))
        }
        catch (e: RepeatedException){
            throw BadRequestResponse("Email already used")
        }
    }

    fun login(ctx: Context){
        val body = ctx.body<RegisterUserDTO>()
        try {
            val user = system.login(body.email,body.password)
            ctx.header("Authorization", tokenController.generate(user))
            ctx.json(UserDTO(user))
        }
        catch (e: NotFound){
            throw BadRequestResponse("Email or password incorrect")
        }
    }
}