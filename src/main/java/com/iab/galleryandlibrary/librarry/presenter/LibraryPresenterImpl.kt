package com.iab.galleryandlibrary.librarry.presenter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.galleryproject.utils.calculateRecyclerItemHeightWidth
import com.example.galleryproject.utils.creatingImageTextDataModel
import com.example.galleryproject.utils.loadMediaImagesListByBucketName
import com.iab.galleryandlibrary.adapter.RecyclerAdapter
import com.iab.galleryandlibrary.adapter.adapterInterface.RecyclerAdapterInterface
import com.iab.galleryandlibrary.adapter.adapterView.RecyclerAdapterViewHolder
import com.iab.galleryandlibrary.librarry.view.LibraryViewImpl
import com.iab.galleryandlibrary.librarry.view.LibraryViewInterface
import com.iab.galleryandlibrary.utils.ImageUtils
import com.iab.imagetext.model.ImageTextDataModel
import com.iab.imagetext.presenter.ImageTextPresenterImpl
import com.iab.imagetext.presenter.ImageTextPresenterInterface
import java.lang.ref.WeakReference

class LibraryPresenterImpl(builder: Builder): LibraryPresenterInterface, RecyclerAdapterInterface{

//    Weak Reference
    var contextWeakReference = WeakReference<Context>(builder.context)
    var libraryListenerWeakReference = WeakReference<LibraryPresenterInterface.LibraryListener>(builder.libraryListener)
//    Strong reference
    var libraryViewInterface: LibraryViewInterface = LibraryViewImpl(contextWeakReference.get()!!, this)
    var recyclerAdapter:RecyclerAdapter = RecyclerAdapter(this)
//    variables
    var recyclerItemBackgroundColor: Int = Color.TRANSPARENT
    var spanCount:Int = 3
    var recyclerItemMargin:Int = 0
    var recyclerItemPadding:Int = 0
    var imageTextDataModels: ArrayList<ImageTextDataModel> = ArrayList()
//    Listeners
    var imageTextListener = object : ImageTextPresenterInterface.ImageTextListener {
        override fun onImageTextViewClicked(id: Int, isSelected: Boolean) {
//            open image in ViewImage Activity
        }
    }

    companion object LibraryPresenterCompanionObject {
        fun newBuilder(context: Context, libraryListener: LibraryPresenterInterface.LibraryListener): Builder {
            return Builder(context, libraryListener)
        }
    }

    class Builder(var context: Context, var libraryListener: LibraryPresenterInterface.LibraryListener) {
//        properties
        var paddingInView = 0
        var paddingInRecyclerItem = 0
        var marginInView = 0
        var marginInRecyclerViewItem = 0
        var spanCount = 3
        var recyclerOrientation: Int = LinearLayoutManager.VERTICAL
        var viewBackgroundColor = Color.TRANSPARENT
        var recyclerItemBackgroundColor = Color.TRANSPARENT

        fun withPaddingInView(padding: Int): Builder{
            this.paddingInView = padding
            return this
        }
        fun withPaddingInRecyclerItem(padding: Int):Builder{
            this.paddingInRecyclerItem = padding
            return this
        }
        fun withMarginInView(margin: Int):Builder{
            this.marginInView = margin
            return this
        }
        fun withMarginInRecyclerViewItem(margin: Int): Builder{
            this.marginInRecyclerViewItem = margin
            return this
        }
        fun withSpanCount(spanCount: Int): Builder{
            this.spanCount = spanCount
            return this
        }
        fun withViewBackgroundColor(viewBackgroundColor: Int): Builder{
            this.viewBackgroundColor = viewBackgroundColor
            return this
        }
        fun withRecyclerViewItemBackgroundColor(recyclerItemBackgroundColor: Int): Builder{
            this.recyclerItemBackgroundColor = recyclerItemBackgroundColor
            return this
        }
        fun withRecyclerViewOrientation(recyclerOrientation: Int): Builder{
            this.recyclerOrientation = recyclerOrientation
            return this
        }
        fun build():LibraryPresenterInterface{
            return LibraryPresenterImpl(this)
        }
    }

    init {
        this.recyclerItemBackgroundColor = builder.recyclerItemBackgroundColor
        this.spanCount = builder.spanCount
        this.recyclerItemMargin = builder.marginInRecyclerViewItem
        this.recyclerItemPadding = builder.paddingInRecyclerItem
        libraryViewInterface.setViewMargin(builder.marginInView)
        libraryViewInterface.setViewPadding(builder.paddingInView)
        libraryViewInterface.setViewBackgroundColor(builder.viewBackgroundColor)
//        Now creating adapter object and layout manager and set to view
        libraryViewInterface.setRecyclerLayoutManager(GridLayoutManager(contextWeakReference.get(),builder.spanCount, builder.recyclerOrientation,false))
        libraryViewInterface.setRecyclerAdapter(recyclerAdapter)
    }

    override fun createView(folderName:String) {
        val mediaImageTextDataModel = loadMediaImagesListByBucketName(contextWeakReference.get()!!, folderName, null)
        imageTextDataModels.clear()
        imageTextDataModels = creatingImageTextDataModel(mediaImageTextDataModel)
//        Toast.makeText(contextWeakReference.get(),imageTextDataModels.size.toString(), Toast.LENGTH_LONG).show()
    }

    override fun getView(): View {
        return libraryViewInterface as View
    }

    //    Recycler Adapter View Interface methods implementation
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val imageTextPresenterInterface =
            ImageTextPresenterImpl.newBuilder(contextWeakReference.get()!!, imageTextListener)
                .withViewWidthInDP(ImageUtils.pxToDp(contextWeakReference.get(), calculateRecyclerItemHeightWidth(this.spanCount).x).toInt())
                .withViewHeightInDP(ImageUtils.pxToDp(contextWeakReference.get(),calculateRecyclerItemHeightWidth(this.spanCount).y).toInt())
                .withViewPaddingInDP(this.recyclerItemPadding)
                .withViewMarginInDP(this.recyclerItemMargin)
                .withImageTextImageViewScaleType(ImageView.ScaleType.FIT_CENTER)
                .withImageTextTextViewVisibility(View.GONE)
                .build()
        val view = imageTextPresenterInterface.getView()
        return RecyclerAdapterViewHolder(view, imageTextPresenterInterface)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RecyclerAdapterViewHolder).imageTextPresenterInterface.createView(imageTextDataModel = imageTextDataModels.get(position))
    }

    override fun getItemCount(): Int {
        return imageTextDataModels.size
    }
}