package com.efesen.stopwatch

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.efesen.stopwatch.model.LapRecord

/**
 * Created by Efe Şen on 23.01.2024.
 */
class LapAdapter(private val context: Context, private val lapTimes: MutableList<LapRecord>): RecyclerView.Adapter<LapAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lapTextView: TextView = itemView.findViewById(R.id.lapCount)
        val minutesTextView: TextView = itemView.findViewById(R.id.minutes)
        val secondsTextView: TextView = itemView.findViewById(R.id.seconds)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lap, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lapTimes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lapRecord = lapTimes[position]

        holder.lapTextView.text = (lapRecord.lapCount).toString().padStart(2,'0')
        holder.minutesTextView.setText(formatTime(lapRecord.lapTime))
        holder.secondsTextView.setText(formatTime(lapRecord.overallTime))

        // Show the longest lap time in red
        if (lapRecord.lapCount > 3 && lapRecord.lapTime == lapTimes.maxByOrNull { it.lapTime }?.lapTime) {
            holder.lapTextView.setTextColor(Color.RED)
        } else {
            holder.lapTextView.setTextColor(context.getColor(R.color.cool_gray))
        }

    }

    private fun formatTime(timeInMillis: Long): String {
        val minutes = (timeInMillis / 60000).toString().padStart(2,'0')
        val seconds = ((timeInMillis % 60000) / 1000).toString().padStart(2,'0')
        val milliseconds = ((timeInMillis % 1000) / 10).toString().padStart(2,'0')
        return "$minutes: $seconds.$milliseconds"
    }
}