package com.tmdb.movie.ui.detail

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tmdb.movie.R
import com.tmdb.movie.component.ErrorPage
import com.tmdb.movie.data.AccountState
import com.tmdb.movie.data.Cast
import com.tmdb.movie.data.Credits
import com.tmdb.movie.data.Genre
import com.tmdb.movie.data.ImageType
import com.tmdb.movie.data.ImagesData
import com.tmdb.movie.data.MediaType
import com.tmdb.movie.data.MovieDetails
import com.tmdb.movie.ui.detail.component.MovieBackdropLayout
import com.tmdb.movie.ui.detail.component.MovieCastLayout
import com.tmdb.movie.ui.detail.component.MovieDetailImageComponent
import com.tmdb.movie.ui.detail.component.MovieDetailLoadingComponent
import com.tmdb.movie.ui.detail.component.MovieMiddleLayout
import com.tmdb.movie.ui.detail.component.MovieOverviewLayout
import com.tmdb.movie.ui.detail.component.MovieVideoComponent
import com.tmdb.movie.ui.detail.vm.MovieDetailUiState
import com.tmdb.movie.ui.detail.vm.MovieDetailViewModel
import com.tmdb.movie.ui.theme.TMDBMovieTheme
import com.tmdb.movie.utils.playYoutubeVideo
import com.tmdb.movie.utils.shareTMDBMedia

