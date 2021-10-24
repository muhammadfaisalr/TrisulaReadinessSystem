package id.muhammadfaisal.trisula.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import id.muhammadfaisal.trisula.R
import id.muhammadfaisal.trisula.activity.DetailGroupActivity
import id.muhammadfaisal.trisula.activity.SuccessActivity
import id.muhammadfaisal.trisula.databinding.ItemUserBinding
import id.muhammadfaisal.trisula.helper.DatabaseHelper
import id.muhammadfaisal.trisula.helper.GeneralHelper
import id.muhammadfaisal.trisula.model.firebase.UserModelFirebase
import id.muhammadfaisal.trisula.model.view.ErrorModel
import id.muhammadfaisal.trisula.ui.Loading
import id.muhammadfaisal.trisula.utils.BottomSheets
import id.muhammadfaisal.trisula.utils.Constant

class UserAdapter(
    var context: Context,
    var users: ArrayList<UserModelFirebase>,
    var groupName: String
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var binding = ItemUserBinding.bind(itemView)

        fun bind(
            context: Context,
            position: Int,
            userModelFirebase: UserModelFirebase,
            groupName: String
        ) {
            binding.textPosition.text = (position + 1).toString()
            binding.textName.text = userModelFirebase.name
            binding.textPhone.text = "+62 ${userModelFirebase.phoneNumber.toString()}"

            binding.imageDelete.setOnClickListener {
                val loading = Loading(context)
                loading.setCancelable(false)
                loading.show()

                val bundle = Bundle()
                bundle.putString(
                    Constant.Key.SUCCESS_TITLE,
                    context.getString(R.string.member_deleted)
                )

                var userId: String? = null
                DatabaseHelper
                    .getUserReference()
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (userSnapshot in snapshot.children) {
                                val data = userSnapshot.getValue(UserModelFirebase::class.java)!!

                                if (userModelFirebase.email == data.email) {
                                    //dapetin key nya
                                    userId = userSnapshot.key
                                }
                            }

                            val userInGroupReference =
                                DatabaseHelper.getUserInGroupReference(groupName)

                            userInGroupReference
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {

                                        if (snapshot.childrenCount > 1) {
                                            for (userSnapshot in snapshot.children) {
                                                val data =
                                                    userSnapshot.getValue(String::class.java)!!

                                                if (data == userId) {
                                                    //Key nya sesuai dengan data
                                                    userInGroupReference.child(userSnapshot.key!!)
                                                        .removeValue().addOnSuccessListener {
                                                        loading.dismiss()
                                                        GeneralHelper.move(
                                                            context,
                                                            SuccessActivity::class.java,
                                                            bundle,
                                                            true
                                                        )
                                                    }
                                                }
                                            }
                                        }


                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        loading.dismiss()
                                        BottomSheets.error(
                                            ErrorModel(
                                                error.code.toString(),
                                                error.message,
                                                error.details
                                            ),
                                            context as AppCompatActivity,
                                            true,
                                            null
                                        )
                                    }

                                })
                        }

                        override fun onCancelled(error: DatabaseError) {
                            loading.dismiss()
                            BottomSheets.error(
                                ErrorModel(error.code.toString(), error.message, error.details),
                                context as AppCompatActivity,
                                true,
                                null
                            )
                        }

                    })
/*
                DatabaseHelper
                    .getUserInGroupReference(groupName)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (inGroupSnapshot in snapshot.children) {
                                val user = inGroupSnapshot.getValue(String::class.java)
                                DatabaseHelper
                                    .getUserReference()
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for (userSnapshot in snapshot.children){
                                                if (user == userSnapshot.key){
                                                    Log.d(
                                                        UserAdapter::class.simpleName,
                                                        "Delete User With Key ${inGroupSnapshot.key}"
                                                    )
                                                    DatabaseHelper
                                                        .getUserInGroupReference(groupName)
                                                        .child(inGroupSnapshot.key!!)
                                                        .removeValue()
                                                        .addOnSuccessListener {
                                                            loading.dismiss()
                                                            GeneralHelper.move(
                                                                context,
                                                                SuccessActivity::class.java,
                                                                bundle,
                                                                true
                                                            )
                                                        }
                                                }
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            error.toException().printStackTrace()
                                        }

                                    })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            loading.dismiss()
                            BottomSheets.error(
                                ErrorModel(error.code.toString(), error.message, error.details),
                                context as AppCompatActivity,
                                true,
                                null
                            )
                        }
                    })*/
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(this.context, position, this.users[position], groupName)
    }

    override fun getItemCount(): Int {
        return this.users.size
    }
}