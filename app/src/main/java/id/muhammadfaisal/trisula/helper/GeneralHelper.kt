package id.muhammadfaisal.trisula.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import id.muhammadfaisal.trisula.utils.Constant

class GeneralHelper {
    companion object {
        fun move(context: Context, destination: Class<*>, forget: Boolean) {
            context.startActivity(Intent(context, destination))
            if (forget) {
                (context as Activity).finish()
            }
        }

        fun move(context: Context, destination: Class<*>, bundle: Bundle, forget: Boolean) {
            context.startActivity(
                Intent(context, destination)
                    .putExtra(Constant.Key.BUNDLING, bundle)
            )
            if (forget) {
                (context as Activity).finish()
            }
        }

        fun getScreenHeight(): Int {
            return Resources.getSystem().displayMetrics.heightPixels
        }

        fun textAvatar(s: String) : String{
            val splitted = s.split(" ")

            var strings = ""
            var index = 0
            for (i in splitted){
                if (index < 2){
                    strings += i[0].toString()
                }
                index += 1
            }

            return strings
        }
    }
}