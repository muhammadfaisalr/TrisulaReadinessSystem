package id.muhammadfaisal.trisula.utils

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.muhammadfaisal.trisula.bottom_sheet.AccountInformationBottomSheetDialogFragment
import id.muhammadfaisal.trisula.bottom_sheet.BottomSheetChangePasswordFragment
import id.muhammadfaisal.trisula.bottom_sheet.ErrorBottomSheetFragment
import id.muhammadfaisal.trisula.model.firebase.UserModelFirebase
import id.muhammadfaisal.trisula.model.view.ErrorModel

class BottomSheets() {
    companion object{
        private val TAG = BottomSheets::class.java.simpleName

        private fun open(activity: AppCompatActivity, bottomSheet : BottomSheetDialogFragment, isCancelable : Boolean){
            bottomSheet.isCancelable = isCancelable
            bottomSheet.show(activity.supportFragmentManager, TAG)
        }

        fun error(errorModel : ErrorModel, activity: AppCompatActivity, isCancelable: Boolean, listener : View.OnClickListener?){
            open(activity, ErrorBottomSheetFragment(errorModel, listener), isCancelable)
        }

        fun changePassword(activity: AppCompatActivity, isCancelable: Boolean){
            open(activity, BottomSheetChangePasswordFragment(), isCancelable)
        }

        fun accountInformation(activity: AppCompatActivity, userModelFirebase: UserModelFirebase, isCancelable: Boolean){
            open(activity, AccountInformationBottomSheetDialogFragment(userModelFirebase), isCancelable)
        }
    }
}