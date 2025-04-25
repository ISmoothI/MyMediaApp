package com.example.mediatracker.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.mediatracker.R
import com.example.mediatracker.data.movie.Movie
import com.example.mediatracker.data.movie.MovieViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun MovieInfoBox(navController: NavController, movie: Movie) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AsyncImage(
                model = File(movie.poster),
                contentDescription = "Movie poster",
                contentScale = ContentScale.Inside,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_foreground),
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).clickable { navController.navigate("add/${movie.movieId}") }
            )

            Column(Modifier.weight(2f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = movie.title, style = MaterialTheme.typography.titleLarge)
                Text(text = movie.director, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text(text = movie.body, maxLines = 3, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = movie.year.toString(), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text(text = "${movie.runtime}m", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun RowOfMovies(navController: NavController, movie: Movie) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = File(movie.poster),
            contentDescription = "Movie poster",
            contentScale = ContentScale.Fit,
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            error = painterResource(R.drawable.ic_launcher_foreground),
            modifier = Modifier.height(170.dp).width(116.dp).clip(RoundedCornerShape(8.dp)).clickable { navController.navigate("add/${movie.movieId}") }
        )
        Text(text = movie.title, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieTopAppBar(navController: NavController, scope: CoroutineScope, drawerState: DrawerState) {
    CenterAlignedTopAppBar(
        title = {

        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    if (drawerState.isClosed) {
                        drawerState.open()
                    } else {
                        drawerState.close()
                    }
                }
            }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate("add/${0}") }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add movie")
            }
            IconButton(onClick = { navController.navigate("search") }) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun HomePreview(){
    val navController = rememberNavController()

    Home(navController)
}

@Composable
fun Home(navController: NavHostController, movieViewModel: MovieViewModel = hiltViewModel()) {
    val isLoading by movieViewModel.isLoading.collectAsState()
    val isLoadingAnother by movieViewModel.isLoadingAnother.collectAsState()
    val movieList by movieViewModel.entries.collectAsState()
    val genreMovieList by movieViewModel.genresWithMoviesEntries.collectAsState()

    LaunchedEffect(null) {
        movieViewModel.getAllMovies()
        movieViewModel.getGenresWithMovies()
    }

    if(isLoading || isLoadingAnother) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    else {
        val scrollState = rememberScrollState()

        Column(modifier = Modifier.padding(12.dp).verticalScroll(scrollState), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (movieList.isEmpty()) {
                Text(text = "Import a file or start building your media list to get started!")
            }
            else {
                //LATEST ENTRY
                Text(text = "Latest Entry", style = MaterialTheme.typography.headlineSmall)
                MovieInfoBox(navController = navController, movie = movieList.last())

                //WATCHLIST
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Watchlist", style = MaterialTheme.typography.headlineSmall)
                        IconButton(onClick = {  }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Search for similar titles")
                        }
                    }
                    LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        items(movieList) { movie ->
                            RowOfMovies(navController, movie)
                        }
                    }
                }
                //RANDOM PICKS ROW
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Random Picks", style = MaterialTheme.typography.headlineSmall)
                        IconButton(onClick = { } ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Search for similar titles")
                        }
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val randRecList = listOf(movieList.random(), movieList.random(), movieList.random(), movieList.random(), movieList.random())

                        items(randRecList) { movie ->
                            Column(horizontalAlignment = Alignment.Start) {
                                AsyncImage(
                                    model = File(movie.poster),
                                    contentDescription = "Movie poster",
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                                    error = painterResource(R.drawable.ic_launcher_foreground),
                                    modifier = Modifier.height(170.dp).width(300.dp).clip(RoundedCornerShape(8.dp)).clickable {
                                        navController.navigate("add/${movie.movieId}")
                                    }
                                )
                                Text(text = movie.title, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.headlineSmall)
                            }
                        }
                    }
                }
                //SHOW A ROW OF MOVIES FOR EACH GENRE IF THE GENRE HAS MOVIES
                genreMovieList.forEach { genreList ->
                    if(genreList.movie.isNotEmpty()) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = genreList.genre.name, style = MaterialTheme.typography.headlineSmall)
                                IconButton(onClick = { navController.navigate("genre/${genreList.genre.genreId}") }) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Search for similar titles")
                                }
                            }
                            LazyRow {
                                items(genreList.movie) { movie ->
                                    RowOfMovies(navController = navController, movie = movie)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}