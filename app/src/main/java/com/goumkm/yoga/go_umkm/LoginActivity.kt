package com.goumkm.yoga.go_umkm

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.internal.FirebaseAppHelper.getUid
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.content.Intent
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthCredential
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class LoginActivity : BaseActivity(), View.OnClickListener {


    // [START declare_auth]
    private var mAuth: FirebaseAuth? = null
    // [END declare_auth]

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mStatusTextView: TextView? = null
    private var mDetailTextView: TextView? = null
    internal var myRef: DatabaseReference? = null
    internal lateinit var database : FirebaseDatabase;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        database = FirebaseDatabase.getInstance()
        // Views

        if(isLogin()){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)

        }


        // Button listeners
        findViewById<View>(R.id.sign_in_button).setOnClickListener(this)
        findViewById<View>(R.id.sign_out_button).setOnClickListener(this)
        findViewById<View>(R.id.disconnect_button).setOnClickListener(this)

        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance()
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        updateUI(currentUser)
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // [START_EXCLUDE]
                updateUI(null)
                // [END_EXCLUDE]
            }

        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        // [START_EXCLUDE silent]
        showProgressDialog()
        // [END_EXCLUDE]

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information

                        Log.d(TAG, "signInWithCredential:success")
                        val user = mAuth!!.currentUser
                        myRef = database.getReference("users").child(user!!.uid)
                        myRef!!.child("nama").setValue(user!!.displayName)
                        myRef!!.child("email").setValue(user!!.email)


                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.

                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Snackbar.make(findViewById<View>(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                        updateUI(null)
                    }

                    // [START_EXCLUDE]
                    hideProgressDialog()
                    // [END_EXCLUDE]
                }
    }
    // [END auth_with_google]

    // [START signin]
    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // [END signin]

    private fun signOut() {
        // Firebase sign out
        mAuth!!.signOut()

        // Google sign out
        mGoogleSignInClient!!.signOut().addOnCompleteListener(this
        ) { updateUI(null) }
    }

    private fun revokeAccess() {
        // Firebase sign out
        mAuth!!.signOut()

        // Google revoke access
        mGoogleSignInClient!!.revokeAccess().addOnCompleteListener(this
        ) { updateUI(null) }
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) {
            val intent = Intent(this,MainActivity::class.java);
            startActivity(intent)
        } else {

        }
    }

    override fun onClick(v: View) {
        val i = v.getId()
        if (i == R.id.sign_in_button) {
            signIn()
        } else if (i == R.id.sign_out_button) {
            signOut()
        } else if (i == R.id.disconnect_button) {
            revokeAccess()
        }
    }

    companion object {

        private val TAG = "GoogleActivity"
        private val RC_SIGN_IN = 9001
    }
}