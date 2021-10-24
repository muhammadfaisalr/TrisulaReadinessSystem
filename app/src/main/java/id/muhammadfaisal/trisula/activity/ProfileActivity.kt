package id.muhammadfaisal.trisula.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import id.muhammadfaisal.trisula.databinding.ActivityProfileBinding
import id.muhammadfaisal.trisula.helper.AuthHelper
import id.muhammadfaisal.trisula.helper.DatabaseHelper
import id.muhammadfaisal.trisula.helper.GeneralHelper
import id.muhammadfaisal.trisula.model.firebase.UserModelFirebase
import id.muhammadfaisal.trisula.model.view.ErrorModel
import id.muhammadfaisal.trisula.utils.BottomSheets
import id.muhammadfaisal.trisula.utils.Constant
import id.muhammadfaisal.trisula.utils.SharedPreferences

class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityProfileBinding.inflate(this.layoutInflater)
        super.setContentView(this.binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        this.init()
    }

    private fun init(){
        DatabaseHelper
            .getUserReference()
            .child(AuthHelper.getUid()!!)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserModelFirebase::class.java)

                    this@ProfileActivity.binding.textName.text = user?.name
                    this@ProfileActivity.binding.textInitial.text = GeneralHelper.textAvatar(user?.name!!)
                    this@ProfileActivity.binding.textPhone.text = "+62 ${user?.phoneNumber}"

                    this@ProfileActivity.binding.textAccountInfo.setOnClickListener{
                        BottomSheets.accountInformation(this@ProfileActivity, user, true)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    BottomSheets.error(
                        ErrorModel(error.code.toString(), error.message, error.details),
                        this@ProfileActivity,
                        true,
                        null
                    )
                }

            })

        this.binding.textAccountInfo.setOnClickListener(this)
        this.binding.textChangePassword.setOnClickListener(this)
        this.binding.buttonLogout.setOnClickListener(this)
        this.binding.imageBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            this.binding.imageBack -> {
                finish()
            }
            this.binding.buttonLogout -> {
                this.logout()
            }
            this.binding.textChangePassword -> {
                this.changePassword()
            }
        }
    }

    private fun changePassword() {
        BottomSheets.changePassword(this, true)
    }

    private fun logout() {
        AuthHelper.Account.logout()
        SharedPreferences.delete(this, Constant.Key.ROLE_ID)
        GeneralHelper.move(this, LoginActivity::class.java, true)
    }
}