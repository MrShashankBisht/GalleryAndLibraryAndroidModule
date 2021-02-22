package com.example.galleryproject.utils

import android.content.ContentUris
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.provider.MediaStore
import com.iab.galleryandlibrary.model.MediaStoreFolderDataModel
import com.iab.galleryandlibrary.model.MediaStoreItemDataModel
import com.iab.galleryandlibrary.utils.MediaType
import com.iab.galleryandlibrary.utils.Size
import com.iab.imagetext.model.ImageTextDataModel
import com.iab.imagetext.model.ImageType
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

fun loadFoldersList(context: Context, formatArray: Array<String?>?, mediaType: MediaType): ArrayList<MediaStoreFolderDataModel> {
    val foldersNameList = ArrayList<MediaStoreFolderDataModel>()
    val picPaths = ArrayList<String>()
    val projection = arrayOf( //media-database-columns-to-retrieve
        MediaStore.Images.ImageColumns._ID,
        MediaStore.Images.ImageColumns.DATE_ADDED,
        MediaStore.Images.ImageColumns.SIZE,
        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
        MediaStore.Images.ImageColumns.BUCKET_ID,
        MediaStore.Images.ImageColumns.MIME_TYPE,
        MediaStore.Images.ImageColumns.DATA,
        MediaStore.Images.Thumbnails.DATA
    )
    val selection: String? = null //sql-where-clause-with-placeholder-variables;
    val selectionArgs: Array<String>? = null //new String[] {
    //values-of-placeholder-variables
    //};
    val sortOrder = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC" //sql-order-by-clause;
    val cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )
    // WE WILL CACHE THE Ids of the Coloumns
    val idColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
    val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
    val bucketDisplayColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
    val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
    val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
    val dataPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    val thumbPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA)
    val count = cursor.count
    if (count > 0) {
        cursor.moveToFirst()
        do {
            // Use an ID column from the projection to get
            // a URI representing the media item itself.
            val id = cursor.getLong(idColumn)
            val dateAdded = Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateAddedColumn)))
            val bucketDisplayName = cursor.getString(bucketDisplayColumn)
            val bucketId = cursor.getInt(bucketIdColumn)
            val mimeType = cursor.getString(mimeTypeColumn)
            val dataPath = cursor.getString(dataPathColumn)
            val thumbPath = cursor.getString(thumbPathColumn)

//                String type = mimeType.replace("image/", "");

            // Add this to the Model
            val contentURI = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )
            if (mimeType != "") {
                if (formatArray != null && formatArray.size > 0) {
                    for (formatType in formatArray) {
                        //TODO all lower case mimeType
                        if (mimeType.endsWith(formatType!!)) {
                            addValueInModel(
                                context,
                                dataPath,
                                bucketDisplayName,
                                picPaths,
                                bucketId,
                                contentURI,
                                dateAdded,
                                foldersNameList,
                                mediaType,
                                thumbPath
                            )
                        }
                    }
                } else {
                    addValueInModel(
                        context,
                        dataPath,
                        bucketDisplayName,
                        picPaths,
                        bucketId,
                        contentURI,
                        dateAdded,
                        foldersNameList,
                        mediaType,
                        thumbPath
                    )
                }
            }
        } while (cursor.moveToNext())
        cursor.close()
    }
    return foldersNameList
}

