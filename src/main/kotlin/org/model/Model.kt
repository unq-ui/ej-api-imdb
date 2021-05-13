package org.model

data class User(val id: String, val email: String, val password: String, val movies: MutableList<Movie> = mutableListOf())
data class Movie (val id: String, var title: String, var description: String, var cover: String)
data class DraftMovie (var title: String, var description: String, var cover: String = "")


class RepeatedException(val msg: String): Exception(msg)
class NotFound(val msg: String): Exception("Not found $msg")

class MovieSystem() {

    private val users = mutableListOf<User>()
    private val idGenerator = IdGenerator()


    fun addMovie(userId: String, movie: DraftMovie) {
        val user = getUser(userId)
        checkIfTitleIsNotRepeted(user.movies, movie.title)
        user.movies.add(Movie(idGenerator.nextMovieId(), movie.title, movie.description, movie.cover))
    }

    fun editMovie(userId: String, movieId: String, draftMovie: DraftMovie) : Movie{
        val movie = getMovie(userId, movieId)
        movie.title = draftMovie.title
        movie.description = draftMovie.description
        movie.cover = draftMovie.cover
        return movie
    }

    fun removeMovie(userId: String, movieId: String) {
        val user = getUser(userId)
        user.movies.removeIf { it.id == movieId }
    }

    private fun checkIfTitleIsNotRepeted(movies: MutableList<Movie>, title: String) {
        if (movies.any { it.title == title }) throw RepeatedException("Found movie with same title: $title")
    }

    fun getMovie(userId: String, movieId: String): Movie {
        val user = getUser(userId)
        return user.movies.find { it.id == movieId } ?: throw NotFound(movieId)
    }

    fun getUser(userId: String): User = users.find { it.id == userId } ?: throw NotFound("User")

    fun login(email: String, password: String): User = users.find { it.email == email && it.password == password } ?: throw NotFound("User")

    fun register(email: String, password: String) : User {
        if (isRegisteredEmail(email)) throw RepeatedException("Email repeted")
        val user = User(idGenerator.nextUserId(), email, password)
        users.add(user)
        return user
    }

    private fun isRegisteredEmail(email: String) : Boolean = users.any { it.email == email }
}

fun getMovieSystem(): MovieSystem {
    val system = MovieSystem()

    val userJuan = system.register("juan@gmail.com", "juan")
    val userJuli = system.register("juli@gmail.com", "juli")

    system.addMovie(userJuan.id, DraftMovie("The Lord of the Rings: The Two Towers", "description 2"))
    system.addMovie(userJuan.id, DraftMovie("The Lord of the Rings: The Fellowship of the Ring", "description 1"))
    system.addMovie(userJuan.id, DraftMovie("The Lord of the Rings: The Return of the King", "description 3"))
    system.addMovie(userJuli.id, DraftMovie("Pulp Fiction", "description 4"))
    system.addMovie(userJuli.id, DraftMovie("Fight Club", "description 5"))
    system.addMovie(userJuli.id, DraftMovie("Bravehear", "description 6"))
    return system
}