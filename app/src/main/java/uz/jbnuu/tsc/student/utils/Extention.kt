@file:Suppress(
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)

package uz.jbnuu.tsc.student.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import uz.jbnuu.tsc.student.BuildConfig
import uz.jbnuu.tsc.student.R
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

fun View.snackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("ok") {
            snackbar.dismiss()
        }
    }.show()
}

fun snackBar(binding: ViewBinding, message: String) {
    binding.root.let {
        val snackbar = Snackbar.make(it, message, Snackbar.LENGTH_SHORT)
        snackbar.show()
    }
}

fun lg(message: String) {
//    if (BuildConfig.isDebug) {
        Log.d("TSC", message)
//    }
}

fun getHour(): Int {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("HH")
    calendar.add(Calendar.DATE, 0)
    return dateFormat.format(calendar.time).toInt()
}

fun Fragment.findNavControllerSafely(): NavController? {
    return if (isAdded) {
        findNavController()
    } else {
        null
    }
}
public infix fun <M, N> M.toGo(that: N): Pair<M, N> = Pair(this, that)

fun Activity.findNavControllerSafely(fragment: Fragment): NavController? {
    return findNavControllerSafely(fragment)
}

fun String.toDate(): Date {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(this)
}

fun URL.getFileSize(): Int? {
    return try {
        openConnection().contentLength
    } catch (x: Exception) {
        null
    }
}

@SuppressLint("NewApi", "ObsoleteSdkInt")
fun String.decode(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
        android.util.Base64.decode(this, android.util.Base64.DEFAULT).toString(charset("UTF-8"))
    } else {
        TODO("VERSION.SDK_INT < FROYO")
    }
}

@SuppressLint("NewApi", "ObsoleteSdkInt")
fun ByteArray.encode(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
        android.util.Base64.encode(this, android.util.Base64.DEFAULT).toString(charset("UTF-8"))
    } else {
        TODO("VERSION.SDK_INT < FROYO")
    }
}

fun getRootDirPath_(context: Context): String? {
    return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        val file = ContextCompat.getExternalFilesDirs(context.applicationContext, null)[0]
        file.absolutePath
    } else {
        context.applicationContext.filesDir.absolutePath
    }
}

fun TextView.ellipsizeText() {
    isSingleLine = true
    maxLines = 1
    ellipsize = TextUtils.TruncateAt.END
}

fun String.getTime() = this.split(" ")[1].substring(0, 5)

fun Int.getDuration(): String {
    val a = this / 3600
    val b = this / 60
    val h = if (a <= 9) "0$a" else (a).toString()
    val m = if (b <= 9) "0${b}" else (b).toString()
    return "$h:$m"
}

fun getDateTime(s: Long): String? {
    return try {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val netDate = Date(s * 1000)
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}
@SuppressLint("SimpleDateFormat")
fun Int.getDate(): String {
    val c: Calendar = GregorianCalendar()
    c.time = Date()
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    c.add(Calendar.MONTH, -this)
    return sdf.format(c.time)
}


fun EditText.showKeyboard() {
    post {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun BottomNavigationView.disableTooltip() {

    if (this == null) {
        return
    }

    val count: Int = this.childCount;
    if (count <= 0) {
        return
    }

    val menuView: ViewGroup = this.getChildAt(0) as ViewGroup
    if (menuView == null) {
        return;
    }

    val menuItemViewSize: Int = menuView.childCount
    if (menuItemViewSize <= 0) {
        return
    }

    for (i in 0 until menuItemViewSize) {
        menuView.getChildAt(
            i
        ).setOnLongClickListener { true }
    }
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun AppCompatActivity.startFragment(
    fragment: Fragment,
    bundle: Bundle? = null,
    isAnimate: Boolean = false
) {
    //  val resId = ViewModelProviders.of(this)[BaseFragmentViewModel::class.java].resId
    // fragment.senderData = senderData
    val transition = supportFragmentManager.beginTransaction()
    if (isAnimate)
        transition.setCustomAnimations(
            R.anim.slide_in_left,
            R.anim.slide_out_left,
            R.anim.slide_in_right,
            R.anim.slide_out_right
        )
    fragment.arguments = bundle
    transition.replace(R.id.nav_host_fragment, fragment).commit()
}

fun Activity.hideKeyboard() {
    if (currentFocus == null) View(this) else currentFocus?.let { hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View?.blockClickable(
    blockTimeMilles: Long = 500
) {
    this?.isClickable = false
    Handler().postDelayed({ this?.isClickable = true }, blockTimeMilles)
}