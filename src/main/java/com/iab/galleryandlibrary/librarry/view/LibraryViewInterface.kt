package com.iab.galleryandlibrary.librarry.view

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

interface LibraryViewInterface {
    fun prepareView()
//    background color
    fun setViewBackgroundColor(backgroundColor: Int)
//    padding
    fun setViewPadding(padding:Int)
//    margin
    fun setViewMargin(margin: Int)
//    adapter and layout manager
    fun setRecyclerAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>)
    fun setRecyclerLayoutManager(layoutManager: LinearLayoutManager)
    fun setRecyclerLayoutManager(layoutManager: GridLayoutManager)
    fun setRecyclerLayoutManager(layoutManager: StaggeredGridLayoutManager)

}