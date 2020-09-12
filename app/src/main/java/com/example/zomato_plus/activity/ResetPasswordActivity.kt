package com.example.zomato_plus.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.zomato_plus.R
import com.example.zomato_plus.util.ConnectionManager
import com.example.zomato_plus.util.RESET_PASSWORD
import com.example.zomato_plus.util.Validations
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var otp: EditText
    private lateinit var passwordnew: EditText
    private lateinit var passwordcon: EditText
    private lateinit var next2: Button
    private lateinit var rlOTP: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var mobileNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        otp= findViewById(R.id.otp)
       passwordnew = findViewById(R.id.passwordnew)
        passwordcon = findViewById(R.id.passwordcon)
        next2 = findViewById(R.id.next2)
        rlOTP = findViewById(R.id.rlOTP)
        progressBar = findViewById(R.id.progressBar)

        rlOTP.visibility = View.VISIBLE
        progressBar.visibility = View.GONE

        if (intent != null) {
            mobileNumber = intent.getStringExtra("user_mobile") as String
        }

    next2.setOnClickListener {
            rlOTP.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            if (ConnectionManager().isNetworkAvailable(this@ResetPasswordActivity)) {
                if (otp.text.length == 4) {
                    if (Validations.validatePasswordLength(passwordnew.text.toString())) {
                        if (Validations.matchPassword(
                                passwordnew.text.toString(),
                                passwordcon.text.toString()
                            )
                        ) {
                            resetPassword(
                                mobileNumber,
                            otp.text.toString(),
                              passwordnew.text.toString()
                            )
                        } else {
                            rlOTP.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                    this@ResetPasswordActivity,
                                    "Passwords do not match",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    } else {
                        rlOTP.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Invalid Password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    rlOTP.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@ResetPasswordActivity, "Incorrect OTP", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                rlOTP.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "No Internet Connection!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun resetPassword(mobileNumber: String, otp: String, password: String) {
        val queue = Volley.newRequestQueue(this)

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobileNumber)
        jsonParams.put("password", password)
        jsonParams.put("otp", otp)

        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, RESET_PASSWORD, jsonParams, Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        progressBar.visibility = View.INVISIBLE
                        val builder = AlertDialog.Builder(this@ResetPasswordActivity)
                        builder.setTitle("Confirmation")
                        builder.setMessage("Your password has been successfully changed")
                        builder.setIcon(R.drawable.ic_action_success)
                        builder.setCancelable(false)
                        builder.setPositiveButton("Ok") { _, _ ->
                            startActivity(
                                Intent(
                                    this@ResetPasswordActivity,
                                    LoginActivity::class.java
                                )
                            )
                            ActivityCompat.finishAffinity(this@ResetPasswordActivity)
                        }
                        builder.create().show()
                    } else {
                        rlOTP.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        val error = data.getString("errorMessage")
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    rlOTP.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Incorrect Response!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener {
                rlOTP.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                VolleyLog.e("Error::::", "/post request fail! Error: ${it.message}")
                Toast.makeText(this@ResetPasswordActivity, it.message, Toast.LENGTH_SHORT).show()
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"

                    /*The below used token will not work, kindly use the token provided to you in the training*/
                    headers["token"] = "9bf534118365f1"
                    return headers
                }
            }
        queue.add(jsonObjectRequest)
    }
}
