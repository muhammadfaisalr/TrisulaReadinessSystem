package id.muhammadfaisal.trisula.bottom_sheet

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.muhammadfaisal.trisula.R
import id.muhammadfaisal.trisula.databinding.FragmentErrorBottomSheetBinding
import id.muhammadfaisal.trisula.model.view.ErrorModel

class ErrorBottomSheetFragment(var errorModel: ErrorModel, var listener: View.OnClickListener?) :
    BottomSheetDialogFragment(),
    View.OnClickListener {

    private lateinit var binding: FragmentErrorBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.binding = FragmentErrorBottomSheetBinding.inflate(this.layoutInflater)
        return this.binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.init()
    }

    private fun init() {
        if (this.listener == null) {
            this.listener = this
        }

        if (errorModel.code != null){
            this.binding.textTitle.text = "${errorModel.title} (${errorModel.code})"
        }else{
            this.binding.textTitle.text = "${errorModel.title}"
        }
        this.binding.textDescription.text = errorModel.description

        this.binding.buttonClose.setOnClickListener(this.listener)
    }

    override fun onClick(v: View?) {
        if (v == this.binding.buttonClose) {
            this.dismiss()
        }
    }
}