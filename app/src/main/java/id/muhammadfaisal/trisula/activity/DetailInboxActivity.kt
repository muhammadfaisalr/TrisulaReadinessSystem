package id.muhammadfaisal.trisula.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import id.muhammadfaisal.trisula.databinding.ActivityDetailInboxBinding
import id.muhammadfaisal.trisula.utils.Constant
import java.text.SimpleDateFormat
import java.util.*

class   DetailInboxActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityDetailInboxBinding

    private var groupName : String? = null
    private var title : String? = null
    private var content : String? = null
    private var date : Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityDetailInboxBinding.inflate(this.layoutInflater)
        super.setContentView(this.binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        this.extract()
        this.init()
    }

    private fun extract() {
        val bundle = this.intent.getBundleExtra(Constant.Key.BUNDLING)!!
        this.groupName = bundle.getString(Constant.Key.GROUP_NAME)
        this.title = bundle.getString(Constant.Key.INBOX_TITLE)
        this.content = bundle.getString(Constant.Key.INBOX_CONTENT)
        this.date = bundle.getLong(Constant.Key.INBOX_DATE)
    }

    private fun init() {
        val formatter = SimpleDateFormat("HH:mm EEE, dd/MM/yy   yy")

        this.binding.textGroupName.text = "Untuk ${this.groupName}"
        this.binding.textTitle.text = this.title
        this.binding.textContent.text = this.content
        this.binding.textDate.text = formatter.format(this.date)

        this.binding.imageBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == this.binding.imageBack){
            finish()
        }
    }
}