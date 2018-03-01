package com.example.denero.handmadeevent

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import java.util.*




class AuthenticationActivity : AppCompatActivity() {
    val SIGN_IN_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()

        if(FirebaseAuth.getInstance().currentUser == null) {

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                            ))
                            .build(),
                    SIGN_IN_REQUEST_CODE)

        } else {
            Toast.makeText(this,
                    getString(R.string.auth_text_welcome) + (FirebaseAuth.getInstance()
                            .currentUser?.displayName  ),
                    Toast.LENGTH_LONG)
                    .show()
            //startActivity(...)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        pushLog("onActivityResult",requestCode)
        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,
                        getString(R.string.auth_text_success_authentication),
                        Toast.LENGTH_LONG)
                        .show()
                //startActivity(...)
            } else {
                Toast.makeText(this,
                        getString(R.string.auth_text_fail_authentication),
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }
    }
    private val LOG_TAG: String = "DZ"
    private val LOG_HEAD: String = AuthenticationActivity::class.java.simpleName
    private fun pushLog(topic: String, message: Any) {
        Log.d(LOG_TAG, "$LOG_HEAD $topic $message")
    }
}
