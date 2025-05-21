package com.example.mediatracker.compose

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mediatracker.data.movie.Movie
import com.example.mediatracker.data.movie.MovieViewModel
import java.io.InputStream

@Preview(showBackground = true)
@Composable
fun FilesPreview(){
    val navController = rememberNavController()

    Files(navController)
}

fun importFile(movieViewModel: MovieViewModel, context: Context, csvUri: Uri) {
    //get file location picked by user via URI and read the data
    val inputStream: InputStream? = context.contentResolver.openInputStream(csvUri)
    val reader = inputStream?.bufferedReader()
    val regex = Regex("\"([^\"]*)\",(\\d{4}),\"([^\"]*)\",\"([^\"]*)\",(\\d+),\"([^\"]*)\",(\\d{1,2}),\"([^\"]*)\"")

    //read header line and skip it
    reader?.readLine()

    //read each movie entry and add to database
    reader?.forEachLine { line ->
        val movieRegexVals = regex.findAll(line)

        movieRegexVals.forEach { match ->
            movieViewModel.upsertMovie(Movie(
                title = match.groupValues[1],
                year = match.groupValues[2].toInt(),
                director = match.groupValues[3],
                body = match.groupValues[4],
                runtime = match.groupValues[5].toInt(),
                tagline = match.groupValues[6],
                rating = match.groupValues[7].toInt(),
                note = match.groupValues[8],
                watchlist = false,
                completed = false
            ))
        }
    }

    inputStream?.close()
}

fun exportFile(context: Context, dirUri: Uri, movieList: List<Movie>) {
    //get directory location picked by user and create a writer
    val bw = context.contentResolver.openOutputStream(dirUri)

    //write the headers
    bw?.write(("title,year,director,body,runtime,tagline,rating,note\n").toByteArray())

    //write each movie entry into the csv file
    movieList.forEach { movie ->
        bw?.write(("\"${movie.title}\",${movie.year},\"${movie.director}\",\"${movie.body}\",${movie.runtime},\"${movie.tagline}\",${movie.rating},\"${movie.note}\"\n").toByteArray())
        println(movie.toString())
    }

    bw?.flush()
    bw?.close()
}

fun fileColumn(): Map<String, String> {
    val columnMap: Map<String, String> = mapOf(
        "Title" to "title of movie",
        "Year" to "year of movie release",
        "Director" to "director of movie",
        "Details" to "details/summary of movie",
        "Runtime" to "length of movie",
        "Tagline" to "movie tagline",
        "Rating" to "rating from 1 - 10",
        "Notes" to "notes of movie",
    )

    return columnMap
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Files(navController: NavHostController, movieViewModel: MovieViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val movieList by movieViewModel.entries.collectAsState()
    val isListLoading by movieViewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()
    var isImportLoading by remember { mutableStateOf(false) }
    var isExportLoading by remember { mutableStateOf(false) }
    var showImportInfo by remember { mutableStateOf(false) }
    var showExportInfo by remember { mutableStateOf(false) }
    val columnMap = fileColumn()

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { csvUri ->
        if (csvUri != null) {
            isImportLoading = true
            importFile(movieViewModel = movieViewModel, context = context, csvUri = csvUri)
            isImportLoading = false
        }
    }

    if(isListLoading || isImportLoading || isExportLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    else {
        val exportLauncher = rememberLauncherForActivityResult(CreateDocument("text/csv")) { directoryUri ->
            if(directoryUri != null) {
                isExportLoading = true
                exportFile(context =  context, dirUri = directoryUri, movieList = movieList)
                isExportLoading = false
            }
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("File Management") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
//                    actions = {
//                        IconButton(onClick = {  }) {
//                            Icon(imageVector = Icons.Outlined.Info, contentDescription = "Info")
//                        }
//                    }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).padding(horizontal = 12.dp).verticalScroll(scrollState).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Importing a movie list", style = MaterialTheme.typography.titleLarge)
                        IconButton(onClick = { showImportInfo = !showImportInfo }) {
                            Icon(imageVector = if(showImportInfo) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = "Import information bar opened")
                        }
                    }
                    if(showImportInfo) {
                        Text(
                            text = "Make sure your .csv file contains the following column titles and is placed in the same order for the best possible results.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Column {
                            columnMap.forEach { (header, description) ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(modifier = Modifier.background(color = MaterialTheme.colorScheme.onSecondary).border(BorderStroke(1.dp, Color.White)).weight(0.3f), text = "  $header", style = MaterialTheme.typography.bodyLarge)
                                    Text(modifier = Modifier.border(BorderStroke(1.dp, Color.White)).weight(1f), text = "  $description", style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                        Text(
                            text = "Your .csv file MUST use a comma (,) as the delimiter for each column. ",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Your .csv file MUST ALSO use a comma (,) as the delimiter for each column. ",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(modifier = Modifier.fillMaxWidth(), onClick = { importLauncher.launch("*/*") }) {
                            Text("Import")
                        }
                    }
                }
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Exporting a movie list", style = MaterialTheme.typography.titleLarge)
                        IconButton(onClick = {
                            movieViewModel.getAllMovies()
                            showExportInfo = !showExportInfo
                        }) {
                            Icon(imageVector = if(showExportInfo) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = "Export information bar opened")
                        }
                    }
                    if(showExportInfo) {
                        Text(
                            text = "Choose the directory where you want your .csv file, which will contain all of your inserted movie information, to be placed.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { exportLauncher.launch("movies.csv") }) {
                            Text("Export")
                        }
                    }
                }
            }
        }
    }
}