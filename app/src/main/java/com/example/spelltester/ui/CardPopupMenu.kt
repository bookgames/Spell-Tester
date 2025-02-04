package com.example.spelltester.ui

import android.view.*
import androidx.appcompat.app.*
import androidx.appcompat.widget.*
import androidx.recyclerview.widget.*
import com.example.spelltester.*
import com.example.spelltester.data.repositories.*


class CardPopupMenu(
    view : View,
    private val position: Int,
    private val quizId:Int,
    private val adapter: RecyclerView.Adapter<*>,
):PopupMenu(view.context,view) {
    init {
        inflate(R.menu.quiz_card_menu)
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle(view.context.getString(R.string.delete_data))
        builder.setMessage(view.context.getString(R.string.are_you_sure_you_want_to_delete_this_quiz))
        builder.setPositiveButton(view.context.getString(R.string.yes)) { _, _ ->
            val attempts = AppRepository.getInstance().getAttemptsByQuizId(quizId)
            attempts.forEach { attempt ->
                attempt.points = 0f
                attempt.lastAttempt = 0
                AppRepository.getInstance().upsert(attempt)
            }
            adapter.notifyItemChanged(position)
        }
        setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_delete -> {
                    builder.show()
                    true
                }
                else -> false
            }
        }
    }
 
}