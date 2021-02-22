package com.iab.galleryandlibrary.adapter.adapterView

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.iab.imagetext.presenter.ImageTextPresenterInterface

class RecyclerAdapterViewHolder(var itemView: View,
                               var imageTextPresenterInterface: ImageTextPresenterInterface) : RecyclerView.ViewHolder(itemView) {
}