package org.model

data class Movie (val id: String, var title: String, var description: String)
data class DraftMovie (var title: String, var description: String)

class IdGenerator {
    var id = 0
        private set
    fun nextId(): String = "m_${++id}"
}

class RepeatedException(val msg: String): Exception(msg)
class NotFound(val msg: String): Exception("Not found $msg")

class MovieSystem() {

    val movies = mutableListOf<Movie>()
    val idGenerator = IdGenerator()

    fun addMovie(movie: DraftMovie) : Movie {
        checkIfTitleIsNotRepeted(movie.title)
        val movie = Movie(idGenerator.nextId(), movie.title, movie.description)
        movies.add(movie)
        return movie
    }

    fun editMovie(movieId: String, draftMovie: DraftMovie) : Movie{
        val movie = getMovie(movieId)
        movie.title = draftMovie.title
        movie.description = draftMovie.description
        return movie
    }

    fun removeMovie(movieId: String) {
        movies.removeIf { it.id == movieId }
    }

    private fun checkIfTitleIsNotRepeted(title: String) {
        if (movies.any { it.title == title }) throw RepeatedException("Found movie with same title: $title")
    }

    fun getMovie(movieId: String): Movie = movies.find { it.id == movieId } ?: throw NotFound(movieId)
}

fun getMovieSystem(): MovieSystem {
    val movieSystem = MovieSystem()
    movieSystem.addMovie(DraftMovie("The Lord of the Rings: The Fellowship of the Ring", "description 1"))
    movieSystem.addMovie(DraftMovie("The Lord of the Rings: The Two Towers", "description 2"))
    movieSystem.addMovie(DraftMovie("The Lord of the Rings: The Return of the King", "description 3"))
    movieSystem.addMovie(DraftMovie("Pulp Fiction", "description 4"))
    movieSystem.addMovie(DraftMovie("Fight Club", "description 5"))
    movieSystem.addMovie(DraftMovie("Bravehear", "description 6"))
    return movieSystem
}