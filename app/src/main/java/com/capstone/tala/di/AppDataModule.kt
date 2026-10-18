package com.capstone.tala.di

import android.content.Context
import com.capstone.tala.data.content.TalaContentLoader
import com.capstone.tala.data.settings.AppSettingsRepository
import com.capstone.tala.data.settings.appSettingsStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDataModule {
    @Provides
    @Singleton
    fun provideTalaContentLoader(
        @ApplicationContext context: Context
    ): TalaContentLoader = TalaContentLoader(context)

    @Provides
    @Singleton
    fun provideAppSettingsRepository(
        @ApplicationContext context: Context
    ): AppSettingsRepository = AppSettingsRepository(context.appSettingsStore)
}
