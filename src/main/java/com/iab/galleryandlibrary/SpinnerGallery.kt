package com.iab.galleryandlibrary

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.galleryproject.utils.calculateRecyclerItemHeightWidth
import com.example.galleryproject.utils.creatingImageTextDataModel
import com.example.galleryproject.utils.loadFoldersList
import com.example.galleryproject.utils.loadMediaImagesList
import com.iab.galleryandlibrary.adapter.RecyclerAdapter
import com.iab.galleryandlibrary.adapter.SpinnerAdapter
import com.iab.galleryandlibrary.adapter.adapterInterface.RecyclerAdapterInterface
import com.iab.galleryandlibrary.adapter.adapterView.RecyclerAdapterViewHolder
import com.iab.galleryandlibrary.model.FolderNameAndItemCount
import com.iab.galleryandlibrary.model.MediaStoreFolderDataModel
import com.iab.galleryandlibrary.model.MediaStoreItemDataModel
import com.iab.galleryandlibrary.utils.ImageUtils
import com.iab.galleryandlibrary.utils.MediaType
import com.iab.imagetext.model.ImageTextDataModel
import com.iab.imagetext.model.ImageType
import com.iab.imagetext.presenter.ImageTextPresenterImpl
import com.iab.imagetext.presenter.ImageTextPresenterInterface
import kotlinx.android.synthetic.main.activity_spinner_gallery.*
import com.iab.galleryandlibrary.utils.Size

class SpinnerGallery : AppCompatActivity(), RecyclerAdapterInterface {
    var folderNameList: ArrayList<FolderNameAndItemCount> = ArrayList<FolderNameAndItemCount>()
    lateinit var spinnerAdapter: SpinnerAdapter
    var selectMultiple = false
    var mediaStoreFolderDataModels: ArrayList<MediaStoreFolderDataModel> =
        ArrayList<MediaStoreFolderDataModel>()
    var mediaImageDataModels: ArrayList<MediaStoreItemDataModel> =
        ArrayList<MediaStoreItemDataModel>()
    var imageTextDataModelList = ArrayList<ImageTextDataModel>()
    var recyclerViewAdapter: RecyclerAdapter = RecyclerAdapter(this)
//    local variables
    var spanCount:Int = 3

    //    creating imageTextListener object
    var imageTextListener = object : ImageTextPresenterInterface.ImageTextListener {
        override fun onImageTextViewClicked(id: Int, isSelected: Boolean) {
            val returnIntent = Intent()
            returnIntent.data = imageTextDataModelList[id].imageUri
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spinner_gallery)
        mediaStoreFolderDataModels = loadFoldersList(this, null, MediaType.IMAGE)
        mediaImageDataModels = loadMediaImagesList(
            this@SpinnerGallery,
            mediaStoreFolderDataModels[0].bucketId,
            null
        )
        imageTextDataModelList = creatingImageTextDataModel(mediaImageDataModels)
//        getting file name from the mediaStoreFolder Data model
        folderNameList.clear()
        for (folderData in mediaStoreFolderDataModels) {
            folderNameList.add(
                FolderNameAndItemCount(
                    folderData.bucketName,
                    folderData.bucketId,
                    folderData.numberOfMedia
                )
            )
        }
        spinnerAdapter = SpinnerAdapter(this, R.layout.spinner_item_layout, folderNameList);
        gallery_dir_spinner.adapter = spinnerAdapter

        gallery_dir_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var bucketId = folderNameList[position].bucketId
                mediaImageDataModels.clear()
                mediaImageDataModels = loadMediaImagesList(this@SpinnerGallery, bucketId, null)
//                creating image text Data model for imageText view
                imageTextDataModelList =
                    creatingImageTextDataModel(mediaImageDataModels)
//                notify recycler adapter for data change
                recyclerViewAdapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

//        set recycler adapter and layout manager to recycler View
//        gallery_recyclerView.layoutManager =
//            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        gallery_recyclerView.layoutManager = StaggeredGridLayoutManager(spanCount, LinearLayout.VERTICAL)
        gallery_recyclerView.adapter = recyclerViewAdapter

//        select Multiple Image On Clicked
        gallery_select_multiple_image.setOnClickListener {
            selectMultiple = !selectMultiple
            if (selectMultiple) {
                gallery_done_image_button.visibility = View.VISIBLE
            }
        }

//        on done button clicked
        gallery_done_image_button.setOnClickListener {
//            get all the images that have checked.

        }


    }




    //    Recycler adapter Interface methods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val imageTextPresenterInterface =
            ImageTextPresenterImpl.newBuilder(this, imageTextListener)
                .withViewWidthInDP(ImageUtils.pxToDp(applicationContext, calculateRecyclerItemHeightWidth(this.spanCount).x).toInt())
                .withViewHeightInDP(ImageUtils.pxToDp(applicationContext,calculateRecyclerItemHeightWidth(this.spanCount).y).toInt())
                .withImageTextImageViewScaleType(ImageView.ScaleType.FIT_CENTER)
                .build()
        val view = imageTextPresenterInterface.getView()
        return RecyclerAdapterViewHolder(view, imageTextPresenterInterface)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RecyclerAdapterViewHolder).imageTextPresenterInterface.createView(imageTextDataModel = imageTextDataModelList[position])
    }

    override fun getItemCount(): Int {
        return imageTextDataModelList.size
    }
}