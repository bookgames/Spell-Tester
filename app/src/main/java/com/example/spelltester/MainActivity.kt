package com.example.spelltester

import android.Manifest.permission.*
import android.app.*
import android.content.*
import android.content.pm.*
import android.os.*
import android.util.*
import androidx.activity.result.contract.*
import androidx.appcompat.app.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.*
import com.example.spelltester.data.db.*
import com.example.spelltester.data.db.JsonConverter.DATA_FILE_NAME
import com.example.spelltester.data.db.JsonConverter.KEY_VERSION
import com.example.spelltester.data.db.JsonConverter.jsonTOQuizzes
import com.example.spelltester.data.db.JsonConverter.jsonToWords
import com.example.spelltester.data.remote.*
import com.example.spelltester.data.repositories.*
import com.example.spelltester.data.storage.*
import com.example.spelltester.databinding.*
import com.example.spelltester.ui.*
import org.json.*


class MainActivity : AppCompatActivity() {
    val TAG = "KH_MAIN_ACT"
    private lateinit var binding: ActivityMainBinding
    private val alarmTime = 1000 * 7
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeComponents()
    }

    private fun initializeComponents() {
        Log.d(TAG, "before build")
        AppDatabase.invoke(this)
        AppRepository.invoke(AppDatabase.getInstance())
        ReminderNotification.getInstance(this)
        val localStorage = LocalStorage.invoke(this)
        Log.d(TAG, "after build")
        val repo = AppRepository.getInstance()
        setupQuizList(repo)
        loadDataIfNeeded(localStorage, repo)
        setupSettingButton()
        handleNotificationPermission()
    }

    private fun setupQuizList(repo: AppRepository) {
        val live = repo.getAllQuiz()
        val adapter = QuizAdapter()
        live.observe(this) { quiz ->
            adapter.quizList = quiz
            adapter.notifyDataSetChanged()
        }
        binding.mainRecycle.adapter = adapter
        binding.mainRecycle.layoutManager = LinearLayoutManager(this)
    }

    private fun loadDataIfNeeded(localStorage: LocalStorage, repo: AppRepository) {
        RemoteService.getData(DATA_FILE_NAME) { data ->
            val root = JSONObject(data)
            val version = root.getInt(KEY_VERSION)
            val currentVersion = localStorage.getVersion()
            if (version > currentVersion) {
                localStorage.saveVersion(version)
                repo.upsertAllWords(jsonToWords(data))
                repo.upsert(jsonTOQuizzes(data))
                Log.d(TAG, "version updated")
            } else {
                Log.d(TAG, "version is up to date")
            }
        }
    }

    private fun setupSettingButton() {
        binding.settingBtn.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

    private fun handleNotificationPermission() {
        if (isNotificationPermissionGranted()) {
            scheduleNotification()
        } else {
            requestNotificationPermission()
        }
    }

    private fun scheduleNotification() {
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, NotificationReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC, System.currentTimeMillis() + alarmTime, pendingIntent
        )
    }

    private fun requestNotificationPermission() {
        val requestNotification = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                scheduleNotification()
            } else {
                Log.d(TAG, "permission denied")
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            requestNotification.launch(POST_NOTIFICATIONS)
            return
        }
        if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
            AlertDialog.Builder(this).setTitle(getString(R.string.notification_permission_needed))
                .setMessage(getString(R.string.we_need_notification_permission_to_schedule_reminders))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    requestNotification.launch(POST_NOTIFICATIONS)
                }.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }.create().show()
        } else {
            requestNotification.launch(POST_NOTIFICATIONS)
        }

    }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permissions are not required for older versions
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mainRecycle.adapter?.notifyDataSetChanged()
    }

}