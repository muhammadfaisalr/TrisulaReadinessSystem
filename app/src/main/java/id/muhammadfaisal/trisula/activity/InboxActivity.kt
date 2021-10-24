package id.muhammadfaisal.trisula.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import id.muhammadfaisal.trisula.adapter.InboxAdapter
import id.muhammadfaisal.trisula.databinding.ActivityInboxBinding
import id.muhammadfaisal.trisula.helper.DatabaseHelper
import id.muhammadfaisal.trisula.helper.GeneralHelper
import id.muhammadfaisal.trisula.model.firebase.InboxModelFirebase
import id.muhammadfaisal.trisula.model.view.ErrorModel
import id.muhammadfaisal.trisula.utils.BottomSheets
import id.muhammadfaisal.trisula.utils.Constant

class InboxActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityInboxBinding

    private var groupName : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityInboxBinding.inflate(this.layoutInflater)
        super.setContentView(this.binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        this.init()
        this.setup()
    }

    private fun setup() {
        val inboxes = ArrayList<InboxModelFirebase>()
        DatabaseHelper
            .getInboxInGroupReference(this.groupName!!)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapshot in snapshot.children){
                        inboxes.add(dataSnapshot.getValue(InboxModelFirebase::class.java)!!)
                    }

                    this@InboxActivity.binding.recyclerData.apply {
                        layoutManager = LinearLayoutManager(this@InboxActivity, RecyclerView.VERTICAL, false)
                        adapter = InboxAdapter(this@InboxActivity, this@InboxActivity.groupName!!, inboxes)
                        addItemDecoration(DividerItemDecoration(this@InboxActivity, RecyclerView.VERTICAL))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    BottomSheets.error(
                        ErrorModel(error.code.toString(), error.message, error.details),
                        this@InboxActivity,
                        true,
                        null
                    )
                }
            })
    }

    private fun init() {
        val bundle = this.intent.getBundleExtra(Constant.Key.BUNDLING)
        this.groupName = bundle?.getString(Constant.Key.GROUP_NAME)

        this.binding.textGroupName.text = this.groupName

        this.binding.imageBack.setOnClickListener(this)
        this.binding.exfabAdd.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == this.binding.imageBack){
            finish()
        }else if(v == this.binding.exfabAdd){
            val bundle = Bundle()
            bundle.putString(Constant.Key.GROUP_NAME, this.binding.textGroupName.text.toString())
            GeneralHelper.move(this, WriteInboxActivity::class.java, bundle, false)
        }
    }
}