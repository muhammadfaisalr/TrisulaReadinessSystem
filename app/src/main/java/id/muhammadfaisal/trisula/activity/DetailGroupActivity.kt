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
import id.muhammadfaisal.trisula.database.entity.UserEntity
import id.muhammadfaisal.trisula.utils.BottomSheets
import id.muhammadfaisal.trisula.databinding.ActivityDetailGroupBinding
import id.muhammadfaisal.trisula.helper.DataHelper
import id.muhammadfaisal.trisula.utils.Constant
import id.muhammadfaisal.trisula.helper.DatabaseHelper
import id.muhammadfaisal.trisula.helper.GeneralHelper
import id.muhammadfaisal.trisula.helper.RoomHelper
import id.muhammadfaisal.trisula.model.firebase.UserModelFirebase
import id.muhammadfaisal.trisula.model.view.ErrorModel
import id.muhammadfaisal.trisula.ui.Loading
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

    private fun init() {
        this.binding.imageLogo.setImageResource(DataHelper.getGroupImage(this.groupName))

        this.binding.imageMessage.setOnClickListener(this)
        this.binding.buttonSend.setOnClickListener(this)
        this.binding.imageBack.setOnClickListener(this)
    }

    private fun extract() {
        val bundle = this.intent.getBundleExtra(Constant.Key.BUNDLING)
        this.groupName = bundle?.getString(Constant.Key.GROUP_NAME)!!

        val roleId = SharedPreferences.get(this, Constant.Key.ROLE_ID)

        if (roleId != Constant.Role.SUPER_ADMIN) {
            this.binding.buttonAdd.visibility = View.GONE
        }

        this.binding.textTitle.text = groupName
        this.binding.buttonAdd.setOnClickListener(this)
    }

    private fun setupBehavior() {
        val userDao = RoomHelper.userDao(this)
        val users: ArrayList<UserEntity> = ArrayList()
        val usersInGroup = userDao.getUserInGroup(groupName)

        for (i in usersInGroup){
            users.add(i)
        }

        val bindingBehavior = this.binding.behavior
        val bottomSheetBehavior = BottomSheetBehavior.from(bindingBehavior.behavior)
        bottomSheetBehavior.peekHeight = GeneralHelper.getScreenHeight() / 4
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.isDraggable = true

        bindingBehavior.textTotalUser.text = "${users.size} Anggota"

        bindingBehavior.recyclerUser.adapter = UserAdapter(this, users, groupName)
        bindingBehavior.recyclerUser.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        bindingBehavior.recyclerUser.addItemDecoration(
            DividerItemDecoration(
                this,
                RecyclerView.VERTICAL
            )
        )

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
                bundle.putString(
                    Constant.Key.GROUP_ID,
                    DataHelper.setGroupId(this.binding.textTitle.text.toString())
                )
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