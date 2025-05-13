package com.example.mediatracker.compose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.mediatracker.data.movie.Movie
import com.example.mediatracker.data.movie.MovieViewModel
import com.example.mediatracker.R
import com.example.mediatracker.data.movie.Genre
import com.example.mediatracker.data.movie.MovieGenreCrossRef
import com.example.mediatracker.data.movie.MovieWithGenres
import java.io.File
import java.io.FileOutputStream
import java.net.URI

@Preview(showBackground = true)
@Composable
fun AddPreview(){
    val navController = rememberNavController()

    Add(navController)
}

@Composable
fun RatingDialog(rating: Int, onValueChange: (String) -> Unit, onDismissRequest: () -> Unit, onConfirmRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary)) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Rate:")
                Row {
                    BasicTextField(
                        value = if(rating == 0) "" else rating.toString(),
                        onValueChange = { onValueChange(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
                        cursorBrush = SolidColor(Color.White),
                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
                    )
                    Text(text = "/ 10")
                }
                Row {
                    TextButton(onClick = { onDismissRequest() }) {
                        Text(text = "Cancel")
                    }
                    TextButton(onClick = { onConfirmRequest() }) {
                        Text(text = "Confirm")
                    }
                }
            }
        }
    }
}

@Composable
fun GenreTextButton(genreName: String, onClick: () -> Unit) {
    TextButton(
        modifier = Modifier.size(height = 40.dp, width = 76.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onTertiary),
        onClick = { onClick() }
    ) {
        Text(text = genreName, color = Color.White)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenreDialog(movieID: Long, genres: MutableList<Genre>, genresAvailable: List<Genre>, movieViewModel: MovieViewModel, onDismissRequest: () -> Unit) {
    var genreName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary)) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text( text = "Click to add genre:")
                FlowRow {
                    genresAvailable.forEach { genre ->
                        TextButton(modifier = Modifier.size(height = 40.dp, width = 76.dp),
                            onClick = {
                                movieViewModel.upsertMovieGenreCrossRef(MovieGenreCrossRef(movieID, genre.genreId))
                                if(!genres.contains(genre)) {
                                    genres.add(genre)
                                }
                                onDismissRequest()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onTertiary)
                        ) {
                            Text(text = genre.name, color = Color.White)
                        }
                    }
                }
                Text( text = "Or add new genre:")
                BasicTextField(
                    value = genreName,
                    onValueChange = { genreName = it },
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    cursorBrush = SolidColor(Color.White),
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                )
                Row {
                    TextButton(onClick = { onDismissRequest() }) {
                        Text(text = "Cancel")
                    }
                    TextButton(
                        onClick = {
                            val newGenre = Genre(0, genreName)
                            movieViewModel.addGenreWithMovie(newGenre, movieID)
                            genres.add(newGenre)
                            onDismissRequest()
                        }
                    ) {
                        Text(text = "Confirm")
                    }
                }
            }
        }
    }
}

