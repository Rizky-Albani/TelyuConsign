package org.d3ifcool.telyuconsign.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.d3ifcool.telyuconsign.Model.SuggestModel
import org.d3ifcool.telyuconsign.R

class BarangRecyclerAdapter (private val barangList: List<SuggestModel>) : RecyclerView.Adapter<BarangRecyclerAdapter.ViewHolder>() {

   var onItemClick: ((SuggestModel) -> Unit)? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_barang, parent, false)


        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = barangList[position]

        Glide.with(holder.itemView.context)
            .load(data.image)
            .into(holder.gambar)
        holder.nama.text = data.itemName
        holder.deskripsi.text = data.desc
        holder.harga.text = data.price
        holder.kondisi.text = data.condition
        if(data.categories == "0"){
            holder.katergori.text = "Elektronik"
        }else if(data.categories == "1"){
            holder.katergori.text = "Furniture"
        }else if(data.categories == "2"){
            holder.katergori.text = "Kitchenware"
        }else if(data.categories == "3"){
            holder.katergori.text = "Vehicle"
        }else if(data.categories == "4"){
            holder.katergori.text = "Sport"
        }else if(data.categories == "5"){
            holder.katergori.text = "Others"
        }
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(data)
        }
    }

    override fun getItemCount(): Int {
        return barangList.size
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val gambar: ImageView = itemView.findViewById(R.id.imageViewBarang)
        val nama: TextView = itemView.findViewById(R.id.textViewNamaBarang)
        val deskripsi: TextView = itemView.findViewById(R.id.textViewDeskripsiBarang)
        val harga: TextView = itemView.findViewById(R.id.textViewHargaBarang)
        val kondisi: TextView = itemView.findViewById(R.id.textViewKondisiBarang)
        val katergori: TextView = itemView.findViewById(R.id.categoriesBarang)
    }


}