package com.iab.galleryandlibrary.librarry.presenter

import android.view.View
import com.iab.imagetext.model.ImageTextDataModel

interface LibraryPresenterInterface {
    fun createView(folderName:String)
    fun getView(): View
    fun removeItemFromRecyclerView(id:Int)
    fun reloadView();

    interface LibraryListener {
        fun onImageViewClicked(imageTextDataModel: ImageTextDataModel)
    }
}