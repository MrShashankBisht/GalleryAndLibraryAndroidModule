package com.iab.galleryandlibrary.adapter.adapterInterface

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface RecyclerAdapterInterface {
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    fun getItemCount(): Int
}