package id.muhammadfaisal.trisula.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import id.muhammadfaisal.trisula.adapter.GroupAdapter
import id.muhammadfaisal.trisula.utils.BottomSheets
import id.muhammadfaisal.trisula.databinding.ActivityMainBinding
import id.muhammadfaisal.trisula.helper.*
import id.muhammadfaisal.trisula.model.firebase.GroupModelFirebase
import id.muhammadfaisal.trisula.model.view.ErrorModel
import id.muhammadfaisal.trisula.utils.Constant
import id.muhammadfaisal.trisula.utils.SharedPreferences
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    private var groups: ArrayList<GroupModelFirebase> = ArrayList()

    private var roleId = 0
    private var groupId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(this.layoutInflater)
        super.setContentView(this.binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        this.extract()
        this.init()
        this.setup()

    }

    private fun extract() {
        this.roleId = SharedPreferences.get(this, Constant.Key.ROLE_ID).toString().toInt()

        if (this.roleId == Constant.Role.ADMIN){
            this.binding.cardAddAdmin.visibility = View.GONE
            this.groupId = SharedPreferences.get(this, Constant.Key.GROUP_ID).toString()
        }
    }

    private fun init(){
        this.binding.textAvatar.text = GeneralHelper.textAvatar(SharedPreferences.get(this, Constant.Key.USER_NAME).toString())

        this.binding.cardProfile.setOnClickListener(this)
        this.binding.buttonAddAdmin.setOnClickListener(this)
    }

    private fun setup() {
        DatabaseHelper.getGroupReference().addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children){
                    val group = data.getValue(GroupModelFirebase::class.java)

                    if (group != null) {
                        val groupIdGenerated = DataHelper.setGroupId(group.name!!)
                        if (this@MainActivity.roleId == Constant.Role.SUPER_ADMIN){
                            MessagingHelper.subscribe(groupIdGenerated)
                            this@MainActivity.groups.add(group)
                        }else{
                            //Search by Group ID
                            if (DataHelper.setGroupId(groupId) == groupIdGenerated){
                                this@MainActivity.groups.add(group)
                            }
                        }
                    }
                }

                this@MainActivity.binding.recyclerData.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
                this@MainActivity.binding.recyclerData.adapter = GroupAdapter(this@MainActivity, this@MainActivity.groups)
            }

            override fun onCancelled(error: DatabaseError) {
                BottomSheets.error(
                    ErrorModel(error.code.toString(), error.message, error.details),
                    this@MainActivity,
                    true,
                    null
                )
            }

        })

        this.binding.recyclerData.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    override fun onClick(v: View?) {
        if (v == this.binding.cardProfile){
            GeneralHelper.move(this, ProfileActivity::class.java, false)
        }else if (v == this.binding.buttonAddAdmin){
            val bundle = Bundle()
            bundle.putString(Constant.Key.GROUP_ID, "")
            bundle.putInt(Constant.Key.ROLE_ID, Constant.Role.SUPER_ADMIN)
            GeneralHelper.move(this, AddUserActivity::class.java, bundle, false)
        }
    }
}