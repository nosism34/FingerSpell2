package com.example.fingerspell.data.di

import android.content.Context
import com.example.fingerspell.data.API.ApiConfig
import com.example.fingerspell.data.pref.UserPreference
import com.example.fingerspell.data.pref.UserRepository
import com.example.fingerspell.data.pref.datastore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.datastore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService, pref)
    }
}