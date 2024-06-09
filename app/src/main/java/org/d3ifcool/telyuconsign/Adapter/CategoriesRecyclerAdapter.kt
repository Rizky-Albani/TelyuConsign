package org.d3ifcool.telyuconsign.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.d3ifcool.telyuconsign.Model.CategoriesRoomModel
import org.d3ifcool.telyuconsign.Model.SuggestModel
import org.d3ifcool.telyuconsign.R

class CategoriesRecyclerAdapter (private val categoriList: List<CategoriesRoomModel>) : RecyclerView.Adapter<CategoriesRecyclerAdapter.ViewHolder>() {
    var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categories, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = categoriList[position]

        holder.gambar.setImageResource(data.imageCategories)
        holder.nama.text = data.namaCategories

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(data.namaCategories)
        }

    }

    override fun getItemCount(): Int {
        return categoriList.size
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val gambar: ImageView = itemView.findViewById(R.id.categoriesImage)
        val nama: TextView = itemView.findViewById(R.id.categoriesName)
    }

}
