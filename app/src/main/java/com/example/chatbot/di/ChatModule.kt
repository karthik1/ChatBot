package com.example.chatbot.di

import android.app.Application
import androidx.room.Room
import com.example.chatbot.api.ChatApiService
import com.example.chatbot.model.ChatFactory
import com.example.chatbot.persistence.ChatDao
import com.example.chatbot.persistence.ChatDatabase
import com.example.chatbot.repository.ChatRepository
import com.example.chatbot.session.SessionManager
import com.example.chatbot.util.Constants.BASE_URL
import com.example.chatbot.util.LiveDataCallAdapterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
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



    @Singleton
    @Provides
    fun provideChatRepository(
        chatDao: ChatDao,
        chatApiService: ChatApiService,
        sessionManager: SessionManager,
        chatFactory: ChatFactory
    ): ChatRepository {
        return ChatRepository(chatDao,chatApiService,sessionManager,chatFactory)
    }

    @Singleton
    @Provides
    fun provideDateFormat(): SimpleDateFormat {
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC-7") // match firestore
        return sdf
    }

//    @Singleton
//    @Provides
//    fun provideSessionManager(@ApplicationContext application:Application): SessionManager {
//        return SessionManager(application)
//    }

}