package uz.jbnuu.tsc.student.base

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import uz.jbnuu.tsc.student.R
import uz.jbnuu.tsc.student.databinding.DialogWaitBinding

class ProgressDialog : AlertDialog {

    var binding:DialogWaitBinding



    constructor(context: Context, message: String) : super(context) {
        this.setCancelable(false)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_wait, null, false)
        binding = DialogWaitBinding.bind(view)

        view?.apply {
            binding.textPd.text = message
        }
        setView(view)
    }

    fun setMessage(s: String) {
        binding.textPd.text = s
    }
}