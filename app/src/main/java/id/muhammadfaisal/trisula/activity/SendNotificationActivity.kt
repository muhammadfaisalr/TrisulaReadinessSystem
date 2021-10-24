package id.muhammadfaisal.trisula.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import id.muhammadfaisal.trisula.R
import id.muhammadfaisal.trisula.databinding.ActivitySendNotificationBinding
import id.muhammadfaisal.trisula.helper.GeneralHelper
import id.muhammadfaisal.trisula.messaging.MessagingService
import id.muhammadfaisal.trisula.ui.Loading
import id.muhammadfaisal.trisula.utils.Constant

class SendNotificationActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySendNotificationBinding

    private lateinit var groupId : String
    private lateinit var titleText : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivitySendNotificationBinding.inflate(this.layoutInflater)
        super.setContentView(this.binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        this.extract()
        this.init()
    }

    private fun extract() {
        val bundle = this.intent.getBundleExtra(Constant.Key.BUNDLING)!!
        this.groupId = bundle.getString(Constant.Key.GROUP_ID)!!
    }

    private fun init() {

        this.binding.exfabSend.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == this.binding.exfabSend) {
            this.send()
        }
    }

    private fun send() {
        val loading = Loading(this)
        loading.setCancelable(false)
        loading.show()

        val title : String?

        when (this.binding.groupRadio.checkedRadioButtonId) {
            R.id.radioApelKesiapsiagaan -> {
                title = this.binding.radioApelKesiapsiagaan.text.toString()
            }
            R.id.radioApelLuarBiasa -> {
                title = this.binding.radioApelLuarBiasa.text.toString()
            }
            R.id.radioBantuanKepolisian -> {
                title = this.binding.radioBantuanKepolisian.text.toString()
            }
            R.id.radioAlarmStelling -> {
                title = this.binding.radioAlarmStelling.text.toString()
            }
            R.id.radioBahayaKebakaran -> {
                title = this.binding.radioBahayaKebakaran.text.toString()
            }
            R.id.radioBencanaAlam -> {
                title = this.binding.radioBencanaAlam.text.toString()
            }
            else -> {
                title = this.binding.radioTandaAman.text.toString()
            }
        }

        Log.d(SendNotificationActivity::class.java.simpleName, this.groupId)

        MessagingService.sendMessage(title, "Segera Laksanakan!.", this.groupId)

        val bundle = Bundle()
        bundle.putString(Constant.Key.SUCCESS_TITLE, getString(R.string.notification_sended))

        Handler(Looper.myLooper()!!).postDelayed({
            loading.dismiss()
            GeneralHelper.move(this, SuccessActivity::class.java, bundle, true)
        }, 3000L)
    }
}