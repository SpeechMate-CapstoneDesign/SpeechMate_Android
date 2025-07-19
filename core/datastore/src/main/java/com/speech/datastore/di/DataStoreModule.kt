package com.speech.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.speech.datastore.datasource.LocalTokenDataSource
import com.speech.datastore.datasource.LocalTokenDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreProvidesModule {
    private const val TOKEN_DATASTORE_NAME = "TOKEN_PREFERENCES"
    private val Context.tokenDataStore by preferencesDataStore(name = TOKEN_DATASTORE_NAME)

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    @Named("token")
    fun provideTokenDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.tokenDataStore

}

@Module
@InstallIn(SingletonComponent::class)
abstract class DatastoreBindsModule {
    @Binds
    @Singleton
    abstract fun bindsLocalTokenDataSource(
        localTokenDataSourceImpl: LocalTokenDataSourceImpl,
    ): LocalTokenDataSource
}
