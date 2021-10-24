package id.muhammadfaisal.trisula.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import id.muhammadfaisal.trisula.R
import id.muhammadfaisal.trisula.databinding.ActivityWriteInboxBinding
import id.muhammadfaisal.trisula.helper.DatabaseHelper
import id.muhammadfaisal.trisula.helper.GeneralHelper
import id.muhammadfaisal.trisula.messaging.MessagingService
import id.muhammadfaisal.trisula.model.firebase.GroupModelFirebase
import id.muhammadfaisal.trisula.model.firebase.InboxModelFirebase
import id.muhammadfaisal.trisula.model.view.ErrorModel
import id.muhammadfaisal.trisula.ui.Loading
import id.muhammadfaisal.trisula.utils.BottomSheets
import id.muhammadfaisal.trisula.utils.Constant

class WriteInboxActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityWriteInboxBinding

    private var groupName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityWriteInboxBinding.inflate(this.layoutInflater)
        super.setContentView(this.binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        this.extract()
        this.init()
    }

    private fun extract() {
        val bundle = this.intent.getBundleExtra(Constant.Key.BUNDLING)
        this.groupName = bundle?.getString(Constant.Key.GROUP_NAME)
    }

    private fun init() {
        this.binding.textGroupName.text = "Untuk ${this.groupName}"
        this.binding.exfabSend.setOnClickListener(this)
        this.binding.imageBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == this.binding.exfabSend) {
            this.send()
        }else if (v == this.binding.imageBack){
            finish()
        }
    }

    private fun send() {
        val loading = Loading(this)
        loading.setCancelable(false)
        loading.show()
        val message = InboxModelFirebase(
            this.binding.inputTitle.text.toString(),
            this.binding.inputMessage.text.toString(),
            System.currentTimeMillis()
        )

        val messages = ArrayList<InboxModelFirebase>()

        var next = true

        DatabaseHelper
            .getDetailGroupReference(this.groupName!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val groupDetail = snapshot.getValue(GroupModelFirebase::class.java)

                    for (i in groupDetail?.messages!!) {
                        messages.add(i)
                    }

                    messages.add(message)

                    val bundle = Bundle()
                    bundle.putString(Constant.Key.SUCCESS_TITLE, getString(R.string.notification_sended))

                    if (next) {
                        DatabaseHelper
                            .getInboxInGroupReference(this@WriteInboxActivity.groupName!!)
                            .setValue(messages)
                            .addOnSuccessListener {
                                loading.dismiss()
                                GeneralHelper.move(this@WriteInboxActivity, SuccessActivity::class.java, bundle, true)

                                MessagingService.sendMessage(
                                    message.title!!,
                                    message.message!!,
                                    this@WriteInboxActivity.groupName!!.replace(" ", "")
                                )
                            }
                    }

                    next = false
                }

                override fun onCancelled(error: DatabaseError) {
                    loading.dismiss()
                    BottomSheets.error(
                        ErrorModel(error.code.toString(), error.message, error.details),
                        this@WriteInboxActivity,
                        true,
                        null
                    )
                }
            })

    }
}