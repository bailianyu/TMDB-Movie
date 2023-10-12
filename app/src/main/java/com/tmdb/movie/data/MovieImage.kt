package com.tmdb.movie.data

import com.google.gson.annotations.SerializedName


data class ImagesData(
    @SerializedName("backdrops")
    val backdrops: List<MovieImage>? = null,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("logos")
    val logos: List<MovieImage>? = null,
    @SerializedName("posters")
    val posters: List<MovieImage>? = null
)

data class MovieImage(
    @SerializedName("aspect_ratio")
    val aspectRatio: Float = 0.0f,
    @SerializedName("file_path")
    val filePath: String? = null,
    @SerializedName("height")
    val height: Int = 0,
    @SerializedName("iso_639_1")
    val iso6391: String? = null,
    @SerializedName("vote_average")
    val voteAverage: Float = 0.0f,
    @SerializedName("vote_count")
    val voteCount: Int = 0,
    @SerializedName("width")
    val width: Int = 0,
)
