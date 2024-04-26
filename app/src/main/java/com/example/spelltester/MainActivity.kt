package com.example.spelltester

import android.content.*
import android.os.*
import android.util.*
import androidx.appcompat.app.*
import androidx.recyclerview.widget.*
import com.example.spelltester.data.db.JsonConverter.DATA_FILE_NAME
import com.example.spelltester.data.db.JsonConverter.KEY_VERSION
import com.example.spelltester.data.db.JsonConverter.jsonTOQuizzes
import com.example.spelltester.data.db.JsonConverter.jsonToWords
import com.example.spelltester.data.remote.*
import com.example.spelltester.data.repositories.*
import com.example.spelltester.data.storage.*
import com.example.spelltester.databinding.*
import com.example.spelltester.ui.*
import com.example.spelltester.ui.notification.*
import com.google.firebase.storage.*
import org.json.*

class MainActivity : AppCompatActivity() {
    val TAG = "KH_MAIN_ACT"
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        uploadLogToFirebase()
        setContentView(binding.root)
        initializeComponents()
    }

    private fun uploadLogToFirebase() {
        val storage = FirebaseStorage.getInstance().reference
        val ls = LocalStorage.getInstance()
        if (System.currentTimeMillis() - ls.getLastUpload() < (24 * 60 * 60 * 1000)) {
            storage.child("logs/${ls.deviceId}/log.txt")
                .putBytes(ls.getLog().toByteArray()).addOnSuccessListener {
                    ls.updateLastUpload()
                    ls.logDebug("Uploaded to fire base successfully")
                }.addOnFailureListener {
                    Log.d(TAG, "failed to upload to firebase:${it.message}")
                    it.printStackTrace()
                }
        }
    }

    private fun initializeComponents() {
        val localStorage = LocalStorage.getInstance()
        val repo = AppRepository.getInstance()
        val notification = ReminderNotification.getInstance(this)
        setupQuizList(repo)
        loadDataIfNeeded(localStorage, repo)
        setupSettingButton()
        notification.handleNotification()
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
                localStorage.log("data loaded $currentVersion:$version", TAG)
            } else {
                localStorage.log("data already loaded", TAG)
            }
        }
    }

    private fun setupSettingButton() {
        binding.settingBtn.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        binding.mainRecycle.adapter?.notifyDataSetChanged()
    }


}