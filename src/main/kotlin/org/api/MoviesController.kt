package org.api

import org.model.*
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import io.javalin.http.UnauthorizedResponse

data class StatusOk(val status: String = "ok")
data class ErrorResponse(val result: String)
data class MovieBody(var title: String, var description: String, var cover: String){
    fun toDraftMovie(): DraftMovie {
        return DraftMovie(this.title, this.description)
    }
}

class ImdbController(val system: MovieSystem){

    fun getById(ctx: Context) {
        val userId = getUserId(ctx)
        val movieId = ctx.pathParam("id")
        try {

            ctx.status(200).json(system.getMovie(userId,movieId))
        } catch (e: NotFound) {
            throw BadRequestResponse(e.msg)
        }
    }

    fun get(ctx: Context){
        val userId = getUserId(ctx)
        try {
            ctx.status(200).json(system.getUser(userId).movies)
        } catch (e: NotFound) {
            throw BadRequestResponse(e.msg)
        }
    }

    fun post(ctx: Context){
        val userId = getUserId(ctx)
        try {
            val body = ctx.body<MovieBody>()
            ctx.status(201).json(system.addMovie(userId,body.toDraftMovie()))
        } catch (e : RepeatedException) {
            throw BadRequestResponse(e.msg)
        }
    }

    fun putById(ctx: Context){
        val userId = getUserId(ctx)
        val movieId = ctx.pathParam("id")
        try {
            val body = ctx.body<MovieBody>()
            ctx.status(201).json(system.editMovie(userId,movieId,body.toDraftMovie()))
        } catch (e : NotFound) {
            throw BadRequestResponse(e.msg)
        }
    }

    fun deleteById(ctx: Context){
        val userId = getUserId(ctx)
        val movieId = ctx.pathParam("id")
        system.removeMovie(userId,movieId)
        ctx.status(200).json(StatusOk())
    }

    private fun getUserId(ctx: Context) : String {
        val id : String? = ctx.attribute("id")
        if (id == null) {
            UnauthorizedResponse()
        }
        return id!!
    }
}