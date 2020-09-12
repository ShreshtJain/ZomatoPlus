package com.example.zomato_plus.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.zomato_plus.R
import com.example.zomato_plus.util.ConnectionManager
import com.example.zomato_plus.util.FORGOT_PASSWORD
import com.example.zomato_plus.util.Validations
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var mob: EditText
    lateinit var etForgetEmail: EditText
    lateinit var next: Button
    lateinit var progressBar: ProgressBar
    lateinit var rlContentMain: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgetpass)

       mob= findViewById(R.id.mob)
        etForgetEmail = findViewById(R.id.etForgetEmail)
        next = findViewById(R.id.next)
        rlContentMain = findViewById(R.id.rlContentMain)
        progressBar = findViewById(R.id.progressBar)
        rlContentMain.visibility = View.VISIBLE
        progressBar.visibility = View.GONE


     next.setOnClickListener {
            val forgotMobileNumber = mob.text.toString()
            if (Validations.validateMobile(forgotMobileNumber)) {
               mob.error = null
                if (Validations.validateEmail(etForgetEmail.text.toString())) {
                    if (ConnectionManager().isNetworkAvailable(this@ForgotPasswordActivity)) {
                        rlContentMain.visibility = View.GONE
                        progressBar.visibility = View.VISIBLE
                        sendOTP(mob.text.toString(), etForgetEmail.text.toString())
                    } else {
                        rlContentMain.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                                this@ForgotPasswordActivity,
                                "No Internet Connection!",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                } else {
                    rlContentMain.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    etForgetEmail.error = "Invalid Email"
                }
            } else {
                rlContentMain.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
               mob.error = "Invalid Mobile Number"
            }
        }
    }

    private fun sendOTP(mobileNumber: String, email: String) {
        val queue = Volley.newRequestQueue(this)

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobileNumber)
        jsonParams.put("email", email)

        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, FORGOT_PASSWORD, jsonParams, Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val firstTry = data.getBoolean("first_try")
                        if (firstTry) {
                            val builder = AlertDialog.Builder(this@ForgotPasswordActivity)
                            builder.setTitle("Information")
                            builder.setMessage("Please check your registered Email for the OTP.")
                            builder.setCancelable(false)
                            builder.setPositiveButton("Ok") { _, _ ->
                                val intent = Intent(
                                    this@ForgotPasswordActivity,
                                    ResetPasswordActivity::class.java
                                )
                                intent.putExtra("user_mobile", mobileNumber)
                                startActivity(intent)
                            }
                            builder.create().show()
                        } else {
                            val builder = AlertDialog.Builder(this@ForgotPasswordActivity)
                            builder.setTitle("Information")
                            builder.setMessage("Please refer to the previous email for the OTP.")
                            builder.setCancelable(false)
                            builder.setPositiveButton("Ok") { _, _ ->
                                val intent = Intent(
                                    this@ForgotPasswordActivity,
                                    ResetPasswordActivity::class.java
                                )
                                intent.putExtra("user_mobile", mobileNumber)
                                startActivity(intent)
                            }
                            builder.create().show()
                        }
                    } else {
                        rlContentMain.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Mobile number not registered!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    rlContentMain.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Incorrect response error!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener {
                rlContentMain.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                VolleyLog.e("Error::::", "/post request fail! Error: ${it.message}")
                Toast.makeText(this@ForgotPasswordActivity, it.message, Toast.LENGTH_SHORT).show()
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
