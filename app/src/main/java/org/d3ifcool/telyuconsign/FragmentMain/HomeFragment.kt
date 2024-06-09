package org.d3ifcool.telyuconsign.FragmentMain

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.search.SearchBar
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.d3ifcool.telyuconsign.Adapter.BarangRecyclerAdapter
import org.d3ifcool.telyuconsign.Adapter.CategoriesRecyclerAdapter
import org.d3ifcool.telyuconsign.Model.CategoriesRoomModel
import org.d3ifcool.telyuconsign.Model.SuggestModel
import org.d3ifcool.telyuconsign.R
import org.d3ifcool.telyuconsign.databinding.FragmentHomeBinding
import org.d3ifcool.telyuconsign.ui.DetailItemActivity
import org.d3ifcool.telyuconsign.ui.SearchActivity
import org.d3ifcool.telyuconsign.ui.customView.ListBarangActivity


class HomeFragment : Fragment() {

    private lateinit var bindingHome: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var suggestAdapter: BarangRecyclerAdapter
    private lateinit var catAdapter: CategoriesRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        bindingHome = FragmentHomeBinding.inflate(layoutInflater, container, false)
        suggestAdapter = BarangRecyclerAdapter(mutableListOf())

        categoriesRecyclerView(view)
        suggestRecyclerView(view)

        suggestAdapter.onItemClick = { item ->
            val intent = Intent(activity, DetailItemActivity::class.java)
            intent.putExtra("id", item.id)
            startActivity(intent)
        }

        val searchBar = view.findViewById<EditText>(R.id.search_view)
        searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                // Perform the search operation here
                val query = searchBar.text.toString()
                val intent = Intent(activity, ListBarangActivity::class.java)
                intent.putExtra("category", query)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                return@setOnEditorActionListener true
            }
            false
        }

        return view
    }


    private fun categoriesRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view_categories)
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        // Example data
        val dataList = listOf(
            CategoriesRoomModel(R.drawable.cat_1, "Furniture"),
            CategoriesRoomModel(R.drawable.cat_2, "Vehicle"),
            CategoriesRoomModel(R.drawable.cat_3, "Kitchen"),
            CategoriesRoomModel(R.drawable.cat_4, "Sport"),
            CategoriesRoomModel(R.drawable.cat_5, "Electronic"),
        )
        catAdapter = CategoriesRecyclerAdapter(categoriList = dataList)
        recyclerView.adapter = catAdapter
        catAdapter.onItemClick = { item ->
            val intent = Intent(activity, ListBarangActivity::class.java)
            intent.putExtra("category", item)
            startActivity(intent)
        }
    }

    private fun suggestRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view_suggested_item)
        recyclerView.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)

        val db = Firebase.firestore
        val dataList = ArrayList<SuggestModel>()

        val suggestAdapter = BarangRecyclerAdapter(dataList)

        suggestAdapter.onItemClick = { item ->
            val intent = Intent(activity, DetailItemActivity::class.java)
            intent.putExtra("id", item.id)
            startActivity(intent)
        }

        db.collection("items").get()
            .addOnSuccessListener { result ->
                for (document in result) {

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

                recyclerView.adapter = suggestAdapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this.context, "Failed to get data", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val imageSlider = view.findViewById<ImageSlider>(R.id.image_slide)
        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel("https://res.cloudinary.com/ruparupa-com/image/upload/f_auto,q_auto/v1691484454/Products/10547237_1.jpg","Furniture"))
        imageList.add(SlideModel("https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/iphone-15-finish-select-202309-6-7inch-pink?wid=2560&hei=1440&fmt=jpeg&qlt=95&.v=1692923784895","Smartphone"))
        imageList.add(SlideModel("https://jete.id/wp-content/uploads/2023/05/JETE-08-PRO-11.jpg","Headphone"))
        imageList.add(SlideModel("https://www.ruparupa.com/blog/wp-content/uploads/2021/09/Screen-Shot-2021-09-02-at-14.56.22.jpg","Bedroom Stuff"))

        imageSlider.setImageList(imageList, ScaleTypes.FIT)


    }

    override fun onResume() {
        super.onResume()
        suggestRecyclerView(requireView())
    }

}