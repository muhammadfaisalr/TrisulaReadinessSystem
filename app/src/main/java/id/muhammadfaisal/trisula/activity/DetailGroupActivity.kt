package id.muhammadfaisal.trisula.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import id.muhammadfaisal.trisula.adapter.UserAdapter
import id.muhammadfaisal.trisula.utils.BottomSheets
import id.muhammadfaisal.trisula.databinding.ActivityDetailGroupBinding
import id.muhammadfaisal.trisula.helper.DataHelper
import id.muhammadfaisal.trisula.utils.Constant
import id.muhammadfaisal.trisula.helper.DatabaseHelper
import id.muhammadfaisal.trisula.helper.GeneralHelper
import id.muhammadfaisal.trisula.model.firebase.UserModelFirebase
import id.muhammadfaisal.trisula.model.view.ErrorModel
import id.muhammadfaisal.trisula.utils.SharedPreferences

class DetailGroupActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDetailGroupBinding

    private lateinit var groupName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityDetailGroupBinding.inflate(this.layoutInflater)
        super.setContentView(this.binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        this.extract()
        this.init()
        this.setupBehavior()
    }

    private fun init(){
        this.binding.imageLogo.setImageResource(DataHelper.getGroupImage(this.groupName))

        this.binding.imageMessage.setOnClickListener(this)
        this.binding.buttonSend.setOnClickListener(this)
        this.binding.imageBack.setOnClickListener(this)
    }

    private fun extract() {
        val bundle = this.intent.getBundleExtra(Constant.Key.BUNDLING)
        this.groupName = bundle?.getString(Constant.Key.GROUP_NAME)!!

        val roleId = SharedPreferences.get(this, Constant.Key.ROLE_ID)

        if (roleId != Constant.Role.SUPER_ADMIN){
            this.binding.buttonAdd.visibility = View.GONE
        }

        this.binding.textTitle.text = groupName
        this.binding.buttonAdd.setOnClickListener(this)
    }

    private fun setupBehavior() {
        val bindingBehavior = this.binding.behavior
        val bottomSheetBehavior = BottomSheetBehavior.from(bindingBehavior.behavior)
        bottomSheetBehavior.peekHeight = GeneralHelper.getScreenHeight() / 3 + 400
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.isDraggable = true

        val strings = ArrayList<String>()
        DatabaseHelper.getUserInGroupReference(this.binding.textTitle.text.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val user = data.getValue(String::class.java)

                    if (user != null) {
                        strings.add(user)

                        val users = ArrayList<UserModelFirebase>()
                        for (user2 in strings) {
                            DatabaseHelper.getUserReference()
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (data2 in snapshot.children) {
                                            Log.d(
                                                DetailGroupActivity::class.java.simpleName,
                                                "Start Validating User ID ${data2.key}"
                                            )
                                            if (data2.key == user2) {
                                                users.add(data2.getValue(UserModelFirebase::class.java)!!)

                                                bindingBehavior.recyclerUser.layoutManager =
                                                    LinearLayoutManager(
                                                        this@DetailGroupActivity,
                                                        RecyclerView.VERTICAL,
                                                        false
                                                    )

                                                bindingBehavior.textTotalUser.text = "${users.size} Anggota"

                                                bindingBehavior.recyclerUser.adapter =
                                                    UserAdapter(this@DetailGroupActivity,
                                                        users, groupName)

                                                bindingBehavior.recyclerUser.addItemDecoration(
                                                    DividerItemDecoration(
                                                        this@DetailGroupActivity,
                                                        RecyclerView.VERTICAL
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        BottomSheets.error(
                                            ErrorModel(error.code.toString(), error.message, error.details),
                                            this@DetailGroupActivity,
                                            true,
                                            null
                                        )
                                    }

                                })
                        }

                    }else{
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                BottomSheets.error(
                    ErrorModel(error.code.toString(), error.message, error.details),
                    this@DetailGroupActivity,
                    true,
                    null
                )
            }
        })



        bindingBehavior.buttonAdd.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constant.Key.ROLE_ID, Constant.Role.MEMBER)
            bundle.putString(Constant.Key.GROUP_ID, this.binding.textTitle.text.toString())
            GeneralHelper.move(this, AddUserActivity::class.java, bundle, false)
        }
    }

    override fun onClick(v: View?) {

        val bundle = Bundle()

        when (v) {
            this.binding.imageMessage -> {
                bundle.putString(Constant.Key.GROUP_NAME, this.binding.textTitle.text.toString())
                GeneralHelper.move(this, InboxActivity::class.java, bundle, false)
            }
            this.binding.buttonSend -> {
                bundle.putString(Constant.Key.GROUP_ID, DataHelper.setGroupId(this.binding.textTitle.text.toString()))
                GeneralHelper.move(this, SendNotificationActivity::class.java, bundle, false)
            }
            this.binding.imageBack -> {
                finish()
            }
            this.binding.buttonAdd -> {
                bundle.putString(Constant.Key.GROUP_ID, this.binding.textTitle.text.toString())
                bundle.putInt(Constant.Key.ROLE_ID, Constant.Role.ADMIN)
                GeneralHelper.move(this, AddUserActivity::class.java, bundle, false)
            }
        }
    }
}