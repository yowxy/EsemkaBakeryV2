package com.example.esemkabakeryv2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.esemkabakeryv2.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class Register : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.Singnin.setOnClickListener {
            startActivity(Intent(this@Register,MainActivity::class.java))
            finish()
        }

        binding.Singnup.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO){

                val conn = URL("http://10.0.2.2:5000/api/Auth/Register").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type","application/json")

                val jsons = JSONObject().apply {
                    put("firstName",binding.firstname.text)
                    put("lastName",binding.lastname.text)
                    put("username",binding.username.text)
                    put("email",binding.EmailAdress.text)
                    put("password",binding. Password.text)
                    put("passwordConfirmation",binding.connPassword.text)
                }

                conn.outputStream.write(jsons.toString().toByteArray())

                if(conn.responseCode in 200 ..299){
                    startActivity(Intent(this@Register,Home::class.java))
                    finish()
                }
                else{
                    val error = conn.errorStream.bufferedReader().readText()
                    runOnUiThread{
                        try {
                            Toast.makeText(this@Register, "${JSONObject(error).getString("message")}", Toast.LENGTH_SHORT).show()
                        }
                        catch (e : Exception) {
                            Toast.makeText(this@Register, "Pastikan Semua Data Terisi!"    , Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }

        }

    }
}