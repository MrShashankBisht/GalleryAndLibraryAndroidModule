package com.iab.galleryandlibrary.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iab.galleryandlibrary.adapter.adapterInterface.RecyclerAdapterInterface

class RecyclerAdapter(private val adapterInterface: RecyclerAdapterInterface): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return adapterInterface.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        adapterInterface.onBindViewHolder(holder, position)
    }

    override fun getItemCount(): Int {
        return adapterInterface.getItemCount()
    }
}