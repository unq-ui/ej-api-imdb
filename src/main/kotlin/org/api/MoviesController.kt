package org.api

import org.model.*
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context

data class StatusOk(val status: String = "ok")
data class ErrorResponse(val result: String)
data class MovieBody(var title: String, var description: String){
    fun toDraftMovie(): DraftMovie {
        return DraftMovie(this.title, this.description)
    }
}

data class MoviesRespose(val content: List<Movie>)

class ImdbController(val system: MovieSystem){

    fun get(ctx: Context) {
        ctx.status(200).json(MoviesRespose(system.movies))
    }

    fun getById(ctx: Context) {
        try {
            val id = ctx.pathParam("id")
            ctx.status(200).json(system.getMovie(id))
        } catch (e: NotFound) {
            throw BadRequestResponse(e.msg)
        }
    }

    fun post(ctx: Context){
        try {
            val body = ctx.body<MovieBody>()
            ctx.status(201).json(system.addMovie(body.toDraftMovie()))
        } catch (e : RepeatedException) {
            throw BadRequestResponse(e.msg)
        }
    }

    fun putById(ctx: Context){
        try {
            val id = ctx.pathParam("id")
            val body = ctx.body<MovieBody>()
            ctx.status(201).json(system.editMovie(id,body.toDraftMovie()))
        } catch (e : NotFound) {
            throw BadRequestResponse(e.msg)
        }
    }

    fun deleteById(ctx: Context){
        val id = ctx.pathParam("id")
        try {
            system.getMovie(id)
            system.removeMovie(id)
            ctx.status(200).json(StatusOk())
        } catch (e: NotFound ){
            throw BadRequestResponse(e.msg)
        }
    }
}