@Composable
fun MovieDetailRoute(
    mediaId: Int,
    @MediaType mediaType: Int,
    movieFrom: Int,
    onBackClick: (Boolean) -> Unit,
    onNavigateToPeopleDetail: (Int) -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel(),
) {

    BackHandler { onBackClick(movieFrom == 0) }
    val context = LocalContext.current
    val config by viewModel.configStream.collectAsStateWithLifecycle()
    val detailsUiState by viewModel.movieDetail.collectAsStateWithLifecycle()
    val movieImages: ImagesData? by viewModel.movieImages.collectAsStateWithLifecycle()
    val accountState by viewModel.accountState.collectAsStateWithLifecycle()

    LaunchedEffect(config.userData?.sessionId) {
        val sessionId = config.userData?.sessionId
        if (!sessionId.isNullOrEmpty()) {
            viewModel.requestAccountState(sessionId)
        }
    }

    MovieDetailScreen(
        mediaType = mediaType,
        movieDetailUiState = detailsUiState,
        movieImages = movieImages,
        accountState = accountState,
        onBackClick = { onBackClick(movieFrom == 0) },
        onBuildImage = { url, type ->
            config.buildImageUrl(type, url)
        },
        onVideoClick = { videoKey ->
            playYoutubeVideo(context, videoKey)
        },
        onRetry = viewModel::onRetry,
        onMoreCasts = { },
        onMoreVideos = { },
        onMoreImages = { id, type -> },
        onPeopleDetail = onNavigateToPeopleDetail,
        onFavorite = {
            val favorite = accountState?.favorite ?: false
            val sessionId = config.userData?.sessionId
            val accountId = config.userData?.id
            if (!sessionId.isNullOrEmpty() && accountId != null) {
                viewModel.toggleFavorite(accountId, sessionId, !favorite)
            }
        },
        onWatchlist = {
            val watchlist = accountState?.watchlist ?: false
            val sessionId = config.userData?.sessionId
            val accountId = config.userData?.id
            if (!sessionId.isNullOrEmpty() && accountId != null) {
                viewModel.toggleWatchlist(accountId, sessionId, !watchlist)
            }
        },
        onAddList = { },
        onShare = {
            shareTMDBMedia(context, mediaId, mediaType)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    @MediaType mediaType: Int,
    movieDetailUiState: MovieDetailUiState,
    movieImages: ImagesData?,
    accountState: AccountState?,
    onBackClick: (Boolean) -> Unit,
    onBuildImage: (String?, @ImageType Int) -> String? = { url, _ -> url },
    onVideoClick: (String?) -> Unit = {},
    onRetry: () -> Unit = {},
    onMoreCasts: (Int) -> Unit,
    onMoreVideos: (Int) -> Unit,
    onMoreImages: (Int, @ImageType Int) -> Unit,
    onPeopleDetail: (Int) -> Unit,
    onFavorite: () -> Unit,
    onWatchlist: () -> Unit,
    onAddList: () -> Unit,
    onShare: () -> Unit,
) {

    val title = if (movieDetailUiState is MovieDetailUiState.Success) {
        movieDetailUiState.movieDetails.getMovieName(mediaType) ?: ""
    } else {
        ""
    }
    val scrollState = rememberScrollState()
    var topBarHeight by remember { mutableIntStateOf(0) }
    var topBarAlpha by remember { mutableFloatStateOf(0f) }
    var showDropdownMenu by remember { mutableStateOf(false) }

    LaunchedEffect(scrollState.value) {
        val scrollValue = scrollState.value.toFloat()
        val deltaY = scrollValue.coerceAtMost(topBarHeight.toFloat())
        topBarAlpha = deltaY / topBarHeight.toFloat()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        AnimatedContent(
            targetState = movieDetailUiState,
            label = "",
            transitionSpec = {
                (fadeIn(animationSpec = tween(500)))
                    .togetherWith(fadeOut(animationSpec = tween(500)))
            }
        ) { targetState ->
            when (targetState) {
                is MovieDetailUiState.Error -> ErrorPage(onRetry = onRetry)
                MovieDetailUiState.Loading -> MovieDetailLoadingComponent()
                is MovieDetailUiState.Success -> MovieDetailComponent(
                    mediaType = mediaType,
                    movieDetails = targetState.movieDetails,
                    movieImages = movieImages,
                    onBuildImage = onBuildImage,
                    onVideoClick = onVideoClick,
                    scrollState = scrollState,
                    onMoreCasts = onMoreCasts,
                    onMoreVideos = onMoreVideos,
                    onMoreImages = onMoreImages,
                    onPeopleDetail = onPeopleDetail,
                )
            }
        }
        TopAppBar(
            modifier = Modifier.onGloballyPositioned {
                topBarHeight = it.size.height
            },
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = topBarAlpha
                        )
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    onBackClick(true)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = ""
                    )
                }
            },
            actions = {
                IconButton(onClick = onFavorite) {
                    Icon(
                        painter = painterResource(id = if (accountState?.favorite == true) R.drawable.baseline_favorite_24 else R.drawable.outline_favorite_24),
                        contentDescription = "",
                        tint = if (accountState?.favorite == true) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box {
                    IconButton(
                        onClick = {
                            showDropdownMenu = true
                        }) {
                        Icon(
                            painter = painterResource(id = R.drawable.more_horiz_24),
                            contentDescription = "",
                        )
                    }
                    DropdownMenu(
                        expanded = showDropdownMenu,
                        modifier = Modifier.align(Alignment.TopEnd),
                        properties = PopupProperties(dismissOnClickOutside = true),
                        onDismissRequest = { showDropdownMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(if (accountState?.watchlist == true) R.string.key_remove_from_watchlist else R.string.key_add_to_watchlist)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = if (accountState?.watchlist == true) R.drawable.baseline_bookmark_added_24 else R.drawable.outline_bookmark_add_24),
                                    contentDescription = "",
                                    tint = if (accountState?.watchlist == true) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = onWatchlist,
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.key_add_to_list)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_format_list_24),
                                    contentDescription = "",
                                )
                            },
                            onClick = onAddList,
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.key_share)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_share_24),
                                    contentDescription = "",
                                )
                            },
                            onClick = {
                                onShare()
                                showDropdownMenu = false
                            })
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = topBarAlpha),
            )
        )
    }
}

