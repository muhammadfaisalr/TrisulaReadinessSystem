package id.muhammadfaisal.trisula.bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import id.muhammadfaisal.trisula.R
import id.muhammadfaisal.trisula.activity.MainActivity
import id.muhammadfaisal.trisula.activity.MemberActivity
import id.muhammadfaisal.trisula.databinding.FragmentBottomSheetChangePasswordBinding
import id.muhammadfaisal.trisula.helper.AuthHelper
import id.muhammadfaisal.trisula.helper.DatabaseHelper
import id.muhammadfaisal.trisula.helper.GeneralHelper
import id.muhammadfaisal.trisula.model.firebase.UserModelFirebase
import id.muhammadfaisal.trisula.model.view.ErrorModel
import id.muhammadfaisal.trisula.ui.Loading
import id.muhammadfaisal.trisula.utils.BottomSheets
import id.muhammadfaisal.trisula.utils.Constant


class BottomSheetChangePasswordFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentBottomSheetChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        this.binding = FragmentBottomSheetChangePasswordBinding.inflate(this.layoutInflater)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.init()
    }

    private fun init() {
        this.binding.buttonSave.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == this.binding.buttonSave) {
            this.save()
        }
    }

    private fun save() {
        val loading = Loading(requireContext())
        loading.setCancelable(false)
        loading.show()

        val password = this.binding.inputPassword.text.toString()
        val confirmPassword = this.binding.inputConfirmPassword.text.toString()

        when {
            password.isEmpty() or confirmPassword.isEmpty() -> {
                loading.dismiss()
                BottomSheets.error(
                    ErrorModel(
                        null,
                        this.getString(R.string.something_wrong),
                        this.getString(R.string.email_or_password_can_t_be_empty)
                    ),
                    requireActivity() as AppCompatActivity,
                    true,
                    null
                )
            }
            password != confirmPassword -> {
                loading.dismiss()
                BottomSheets.error(
                    ErrorModel(
                        null,
                        this.getString(R.string.password_not_equal),
                        this.getString(R.string.password_not_equal_desc)
                    ),
                    requireActivity() as AppCompatActivity,
                    true,
                    null
                )
            }
            else -> {
                var next = true
                DatabaseHelper
                    .getUserReference()
                    .child(AuthHelper.getUid()!!)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(UserModelFirebase::class.java)!!
                            user.password = password
                            user.isActivated = true

                            AuthHelper
                                .Account
                                .updatePassword(user)
                                .addOnSuccessListener {
                                    if (next) {
                                        DatabaseHelper
                                            .getUserReference()
                                            .child(AuthHelper.getUid()!!)
                                            .setValue(user)
                                            .addOnSuccessListener {
                                                loading.dismiss()
                                                Toast.makeText(requireContext(), "Berhasil Mengganti Kata Sandi.", Toast.LENGTH_SHORT).show()
                                                if (user.roleId == Constant.Role.MEMBER){
                                                    GeneralHelper.move(requireContext(), MemberActivity::class.java, true)
                                                }else {
                                                    GeneralHelper.move(requireContext(), MainActivity::class.java, true)
                                                }
                                            }
                                            .addOnFailureListener{
                                                loading.dismiss()
                                                BottomSheets.error(
                                                    ErrorModel(null, getString(R.string.something_wrong), it.message),
                                                    requireActivity() as AppCompatActivity,
                                                    true,
                                                    null
                                                )
                                            }
                                    }

                                    next = false
                                }
                                .addOnFailureListener{
                                    loading.dismiss()
                                    BottomSheets.error(
                                        ErrorModel(null, getString(R.string.something_wrong), it.message),
                                        requireActivity() as AppCompatActivity,
                                        true,
                                        null
                                    )
                                }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            BottomSheets.error(
                                ErrorModel(error.code.toString(), error.message, error.details),
                                requireActivity() as AppCompatActivity,
                                true,
                                null
                            )
                        }

                    })
            }
        }
    }

}