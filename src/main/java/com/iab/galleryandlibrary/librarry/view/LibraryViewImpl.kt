package com.iab.galleryandlibrary.librarry.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.iab.galleryandlibrary.R
import com.iab.galleryandlibrary.librarry.presenter.LibraryPresenterInterface
import kotlinx.android.synthetic.main.library_layout.view.*

class LibraryViewImpl(context: Context, libraryPresenterInterface: LibraryPresenterInterface) :
    ConstraintLayout(
        context
    ), LibraryViewInterface {

    init {
        prepareView()
    }

    override fun prepareView() {
        //        set Layout Params to this constraint
        val layoutParams: ConstraintLayout.LayoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        this.layoutParams = layoutParams

//        inflate Layout in View
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutInflater.inflate(R.layout.library_layout, this, true)
    }

    override fun setViewBackgroundColor(backgroundColor: Int) {
        library_layout.setBackgroundColor(backgroundColor)
    }

    override fun setViewPadding(padding: Int) {
        library_layout.setPadding(padding, padding, padding, padding)
    }

    override fun setViewMargin(margin: Int) {
        post {
            val marginLayoutParams = layoutParams as MarginLayoutParams
            marginLayoutParams.setMargins(margin, margin, margin, margin)
            requestLayout()
        }
    }

    override fun invalidateRecyclerView() {
        library_recycler_view.invalidate()
    }

    override fun setRecyclerAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?) {
        library_recycler_view.adapter = adapter
    }

    override fun setRecyclerLinearLayoutManager(layoutManager: LinearLayoutManager?) {
        library_recycler_view.layoutManager = layoutManager
    }

    override fun setRecyclerGridLayoutManager(layoutManager: GridLayoutManager?) {
        library_recycler_view.layoutManager = layoutManager
    }

    override fun setRecyclerStaggeredLayoutManager(layoutManager: StaggeredGridLayoutManager?) {
        library_recycler_view.layoutManager = layoutManager
    }
}