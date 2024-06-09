package org.d3ifcool.telyuconsign.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.d3ifcool.telyuconsign.Model.BarangRoomModel
import org.d3ifcool.telyuconsign.Model.ProfileItemRoomModel
import org.d3ifcool.telyuconsign.Model.SuggestModel
import org.d3ifcool.telyuconsign.R

class ProfileItemRecyclerAdapter(private val profileList: List<SuggestModel>) : RecyclerView.Adapter<ProfileItemRecyclerAdapter.ViewHolder>()  {

    var onItemClick: ((SuggestModel) -> Unit)? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profil_barang, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = profileList[position]

        Glide.with(holder.itemView.context)
            .load(data.image)
            .into(holder.gambar)
        holder.nama.text = data.itemName
        if (data.desc.length > 100){
            holder.deskripsi.text = data.desc.substring(0, 80) + "..."
        }else{
            holder.deskripsi.text = data.desc
        }
        holder.harga.text = data.price
        holder.kondisi.text = data.condition

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(data)
        }
    }

    override fun getItemCount(): Int {
        return profileList.size
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val gambar: ImageView = itemView.findViewById(R.id.profile_list_image)
        val nama: TextView = itemView.findViewById(R.id.profile_list_nama_barang)
        val deskripsi: TextView = itemView.findViewById(R.id.profile_list_deskripsi_barang)
        val harga: TextView = itemView.findViewById(R.id.profile_list_harga_barang)
        val kondisi: TextView = itemView.findViewById(R.id.profile_list_kondisi_barang)
    }

}