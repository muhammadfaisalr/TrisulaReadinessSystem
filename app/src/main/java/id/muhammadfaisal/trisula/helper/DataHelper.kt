package id.muhammadfaisal.trisula.helper

import id.muhammadfaisal.trisula.R
import id.muhammadfaisal.trisula.utils.Constant

class DataHelper {
    companion object{
        fun getGroupImage(groupName : String) : Int{
            return when (groupName) {
                Constant.GroupName.BAJRA_YUDHA_501 -> {
                    R.drawable.bajra_yudha_501
                }
                Constant.GroupName.UJWALA_YUDHA_502 -> {
                    R.drawable.ujwala_yudha_502
                }
                Constant.GroupName.MAYANGKARA_503 -> {
                    R.drawable.mayangkara_503
                }
                Constant.GroupName.BRIGIF_18 -> {
                    R.drawable.brigif_18
                }
                else -> {
                    R.drawable.depandu_taikam
                }
            }
        }

        fun setGroupId(groupName : String) : String {
            return groupName.replace(" ", "")
        }
    }
}