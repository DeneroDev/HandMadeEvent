package com.example.denero.handmadeevent

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.denero.handmadeevent.EventList.EventListActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import java.util.*


class AuthenticationActivity : AppCompatActivity() {
    val SIGN_IN_REQUEST_CODE = 1
    private val LOG_TAG: String = "GOT"
    private val LOG_HEAD: String = AuthenticationActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        FirebaseAuth.getInstance().signOut()
//
        if (FirebaseAuth.getInstance().currentUser == null) {

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(Arrays.asList(
                                    AuthUI.IdpConfig.EmailBuilder().build(),
                                    AuthUI.IdpConfig.GoogleBuilder().build()
                            ))
                            .build(),
                    SIGN_IN_REQUEST_CODE)

        } else {
            Toast.makeText(this,
                    getString(R.string.auth_text_welcome) + " " + (FirebaseAuth.getInstance()
                            .currentUser?.displayName),
                    Toast.LENGTH_LONG)
                    .show()
            // TODO: start Map
            startActivity(Intent(applicationContext, MapsActivity::class.java))
//            startActivity(Intent(applicationContext, EventListActivity::class.java)
//                    .putExtra(getString(R.string.key_mission_open_fragment),getString(R.string.key_my_event_fragment)))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        getString(R.string.auth_text_success_authentication),
                        Toast.LENGTH_LONG)
                        .show()
                // TODO: start Map
                startActivity(Intent(applicationContext, MapsActivity::class.java))

            } else {
                Toast.makeText(this,
                        getString(R.string.auth_text_fail_authentication),
                        Toast.LENGTH_LONG)
                        .show()

                // Close the app
                finish()
            }
        }
    }

    override fun onResume() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            finish()
        }
        super.onResume()
    }

    private fun pushLog(topic: String, message: Any) {
        Log.d(LOG_TAG, "$LOG_HEAD $topic $message")
    }
}
