package com.example.mediatracker.compose

//import com.example.mediatracker.ui.theme.MediaTrackerTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mediatracker.Screens
import com.example.mediatracker.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
//                    val movieViewModel: MovieViewModel = hiltViewModel()
//                    movieViewModel.upsertMovie(Movie())

//                    ModNavDrawer()
                    ScaffoldNavBar()
                }
            }
        }
    }
}
//TODO: FIX WATCHLIST SECTION, ADD SEARCH-BY-GENRE SECTION
@Composable
fun ScaffoldNavBar() {
    val navController = rememberNavController()
    var currentPage by remember { mutableStateOf(navController.currentDestination?.route) }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                actions = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        IconButton(onClick = { navController.navigate("home") }) {
                            Icon(imageVector = Icons.Filled.Home, contentDescription = "Menu")
                        }
                        IconButton(onClick = { navController.navigate("search") }) {
                            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = { navController.navigate("add/${0}") }) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add movie")
                        }
                        IconButton(onClick = { navController.navigate("lists") }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "Watchlist")
                        }
                        IconButton(onClick = { navController.navigate("files") }) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screens.Home.screen
            ){
                composable(Screens.Home.screen){
                    Home(navController)
                }
                composable(route = Screens.Add.screen,
                    arguments = listOf(
                        navArgument("id") {
                            type = NavType.LongType
                        }
                    )
                ){ backStackEntry ->
                    val movieID: Long = backStackEntry.arguments?.getLong("id") ?: -1
//                    Log.i("BACKSTACK_TEST", movieID.toString())
                    Add(navController, movieID)
                }
                composable(Screens.Search.screen){
                    Search(navController)
                }
                composable(Screens.Files.screen){
                    Files(navController)
                }
                composable(route = Screens.Genre.screen,
                    arguments = listOf(
                        navArgument("genreid") {
                            type = NavType.LongType
                        }
                    )
                ){ backStackEntry ->
                    val genreID: Long = backStackEntry.arguments?.getLong("genreid") ?: -1
//                    Log.i("BACKSTACK_TEST", genreID.toString())
                    Genre(navController, genreID)
                }
                composable(Screens.Lists.screen){
                    Lists(navController)
                }
            }
        }
    }

}



@Preview(showBackground = true)
@Composable
fun AddMediaDialogPreview() {
//    AddMediaDialog(onDismissRequest = {})
}

@Preview(showBackground = true)
@Composable
fun ModNavDrawer(modifier: Modifier = Modifier){
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        gesturesEnabled = false,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                IconButton(modifier = Modifier.align(Alignment.End),
                    onClick = {
                    scope.launch {
                        if(drawerState.isClosed) {
                            drawerState.open()
                        }
                        else{
                            drawerState.close()
                        }
                    }
                }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Edit")
                }
                HorizontalDivider()

                Text(text = "Media", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = {
                        navController.navigate(Screens.Home.screen){
                            popUpTo(0)
                        }
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
                HorizontalDivider()

                Text(text = "Advanced", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Warning, contentDescription = null) },
                    label = { Text("File Management") },
                    selected = false,
                    onClick = {
                        navController.navigate("files")
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {}
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                    label = { Text("Help") },
                    selected = false,
                    onClick = {}
                )
            }
        }
    ) {
        Scaffold() { innerPadding ->
            NavHost(modifier = Modifier.padding(innerPadding), navController = navController, startDestination = Screens.Home.screen){
                composable(Screens.Home.screen){
                    Home(navController)
                }
                composable(route = Screens.Add.screen,
                    arguments = listOf(
                        navArgument("id") {
                            type = NavType.LongType
                        }
                    )
                ){ backStackEntry ->
                    val movieID: Long = backStackEntry.arguments?.getLong("id") ?: -1
//                    Log.i("BACKSTACK_TEST", movieID.toString())
                    Add(navController, movieID)
                }
                composable(Screens.Search.screen){
                    Search(navController)
                }
                composable(Screens.Files.screen){
                    Files(navController)
                }
            }
        }
    }
}