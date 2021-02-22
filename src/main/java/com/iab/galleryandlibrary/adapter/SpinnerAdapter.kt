package com.iab.galleryandlibrary.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.iab.galleryandlibrary.model.FolderNameAndItemCount
import com.iab.galleryandlibrary.R

class SpinnerAdapter(context: Context, val res: Int, private val arrayList: ArrayList<FolderNameAndItemCount>) : ArrayAdapter<FolderNameAndItemCount>(context, 0, arrayList){

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    fun initView(position: Int, convertView: View?, parent: ViewGroup) : View{
        var itemView: View
        if(convertView == null){
            itemView = LayoutInflater.from(context).inflate(res, null)
        }else{
            itemView = convertView
        }
        itemView.findViewById<TextView>(R.id.spinner_item_textView).text = arrayList[position].bucketName+" ("+arrayList[position].itemCount+")"
        return itemView
    }
}