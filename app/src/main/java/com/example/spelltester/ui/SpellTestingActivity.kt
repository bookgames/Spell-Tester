package com.example.spelltester.ui

import android.os.*
import androidx.appcompat.app.*
import androidx.lifecycle.*
import com.example.spelltester.*
import com.example.spelltester.data.db.attempt.*
import com.example.spelltester.databinding.*
import com.example.spelltester.ui.SpellTestingViewModel.*


class SpellTestingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpellTestingBinding
    var quizId: Int? = null
    private lateinit var viewModel: SpellTestingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpellTestingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        quizId = intent.getIntExtra(QUIZ_ID_KEY, -1)
        viewModel = ViewModelProvider(this)[SpellTestingViewModel::class.java]
        viewModel.init(quizId!!)
        binding.btnNext.setOnClickListener {
            when (viewModel.processClicking(binding.txtSpell.text.toString())) {
                SpellTestingViewModel.Message.FINISH -> {
                    finish()
                }
                else -> refreshUI()
            }
        }
        refreshUI()
    }

    private fun refreshUI() {
        val gainedPoints = viewModel.gainedPoints.toInt()
        val currentPoints = viewModel.currentPoints.toInt()
        when (viewModel.status) {
            Status.ANSWERING -> {
                val attempt = viewModel.attempt!!
                binding.txtHead.text =
                    attempt.word?.arabicWord ?: getString(R.string.error_finding_word)
                binding.btnNext.setText(R.string.answer)
                //TODO: set color to default
                binding.remainsTv.text =
                    "${getString(R.string.remaining)} :${viewModel.attempts.filter { it.points != Attempt.MAX_POINT }.size}"
                binding.pointsTv.text = "pts $currentPoints"
                binding.pointsTv.background.setTint(getColor(R.color.mid))
            }

            Status.SHOWING -> {

                binding.pointsTv.text =
                    "pts $currentPoints :" + (if (gainedPoints > 0) "+" else "") + gainedPoints
                when {
                    gainedPoints > 2 -> binding.pointsTv.background.setTint(getColor(R.color.very_good))
                    gainedPoints > 0 -> binding.pointsTv.background.setTint(getColor(R.color.good))
                    gainedPoints == 0 -> binding.pointsTv.background.setTint(getColor(R.color.mid))
                    gainedPoints > -2 -> binding.pointsTv.background.setTint(getColor(R.color.bad))
                    else -> binding.pointsTv.background.setTint(getColor(R.color.very_bad))
                }
                var attempt = viewModel.attempt!!
                binding.btnNext.setText(R.string.next)
                binding.txtHead.text = attempt.word?.englishWord + "\n" + attempt.word?.arabicWord
            }

            Status.DONE -> {
                binding.remainsTv.text=getText(R.string.done)
                binding.txtHead.text = getString(R.string.congratulation)
                binding.btnNext.setText(R.string.done)
            }

            Status.ERROR -> {
                binding.txtHead.text = getString(viewModel.errorMessage)
                binding.btnNext.setText(R.string.done)
            }
        }
    }

}