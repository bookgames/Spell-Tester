package com.example.spelltester.ui

import android.os.*
import androidx.appcompat.app.*
import com.example.spelltester.*
import com.example.spelltester.data.repositories.*
import com.example.spelltester.databinding.*

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val builder = AlertDialog.Builder(this).setTitle(getString(R.string.delete_data))
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_your_progress))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
               // AppRepository.getInstance().deleteAttempts()
            }
        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.deleteAllBtn.setOnClickListener {
            builder.show()
        }

    }
}