package com.example.esemkabakeryv2

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.esemkabakeryv2.databinding.ActivityDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class Detail : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolnarDetail.setNavigationOnClickListener{
            startActivity(Intent(this@Detail,Search::class.java))
            finish()
        }

        val DE = JSONObject(intent.getStringExtra("detail"))

        GlobalScope.launch(Dispatchers.IO){
            val conn = URL("http://10.0.2.2:5000/api/Cake/${DE.getString("cakeID")}").openStream().bufferedReader().readText()
            val data =JSONObject(conn)
            val image =URL(data.getString("imageURL")).openStream()
            val img = BitmapFactory.decodeStream(image)

            GlobalScope.launch(Dispatchers.Main) {
                binding.imageDetail.setImageBitmap(img)
                binding.nama.text = data.getString("name")
                binding.harga.text = "$ ${data.getString("price")}"
                binding.desc.text = data.getString("description")
            }
        }



    }
}