//gets chosen image from external storage, compress/save it to local storage, return filepath of internal copy
fun getPosterFilePath(context: Context, pImageUri: Uri, movie: Movie): String {
    //save image to movies folder for cleanliness
    val subfolder = File(context.filesDir, "movies")

    if(!subfolder.exists()) {
        subfolder.mkdir()
    }

    val inputStream = context.contentResolver.openInputStream(pImageUri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    val filename = "${movie.title.replace(" ", "")}${movie.movieId}.jpg"
    val file = File(subfolder, filename)

    if(file.exists()) {
        file.delete()
    }

    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, out)
    }

    inputStream?.close()

    return file.absolutePath
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
fun Add(navController: NavHostController, movieID: Long = 0, movieViewModel: MovieViewModel = hiltViewModel()){
    val isLoading by movieViewModel.isLoading.collectAsState()
    val isLoadingAnother by movieViewModel.isLoadingAnother.collectAsState()
    val movieInfo by movieViewModel.movieWithGenresEntry.collectAsState()
    val genresAvailable by movieViewModel.genres.collectAsState()

    // If a new movie is being added, the viewmodel will make a default value
    // else, if a movie is being edited, search and set the values to the movie's values

    LaunchedEffect(movieID) {
        movieViewModel.getMovieWithGenres(movieID)
        movieViewModel.getGenres()
    }

    if(isLoading || isLoadingAnother) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    else {
        val context = LocalContext.current
        var showEditTopAppBar by remember { mutableStateOf(false) }
        var readInfoOnly by remember { mutableStateOf(true) }

        var poster by remember { mutableStateOf(movieInfo.movie.poster) }
        var title by remember { mutableStateOf(movieInfo.movie.title) }
        var director by remember { mutableStateOf(movieInfo.movie.director) }
        var year by remember { mutableIntStateOf(movieInfo.movie.year) }
        var runtime by remember { mutableIntStateOf(movieInfo.movie.runtime) }
        var tagline by remember { mutableStateOf(movieInfo.movie.tagline) }
        var body by remember { mutableStateOf(movieInfo.movie.body) }
        var rating by remember { mutableIntStateOf(movieInfo.movie.rating) }
        var note by remember { mutableStateOf(movieInfo.movie.note) }
        var ownPhysical by remember { mutableStateOf(movieInfo.movie.ownPhysical) }
        var ownDigital by remember { mutableStateOf(movieInfo.movie.ownDigital) }
        Log.d("MovieDetails", "Fetched movie: $movieInfo")
        val genres by remember { mutableStateOf(movieInfo.genres) }

        val posterImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { pImageUri ->
            if (pImageUri != null) {
                poster = getPosterFilePath(context, pImageUri, movieInfo.movie)
            }
        }

        Scaffold(
            topBar = {
                if (!showEditTopAppBar) {
                    CenterAlignedTopAppBar(
                        title = { Text("$movieID") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                readInfoOnly = false
                                showEditTopAppBar = true
                            }) {
                                Icon(imageVector = Icons.Filled.Create, contentDescription = "Edit")
                            }
                        }
                    )
                }
                else {
                    CenterAlignedTopAppBar(
                        title = { Text("Add Movie") },
                        navigationIcon = {
                            IconButton(onClick = {
                                readInfoOnly = true
                                showEditTopAppBar = false
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Cancel")
                            }
                        },
                        actions = {
                            if(movieID != 0.toLong()){
                                IconButton(onClick = {
                                    movieViewModel.deleteMovie(movieInfo.movie)
                                    navController.popBackStack()
                                }) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                                }
                            }
                            IconButton(onClick = {
                                //if movie is in table, update it when the user clicks checkmark button
                                //else, add new entry with info entered
                                val newMovieInfo = Movie(
                                    movieId = movieID,
                                    poster = poster,
                                    title = title,
                                    year = year,
                                    director = director,
                                    body = body,
                                    runtime = runtime,
                                    tagline = tagline,
                                    rating = rating,
                                    note = note,
                                    ownPhysical = ownPhysical,
                                    ownDigital = ownDigital
                                )
                                movieViewModel.upsertMovie(newMovieInfo)

                                readInfoOnly = true
                                showEditTopAppBar = false
                                navController.navigate("home") {
                                    popUpTo(0)
                                }
                            }) {
                                Icon(imageVector = Icons.Filled.Check, contentDescription = "Confirm")
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            val scrollState = rememberScrollState()
            var showRatingPopup by remember { mutableStateOf(false) }

            Column(modifier = Modifier.padding(innerPadding).verticalScroll(scrollState)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(modifier = Modifier.fillMaxWidth().background(Color.Black), horizontalArrangement = Arrangement.Center) {
                        AsyncImage(
                            model = File(poster),
                            contentDescription = "Movie banner",
                            contentScale = ContentScale.FillWidth,
                            placeholder = painterResource(R.drawable.ic_launcher_foreground),
                            error = painterResource(R.drawable.ic_launcher_foreground),
                            modifier = Modifier.size(250.dp)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.background)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f).padding(12.dp)) {
                            AsyncImage(
                                modifier = Modifier.clickable {
                                    if (readInfoOnly) {
                                        println(poster)
                                    } else {
                                        posterImageLauncher.launch("*/*")
                                    }
                                },
                                model = File(poster),
                                contentDescription = "Movie poster",
                                contentScale = ContentScale.Fit,
                                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                                error = painterResource(R.drawable.ic_launcher_foreground)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(onClick = { showRatingPopup = true }) {
                                    Icon(imageVector = Icons.TwoTone.Star, contentDescription = "Rate")
                                }
                                if(rating == 0) {
                                    Text(text = "Rate")
                                }
                                else {
                                    Text(text = "$rating", style = MaterialTheme.typography.headlineSmall)
                                    Text(text = "   /10")
                                }
                            }

                            if(showRatingPopup) {
                                RatingDialog(
                                    rating = rating,
                                    onValueChange = {
                                        if(it == "") {
                                            rating = 0
                                        }
                                        else if (it.isDigitsOnly() && it.toInt() > -1 && it.toInt() < 11 && it.length < 3) {
                                            rating = it.toInt()
                                        }
                                    },
                                    onDismissRequest = {
                                        rating = movieInfo.movie.rating
                                        showRatingPopup = false
                                    },
                                    onConfirmRequest = {
                                        if(movieID != 0.toLong()) {
                                            movieViewModel.updateRating(movieID, rating)
                                        }
                                        showRatingPopup = false
                                    }
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(onClick = {  }) {
                                    Icon(imageVector = Icons.TwoTone.Add, contentDescription = "Add to List")
                                }
                                Text(text = "Add to List")
                            }
                        }
                        Column(modifier = Modifier.weight(2f).padding(2.dp), verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.Start) {
                            BasicTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = title,
                                onValueChange = { title = it },
                                readOnly = readInfoOnly,
                                textStyle = MaterialTheme.typography.headlineMedium.copy(Color.White),
                                cursorBrush = SolidColor(Color.White),
                                decorationBox = { innerTextField ->
                                    if(title.isEmpty()) {
                                        Text(text = "Title...", style = MaterialTheme.typography.headlineMedium, color = Color.Gray)
                                    }

                                    innerTextField()
                                }
                            )
                            Spacer(Modifier.size(4.dp))

                            Text(text = "Directed By", color = Color.Gray)
                            BasicTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = director,
                                onValueChange = { director = it },
                                readOnly = readInfoOnly,
                                textStyle = MaterialTheme.typography.titleMedium.copy(color = Color.Gray, fontWeight = FontWeight.Bold),
                                cursorBrush = SolidColor(Color.White),
                                decorationBox = { innerTextField ->
                                    if(director.isEmpty()) {
                                        Text(text = "Director...", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    }

                                    innerTextField()
                                }
                            )
                            Row(modifier = Modifier.fillMaxWidth()) {
                                BasicTextField(
                                    modifier = Modifier.weight(0.4f),
                                    value = if(year == 0) "" else year.toString(),
                                    onValueChange = {
                                        if(it.isDigitsOnly() && it.length < 5) {
                                            if(it == "") {
                                                year = 0
                                            }
                                            else {
                                                year = it.toInt()
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    readOnly = readInfoOnly,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = MaterialTheme.typography.titleMedium.copy(Color.Gray),
                                    cursorBrush = SolidColor(Color.White),
                                    decorationBox = { innerTextField ->
                                        if(year == 0) {
                                            Text(text = "YYYY", color = Color.Gray)
                                        }

                                        innerTextField()
                                    }
                                )
                                BasicTextField(
                                    modifier = Modifier.weight(1f),
                                    value =
                                        if(readInfoOnly && runtime != 0)
                                            "$runtime m"
                                        else if(runtime == 0)
                                            ""
                                        else
                                            runtime.toString(),
                                    onValueChange = {
                                        if(it.isDigitsOnly()) {
                                            if(it == "") {
                                                runtime = 0
                                            }
                                            else {
                                                runtime = it.toInt()
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    readOnly = readInfoOnly,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = MaterialTheme.typography.titleMedium.copy(Color.Gray),
                                    cursorBrush = SolidColor(Color.White),
                                    decorationBox = { innerTextField ->
                                        if(runtime == 0) {
                                            Text(text = "Runtime mins...", color = Color.Gray)
                                        }

                                        innerTextField()
                                    }
                                )
                            }
                            BasicTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = tagline,
                                onValueChange = { tagline = it },
                                readOnly = readInfoOnly,
                                textStyle = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
                                cursorBrush = SolidColor(Color.White),
                                decorationBox = { innerTextField ->
                                    if(tagline.isEmpty()) {
                                        Text(text = "Tagline...", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    }

                                    innerTextField()
                                }
                            )
                            BasicTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = body,
                                onValueChange = { body = it },
                                readOnly = readInfoOnly,
                                textStyle = MaterialTheme.typography.titleSmall.copy(Color.White),
                                cursorBrush = SolidColor(Color.White),
                                decorationBox = { innerTextField ->
                                    if(body.isEmpty()) {
                                        Text(text = "Details...", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                                    }

                                    innerTextField()
                                }
                            )
                        }
                    }
                }
                if(movieID != 0.toLong()) {
                    Column(modifier = Modifier.fillMaxHeight().background(color = MaterialTheme.colorScheme.primaryContainer)) {
                        val buttonChoices = listOf("Genres", "Notes")
                        var buttonClicked by remember { mutableStateOf("Genres") }
                        var showGenreDialog by remember { mutableStateOf(false) }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            buttonChoices.forEach { choice ->
                                TextButton(onClick = { buttonClicked = choice }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                                    Text(text = choice, color = Color.White, textDecoration = if (buttonClicked == choice) TextDecoration.Underline else TextDecoration.None)
                                }
                            }
                        }
                        FlowRow(modifier = Modifier.fillMaxWidth().padding(all = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (buttonClicked == "Genres") {
                                genres.forEach { genre ->
                                    GenreTextButton(genreName = genre.name, onClick = { } )
                                }
                                GenreTextButton(genreName = "+", onClick = { showGenreDialog = true })

                                if(showGenreDialog) {
                                    GenreDialog(movieID = movieID, genres = genres, genresAvailable = genresAvailable, movieViewModel = movieViewModel, onDismissRequest = { showGenreDialog = false })
                                }
                            }
                            else if (buttonClicked == "Notes") {
                                BasicTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = note,
                                    onValueChange = { note = it },
                                    readOnly = readInfoOnly,
                                    textStyle = MaterialTheme.typography.titleMedium.copy(Color.White),
                                    cursorBrush = SolidColor(Color.White),
                                    decorationBox = { innerTextField ->
                                        if (note.isEmpty()) {
                                            Text(text = "Note...", color = Color.Gray)
                                        }

                                        innerTextField()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}