package uz.jbnuu.tsc.student.base

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import uz.jbnuu.tsc.student.R

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) : Fragment() {
    private var isUseBackPress = true
//    lateinit var languageManager: LanguageManager
//    lateinit var textSizes: TextSizes


    private var _binding: VB? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        binding.root.context?.let {
//            languageManager = LanguageManager(it)
//            textSizes = TextSizes(it)

        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflate.invoke(LayoutInflater.from(context), null, false)
//        binding.root.context?.let {
//            languageManager = LanguageManager(it)
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener { _, keyCode, e ->
            if (keyCode == KeyEvent.KEYCODE_BACK && e.action == KeyEvent.ACTION_DOWN || keyCode == KeyEvent.FLAG_SOFT_KEYBOARD) {
                isUseBackPress = true
                onBackPressed()
                return@setOnKeyListener isUseBackPress
            } else return@setOnKeyListener false
        }
        onViewCreatedd(view, savedInstanceState)
//        notifyLanguageChanged()
//        notifyOnTextSize()
    }

    abstract fun onViewCreatedd(view: View, savedInstanceState: Bundle?)

    open fun onBackPressed() {
        isUseBackPress = false
    }

    fun finish() {
        findNavController().popBackStack()
    }

    fun <T> finish(key: String, value: T) {
        val navController = findNavController()
        navController.previousBackStackEntry?.savedStateHandle?.set(key, value)
        navController.popBackStack()
    }

    fun hideKeyBoard() {
        val view = activity?.currentFocus ?: View(activity)
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeyBoard(view: EditText) {
//        val view = activity?.currentFocus ?: View(activity)
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun snackBar(binding: ViewBinding, message: String) {
        try {
            binding.root.let {
                val snackbar = Snackbar.make(it, message, Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
        } catch (e: Exception) {

        }
    }

    fun snackBarAction(message: String) {
        try {
            binding.root.let {
                val snackbar = Snackbar.make(it, message, Snackbar.LENGTH_INDEFINITE)
                snackbar.setAction(it.context.getString(R.string.close)) {
                    snackbar.dismiss()
                }
                val textView: TextView = snackbar.view.findViewById(com.google.android.material.R.id.snackbar_text)
                textView.maxLines = 6
                snackbar.show()
            }
        } catch (e: Exception) {

        }
    }

//    protected fun notifyLanguageChanged() = onCreateLanguage(languageManager.currentLanguage)
//    protected fun notifyOnTextSize() = onTextSiz(textSizes)


//    open fun onCreateLanguage(language: Language) {
//        binding.root.apply {
//            val configuration = Configuration()
//            configuration.setLocale(
//                Locale(languageManager.currentLanguage.userName)
//            )
//            resources.updateConfiguration(configuration, resources.displayMetrics)
//        }
//    }

//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (isVisibleToUser) {
//            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
//        }
//    }

//    abstract fun onTextSiz(textSizes: TextSizes)
}

