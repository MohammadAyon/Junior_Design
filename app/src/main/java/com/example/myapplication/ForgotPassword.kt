package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.myapplication.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var editTextEmail: EditText
    private lateinit var editTextVerificationCode: EditText
    private lateinit var editTextNewPassword: EditText

    private lateinit var buttonResetPassword: Button
    private lateinit var buttonChangePassword: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextVerificationCode = findViewById(R.id.editTextVerificationCode)
        editTextNewPassword = findViewById(R.id.editTextNewPassword)

        buttonResetPassword = findViewById(R.id.buttonResetPassword)
        buttonChangePassword = findViewById(R.id.buttonChangePassword)

        auth = FirebaseAuth.getInstance()

        buttonResetPassword.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                resetPassword(email)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }

        buttonChangePassword.setOnClickListener {
            val verificationCode = editTextVerificationCode.text.toString().trim()
            val newPassword = editTextNewPassword.text.toString().trim()

            if (verificationCode.isNotEmpty() && newPassword.isNotEmpty()) {
                changePassword(verificationCode, newPassword)
            } else {
                Toast.makeText(this, "Please enter verification code and new password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Reset email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun changePassword(verificationCode: String, newPassword: String) {
        val user = auth.currentUser
        if (user != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, verificationCode)
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { passwordUpdateTask ->
                                if (passwordUpdateTask.isSuccessful) {
                                    Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                                    // You can navigate to another screen or perform additional actions here
                                } else {
                                    Toast.makeText(this, "Failed to change password", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Failed to verify code", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}