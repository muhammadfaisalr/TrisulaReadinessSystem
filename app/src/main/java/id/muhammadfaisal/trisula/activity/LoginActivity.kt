package id.muhammadfaisal.trisula.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import id.muhammadfaisal.trisula.R
import id.muhammadfaisal.trisula.utils.BottomSheets
import id.muhammadfaisal.trisula.databinding.ActivityLoginBinding
import id.muhammadfaisal.trisula.helper.*
import id.muhammadfaisal.trisula.model.firebase.UserModelFirebase
import id.muhammadfaisal.trisula.model.view.ErrorModel
import id.muhammadfaisal.trisula.ui.Loading
import id.muhammadfaisal.trisula.utils.Constant
import id.muhammadfaisal.trisula.utils.SharedPreferences

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityLoginBinding.inflate(this.layoutInflater)
        super.setContentView(this.binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        this.init()
    }

    private fun init() {

        val roleId = SharedPreferences.get(this, Constant.Key.ROLE_ID)

        if (AuthHelper.getCurrentUser() != null){
           if (roleId != null){
               val bundle = Bundle()
               bundle.putString(Constant.Key.ROLE_ID, roleId.toString())
               when (roleId) {
                   Constant.Role.MEMBER -> {
                       GeneralHelper.move(this, MemberActivity::class.java, true)
                   }
                   else -> {
                       GeneralHelper.move(this, MainActivity::class.java, bundle, true)
                   }
               }
           }
        }

        this.binding.buttonLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == this.binding.buttonLogin) {
            this.processLogin()
        }
    }

    private fun processLogin() {
        val loading = Loading(this)
        loading.setCancelable(false)
        loading.show()

        val email = this.binding.inputEmail.text
        val password = this.binding.inputPassword.text

        if (email != null && password != null) {
            if (email.isEmpty() && password.isEmpty()) {
                loading.dismiss()
                BottomSheets.error(
                    ErrorModel(
                        null,
                        getString(R.string.something_wrong),
                        getString(R.string.email_or_password_can_t_be_empty)
                    ),
                    this,
                    true,
                    null
                )
            } else {
                AuthHelper
                    .Account
                    .signInWithEmailPassword(email.toString(), password.toString())
                    .addOnSuccessListener {

                        Log.d(
                            LoginActivity::class.java.simpleName,
                            "Uid ${it.user!!.uid} Success Login to App!."
                        )

                        DatabaseHelper
                            .getAccountReference(it.user!!.uid)
                            .addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val user = snapshot.getValue(UserModelFirebase::class.java)!!

                                    loading.dismiss()

                                    SharedPreferences.save(this@LoginActivity, Constant.Key.ROLE_ID, user.roleId!!)
                                    SharedPreferences.save(this@LoginActivity, Constant.Key.USER_NAME, user.name!!)
                                    SharedPreferences.save(this@LoginActivity, Constant.Key.USER_EMAIL, user.email!!)
                                    SharedPreferences.save(this@LoginActivity, Constant.Key.USER_PASSWORD, user.password!!)

                                    when (user.roleId) {
                                        Constant.Role.SUPER_ADMIN -> {
                                            GeneralHelper.move(this@LoginActivity, MainActivity::class.java, true)
                                        }
                                        Constant.Role.ADMIN -> {
                                            SharedPreferences.save(this@LoginActivity, Constant.Key.GROUP_ID, user.groupId!!)
                                            GeneralHelper.move(this@LoginActivity, MainActivity::class.java, true)
                                        }
                                        else -> {
                                            SharedPreferences.save(this@LoginActivity, Constant.Key.GROUP_ID, user.groupId!!)
                                            GeneralHelper.move(this@LoginActivity, MemberActivity::class.java, true)
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    BottomSheets
                                        .error(
                                            ErrorModel(error.code.toString(), error.message, error.details),
                                            this@LoginActivity,
                                            true,
                                            null
                                        )
                                }
                            })


                    }
                    .addOnFailureListener {
                        loading.dismiss()

                        Log.d(
                            LoginActivity::class.java.simpleName,
                            "Failed to Login Because ${it.message}"
                        )

                        BottomSheets.error(
                            ErrorModel("0", this.getString(R.string.something_wrong), it.message),
                            this,
                            true,
                            null
                        )
                    }
            }
        }
    }
}