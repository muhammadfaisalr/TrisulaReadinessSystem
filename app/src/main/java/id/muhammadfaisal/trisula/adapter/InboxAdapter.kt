package id.muhammadfaisal.trisula.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.muhammadfaisal.trisula.R
import id.muhammadfaisal.trisula.activity.DetailInboxActivity
import id.muhammadfaisal.trisula.databinding.ItemInboxBinding
import id.muhammadfaisal.trisula.helper.GeneralHelper
import id.muhammadfaisal.trisula.model.firebase.InboxModelFirebase
import id.muhammadfaisal.trisula.utils.Constant
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class InboxAdapter(val context : Context, val groupName : String, val inboxes : ArrayList<InboxModelFirebase>) : RecyclerView.Adapter<InboxAdapter.ViewHolder>() {
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private var binding = ItemInboxBinding.bind(this.itemView)

        @SuppressLint("SimpleDateFormat")
        fun bind(
            context: Context,
            position: Int,
            groupName: String,
            inboxModelFirebase: InboxModelFirebase
        ) {
            val formatter = SimpleDateFormat("HH:mm EEE, dd/MM/yyyy")

            this.binding.textNo.text = (position + 1).toString()
            this.binding.textTitle.text = inboxModelFirebase.title
            this.binding.textDate.text = formatter.format(Date(inboxModelFirebase.date!!))

            this.itemView.setOnClickListener{
                val bundle = Bundle()
                bundle.putString(Constant.Key.GROUP_NAME, groupName)
                bundle.putString(Constant.Key.INBOX_TITLE, inboxModelFirebase.title)
                bundle.putString(Constant.Key.INBOX_CONTENT, inboxModelFirebase.message)
                bundle.putLong(Constant.Key.INBOX_DATE, inboxModelFirebase.date!!)

                GeneralHelper.move(context, DetailInboxActivity::class.java, bundle, false)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_inbox, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val inboxModelFirebase = this.inboxes[position]
        holder.bind(this.context, position, groupName, inboxModelFirebase)
    }

    override fun getItemCount(): Int {
        return this.inboxes.size
    }
}