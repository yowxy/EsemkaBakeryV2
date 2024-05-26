package com.example.esemkabakeryv2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.esemkabakeryv2.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BTNSingnUp.setOnClickListener {
            startActivity(Intent(this@MainActivity,Register::class.java))
            finish()
        }

        binding.username.setText("john_doe")
        binding.password.setText("password")


        binding.BTNSingnin.setOnClickListener {

            GlobalScope.launch(Dispatchers.IO){
                if(binding.username.text == null){
                    Toast.makeText(this@MainActivity, "email/username tidak boleh kosong", Toast.LENGTH_LONG).show()
                }
                if(binding.password.text ==  null){
                    Toast.makeText(this@MainActivity, "password tidak boleh kosong", Toast.LENGTH_LONG).show()
                }
                val conn = URL("http://10.0.2.2:5000/api/Auth/Login").openConnection() as HttpURLConnection
                conn.requestMethod ="POST"
                conn.setRequestProperty("Content-Type", "application/json")

                conn.outputStream.write(JSONObject().apply {
                    put("usernameOrEmail", binding.username.text)
                    put("password", binding.password.text)
                }.toString().toByteArray())

                val code = conn.responseCode
                runOnUiThread {
                    if(code in 200..299){
                        Toast.makeText(this@MainActivity, "sukses", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@MainActivity, Home::class.java))
                    }else{
                        var builder = AlertDialog.Builder(this@MainActivity)
                        builder.setTitle("informasi")
                        builder.setMessage("username/email dan password anda salah")
                        builder.setPositiveButton("yes"){_,_->
                            Toast.makeText(this@MainActivity, "gagal", Toast.LENGTH_LONG).show()
                        }
                        builder.show()
                    }
                }
            }

        }


    }
}