@Composable
fun MovieDetailComponent(
    @MediaType mediaType: Int,
    scrollState: ScrollState,
    movieDetails: MovieDetails? = null,
    movieImages: ImagesData? = null,
    onBuildImage: (String?, @ImageType Int) -> String? = { url, _ -> url },
    onVideoClick: (String?) -> Unit = {},
    onMoreCasts: (Int) -> Unit,
    onMoreVideos: (Int) -> Unit,
    onMoreImages: (Int, @ImageType Int) -> Unit,
    onPeopleDetail: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        MovieBackdropLayout(
            mediaType = mediaType,
            movieDetails = movieDetails,
            onBuildImage = onBuildImage
        )
        MovieMiddleLayout(
            modifier = Modifier.padding(bottom = 24.dp),
            mediaType = mediaType,
            movieDetails = movieDetails
        )
        MovieOverviewLayout(
            modifier = Modifier.padding(bottom = 24.dp),
            movieDetails = movieDetails,
            onBuildImage = onBuildImage
        )
        if (movieDetails?.credits?.cast?.isNotEmpty() == true) {
            MovieCastLayout(
                modifier = Modifier.padding(bottom = 24.dp),
                castList = movieDetails.credits.cast,
                onBuildImage = onBuildImage,
                onMoreCasts = { onMoreCasts(movieDetails.id) },
                onPeopleDetail = onPeopleDetail
            )
        }
        if (movieDetails?.videos?.results?.isNotEmpty() == true) {
            MovieVideoComponent(
                modifier = Modifier.padding(bottom = 24.dp),
                videos = movieDetails.videos.results,
                onVideoClick = onVideoClick,
                onMoreVideos = { onMoreVideos(movieDetails.id) }
            )
        }
        if (movieImages?.backdrops?.isNotEmpty() == true) {
            MovieDetailImageComponent(
                modifier = Modifier.padding(bottom = 24.dp),
                imageList = movieImages.backdrops,
                imageType = ImageType.BACKDROP,
                onBuildImage = onBuildImage,
                onMoreImages = { onMoreImages(movieDetails?.id ?: 0, ImageType.BACKDROP) }
            )
        }
        if (movieImages?.posters?.isNotEmpty() == true) {
            MovieDetailImageComponent(
                modifier = Modifier.padding(bottom = 24.dp),
                imageList = movieImages.posters,
                imageType = ImageType.POSTER,
                onBuildImage = onBuildImage,
                onMoreImages = { onMoreImages(movieDetails?.id ?: 0, ImageType.POSTER) }
            )
        }
    }
}


@Preview(showBackground = true)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MovieDetailScreenPreview() {
    val movieDetails = MovieDetails(
        title = "阿凡达：水之道",
        releaseDate = "2022-12-14",
        tagline = "传奇导演詹姆斯·卡梅隆全新巨作",
        genres = listOf(Genre(id = 1, name = "动作"), Genre(id = 2, name = "冒险"), Genre(id = 3, name = "科幻")),
        voteAverage = 7.655f,
        revenue = 232025.00,
        overview = "影片设定在《阿凡达》的剧情落幕十余年后，讲述了萨利一家（杰克、奈蒂莉和孩子们）的故事：危机未曾消散，一家人拼尽全力彼此守护、奋力求生，并历经艰险磨难。影片设定在《阿凡达》的剧情落幕十余年后，讲述了萨利一家（杰克、奈蒂莉和孩子们）的故事：危机未曾消散，一家人拼尽全力彼此守护、奋力求生，并历经艰险磨难。影片设定在《阿凡达》的剧情落幕十余年后，讲述了萨利一家（杰克、奈蒂莉和孩子们）的故事：危机未曾消散，一家人拼尽全力彼此守护、奋力求生，并历经艰险磨难。",
        credits = Credits(
            cast = listOf(
                Cast(name = "Sam Worthington", character = "Jake Sully", profilePath = "/mflBcox36s9ZPbsZPVOuhf6axaJ.jpg"),
                Cast(name = "Sam Worthington", character = "Jake Sully", profilePath = "/mflBcox36s9ZPbsZPVOuhf6axaJ.jpg"),
                Cast(name = "Sam Worthington", character = "Jake Sully", profilePath = "/mflBcox36s9ZPbsZPVOuhf6axaJ.jpg"),
            ),
        ),
        videos = com.tmdb.movie.data.Videos(
            results = listOf(
                com.tmdb.movie.data.Video(
                    key = "https://www.youtube.com/watch?v=5PSNL1qE6VY",
                    name = "阿凡达：水之道",
                    site = "YouTube",
                    type = "Trailer",
                ),
                com.tmdb.movie.data.Video(
                    key = "https://www.youtube.com/watch?v=5PSNL1qE6VY",
                    name = "阿凡达：水之道",
                    site = "YouTube",
                    type = "Trailer",
                ),
                com.tmdb.movie.data.Video(
                    key = "https://www.youtube.com/watch?v=5PSNL1qE6VY",
                    name = "阿凡达：水之道",
                    site = "YouTube",
                    type = "Trailer",
                ),
            )
        )
    )

    TMDBMovieTheme {
        MovieDetailScreen(
            mediaType = MediaType.MOVIE,
            movieDetailUiState = MovieDetailUiState.Success(movieDetails),
            movieImages = null,
            onBackClick = {},
            onBuildImage = { url, _ -> url },
            onVideoClick = {},
            onRetry = {},
            onMoreCasts = {},
            onMoreVideos = {},
            onMoreImages = { _, _ -> },
            onPeopleDetail = { },
            accountState = null,
            onFavorite = {},
            onWatchlist = {},
            onAddList = {},
            onShare = {},
        )
    }
}