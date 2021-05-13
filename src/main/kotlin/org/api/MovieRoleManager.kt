package org.api

import io.javalin.core.security.AccessManager
import io.javalin.core.security.Role
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.http.UnauthorizedResponse
import org.model.MovieSystem
import org.model.NotFound
import org.token.InvalidToken
import org.token.TokenController

internal enum class MoviesRoles : Role {
    ANYONE, USER
}

class MovieRoleManager(private val system: MovieSystem) : AccessManager {
    private val tokenController = TokenController()

    override fun manage(handler: Handler, ctx: Context, roles: MutableSet<Role>) {
        val token = ctx.header("Authorization")
        when {
            roles.contains(MoviesRoles.ANYONE) -> handler.handle(ctx)
            token == null -> throw UnauthorizedResponse()
            roles.contains(MoviesRoles.USER) -> {
                try {
                    val id =  tokenController.validate(token)
                    system.getUser(id)
                    ctx.attribute("id",id)
                    handler.handle(ctx)
                } catch (e: NotFound) {
                    throw UnauthorizedResponse()
                } catch (e: InvalidToken) {
                    throw UnauthorizedResponse()
                }
            }
        }
    }
}