private fun addValueInModel(context: Context, dataPath: String, bucketDisplayName: String, picPaths: ArrayList<String>, bucketId: Int, contentURI: Uri, dateAdded: Date, foldersNameList: ArrayList<MediaStoreFolderDataModel>, mediaType: MediaType, thumbPath: String) {
    var bucketDisplayName: String? = bucketDisplayName
    var folderpaths = dataPath.substring(0, dataPath.lastIndexOf("/"))
    if (bucketDisplayName == null) {
        bucketDisplayName = folderpaths.substring(folderpaths.lastIndexOf("/") + 1)
    }
    folderpaths = "$folderpaths/"
    if (!picPaths.contains(folderpaths)) {
        picPaths.add(folderpaths)
        var checkValidUriIs: Boolean
        //            if (mediaType == MediaPickerPresenterImpl.Type.VIDEO) {
//                checkValidUriIs = checkValidVideoUri(context, contentURI);
//            } else {
//                checkValidUriIs = checkValidImageUri(context, thumbPath);
//            }
//
//            if (!checkValidUriIs) {
//                contentURI = null;
//            }
        // Create MediaStoreFolder Object
        val mediaStoreFolder = MediaStoreFolderDataModel(
            bucketId,
            bucketDisplayName,
            folderpaths,
            contentURI,
            1,
            dateAdded
        )
        foldersNameList.add(mediaStoreFolder)
    } else {
        for (i in foldersNameList.indices) {
            if (foldersNameList[i].folderPath == folderpaths) {
                val numberOfItems = foldersNameList[i].numberOfMedia + 1
                foldersNameList[i].numberOfMedia = numberOfItems
                // Check if the Date is Greater than the Previous Date
                if (foldersNameList[i].lastUpdatedDate.before(dateAdded)) {
                    foldersNameList[i].lastUpdatedDate = dateAdded
                }
            }
        }

//            if (foldersNameList.get(foldersNameList.size() - 1).getFirstImageURI() == null) {
//                boolean checkValidUriIs;
//                if (mediaType == MediaPickerPresenterImpl.Type.VIDEO) {
//                    checkValidUriIs = checkValidVideoUri(context, contentURI);
//                } else {
//                    checkValidUriIs = checkValidImageUri(context, thumbPath);
//                }
//                if (checkValidUriIs) {
//                    foldersNameList.get(foldersNameList.size() - 1).setUpdatedFirstImageUri(contentURI);
//                }
//            }
    }
}

// Load Media Data for Image
fun loadMediaImagesList(context: Context, bucketId: Int, formatArray: Array<String?>?): ArrayList<MediaStoreItemDataModel> {
    val imageDataList = ArrayList<MediaStoreItemDataModel>()
    val projection = arrayOf( //media-database-columns-to-retrieve
        MediaStore.Images.ImageColumns._ID,
        MediaStore.Images.ImageColumns.DATE_ADDED,
        MediaStore.Images.ImageColumns.SIZE,
        MediaStore.Images.ImageColumns.BUCKET_ID,
        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
        MediaStore.Images.ImageColumns.DISPLAY_NAME,
        MediaStore.Images.ImageColumns.MIME_TYPE,
        MediaStore.Images.ImageColumns.DATA
    )
    val selection =
        MediaStore.Images.Media.BUCKET_ID + " = ? " //null; //sql-where-clause-with-placeholder-variables;
    val selectionArgs = arrayOf("" + bucketId + "") //new String[] {

    //values-of-placeholder-variables
    //};
    val sortOrder = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC" //sql-order-by-clause;
    val cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )
    // WE WILL CACHE THE Ids of the Coloumns
    val idColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
    val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
    val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
    val mimeTypeColumn = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
    val dataPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    val count = cursor.count
    if (count > 0) {
        while (cursor.moveToNext()) {
            // Use an ID column from the projection to get
            // a URI representing the media item itself.
            val id = cursor.getLong(idColumn)
            val dateAdded = Date(
                TimeUnit.SECONDS.toMillis(
                    cursor.getLong(dateAddedColumn)
                )
            )
            val displayName = cursor.getString(displayNameColumn)
            val mimeType = cursor.getString(mimeTypeColumn)
            val dataPath = cursor.getString(dataPathColumn)
            //                String type = mimeType.replace("image/", "");
            // Add this to the Model
            val contentURI = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )
            if (mimeType != "") {
                if (formatArray != null && formatArray.size > 0) {
                    for (formatType in formatArray) {
                        if (mimeType.endsWith(formatType!!)) {
                            val mediaStoreItemDataModel =
                                MediaStoreItemDataModel(id, contentURI, displayName, dateAdded, 0)
                            imageDataList.add(mediaStoreItemDataModel)
                        }
                    }
                } else {
                    val mediaStoreItemDataModel =
                        MediaStoreItemDataModel(id, contentURI, displayName, dateAdded, 0)
                    imageDataList.add(mediaStoreItemDataModel)
                }
            }
        }
    }
    return imageDataList
}

