package com.example.android.wellnesscoach.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.wellnesscoach.activity.MainActivity
import com.example.android.wellnesscoach.repository.Repository
import com.example.android.wellnesscoach.repository.Repository.UserStatus
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


class GoogleSignInViewModel(private val repository: Repository) : ViewModel() {

    var signOutStatus: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    var loginStatus: MutableLiveData<GoogleSignInAccount> = MutableLiveData<GoogleSignInAccount>()
    var signInStatus: MutableLiveData<GoogleSignInAccount> = MutableLiveData<GoogleSignInAccount>()
    fun signIn(activity: WeakReference<MainActivity>, responseCode: Int) {
        viewModelScope.launch {
            repository.signIn(activity, responseCode)
        }
    }

    fun signOut(activity: WeakReference<MainActivity>, signInOptions: GoogleSignInOptions) {
        viewModelScope.launch {
            repository.signOut(activity, signInOptions,
                object : UserStatus {
                    override fun checkIfUserLoggedOut(exist: Boolean) {
                        signOutStatus.postValue(exist)
                        signInStatus.postValue(null)
                        loginStatus.postValue(null)

                    }
                })
        }
    }

    fun checkIfUserAlreadySignedIn(activity: WeakReference<MainActivity>) {
        viewModelScope.launch {
            val signOutValue = repository.checkIfUserAlreadySignedIn(activity)
            loginStatus.postValue(signOutValue)
        }
    }

    fun handleSignInResult(account: GoogleSignInAccount) {
        signInStatus.postValue(account)
    }

}