package org.api

import org.model.*
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.util.RouteOverviewPlugin
import io.javalin.http.BadRequestResponse

class Api(){
    fun start(port: Int =7000){
        val imdbController = ImdbController(getMovieSystem())
        val app = Javalin.create{
            it.defaultContentType = "application/json"
            it.registerPlugin(RouteOverviewPlugin("/routes"))
        }
        app.routes {
            path("movie") {
                get(imdbController::get)
                post(imdbController::post)
                path(":id") {
                    put(imdbController::putById)
                    get(imdbController::getById)
                    delete(imdbController::deleteById)
                }
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