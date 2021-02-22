package com.iab.galleryandlibrary.librarry.presenter

import android.view.View

interface LibraryPresenterInterface {
    fun createView(folderName:String)
    fun getView(): View

    interface LibraryListener {

    }
}