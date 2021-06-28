package com.example.android.wellnesscoach.di

import com.example.android.wellnesscoach.repository.Repository
import com.example.android.wellnesscoach.viewmodel.GoogleSignInViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module { single { Repository() } }
val viewModelModule = module(override = true) {
    viewModel { GoogleSignInViewModel(get()) }
}