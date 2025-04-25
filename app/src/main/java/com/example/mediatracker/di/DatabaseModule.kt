package com.example.mediatracker.di

import android.content.Context
import androidx.room.Room
import com.example.mediatracker.data.MediaDatabase
import com.example.mediatracker.data.movie.MovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MediaDatabase {
        return Room.databaseBuilder(
            context,
            MediaDatabase::class.java,
            "movie-database"
        ).build()
    }

    @Provides
    fun provideUserDao(database: MediaDatabase): MovieDao {
        return database.movieDao()
    }
}