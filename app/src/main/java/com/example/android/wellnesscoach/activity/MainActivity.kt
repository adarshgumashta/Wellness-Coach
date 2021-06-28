package com.example.android.wellnesscoach.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android.wellnesscoach.R
import com.example.android.wellnesscoach.databinding.ActivityMainBinding
import com.example.android.wellnesscoach.viewmodel.GoogleSignInViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val googleSignInViewModel: GoogleSignInViewModel by viewModel()
    val mainActivityWeakReference: WeakReference<MainActivity> by lazy {
        WeakReference<MainActivity>(this);
    }
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val RC_SIGN_IN = 200
    private lateinit var userName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAnalytics = Firebase.analytics

        binding.signInButton.setOnClickListener(this)
        binding.signOutButton.setOnClickListener(this)
        binding.goToMediaPage.setOnClickListener(this)
        checkIfUserAlreadySignedIn()
        val txtLogout = binding.signOutButton.getChildAt(0) as TextView
        try {
            txtLogout.text = getString(R.string.sign_out)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkIfUserAlreadySignedIn() {
        googleSignInViewModel.checkIfUserAlreadySignedIn(mainActivityWeakReference)
        googleSignInViewModel.loginStatus.observe(this, { googleSignInAccount ->
            if (googleSignInAccount == null) {
                binding.signInButton.visibility = View.VISIBLE
                binding.signOutButton.visibility = View.INVISIBLE
                binding.signInName.visibility = View.VISIBLE
                binding.signInName.text = getString(R.string.please_sign_in)
                binding.goToMediaPage.visibility = View.INVISIBLE
            } else {
                binding.signInButton.visibility = View.INVISIBLE
                binding.signOutButton.visibility = View.VISIBLE
                userName = googleSignInAccount.displayName.toString()
                (getString(R.string.welcome) + " " + userName).also { binding.signInName.text = it }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sign_in_button -> handleSignInClick()
            R.id.sign_out_button -> handleSignOutClick()
            R.id.go_to_media_page -> startUserActivity(userName)
        }
    }

    private fun handleSignOutClick() {
        val signInOptions = GoogleSignInOptions.DEFAULT_SIGN_IN
        googleSignInViewModel.signOut(mainActivityWeakReference, signInOptions)
        googleSignInViewModel.signOutStatus.observe(this, {
            if (it) {
                Toast.makeText(this, getString(R.string.sing_out_toast), Toast.LENGTH_SHORT).show()
                binding.signInButton.visibility = View.VISIBLE
                binding.signOutButton.visibility = View.INVISIBLE
                binding.signInName.visibility = View.INVISIBLE
                binding.goToMediaPage.visibility = View.INVISIBLE
            }
        })
    }

    private fun handleSignInClick() {
        googleSignInViewModel.signIn(mainActivityWeakReference, RC_SIGN_IN)
        googleSignInViewModel.signInStatus.observe(this, {
            if (it != null) {
                binding.signInButton.visibility = View.INVISIBLE
                binding.signOutButton.visibility = View.VISIBLE
                binding.goToMediaPage.visibility = View.VISIBLE
                userName = it.displayName.toString()
                firebaseAnalytics.setUserProperty("user_email", it.email)
                (getString(R.string.welcome) + " " + userName).also { binding.signInName.text = it }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    Log.d("Account", account.displayName.toString())
                    googleSignInViewModel.handleSignInResult(account)
                    startUserActivity(account.displayName.toString())
                }
            } catch (e: ApiException) {
                Toast.makeText(this, getString(R.string.google_sign_in_failed), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun startUserActivity(userName: String) {
        val intent = Intent(this@MainActivity, UserActivity::class.java)
        intent.apply {
            putExtra("USERNAME", userName)
        }
        startActivity(intent)
    }
}