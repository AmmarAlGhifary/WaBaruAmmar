package com.blogspot.yourfavoritekaisar.wabaruammar.ui.activity.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import com.blogspot.yourfavoritekaisar.wabaruammar.R
import com.blogspot.yourfavoritekaisar.wabaruammar.ui.activity.main.MainActivity
import com.blogspot.yourfavoritekaisar.wabaruammar.ui.activity.signup.SignUpActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser?.uid
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_login)

        setTextChangedListener(edt_email, til_email)
        setTextChangedListener(edt_password, til_password)
        progress_layout.setOnTouchListener { _, _ -> true }

        btn_login.setOnClickListener {
            onLogin()
        }

        txt_signup.setOnClickListener {
            onSignup()
        }
    }

    private fun onLogin() {
        var proceed = true
        if (edt_email.text.isNullOrEmpty()) {
            til_email.error = "Required Password"
            til_email.isErrorEnabled = true
            proceed = false
        }
        if (edt_password.text.isNullOrEmpty()) {
            til_password.error = "Required Password"
            til_password.isErrorEnabled = true
            proceed = false
        }

        if (proceed) {
            progress_layout.visibility = View.VISIBLE
            firebaseAuth.signInWithEmailAndPassword(
                edt_email.text.toString(), edt_password.text.toString()
            ).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    progress_layout.visibility = View.GONE
                    Toast.makeText(
                        this@LoginActivity,
                        "Login error: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT
                    ).show()
                }
            }
                .addOnFailureListener { e ->
                    progress_layout.visibility = View.GONE
                    e.printStackTrace()
                }
        }
    }

    private fun onSignup() {
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    private fun setTextChangedListener(edt: EditText, til: TextInputLayout) {
        edt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                til.isErrorEnabled = false
            }
        })
    }
}