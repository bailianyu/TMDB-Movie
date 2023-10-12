package com.tmdb.movie.network

import com.tmdb.movie.data.AccountState
import com.tmdb.movie.data.BaseListData
import com.tmdb.movie.data.FavoriteRequest
import com.tmdb.movie.data.ImagesData
import com.tmdb.movie.data.MovieDetails
import com.tmdb.movie.data.MovieItem
import com.tmdb.movie.data.People
import com.tmdb.movie.data.PeopleCredits
import com.tmdb.movie.data.PeopleDetails
import com.tmdb.movie.data.RequestToken
import com.tmdb.movie.data.RequestTokenInfo
import com.tmdb.movie.data.ResponseResult
import com.tmdb.movie.data.SearchItem
import com.tmdb.movie.data.Session
import com.tmdb.movie.data.SessionData
import com.tmdb.movie.data.TMDBConfiguration
import com.tmdb.movie.data.UserData
import com.tmdb.movie.data.WatchlistRequest
import com.tmdb.movie.utils.getLanguage
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("configuration")
    suspend fun getConfiguration(): TMDBConfiguration

    @GET("trending/movie/day")
    suspend fun getMoviesTrending(
        @Query("language") language: String = getLanguage()
    ): BaseListData<MovieItem>

    @GET("trending/tv/day")
    suspend fun getTVTrending(
        @Query("language") language: String = getLanguage()
    ): BaseListData<MovieItem>

    @GET("trending/person/day")
    suspend fun getPeopleTrending(
        @Query("language") language: String = getLanguage()
    ): BaseListData<People>

    @GET("person/popular")
    suspend fun getPopularPeople(
        @Query("language") language: String = getLanguage()
    ): BaseListData<People>

    @GET("movie/now_playing")
    suspend fun getMoviesNowPlaying(
        @Query("page") page: Int = 1,
        @Query("language") language: String = getLanguage()
    ): BaseListData<MovieItem>

    @GET("tv/airing_today")
    suspend fun getTVAiringToday(
        @Query("page") page: Int = 1,
        @Query("language") language: String = getLanguage()
    ): BaseListData<MovieItem>

    @GET("movie/popular")
    suspend fun getMoviesPopular(
        @Query("page") page: Int = 1,
        @Query("language") language: String = getLanguage()
    ): BaseListData<MovieItem>

    @GET("tv/popular")
    suspend fun getTVPopular(
        @Query("page") page: Int = 1,
        @Query("language") language: String = getLanguage()
    ): BaseListData<MovieItem>

    @GET("discover/movie")
    suspend fun getDiscoveryMovies(
        @Query("page") page: Int = 1,
        @Query("language") language: String = getLanguage()
    ): BaseListData<MovieItem>

    @GET("discover/tv")
    suspend fun getDiscoveryTV(
        @Query("page") page: Int = 1,
        @Query("language") language: String = getLanguage()
    ): BaseListData<MovieItem>

    @GET("movie/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: Int,
        @Query("language") language: String = getLanguage(),
        @Query("append_to_response") appendToResponse: String = "credits,videos"
    ): MovieDetails

    @GET("tv/{id}")
    suspend fun getTVDetails(
        @Path("id") id: Int,
        @Query("language") language: String = getLanguage(),
        @Query("append_to_response") appendToResponse: String = "credits,videos"
    ): MovieDetails

    @GET("movie/{id}/images")
    suspend fun getMovieImages(
        @Path("id") id: Int,
        @Query("language") language: String = getLanguage(),
    ): ImagesData

    @GET("tv/{id}/images")
    suspend fun getTVImages(
        @Path("id") id: Int,
        @Query("language") language: String = getLanguage(),
    ): ImagesData

    @GET("person/{id}")
    suspend fun getPeopleDetails(
        @Path("id") id: Int,
        @Query("language") language: String = getLanguage(),
        @Query("append_to_response") appendToResponse: String = "images"
    ): PeopleDetails

    @GET("person/{id}/combined_credits")
    suspend fun getPeopleCredits(
        @Path("id") id: Int,
        @Query("language") language: String = getLanguage(),
    ): PeopleCredits

    @GET("search/multi")
    suspend fun search(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = getLanguage()
    ): BaseListData<SearchItem>

    @GET("authentication/token/new")
    suspend fun getRequestToken(): RequestTokenInfo

    @POST("authentication/session/new")
    suspend fun createSession(@Body body: RequestToken): SessionData

    @GET("account")
    suspend fun getUserData(@Query("session_id") sessionId: String): UserData

    @HTTP(method = "DELETE", path = "authentication/session", hasBody = true)
    suspend fun deleteSession(@Body session: Session): ResponseResult

    @GET("movie/{movie_id}/account_states")
    suspend fun getMovieAccountState(@Path("movie_id") movieId: Int, @Query("session_id") sessionId: String): AccountState

    @GET("tv/{tv_id}/account_states")
    suspend fun getTVAccountState(@Path("tv_id") tvId: Int, @Query("session_id") sessionId: String): AccountState

    @POST("account/{account_id}/favorite")
    suspend fun addToFavorite(@Path("account_id") accountId: Int, @Body body: FavoriteRequest): ResponseResult

    @POST("account/{account_id}/watchlist")
    suspend fun addToWatchlist(@Path("account_id") accountId: Int, @Body body: WatchlistRequest): ResponseResult
}