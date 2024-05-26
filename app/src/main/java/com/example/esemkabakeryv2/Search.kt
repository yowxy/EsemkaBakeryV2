package com.example.esemkabakeryv2

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkabakeryv2.databinding.ActivitySearchBinding
import com.example.esemkabakeryv2.databinding.ItemcardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.URL

class Search : AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding
    var Launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode in 200..299)finish()
    }
    fun refresh () {
        GlobalScope.launch(Dispatchers.IO) {
            val conn = URL("http://10.0.2.2:5000/api/Cake").openStream().bufferedReader().readText()
            val data = JSONArray(conn)

            runOnUiThread {
                binding.searchRv.adapter = Radapter(data, Launcher)
                binding.searchRv.layoutManager = GridLayoutManager(this@Search,2)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarSearch.setNavigationOnClickListener{
            startActivity(Intent(this,Home::class.java))
            finish()
        }

        binding.searchButton.setOnClickListener {
            try {
                GlobalScope.launch(Dispatchers.IO){
                    val conn = URL("http://10.0.2.2:5000/api/Cake?search=${binding.searchPage.text}").openStream().bufferedReader().readText()
                    val data = JSONArray(conn)

                    runOnUiThread {
                        binding.searchRv.adapter = Radapter(data, Launcher)
                        binding.searchRv.layoutManager = GridLayoutManager(this@Search,2)
                    }
                }
            }catch (e:Exception){
                refresh()
            }
        }
        refresh()
    }

    class Radapter (val roti : JSONArray,val laucher: ActivityResultLauncher<Intent>): RecyclerView.Adapter<Radapter.Rview>(){
        class Rview(val binding : ItemcardBinding, val context: Context) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Rview {
            val builder = ItemcardBinding.inflate(LayoutInflater.from(parent.context),parent, false)
            return Rview(builder, parent.context)
        }

        override fun getItemCount(): Int {
            return roti.length()
        }

        override fun onBindViewHolder(holder: Rview, position: Int) {
            val item = roti.getJSONObject(position)
            holder.binding.cakeName.text = item.getString("name")
            GlobalScope.launch(Dispatchers.IO){
                val image = BitmapFactory.decodeStream(URL(item.getString("imageURL")).openStream())
                GlobalScope.launch(Dispatchers.Main){
                    holder.binding.cakeImage.setImageBitmap(image)
                }
            }

            holder.itemView.setOnClickListener {
                val data = Intent(holder.context, Detail::class.java).putExtra("detail", item.toString())
                laucher.launch(data)
            }
        }


    }
}