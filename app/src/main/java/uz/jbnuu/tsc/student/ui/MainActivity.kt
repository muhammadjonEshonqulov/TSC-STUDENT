package uz.jbnuu.tsc.student.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.work.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.student.R
import uz.jbnuu.tsc.student.databinding.ActivityMainBinding
import uz.jbnuu.tsc.student.model.send_location.SendLocationArrayBody
import uz.jbnuu.tsc.student.model.send_location.SendLocationBody
import uz.jbnuu.tsc.student.model.subjects.SubjectsData
import uz.jbnuu.tsc.student.ui.deadlines.DeadlineNotifyWorker
import uz.jbnuu.tsc.student.ui.student_main.StudentMainViewModel
import uz.jbnuu.tsc.student.utils.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SendDataToActivity {

    private val vm: StudentMainViewModel by viewModels()
    private val timeTest = (6 * 1000).toLong() + 100
    private var timer: CountDownTimer? = null
    private val MYREQUESTCODE = 100

    @Inject
    lateinit var prefs: Prefs

    private var appUpdateManager: AppUpdateManager? = null

    var task: Task<LocationSettingsResponse>? = null
    var locationSettingsRequestBuilder: LocationSettingsRequest.Builder? = null

    lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val REQUEST_CHECK_SETTINGS = 0x1

    var subjectTasks: ArrayList<uz.jbnuu.tsc.student.model.subjects.Task>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (appUpdateManager == null) {
            appUpdateManager = AppUpdateManagerFactory.create(this)
        }
        checkUpdate()

        if (prefs.get(prefs.token, "") != "" && prefs.get(prefs.hemisToken, "") != "") {
            getTasksFromLocal()
        }



        FirebaseMessaging.getInstance().subscribeToTopic("jbnuu_tsc_channel")
    }

    private fun getTasksFromLocal() {
        vm.getTaskData()
        vm.taskDataResponse.collectLatestLA(lifecycleScope) {
            if (it.isNotEmpty()) {
                // cancelAllWork()
                myWorkManager(it)
            }
        }
    }

    @SuppressLint("InvalidPeriodicWorkRequestInterval")
    private fun myWorkManager(list: List<uz.jbnuu.tsc.student.model.subjects.Task>) {
        val constants = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
            .build()

        prefs.save("Deadlines", Gson().toJson(list))

        val myRequest = PeriodicWorkRequest.Builder(DeadlineNotifyWorker::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(constants)
            .build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("my_tsc_jbnuu_id", ExistingPeriodicWorkPolicy.KEEP, myRequest)
    }

    private fun cancelAllWork() {
        WorkManager.getInstance(this)
            .cancelAllWork()
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    private fun checkUpdate() {
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                appUpdateManager?.registerListener(listener)
                appUpdateManager?.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, MYREQUESTCODE)
            } else {
                // lg("No Update available")
            }
        }
    }

    private val listener: InstallStateUpdatedListener = InstallStateUpdatedListener { installState ->
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            // lg("An update has been downloaded")
            showSnackBarForCompleteUpdate()
        }
    }

    private fun showSnackBarForCompleteUpdate() {
        val snackbar = Snackbar.make(
            binding.root, "New app is ready!", Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("Install") { view: View? ->
            appUpdateManager?.completeUpdate()
        }
        snackbar.setActionTextColor(ContextCompat.getColor(binding.root.context, R.color.cl_color_primary))
        snackbar.show()
    }

    private fun checkPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_REQUEST_CODE)
            false
        } else {
            true
        }
    }

    override fun onStop() {
        appUpdateManager?.unregisterListener(listener)
        super.onStop()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    timer?.cancel()
                    timer?.start()
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    snackBar(binding, "Ilovadan foydalanish uchun joylashuvingizni yoqishingizni so'raymiz.")
                    turnOnLocation()
                }
            }
            MYREQUESTCODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        // lg("" + "Result Ok")
                    }
                    Activity.RESULT_CANCELED -> {
                        // lg("" + "Result Cancelled")
                        checkUpdate()
                    }
                    ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                        // lg("" + "Update Failure")
                        checkUpdate()

                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (prefs.get(prefs.token, "") != "" && prefs.get(prefs.token, "") != "") {
            send("Stop")
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager?.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, MYREQUESTCODE)
            }
        }
        if (prefs.get(prefs.token, "") != "" && prefs.get(prefs.token, "") != "") {
            send("Start")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    turnOnLocation()
                } else {
                    snackBar(binding, "Permission denied")
//                    checkPermission()
                }
                return
            }
        }
    }

    private fun turnOnLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000

        if (locationSettingsRequestBuilder == null) {
            locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
            locationSettingsRequestBuilder?.addLocationRequest(locationRequest)
            locationSettingsRequestBuilder?.setAlwaysShow(true)
        }

        val settingsClient = LocationServices.getSettingsClient(this)
        if (task == null) {
            locationSettingsRequestBuilder?.let {
                task = settingsClient.checkLocationSettings(it.build())
            }
        }

        task?.addOnSuccessListener {
            timer?.cancel()
            timer?.start()
            task = null
            locationSettingsRequestBuilder = null

        }
        task?.addOnFailureListener {
            timer?.cancel()
            if (it is ResolvableApiException) {
                try {
                    val resolvableApiException = it
                    resolvableApiException.startResolutionForResult(this@MainActivity, REQUEST_CHECK_SETTINGS)
                } catch (sendIntentException: SendIntentException) {
                    sendIntentException.printStackTrace()
                }
            }
            task = null
            locationSettingsRequestBuilder = null
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()

    }

    private fun sendLocation(sendLocationBody: SendLocationBody) {
        vm.sendLocation(sendLocationBody)
        vm.sendLocationResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.status?.let {
                    }
                }
                is NetworkResult.Error -> {
                    if (it.code == 401) {
                        navigateToLogin()
                    }
                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun sendLocationArray(sendLocationArrayBody: SendLocationArrayBody) {
        vm.sendLocationArray(sendLocationArrayBody)
        vm.sendLocationArrayResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.status?.let {
                        vm.clearSendLocationBodyData()
                    }
                }
                is NetworkResult.Error -> {
                    if (it.code == 401) {
                        navigateToLogin()
                    }
                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun navigateToLogin() {
        cancelAllWork()
        val navControl = findNavController(R.id.nav_host_fragment)
        vm.clearTaskData()
        vm.clearSendLocationBodyData()
        prefs.clear()
        send("Stop")
        if (navControl.navigateUp()) {
            navControl.navigate(R.id.loginFragment)
        }
    }

    private fun subjects() {
        vm.subjects()
        vm.subjectsResponse.collectLatestLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.data?.let {
                        val currentSemesterSubjects = ArrayList<SubjectsData>()
                        currentSemesterSubjects.clear()
                        it.forEachIndexed { index, subjectsData ->
                            if (prefs.get(prefs.semester, "") == subjectsData._semester) {
                                currentSemesterSubjects.add(subjectsData)
                                subject(subjectsData.subject?.id, subjectsData._semester)
                            }
                        }

                    }
                }
                is NetworkResult.Error -> {
                    if (it.code == 401) {
                        navigateToLogin()
                    }
                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }

//    private fun getActiveTime() {
//        vm.getActiveTime()
//        vm.getActiveTimeResponse.collectLatestLA(lifecycleScope) {
//            when (it) {
//                is NetworkResult.Success -> {
//                    it.data?.allow?.let {
//                        if (!it) {
//                            sendLocationFunction()
//                        } else {
//                            timer?.cancel()
//                        }
//                    }
//                }
//                is NetworkResult.Error -> {
//                    if (it.code == 401) {
//                        navigateToLogin()
//                    }
//                }
//                is NetworkResult.Loading -> {
//
//                }
//            }
//        }
//    }

    private fun sendLocationFunction() {
        if (timer == null) {
            timer = object : CountDownTimer(timeTest, 1000) {
                var time = 0

                @SuppressLint("SetTextI18n", "VisibleForTests", "SimpleDateFormat")
                override fun onTick(millisUntilFinished: Long) {
                    if (prefs.get(prefs.loginStop, 0) == 1) {
                        cancel()
                    } else {
                        time = (timeTest - millisUntilFinished).toInt() / 1000
                        if (time == 2) {
                            if (checkPermission()) {
                                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)// FusedLocationProviderClient(this@MainActivity)
                                fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    val currentDate = sdf.format(Date())
                                    try {
                                        application?.let { appl ->
                                            if (hasInternetConnection(appl)) {
                                                vm.getSendLocationBodyData()
                                                vm.getSendLocationsResponse.collectLA(lifecycleScope) { sendLocations ->
                                                    if (sendLocations.isNotEmpty()) {
                                                        sendLocationArray(SendLocationArrayBody(sendLocations))
                                                    }
                                                }
                                                sendLocation(SendLocationBody(currentDate, "" + it.result.latitude, "" + it.result.longitude))
                                            } else {
                                                vm.insertSendLocationBody(SendLocationBody(currentDate, "" + it.result.latitude, "" + it.result.longitude))
                                            }
                                        }

                                    } catch (e: NullPointerException) {
                                        turnOnLocation()
                                    }
                                }
                            } else {
                                cancel()
                            }
                        }
                    }
                }

                @SuppressLint("SimpleDateFormat")
                override fun onFinish() {
                    cancel()
                    start()
                }
            }
        }
        if (checkPermission()) {
            timer?.start()
        }
    }

    private fun subject(subject: Int?, semester: String) {
        vm.subject(subject, semester)
        vm.subjectResponse.collectLatestLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Success -> {
                    // lg("subject 2 ")

                    if (subjectTasks == null) {
                        subjectTasks = ArrayList()
                    }

                    it.data?.data?.tasks?.let { tasks ->
                        // lg("tasks -> " + tasks.size)
                        val status = vm.insertTaskData(tasks)
                        // lg("status isCancelled -> " + status.isCancelled)
                        // lg("status isActive -> " + status.isActive)
                        // lg("status isCompleted -> " + status.isCompleted)
                    }
                }
                is NetworkResult.Error -> {
                    if (it.code == 401) {
                        navigateToLogin()
                    } else {
                        // lg("error get subject ->" + it.message.toString())
                    }
                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }

    override fun send(value: String) {
        if (value == "Deadline") {
            subjects()
        } else if (value == "Start") {
            sendLocationFunction()
        } else if (value == "Stop") {
            timer?.cancel()
        }
    }
}