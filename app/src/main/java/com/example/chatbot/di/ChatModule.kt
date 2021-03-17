package com.example.chatbot.di

import android.app.Application
import androidx.room.Room
import com.example.chatbot.api.ChatApiService
import com.example.chatbot.persistence.ChatDao
import com.example.chatbot.persistence.ChatDatabase
import com.example.chatbot.util.Constants.BASE_URL
import com.example.chatbot.util.LiveDataCallAdapterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {


    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }

    @Singleton
    @Provides
    fun provideRetrofitBuilder(gsonBuilder: Gson): Retrofit.Builder{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    fun provideChatApiService(retrofitBuilder: Retrofit.Builder): ChatApiService {
        return retrofitBuilder
            .build()
            .create(ChatApiService::class.java)
    }


    @Singleton
    @Provides
    fun provideChatDb(app: Application): ChatDatabase {
        return Room
            .databaseBuilder(app, ChatDatabase::class.java, ChatDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration() // get correct db version if schema changed
            .build()
    }

    @Singleton
    @Provides
    fun provideChatDao(db: ChatDatabase): ChatDao {
        return db.getchatDao()
    }

}