package org.api

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.security.AccessManager
import io.javalin.core.security.Role
import io.javalin.core.util.RouteOverviewPlugin
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.http.UnauthorizedResponse
import javalinjwt.JWTAccessManager
import org.model.*
import org.token.InvalidToken
import org.token.TokenController

class Api(){
    fun start(port: Int =7000){
        val system = getMovieSystem()
        val imdbController = ImdbController(system)
        val userController = UserController(system)

        val app = Javalin.create{
            it.defaultContentType = "application/json"
            it.registerPlugin(RouteOverviewPlugin("/routes"))
            it.accessManager(MovieRoleManager(system));
        }
        app.routes {
            path("movies") {
                post(imdbController::post, setOf(MoviesRoles.USER))
                get(imdbController::get, setOf(MoviesRoles.USER))
                path(":id") {
                    put(imdbController::putById, setOf(MoviesRoles.USER))
                    get(imdbController::getById, setOf(MoviesRoles.USER))
                    delete(imdbController::deleteById, setOf(MoviesRoles.USER))
                }
            }
            path("login") {
                post(userController::login, setOf(MoviesRoles.ANYONE))
            }
            path("register"){
                post(userController::register, setOf(MoviesRoles.ANYONE))
            }
        }
        app.exception(BadRequestResponse::class.java) { e, ctx ->
            ctx.status(404).json(ErrorResponse(e.message!!))
        }

        app.start(port)
    }
}

fun main(args: Array<String>) {
    Api().start(7000)
}