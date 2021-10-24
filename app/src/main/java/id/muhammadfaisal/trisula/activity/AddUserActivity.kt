package id.muhammadfaisal.trisula.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import id.muhammadfaisal.trisula.R
import id.muhammadfaisal.trisula.utils.BottomSheets
import id.muhammadfaisal.trisula.databinding.ActivityAddUserBinding
import id.muhammadfaisal.trisula.helper.AuthHelper
import id.muhammadfaisal.trisula.utils.Constant
import id.muhammadfaisal.trisula.helper.DatabaseHelper
import id.muhammadfaisal.trisula.helper.GeneralHelper
import id.muhammadfaisal.trisula.model.firebase.GroupModelFirebase
import id.muhammadfaisal.trisula.model.firebase.UserModelFirebase
import id.muhammadfaisal.trisula.model.view.ErrorModel
import id.muhammadfaisal.trisula.ui.Loading
import id.muhammadfaisal.trisula.utils.SharedPreferences

class AddUserActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddUserBinding

    private var roleId: Int = Constant.Role.MEMBER
    private var groupId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityAddUserBinding.inflate(this.layoutInflater)
        super.setContentView(this.binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        this.extract()
        this.init()
    }

    private fun extract() {
        val bundle = this.intent.getBundleExtra(Constant.Key.BUNDLING)!!
        this.roleId = bundle.getInt(Constant.Key.ROLE_ID)
        this.groupId = bundle.getString(Constant.Key.GROUP_ID)
    }

    private fun init() {

        if (this.roleId == Constant.Role.ADMIN) {
            this.binding.textTitle.text = this.getString(R.string.add_admin)
        } else if (this.roleId == Constant.Role.MEMBER) {
            this.binding.textTitle.text = this.getString(R.string.add_member)
        }

        if (groupId!!.isEmpty()){
            this.binding.textGroupName.visibility = View.GONE
        }
        this.binding.textGroupName.text = "Untuk ${this.groupId}"

        this.binding.exfabAdd.setOnClickListener(this)
        this.binding.imageBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == this.binding.exfabAdd) {
            this.addMember()
        }else if (v == this.binding.imageBack){
            finish()
        }
    }

    private fun addMember() {
        val loading = Loading(this)
        loading.setCancelable(false)
        loading.show()

        val isValidated = this.validate(
            this.binding.inputEmail,
            this.binding.inputName,
            this.binding.inputPassword,
            this.binding.inputConfirmPassword,
            this.binding.inputPhone
        )

        if (isValidated) {
            val email = this.binding.inputEmail.text
            val password = this.binding.inputPassword.text
            val passwordConfirmed = this.binding.inputPassword.text
            val name = this.binding.inputName.text
            val phone = this.binding.inputPhone.text

            if (password != passwordConfirmed) {
                loading.dismiss()
                BottomSheets.error(
                    ErrorModel(
                        null,
                        getString(R.string.password_not_equal),
                        getString(R.string.password_not_equal_desc)
                    ),
                    this,
                    true,
                    null
                )
                return
            }

            val user = UserModelFirebase(
                phone.toString().toLong(),
                name.toString(),
                email.toString(),
                password.toString(),
                this.groupId,
                this.roleId,
                false,
            )

            AuthHelper
                .Account
                .createAccount(user)
                .addOnSuccessListener { it ->

                    var next = true

                    if (roleId == Constant.Role.MEMBER) {
                        val accounts = ArrayList<String>()
                        DatabaseHelper
                            .getDetailGroupReference(this.groupId!!)
                            .addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val groupDetail = snapshot.getValue(GroupModelFirebase::class.java)

                                    if (groupDetail != null){
                                        if (groupDetail.users != null){
                                            for (i in groupDetail.users!!){
                                                accounts.add(i)
                                            }
                                        }
                                    }

                                    accounts.add(it.user!!.uid)

                                    if (next){
                                        DatabaseHelper
                                            .getUserInGroupReference(this@AddUserActivity.groupId!!)
                                            .setValue(accounts)
                                    }

                                    next = false
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    BottomSheets.error(
                                        ErrorModel(error.code.toString(), error.message, error.details),
                                        this@AddUserActivity,
                                        true,
                                        null
                                    )
                                }
                            })
                    }


                    val bundle = Bundle()
                    bundle.putString(Constant.Key.SUCCESS_TITLE, getString(R.string.success_add_member))

                    DatabaseHelper
                        .getUserReference()
                        .child(it.user!!.uid)
                        .setValue(user)
                        .addOnSuccessListener {
                            //Logout Akun Yang telah berhasil di buat
                            AuthHelper
                                .Account
                                .logout()

                            //Login Kembali Akun Yg Membuat Akun Tersebut
                            val currentEmail = SharedPreferences.get(this, Constant.Key.USER_EMAIL).toString()
                            val currentPassword = SharedPreferences.get(this, Constant.Key.USER_PASSWORD).toString()
                            AuthHelper
                                .Account
                                .signInWithEmailPassword(currentEmail, currentPassword)
                                .addOnSuccessListener {
                                    loading.dismiss()
                                    GeneralHelper.move(this, SuccessActivity::class.java, bundle,true)
                                }
                                .addOnFailureListener{
                                    loading.dismiss()
                                    BottomSheets.error(
                                        ErrorModel(null, this.getString(R.string.can_t_create_account), it.message),
                                        this,
                                        true,
                                        null
                                    )
                                }
                        }
                        .addOnFailureListener {
                            loading.dismiss()
                            BottomSheets.error(
                                ErrorModel(
                                    null,
                                    this.getString(R.string.can_t_create_account),
                                    it.message
                                ),
                                this,
                                true,
                                null
                            )
                        }
                }
                .addOnFailureListener {
                    loading.dismiss()
                    BottomSheets.error(
                        ErrorModel(
                            null,
                            this.getString(R.string.can_t_create_account),
                            it.message
                        ),
                        this,
                        true,
                        null
                    )
                }
        } else {
            loading.dismiss()
            BottomSheets.error(
                ErrorModel(
                    null,
                    this.getString(R.string.something_wrong),
                    this.getString(R.string.make_sure_fill_was_filled)
                ),
                this,
                true,
                null
            )
        }
    }

    private fun validate(vararg views: EditText): Boolean {
        for (view in views) {
            if (view.text.toString() == "") {
                return false
            }

            if (view.text.toString().isEmpty()) {
                return false
            }
        }

        return true
    }

}