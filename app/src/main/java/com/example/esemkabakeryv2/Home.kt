package com.example.esemkabakeryv2

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkabakeryv2.databinding.ActivityHomeBinding
import com.example.esemkabakeryv2.databinding.ActivityRegisterBinding
import com.example.esemkabakeryv2.databinding.ItemcardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class Home : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    var id = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener{
            startActivity(Intent(this@Home,Search::class.java))
            finish()
        }
        binding.homeBtnOrder.setOnClickListener {
            startActivity(Intent(this,order::class.java))
            finish()
        }

        GlobalScope.launch(Dispatchers.IO) {
            val conn = URL("http://10.0.2.2:5000/api/Cake").openStream().bufferedReader().readText()
            val data = JSONArray(conn)

            val connDetails = URL("http://10.0.2.2:5000/api/Cake/$id").openStream().bufferedReader().readText()
            val jsonsDetail = JSONObject(connDetails)

            GlobalScope.launch(Dispatchers.IO) {
                val check = BitmapFactory.decodeStream(URL("${jsonsDetail.getString("imageURL")}").openStream())
                runOnUiThread {
                    binding.detailImage.setImageBitmap(check)
                }
            }

            runOnUiThread {

                binding .detailName.text = jsonsDetail.getString("name")
                binding.detailPrice.text = "$${jsonsDetail.getString("price")}"
                binding.detailDesc.text = jsonsDetail.getString("description")

                val adapter = object : RecyclerView.Adapter<HorizontalRV>() {
                    override fun onCreateViewHolder(
                        parent: ViewGroup,
                        viewType: Int
                    ): HorizontalRV {
                        val infalate = ItemcardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                        return HorizontalRV(infalate)
                    }

                    override fun getItemCount(): Int {
                        return data.length()
                    }

                    override fun onBindViewHolder(holder: HorizontalRV, position: Int) {
                        val sample = data.getJSONObject(position)
                        holder.binding.cakeName.text = sample.getString("name")

                        holder.itemView.setOnClickListener {
                            id = sample.getInt("cakeID")
                            Log.d("oke", id.toString())
                        }

                        GlobalScope.launch(Dispatchers.IO) {
                            val check = BitmapFactory.decodeStream(URL("${sample.getString("imageURL")}").openStream())
                            runOnUiThread {
                                holder.binding.cakeImage.setImageBitmap(check)
                            }

                        }
                    }
                }
                binding.homeRv.adapter = adapter
                binding.homeRv.layoutManager = LinearLayoutManager(this@Home, LinearLayoutManager.HORIZONTAL, false)
            }
        }
    }
    class HorizontalRV(val binding : ItemcardBinding) : RecyclerView.ViewHolder(binding.root)
}