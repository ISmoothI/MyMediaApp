package com.example.mediatracker.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mediatracker.data.movie.Movie
import com.example.mediatracker.data.movie.MovieViewModel
import com.example.mediatracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(navController: NavHostController, movieViewModel: MovieViewModel = hiltViewModel()){
    var searchText by remember { mutableStateOf("") }
    val searchResults by movieViewModel.getMoviesByTitle.collectAsState(emptyList())
    var isTextActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {},
                actions = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    SearchBar(
                        query = searchText,
                        onQueryChange = {
                            searchText = it
                            movieViewModel.setSearch(it)
                        },
                        onSearch = {},
                        active = false,
                        onActiveChange = { isTextActive = it },
                        leadingIcon = {
                            IconButton(onClick = {  }) {
                                Image(imageVector = Icons.Filled.Search, contentDescription = "Search")
                            }
                        },
                        placeholder = { Text(text = "Search") },
                        trailingIcon = {
                            if (isTextActive) {
                                IconButton(onClick = {
                                    searchText = ""
                                    movieViewModel.setSearch(searchText)
                                }) {
                                    Image(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                                }
                            }
                        }
                    ) {

                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).padding(12.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(searchResults) { movie ->
                MovieInfoBox(navController, movie)
            }
        }
    }
}