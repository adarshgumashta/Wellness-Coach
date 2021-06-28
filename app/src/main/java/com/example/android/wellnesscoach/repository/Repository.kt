package com.example.android.wellnesscoach.repository

import com.example.android.wellnesscoach.activity.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import java.lang.ref.WeakReference

class Repository() {
    suspend fun signIn(activity: WeakReference<MainActivity>, responseCode: Int) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(activity.get()!!, gso)
        val signInIntent = googleSignInClient.signInIntent
        activity.get()!!.startActivityForResult(signInIntent, responseCode)
    }

    suspend fun signOut(
        activity: WeakReference<MainActivity>,
        signInOptions: GoogleSignInOptions,
        userStatus: UserStatus?
    ) {
        GoogleSignIn.getClient(activity.get()!!, signInOptions).signOut()
            .addOnCompleteListener(activity.get()!!, OnCompleteListener<Void?> {
                userStatus!!.checkIfUserLoggedOut(true)
            })
    }

    interface UserStatus {
        fun checkIfUserLoggedOut(exist: Boolean)
    }

    fun checkIfUserAlreadySignedIn(activity: WeakReference<MainActivity>): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(activity.get()!!)
    }
}