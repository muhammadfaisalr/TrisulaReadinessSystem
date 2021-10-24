package id.muhammadfaisal.trisula.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import id.muhammadfaisal.trisula.databinding.ActivitySuccessBinding
import id.muhammadfaisal.trisula.helper.GeneralHelper
import id.muhammadfaisal.trisula.utils.Constant

class SuccessActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivitySuccessBinding.inflate(this.layoutInflater)
        super.setContentView(this.binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        this.extract()

        Handler(Looper.myLooper()!!).postDelayed({
            GeneralHelper.move(this, MainActivity::class.java, true )
        },3000L)
    }

    private fun extract() {
        val bundle = this.intent.getBundleExtra(Constant.Key.BUNDLING)
        this.binding.textTitle.text = bundle?.getString(Constant.Key.SUCCESS_TITLE)
    }
}