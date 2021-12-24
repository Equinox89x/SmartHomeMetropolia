package com.example.smarthomeproject

import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_db.view.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class HomeAdapter(var items: Home): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Homeholder(
            LayoutInflater.from(parent.context).inflate(R.layout.activity_main, parent, false)
        )
    }
    override fun getItemCount(): Int {
        return items.HouseName.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.lblHome.text = items.HouseName
    }

    class Homeholder(view: View): RecyclerView.ViewHolder(view){
        val txtHouseName = view.lblHome
    }
}