package id.muhammadfaisal.trisula.bottom_sheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.muhammadfaisal.trisula.R
import id.muhammadfaisal.trisula.databinding.FragmentAccountInformationBottomSheetDialogBinding
import id.muhammadfaisal.trisula.model.firebase.UserModelFirebase
import id.muhammadfaisal.trisula.utils.Constant

class AccountInformationBottomSheetDialogFragment(val user : UserModelFirebase) : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentAccountInformationBottomSheetDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.binding = FragmentAccountInformationBottomSheetDialogBinding.inflate(this.layoutInflater)
        return this.binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.binding.textName.text = this.user.name!!
        this.binding.textPhone.text = "+62 ${this.user.phoneNumber}"

        when (this.user.roleId) {
            Constant.Role.ADMIN -> {
                this.binding.textRole.text = Constant.RoleName.ADMIN
            }
            Constant.Role.MEMBER -> {
                this.binding.textRole.text = Constant.RoleName.MEMBER
            }
            else -> {
                this.binding.textRole.text = Constant.RoleName.SUPER_ADMIN
            }
        }
    }
}