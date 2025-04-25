package com.example.mediatracker.compose

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mediatracker.R
import com.example.mediatracker.data.movie.Movie
import com.example.mediatracker.data.movie.MovieViewModel
import java.io.InputStream
import java.net.URI

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

    //read header line and skip it
    reader?.readLine()

    //read each movie entry and add to database
    reader?.forEachLine { line ->
        val movieCSVLine = line.split(",")

        movieViewModel.upsertMovie(Movie(
            title = movieCSVLine[1],
            year = movieCSVLine[2].toInt(),
            director = movieCSVLine[3],
            body = movieCSVLine[4],
            runtime = movieCSVLine[5].toInt(),
            tagline = movieCSVLine[6],
            rating = movieCSVLine[7].toInt(),
            note = movieCSVLine[8],
            ownPhysical = (movieCSVLine[9] == "true"),
            ownDigital = (movieCSVLine[10] == "true")
        ))
    }

    inputStream?.close()
}

fun exportFile(context: Context, dirUri: Uri, movieList: List<Movie>) {
    //get directory location picked by user and create a writer
    val bw = context.contentResolver.openOutputStream(dirUri)

    //write the headers
    bw?.write(("uid,title,year,director,body,runtime,tagline,rating,note,own_physical,own_digital\n").toByteArray())

    //write each movie entry into the csv file
    movieList.forEach { movie ->
        bw?.write(("${movie.movieId},${movie.title},${movie.year},${movie.director},${movie.body},${movie.runtime},${movie.tagline},${movie.rating},${movie.note},${movie.ownPhysical},${movie.ownDigital}\n").toByteArray())
        println(movie.toString())
    }

    bw?.flush()
    bw?.close()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Files(navController: NavHostController, movieViewModel: MovieViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val movieList by movieViewModel.entries.collectAsState()
    val isListLoading by movieViewModel.isLoading.collectAsState()
    var isImportLoading by remember { mutableStateOf(false) }
    var isExportLoading by remember { mutableStateOf(false) }

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
                    actions = {
                        IconButton(onClick = {  }) {
                            Icon(imageVector = Icons.Outlined.Info, contentDescription = "Info")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = null)
                Text(text = "Manage your media data", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(20.dp))

                Text(text = "Imported files must be in .csv format and separated by commas.", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Exported files will be in .csv format.", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(20.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        movieViewModel.getAllMovies()
                        importLauncher.launch("*/*")
                    }) {
                        Text("Import")
                    }
                    Button(modifier = Modifier.fillMaxWidth(), onClick = { exportLauncher.launch("movies.csv") }) {
                        Text("Export")
                    }
                }
            }
        }
    }
}