package com.iab.galleryandlibrary.model

import android.net.Uri
import java.util.*

class FolderNameAndItemCount(var bucketName: String, var bucketId: Int, var itemCount: Int)

class MediaStoreFolderDataModel(
    val bucketId: Int,
    var bucketName: String,
    val folderPath: String,
    var firstImageURI: Uri,
    var numberOfMedia: Int,
    var lastUpdatedDate: Date
) {
    fun setUpdatedFirstImageUri(firstImageURI: Uri) {
        this.firstImageURI = firstImageURI
    }
}

class MediaStoreItemDataModel(
    var id: Long,
    var contentURI: Uri,
    var displayName: String,
    private val date: Date,
    var duration: Int
) {

    fun getDate(): Date {
        return date
    }

    fun setDate(date: Date?) {
        var date = date
        date = date
    }

}
