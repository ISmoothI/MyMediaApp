package com.example.mediatracker

sealed class Screens(val screen: String) {
    data object Home : Screens("home")
    data object Add : Screens("add/{id}")
    data object Search : Screens("search")
    data object Files : Screens("files")
    data object Genre : Screens("genre/{genreid}")
    data object Lists : Screens("lists")
}