package id.muhammadfaisal.trisula.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import id.muhammadfaisal.trisula.adapter.InboxAdapter
import id.muhammadfaisal.trisula.databinding.ActivityMemberBinding
import id.muhammadfaisal.trisula.helper.*
import id.muhammadfaisal.trisula.model.firebase.InboxModelFirebase
import id.muhammadfaisal.trisula.model.firebase.UserModelFirebase
import id.muhammadfaisal.trisula.model.view.ErrorModel
import id.muhammadfaisal.trisula.utils.BottomSheets

class MemberActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMemberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMemberBinding.inflate(this.layoutInflater)
        super.setContentView(this.binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        this.init()
    }

    private fun init() {
        DatabaseHelper
            .getUserReference()
            .child(AuthHelper.getUid()!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserModelFirebase::class.java)!!

                    val topic = user.groupId?.replace(" ", "")!!
                    MessagingHelper
                        .subscribe(topic)
                        .addOnSuccessListener {
                            Log.d(
                                MemberActivity::class.java.simpleName,
                                "User ${user.name} Success Subscibing Topic: $topic"
                            )
                        }
                        .addOnFailureListener {
                            Log.d(
                                MemberActivity::class.java.simpleName,
                                "User ${user.name} Fail Subscibing Topic: $topic (${it.message})"
                            )
                        }

                    if (!user.isActivated) {
                        BottomSheets.changePassword(this@MemberActivity, false)

                    }

                    val inboxes = ArrayList<InboxModelFirebase>()

                    DatabaseHelper
                        .getInboxInGroupReference(user.groupId!!)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (dataSnapshot in snapshot.children) {
                                    val inbox =
                                        dataSnapshot.getValue(InboxModelFirebase::class.java)

                                    inboxes.add(inbox!!)
                                }

                                this@MemberActivity.binding.recyclerView.apply {
                                    layoutManager = LinearLayoutManager(
                                        this@MemberActivity,
                                        RecyclerView.VERTICAL,
                                        false
                                    )
                                    adapter =
                                        InboxAdapter(this@MemberActivity, user.groupId!!, inboxes)
                                    addItemDecoration(
                                        DividerItemDecoration(
                                            this@MemberActivity,
                                            RecyclerView.VERTICAL
                                        )
                                    )
                                }

                                this@MemberActivity.binding.textInboxQuantity.text =
                                    "${inboxes.size} Pesan"
                            }

                            override fun onCancelled(error: DatabaseError) {
                                BottomSheets.error(
                                    ErrorModel(error.code.toString(), error.message, error.details),
                                    this@MemberActivity,
                                    true,
                                    null
                                )
                            }

                        })

                    this@MemberActivity.binding.textTitle.text = user.groupId
                    this@MemberActivity.binding.textName.text = user.name
                    this@MemberActivity.binding.textInitial.text = GeneralHelper.textAvatar(user.name!!)
                    this@MemberActivity.binding.imageLogo.setImageResource(
                        DataHelper.getGroupImage(
                            user.groupId!!
                        )
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    BottomSheets.error(
                        ErrorModel(error.code.toString(), error.message, error.details),
                        this@MemberActivity,
                        true,
                        null
                    )
                }

            })

        this.binding.buttonLookAccount.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == this.binding.buttonLookAccount) {
            GeneralHelper.move(this, ProfileActivity::class.java, false)
        }
    }
}