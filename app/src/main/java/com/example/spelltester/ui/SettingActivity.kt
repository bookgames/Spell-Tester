package com.example.spelltester.ui

import android.os.*
import android.widget.*
import androidx.appcompat.app.*
import com.example.spelltester.*
import com.example.spelltester.data.repositories.*
import com.example.spelltester.data.storage.*
import com.example.spelltester.databinding.*

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ls = LocalStorage.getInstance()
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val repo = AppRepository.getInstance()
        val builder = AlertDialog.Builder(this).setTitle(getString(R.string.delete_data))
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_your_progress))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                repo.deleteAttempts()
                binding.attemptsTv.text =
                    getString(R.string.answers, 0)
            }.setNegativeButton(getString(R.string.no)) { _, _ -> }
        binding.versionTv.text =
            getString(R.string.version, ls.getVersion())
        binding.attemptsTv.text =
            getString(R.string.answers, repo.getAllAttempt().filter { it.isAnswered() }.size)
        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.deleteAllBtn.setOnClickListener {
            builder.show()
        }
        binding.fetchDataBtn.setOnClickListener {
            repo.fetchRemoteData {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                binding.versionTv.text =
                    getString(R.string.version, ls.getVersion())
            }
        }
        binding.exportLogBtn.setOnClickListener {
            ls.exportLog(this)
        }
        binding.uploadLogBtn.setOnClickListener {

            repo.upload2Firebase(ls.getLog(), "logs/${ls.deviceId}/log.txt", {
                Toast.makeText(this, getString(R.string.successfully_uploaded), Toast.LENGTH_SHORT).show()
                ls.updateLastUpload()
            }, {
                Toast.makeText(this, "err:$", Toast.LENGTH_SHORT).show()
            })


        }

    }
}