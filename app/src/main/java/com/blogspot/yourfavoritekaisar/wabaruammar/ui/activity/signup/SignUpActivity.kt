package com.blogspot.yourfavoritekaisar.wabaruammar.ui.activity.signup

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.Toast
import com.blogspot.yourfavoritekaisar.wabaruammar.R
import com.blogspot.yourfavoritekaisar.wabaruammar.ui.activity.login.LoginActivity
import com.blogspot.yourfavoritekaisar.wabaruammar.ui.activity.main.MainActivity
import com.blogspot.yourfavoritekaisar.wabaruammar.util.DATA_USERS
import com.blogspot.yourfavoritekaisar.wabaruammar.util.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseListener = FirebaseAuth.AuthStateListener {
        val user = FirebaseAuth.getInstance().currentUser?.uid
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_sign_up)

        setTextChangedListener(edt_email, til_email)
        setTextChangedListener(edt_password, til_password)
        setTextChangedListener(edt_name, til_name)
        setTextChangedListener(edt_phone, til_phone)
        progress_layout.setOnTouchListener { v, event -> true }

        btn_signup.setOnClickListener {
            onSignUp()
        }

        txt_login.setOnClickListener {
            onLogin()
        }
    }

    private fun setTextChangedListener(edt: TextInputEditText?, till: TextInputLayout?) {
        edt?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                till?.isErrorEnabled = false
            }
        })
    }

    private fun onSignUp() {
        var proceed = true
        if (edt_name.text.isNullOrEmpty()) {
            til_name.error = "Required Name"
            til_name.isErrorEnabled = true
            proceed = false
        }

        if (edt_phone.text.isNullOrEmpty()) {
            til_phone.error = "Required Phone Number"
            til_phone.isErrorEnabled = true
            proceed = false
        }

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
            firebaseAuth.createUserWithEmailAndPassword(
                edt_email.text.toString(),
                edt_password.text.toString()
            )
                .addOnCompleteListener {
                    if (!it.isSuccessful) {
                        progress_layout.visibility = View.GONE
                        Toast.makeText(
                            this, "SignUp Error: ${it.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (firebaseAuth.uid != null) {
                        val email = edt_email.text.toString()
                        val phone = edt_phone.text.toString()
                        val name = edt_name.text.toString()
                        val user = User(email, phone, name, "", "Hello World! I'm new", "", "")

                        firebaseDB.collection(DATA_USERS)
                            .document(firebaseAuth.uid!!).set(user)
                    }

                    progress_layout.visibility = View.GONE


                }
                .addOnFailureListener {
                    progress_layout.visibility = View.GONE
                    it.printStackTrace()
                }

        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseListener)
    }

    private fun onLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}