package org.model

class IdGenerator {
    var movieId = 0
        private set
    var userId = 0
        private set
    fun nextMovieId(): String = "m_${++movieId}"
    fun nextUserId(): String = "u_${++userId}"
}
