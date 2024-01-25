package com.ksajja.newone.adapter

import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ksajja.newone.Constants
import com.ksajja.newone.R
import com.ksajja.newone.extension.isNotNullNorEmpty
import com.ksajja.newone.interfaces.TaskTemplateListener
import com.ksajja.newone.model.TemplateItem
import com.ksajja.newone.util.GlideApp
import java.util.*

/**
 * Created by shivakartik on 3/1/18.
 */

class TaskHorizontalTemplateAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private val SERVICES_TYPE: Int = 1
        private val LOCATION_TYPE: Int = 2
        private val PRODUCT_TYPE: Int = 3
        private val INFORMATION_VIDEO_TYPE: Int = 4
    }

    private var mArrayList: ArrayList<TemplateItem>? = null
    private var mListener: TaskTemplateListener? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        return when (viewType) {
            LOCATION_TYPE -> LocationViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item_task_template_location, parent, false))
            PRODUCT_TYPE -> ProductViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item_task_template_product, parent, false))
            SERVICES_TYPE -> ServiceViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item_task_template_services, parent, false))
            INFORMATION_VIDEO_TYPE -> InformationVideosViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item_task_template_information_videos, parent, false))
            else -> EmptyViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item_empty_view, parent, false))
        }
    }

    fun setData(arrayList: ArrayList<TemplateItem>?) {
        mArrayList = arrayList;
    }

    fun setListener(listener: TaskTemplateListener?){
        mListener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            Constants.TemplateTypes.SERVICES == mArrayList?.get(position)?.templateType -> 1
            Constants.TemplateTypes.SPONSORED_PARTNER == mArrayList?.get(position)?.templateType -> 2
            Constants.TemplateTypes.PRODUCT_SUGGESTION == mArrayList?.get(position)?.templateType -> 3
            Constants.TemplateTypes.INFORMATION_VIDEOS == mArrayList?.get(position)?.templateType -> 4
            else -> 0
        }
    }

    override fun getItemCount(): Int {
        // if arrayList is not null we are getting the size else we are returning 0
        return mArrayList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when {
            holder is LocationViewHolder -> holder.setData(mArrayList?.get(position))
            holder is ProductViewHolder -> holder.setData(mArrayList?.get(position),mListener)
            holder is ServiceViewHolder -> holder.setData(mArrayList?.get(position))
            holder is InformationVideosViewHolder -> holder.setData(mArrayList?.get(position),mListener)
        }
    }

    internal inner class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mLocationImageView: ImageView = view.findViewById(R.id.locationImageView)
        var mLocationNameTextView: TextView = view.findViewById(R.id.locationNameTextView)
        var mDescriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        var mRatingCountTextView: TextView = view.findViewById(R.id.ratingCountTextView)
        var mPriceRatingTextView: TextView = view.findViewById(R.id.priceRatingTextView)
        var mRatingBar : AppCompatRatingBar = view.findViewById(R.id.ratingBar);

        fun setData(templateItem: TemplateItem?) {
            mLocationNameTextView.text = templateItem?.title
            mDescriptionTextView.text = templateItem?.description
        }
    }

    internal inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mRootView: View = view
        var mProductImageView: ImageView = view.findViewById(R.id.productImageView)
        var mProductNameTextView: TextView = view.findViewById(R.id.productNameTextView)
        var mDescriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        var mRatingCountTextView: TextView = view.findViewById(R.id.ratingCountTextView)
        var mPriceTextView: TextView = view.findViewById(R.id.priceTextView)
        var mRatingBar : AppCompatRatingBar = view.findViewById(R.id.ratingBar);

        fun setData(templateItem: TemplateItem?, listener: TaskTemplateListener?) {
            mProductNameTextView.setText(templateItem?.title)
            mDescriptionTextView.setText(templateItem?.description)
            if(templateItem?.previewImage.isNotNullNorEmpty()){
                GlideApp.with(mProductImageView.context)
                        .asBitmap()
                        .load(templateItem?.previewImage)
                        .into(mProductImageView)
            }

            mRootView.setOnClickListener(View.OnClickListener {
                if(listener!= null)
                    listener.onTemplateItemClick(templateItem)
            })
        }
    }

    internal inner class ServiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mServicesNameTextView: TextView = view.findViewById(R.id.servicesNameTextView)
        var mDescriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        var mRatingCountTextView: TextView = view.findViewById(R.id.ratingCountTextView)
        var mServicesHolderTextView: TextView = view.findViewById(R.id.serviceHolderTextView)
        var mRatingBar : AppCompatRatingBar = view.findViewById(R.id.ratingBar);

        fun setData(templateItem: TemplateItem?) {
            mServicesNameTextView.text = templateItem?.title
            mDescriptionTextView.text = templateItem?.description
        }
    }

    internal inner class InformationVideosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mRootView: View = view
        var mVideoTitleTextView: TextView = view.findViewById(R.id.videoTitleTextView)
        var mDescriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        var mVideoThumbnailImageView: ImageView = view.findViewById(R.id.thumbnailImageView)

        fun setData(templateItem: TemplateItem?, listener: TaskTemplateListener?) {
            mVideoTitleTextView.setText(templateItem?.title)
            mDescriptionTextView.setText(templateItem?.description)

            if(templateItem?.previewImage.isNotNullNorEmpty()){
                GlideApp.with(mVideoThumbnailImageView.context)
                        .asBitmap()
                        .load(templateItem?.previewImage)
                        .into(mVideoThumbnailImageView)
            }

            mRootView.setOnClickListener(View.OnClickListener {
                if(listener!= null)
                    listener.onTemplateItemClick(templateItem)
            })
        }
    }

    internal inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}