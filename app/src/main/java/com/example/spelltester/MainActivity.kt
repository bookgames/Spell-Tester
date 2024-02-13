package com.example.spelltester

import android.os.*
import android.util.*
import androidx.appcompat.app.*
import androidx.recyclerview.widget.*
import com.example.spelltester.data.db.*
import com.example.spelltester.data.repositories.*
import com.example.spelltester.databinding.*
import com.example.spelltester.ui.*
import com.google.firebase.*
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.*

const val TAG = "MAIN_ACT"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val data = Firebase.firestore.collection("words")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // Set the content view to the root view of the binding

        Log.d(TAG, "onCreate: before build")
        AppDatabase.invoke(this)
        AppRepository.invoke(AppDatabase.getInstance())
        Log.d(TAG, "onCreate: after build")

val repo=AppRepository.getInstance()
        val live = repo.getAllQuiz()
        val adapter = QuizAdapter()
        live.observe(this) { quiz ->
            adapter.quizList = quiz
            adapter.notifyDataSetChanged()
        }
        binding.mainRecycle.adapter = adapter
        binding.mainRecycle.layoutManager = LinearLayoutManager(this)
        binding.mainSetting.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d(TAG, "onCreate: before add")
                    var word =repo.getWordByWordId(3)
                    if (word == null) {
                        Log.d(TAG, "onCreate: word is null")
                    return@launch
                    }
                    data.add(word.englishWord to 1).await()
                    Log.d(TAG, "onCreate: added")
                } catch (e: Exception) {
                    Log.d(TAG, "onCreate: ${e.message}")
                }
            }
        }
    }

}