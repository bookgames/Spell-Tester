package com.example.spelltester

import android.content.*
import android.os.*
import android.util.*
import androidx.appcompat.app.*
import androidx.recyclerview.widget.*
import androidx.work.*
import com.example.spelltester.data.*
import com.example.spelltester.data.db.*
import com.example.spelltester.data.remote.*
import com.example.spelltester.data.repositories.*
import com.example.spelltester.data.storage.*
import com.example.spelltester.databinding.*
import com.example.spelltester.ui.*
import org.json.*


class MainActivity : AppCompatActivity() {
 val TAG = "KH_MAIN_ACT"
    private lateinit var binding: ActivityMainBinding

    //    private val data = Firebase.firestore.collection("words")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // Set the content view to the root view of the binding
        Log.d(TAG, "before build")
        AppDatabase.invoke(this)
        AppRepository.invoke(AppDatabase.getInstance())
        val storageManager= StorageManager.invoke(this)
        Log.d(TAG, "after build")
        val repo = AppRepository.getInstance()
        val live = repo.getAllQuiz()
        val adapter = QuizAdapter()
        live.observe(this) { quiz ->
            adapter.quizList = quiz
            adapter.notifyDataSetChanged()
        }
        binding.mainRecycle.adapter = adapter
        binding.mainRecycle.layoutManager = LinearLayoutManager(this)
        RemoteService.getData("data.json"){
            val root= JSONObject(it)
            val version=root.getInt("version")
            val currentVersion=storageManager.getVersion()
            if (version>currentVersion){
                storageManager.saveVersion(version)
                repo.upsertAllWords(JsonToWords(it))
                repo.upsert(JsonToQuizzes(it))
                Log.d(TAG, "version updated")
            }else{
                Log.d(TAG, "version is up to date")
            }
        }
        binding.settingBtn.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

}