package com.blogspot.yourfavoritekaisar.wabaruammar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.yourfavoritekaisar.wabaruammar.R
import com.blogspot.yourfavoritekaisar.wabaruammar.listener.StatusItemClickListener
import com.blogspot.yourfavoritekaisar.wabaruammar.util.StatusListElement
import com.blogspot.yourfavoritekaisar.wabaruammar.util.populateImage
import kotlinx.android.extensions.LayoutContainer

class StatusListAdapter (private val statusList: ArrayList<StatusListElement>)
    : RecyclerView.Adapter<StatusListAdapter.StatusListViewHolder>(){

    private var clickListener: StatusItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StatusListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_status, parent, false)
        )

    override fun getItemCount() = statusList.size

    override fun onBindViewHolder(holder: StatusListViewHolder, position: Int) {
        holder.bindItem(statusList[position], clickListener)

    }

    fun onRefresh() {
        statusList.clear()
        notifyDataSetChanged()
    }

    fun addElement(element: StatusListElement) {
        statusList.add(element)
        notifyDataSetChanged()

    }

    fun setOnItemClickListener(listener: StatusItemClickListener){
        clickListener = listener
        notifyDataSetChanged()
    }

    class StatusListViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindItem(statusElement: StatusListElement, listener: StatusItemClickListener?) {
            populateImage(
                img_status_photo.context,
                statusElement.userUrl,
                img_status_photo,
                R.drawable. ic_user
            )

            txt_status_name.text = statusElement.userName
            txt_status_time.text = statusElement.statusTime
            itemView.setOnClickListener{
                listener?.onItemClicked(statusElement)
            }
        }
    }
}