fun creatingImageTextDataModel(mediaPickerItemDataModels: ArrayList<MediaStoreItemDataModel>): ArrayList<ImageTextDataModel> {
    val imageTextDataModels = ArrayList<ImageTextDataModel>()
    for ((i, mediaPickerItemDataModel) in mediaPickerItemDataModels.withIndex()) {
        val imageTextDataModel: ImageTextDataModel = ImageTextDataModel()
        imageTextDataModel.id = i
        imageTextDataModel.imageUri = mediaPickerItemDataModel.contentURI
        imageTextDataModel.text = mediaPickerItemDataModel.displayName
        imageTextDataModel.imageType = ImageType.URI
        imageTextDataModel.isCheckBoxVisible = false
        imageTextDataModel.isChecked = false
        imageTextDataModels.add(imageTextDataModel)
    }
    return imageTextDataModels
}

fun loadMediaImagesListByBucketName(context: Context, bucketName: String, formatArray: Array<String?>?): ArrayList<MediaStoreItemDataModel> {
    val imageDataList = ArrayList<MediaStoreItemDataModel>()
    val projection = arrayOf( //media-database-columns-to-retrieve
        MediaStore.Images.ImageColumns._ID,
        MediaStore.Images.ImageColumns.DATE_ADDED,
        MediaStore.Images.ImageColumns.SIZE,
        MediaStore.Images.ImageColumns.BUCKET_ID,
        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
        MediaStore.Images.ImageColumns.DISPLAY_NAME,
        MediaStore.Images.ImageColumns.MIME_TYPE,
        MediaStore.Images.ImageColumns.DATA
    )
    val selection =
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ? " //null; //sql-where-clause-with-placeholder-variables;
    val selectionArgs = arrayOf("" + bucketName + "") //new String[] {

    //values-of-placeholder-variables
    //};
    val sortOrder = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC" //sql-order-by-clause;
    val cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )
    // WE WILL CACHE THE Ids of the Coloumns
    val idColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
    val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
    val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
    val mimeTypeColumn = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
    val dataPathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    val count = cursor.count
    if (count > 0) {
        while (cursor.moveToNext()) {
            // Use an ID column from the projection to get
            // a URI representing the media item itself.
            val id = cursor.getLong(idColumn)
            val dateAdded = Date(
                TimeUnit.SECONDS.toMillis(
                    cursor.getLong(dateAddedColumn)
                )
            )
            val displayName = cursor.getString(displayNameColumn)
            val mimeType = cursor.getString(mimeTypeColumn)
            val dataPath = cursor.getString(dataPathColumn)
            //                String type = mimeType.replace("image/", "");
            // Add this to the Model
            val contentURI = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )
            if (mimeType != "") {
                if (formatArray != null && formatArray.size > 0) {
                    for (formatType in formatArray) {
                        if (mimeType.endsWith(formatType!!)) {
                            val mediaStoreItemDataModel =
                                MediaStoreItemDataModel(id, contentURI, displayName, dateAdded, 0)
                            imageDataList.add(mediaStoreItemDataModel)
                        }
                    }
                } else {
                    val mediaStoreItemDataModel =
                        MediaStoreItemDataModel(id, contentURI, displayName, dateAdded, 0)
                    imageDataList.add(mediaStoreItemDataModel)
                }
            }
        }
    }
    cursor.close()
    return imageDataList
}

fun calculateRecyclerItemHeightWidth(spanCount:Int): Size {
    return Size(getScreenWidth()/spanCount.toFloat(), getScreenWidth()/spanCount.toFloat())
}

fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}