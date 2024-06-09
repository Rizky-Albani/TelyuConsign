package org.d3ifcool.telyuconsign.ui.customView

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.d3ifcool.telyuconsign.Adapter.BarangRecyclerAdapter
import org.d3ifcool.telyuconsign.Adapter.MessageRecyclerAdapter.Companion.TAG
import org.d3ifcool.telyuconsign.Adapter.ProfileItemRecyclerAdapter
import org.d3ifcool.telyuconsign.Model.SuggestModel
import org.d3ifcool.telyuconsign.R
import org.d3ifcool.telyuconsign.databinding.ActivityListBarangBinding
import org.d3ifcool.telyuconsign.ui.DetailItemActivity
import java.util.Locale

class ListBarangActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivityListBarangBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filter = intent.getStringExtra("category")
        binding.textView.text = "result for $filter..."
        getData(filter)

    }
    private fun getData(filter: String?){
        recyclerView = binding.recyclerViewList
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = ProfileItemRecyclerAdapter(ArrayList<SuggestModel>())
        recyclerView.layoutManager = LinearLayoutManager(this)
        val db = FirebaseFirestore.getInstance()
        val dataList = ArrayList<SuggestModel>()
        if (filter == "Electronic"){
            db.collection("items").whereEqualTo("categories", "0").get().addOnSuccessListener {
                for (document in it) {
                    val imageUrls = document.get("images") as? List<String>

                    if (imageUrls != null && imageUrls.isNotEmpty()) {
                        // Now you have the array of image URLs
                        // You can use it as needed

                        // Example: Print each URL
                        for (imageUrl in imageUrls) {
                            println(imageUrl)
                        }

                        val data = SuggestModel(
                            document.data["uid"].toString(),
                            document.id,
                            document.data["itemName"].toString(),
                            imageUrls?.get(0).toString(),
                            document.data["price"].toString(),
                            document.data["condition"].toString(),
                            document.data["desc"].toString(),
                            document.data["categories"].toString(),
                            document.data["longitude"].toString(),
                            document.data["latitude"].toString(),
                        )
                        dataList.add(data)
                    }
                }
                val adapter = ProfileItemRecyclerAdapter(dataList)
                adapter.onItemClick = { selectedData ->
                    val intent = Intent(this, DetailItemActivity::class.java)
                    intent.putExtra("id", selectedData.id)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter

            }
        }else if (filter == "Furniture"){
            db.collection("items").whereEqualTo("categories", "1").get().addOnSuccessListener {
                for (document in it) {
                    val imageUrls = document.get("images") as? List<String>

                    if (imageUrls != null && imageUrls.isNotEmpty()) {
                        // Now you have the array of image URLs
                        // You can use it as needed

                        // Example: Print each URL
                        for (imageUrl in imageUrls) {
                            println(imageUrl)
                        }

                        val data = SuggestModel(
                            document.data["uid"].toString(),
                            document.id,
                            document.data["itemName"].toString(),
                            imageUrls?.get(0).toString(),
                            document.data["price"].toString(),
                            document.data["condition"].toString(),
                            document.data["desc"].toString(),
                            document.data["categories"].toString(),
                            document.data["longitude"].toString(),
                            document.data["latitude"].toString(),
                        )
                        dataList.add(data)
                    }
                }
                val adapter = ProfileItemRecyclerAdapter(dataList)
                adapter.onItemClick = { selectedData ->
                    val intent = Intent(this, DetailItemActivity::class.java)
                    intent.putExtra("id", selectedData.id)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
            }
        }else if (filter == "Kitchenware"){
            db.collection("items").whereEqualTo("categories", "2").get().addOnSuccessListener {
                for (document in it) {
                    val imageUrls = document.get("images") as? List<String>

                    if (imageUrls != null && imageUrls.isNotEmpty()) {
                        // Now you have the array of image URLs
                        // You can use it as needed

                        // Example: Print each URL
                        for (imageUrl in imageUrls) {
                            println(imageUrl)
                        }

                        val data = SuggestModel(
                            document.data["uid"].toString(),
                            document.id,
                            document.data["itemName"].toString(),
                            imageUrls?.get(0).toString(),
                            document.data["price"].toString(),
                            document.data["condition"].toString(),
                            document.data["desc"].toString(),
                            document.data["categories"].toString(),
                            document.data["longitude"].toString(),
                            document.data["latitude"].toString(),
                        )
                        dataList.add(data)
                    }
                }
                val adapter = ProfileItemRecyclerAdapter(dataList)
                adapter.onItemClick = { selectedData ->
                    val intent = Intent(this, DetailItemActivity::class.java)
                    intent.putExtra("id", selectedData.id)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
            }
        }else if (filter == "Vehicle"){
            db.collection("items").whereEqualTo("categories", "3").get().addOnSuccessListener {
                for (document in it) {
                    val imageUrls = document.get("images") as? List<String>

                    if (imageUrls != null && imageUrls.isNotEmpty()) {
                        // Now you have the array of image URLs
                        // You can use it as needed

                        // Example: Print each URL
                        for (imageUrl in imageUrls) {
                            println(imageUrl)
                        }

                        val data = SuggestModel(
                            document.data["uid"].toString(),
                            document.id,
                            document.data["itemName"].toString(),
                            imageUrls?.get(0).toString(),
                            document.data["price"].toString(),
                            document.data["condition"].toString(),
                            document.data["desc"].toString(),
                            document.data["categories"].toString(),
                            document.data["longitude"].toString(),
                            document.data["latitude"].toString(),
                        )
                        dataList.add(data)
                    }
                }
                val adapter = ProfileItemRecyclerAdapter(dataList)
                adapter.onItemClick = { selectedData ->
                    val intent = Intent(this, DetailItemActivity::class.java)
                    intent.putExtra("id", selectedData.id)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
            }
        }else if (filter == "Sports"){
            db.collection("items").whereEqualTo("categories", "4").get().addOnSuccessListener {
                for (document in it) {
                    val imageUrls = document.get("images") as? List<String>

                    if (imageUrls != null && imageUrls.isNotEmpty()) {
                        // Now you have the array of image URLs
                        // You can use it as needed

                        // Example: Print each URL
                        for (imageUrl in imageUrls) {
                            println(imageUrl)
                        }

                        val data = SuggestModel(
                            document.data["uid"].toString(),
                            document.id,
                            document.data["itemName"].toString(),
                            imageUrls?.get(0).toString(),
                            document.data["price"].toString(),
                            document.data["condition"].toString(),
                            document.data["desc"].toString(),
                            document.data["categories"].toString(),
                            document.data["longitude"].toString(),
                            document.data["latitude"].toString(),
                        )
                        dataList.add(data)
                    }
                }
                val adapter = ProfileItemRecyclerAdapter(dataList)
                adapter.onItemClick = { selectedData ->
                    val intent = Intent(this, DetailItemActivity::class.java)
                    intent.putExtra("id", selectedData.id)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
            }
        } else if (filter == "chat") {
            binding.textView.text = "Click on the item to chat with the seller"
            db.collection("items").get().addOnSuccessListener {
                for (document in it) {
                    val imageUrls = document.get("images") as? List<String>

                    if (imageUrls != null && imageUrls.isNotEmpty()) {
                        // Now you have the array of image URLs
                        // You can use it as needed

                        // Example: Print each URL
                        for (imageUrl in imageUrls) {
                            println(imageUrl)
                        }

                        val data = SuggestModel(
                            document.data["uid"].toString(),
                            document.id,
                            document.data["itemName"].toString(),
                            imageUrls?.get(0).toString(),
                            document.data["price"].toString(),
                            document.data["condition"].toString(),
                            document.data["desc"].toString(),
                            document.data["categories"].toString(),
                            document.data["longitude"].toString(),
                            document.data["latitude"].toString(),
                        )
                        dataList.add(data)
                    }
                }
                val adapter = ProfileItemRecyclerAdapter(dataList)
                adapter.onItemClick = { selectedData ->
                    val intent = Intent(this, DetailItemActivity::class.java)
                    intent.putExtra("id", selectedData.id)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
            }
        }else {
            //show all item
            db.collection("items").get().addOnSuccessListener {
                for (document in it) {
                    val imageUrls = document.get("images") as? List<String>

                    if (imageUrls != null && imageUrls.isNotEmpty()) {

                        if (document.data["itemName"].toString().toLowerCase(Locale.ROOT)
                                .contains(filter.toString().toLowerCase(Locale.ROOT))
                        ) {
                            // Now you have the array of image URLs
                            // You can use it as needed

                            // Example: Print each URL
                            for (imageUrl in imageUrls) {
                                println(imageUrl)
                            }

                            val data = SuggestModel(
                                document.data["uid"].toString(),
                                document.id,
                                document.data["itemName"].toString(),
                                imageUrls?.get(0).toString(),
                                document.data["price"].toString(),
                                document.data["condition"].toString(),
                                document.data["desc"].toString(),
                                document.data["categories"].toString(),
                                document.data["longitude"].toString(),
                                document.data["latitude"].toString(),
                            )
                            dataList.add(data)
                        }
                    }
                }


                val adapter = ProfileItemRecyclerAdapter(dataList)
                adapter.onItemClick = { selectedData ->
                    val intent = Intent(this, DetailItemActivity::class.java)
                    intent.putExtra("id", selectedData.id)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
            }
        }
    }

}