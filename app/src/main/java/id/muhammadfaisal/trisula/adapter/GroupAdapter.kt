package id.muhammadfaisal.trisula.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.muhammadfaisal.trisula.R
import id.muhammadfaisal.trisula.activity.DetailGroupActivity
import id.muhammadfaisal.trisula.activity.DetailInboxActivity
import id.muhammadfaisal.trisula.databinding.ItemGroupBinding
import id.muhammadfaisal.trisula.helper.DataHelper
import id.muhammadfaisal.trisula.helper.GeneralHelper
import id.muhammadfaisal.trisula.helper.RoomHelper
import id.muhammadfaisal.trisula.model.firebase.GroupModelFirebase
import id.muhammadfaisal.trisula.utils.Constant

class GroupAdapter(var context: Context, var groups: ArrayList<GroupModelFirebase>) :
    RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var binding = ItemGroupBinding.bind(this.itemView)
        private var userDao = RoomHelper.userDao(context = this.binding.root.context)

        @SuppressLint("SetTextI18n")
        fun bind(context: Context, groupModelFirebase: GroupModelFirebase) {

            this.binding.textTitle.text = groupModelFirebase.name
            this.binding.textUsers.text = "${userDao.getTotalUserInGroup(groupModelFirebase.name!!)} Anggota"
            this.binding.imageLogo.setImageResource(DataHelper.getGroupImage(groupModelFirebase.name!!))

            this.itemView.setOnClickListener{
                val bundle = Bundle()
                bundle.putString(Constant.Key.GROUP_NAME, groupModelFirebase.name)
                GeneralHelper.move(context, DetailGroupActivity::class.java, bundle, false)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this.context, this.groups[position])
    }

    override fun getItemCount(): Int {
        return this.groups.size
    }
}