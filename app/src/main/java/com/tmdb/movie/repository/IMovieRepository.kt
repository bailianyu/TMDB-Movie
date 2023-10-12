package com.tmdb.movie.repository

import com.tmdb.movie.data.AccountState
import com.tmdb.movie.data.ImagesData
import com.tmdb.movie.data.MovieDetails
import com.tmdb.movie.data.MovieItem
import com.tmdb.movie.data.MediaType
import com.tmdb.movie.data.People
import com.tmdb.movie.data.PeopleCredits
import com.tmdb.movie.data.PeopleDetails
import com.tmdb.movie.data.RequestTokenInfo
import com.tmdb.movie.data.Result
import com.tmdb.movie.data.TMDBConfig
import com.tmdb.movie.data.TMDBConfiguration
import com.tmdb.movie.paging.DiscoveryMoviePagingSource
import com.tmdb.movie.paging.SearchMoviePagingSource
import kotlinx.coroutines.flow.Flow

interface IMovieRepository {

    val configStream: Flow<TMDBConfig>

    suspend fun updateDarkThemeType(themeType: Int)

    suspend fun updateDynamicTheme(useDynamicTheme: Boolean)

    suspend fun getTMDBConfiguration(): TMDBConfiguration

    fun getMoviesTrending(): Flow<Result<List<MovieItem>>>

    fun getTVTrending(): Flow<Result<List<MovieItem>>>

    fun getPeopleTrending(): Flow<Result<List<People>>>

    fun getPopularPeople(): Flow<Result<List<People>>>

    fun getMovieNowPlaying(page: Int = 1): Flow<Result<List<MovieItem>>>

    fun getTVAiringToday(page: Int = 1): Flow<Result<List<MovieItem>>>

    fun getMoviePopular(page: Int = 1): Flow<Result<List<MovieItem>>>

    fun getTVPopular(page: Int = 1): Flow<Result<List<MovieItem>>>

    fun getDiscoveryMoviePagingSource(discoveryType: Int): DiscoveryMoviePagingSource

    fun getMovieDetails(movieId: Int): Flow<Result<MovieDetails>>

    fun getTVDetails(id: Int): Flow<Result<MovieDetails>>

    fun getMovieImages(id: Int): Flow<Result<ImagesData>>

    fun getTVImages(id: Int): Flow<Result<ImagesData>>

    fun getPeopleDetails(id: Int): Flow<Result<PeopleDetails>>

    fun getPeopleCredits(id: Int): Flow<Result<PeopleCredits>>

    fun getSearchMoviePagingSource(query: String): SearchMoviePagingSource

    fun getRequestToken(): Flow<Result<RequestTokenInfo>>

    fun refreshUserData(requestToken: String): Flow<Result<Boolean>>

    fun signOut(sessionId: String): Flow<Result<Boolean>>

    fun getAccountState(id: Int, sessionId: String, @MediaType mediaType: Int): Flow<Result<AccountState>>

    fun markAsFavorite(accountId: Int, sessionId: String, @MediaType mediaType: Int, mediaId: Int, favorite: Boolean): Flow<Result<Boolean>>

    fun addToWatchlist(accountId: Int, sessionId: String, @MediaType mediaType: Int, mediaId: Int, watchlist: Boolean): Flow<Result<Boolean>>
}