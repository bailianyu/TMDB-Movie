package com.tmdb.movie.ui.account.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tmdb.movie.R
import com.tmdb.movie.data.AccountMediaType
import com.tmdb.movie.ext.customTabIndicatorOffset
import com.tmdb.movie.ui.theme.TMDBMovieTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountMediaListsTopBar(
    modifier: Modifier = Modifier,
    @AccountMediaType accountMediaType: Int,
    onBackClick: (Boolean) -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = stringResource(
                    id = when (accountMediaType) {
                        AccountMediaType.FAVORITE -> R.string.key_favorite
                        AccountMediaType.WATCHLIST -> R.string.key_watchlist
                        else -> R.string.key_ratings
                    }
                ),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
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
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    )
}

@Composable
fun AccountMediaListsTabRow(
    modifier: Modifier = Modifier,
    tabLists: List<String>,
    selectedTabIndex: Int = 0,
    onTabSelected: (Int) -> Unit = {},
) {

    TabRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        indicator = { tabPositions ->
            if (selectedTabIndex < tabPositions.size) {
                TabRowDefaults.Indicator(
                    modifier = Modifier
                        .customTabIndicatorOffset(tabPositions[selectedTabIndex], 50.dp)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
                    height = 4.dp
                )
            }
        },
        divider = {}
    ) {
        tabLists.forEachIndexed { index, title ->
            Tab(modifier = Modifier,
                selected = index == selectedTabIndex,
                onClick = { onTabSelected(index) }
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (index == selectedTabIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AccountMediaListsTopBarPreview() {
    TMDBMovieTheme {
        AccountMediaListsTopBar(
            onBackClick = {},
            accountMediaType = AccountMediaType.FAVORITE
        )
    }
}

@Composable
@Preview(showBackground = true)
fun AccountMediaListsTabRowPreview() {
    TMDBMovieTheme {
        AccountMediaListsTabRow(
            tabLists = listOf("Movies", "TV Shows"),
            selectedTabIndex = 0
        )
    }
}