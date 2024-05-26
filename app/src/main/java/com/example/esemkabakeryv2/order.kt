package com.example.esemkabakeryv2

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkabakeryv2.databinding.ActivityOrderBinding
import com.example.esemkabakeryv2.databinding.CartOrderBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class order : AppCompatActivity() {
    lateinit var binding: ActivityOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarOrder.setNavigationOnClickListener{
            startActivity(Intent(this,Home::class.java))
            finish()
        }

        GlobalScope.launch(Dispatchers.IO){
            val conn = URL("http://10.0.2.2:5000/api/Cake").openStream().bufferedReader().readText()
            val jsons = JSONArray(conn)

            runOnUiThread{
                binding.orderRv.adapter = Radapter(jsons)
                binding.orderRv.layoutManager = LinearLayoutManager(this@order)
            }
        }
    }
    class Radapter (val roti : JSONArray): RecyclerView.Adapter<Radapter.Rview>(){
        class Rview(val binding : CartOrderBinding, val context: Context) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Rview {
            val builder = CartOrderBinding.inflate(LayoutInflater.from(parent.context),parent, false)
            return Rview(builder, parent.context)
        }

        override fun getItemCount(): Int {
            return roti.length()
        }

        override fun onBindViewHolder(holder: Rview, position: Int) {
            val item = roti.getJSONObject(position)
            holder.binding.namaO.text = item.getString("name")
            holder.binding.hargaO.text = item.getString("price")
            GlobalScope.launch(Dispatchers.IO){
                val image = BitmapFactory.decodeStream(URL(item.getString("imageURL")).openStream())
                GlobalScope.launch(Dispatchers.Main){
                    holder.binding.imgO.setImageBitmap(image)
                }
            }

            holder.binding.tambah.setOnClickListener {
                // Get the current quantity as an integer (handle non-numeric input)
                var unit = holder.binding.qty.text.toString().toIntOrNull() ?: 0
                // Increment the quantity by 1
                unit++
                // Update the quantity TextView (avoid flickering)
                holder.binding.qty.setText(unit.toString())
                // Calculate the new total price (prevent overflow)
                val hargaPerItem: Double = holder.binding.hargaO.text.toString().toDoubleOrNull() ?: 0.0
                val totalPrice = hargaPerItem * unit.toLong()

                // Update the total price TextView (avoid flickering)
                holder.binding.hargaO.setText(String.format("%.2f", totalPrice))

            }

            holder.binding.kurang.setOnClickListener{
                // Get the current quantity (handle non-numeric input)
                var unit = holder.binding.qty.text.toString().toIntOrNull() ?: 0
                // Decrement the quantity by 1 (but not less than 0)
                unit--
                unit = maxOf(unit, 0) // Ensure quantity doesn't go below 0
                // Update the quantity TextView
                holder.binding.qty.setText(unit.toString())
                // Calculate the total price (using floating-point arithmetic)
                val hargaPerItem: Double = holder.binding.hargaO.text.toString().toDoubleOrNull() ?: 0.0
                val totalPrice = hargaPerItem * unit
                // Update the total price TextView
                holder.binding.hargaO.setText(String.format("%.2f", totalPrice))

            }
        }
    }
}