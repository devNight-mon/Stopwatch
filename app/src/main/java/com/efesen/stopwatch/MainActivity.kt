package com.efesen.stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.efesen.stopwatch.databinding.ActivityMainBinding
import com.efesen.stopwatch.model.LapRecord
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var counterTextView: TextView
    private lateinit var overallTextView: TextView
    private lateinit var startStopResumeButton: MaterialButton
    private lateinit var lapButton: MaterialButton
    private lateinit var counterInfo : LinearLayout
    private lateinit var line: MaterialCardView
    private lateinit var recyclerView: RecyclerView
    private lateinit var handler: Handler
    private lateinit var lapAdapter: LapAdapter

    private var isRunning = false
    private var isPaused = false
    private var elapsedTime = 0L
    private var lapStartTime = 0L
    private var lapCount = 0

    private var lapTimes = mutableListOf<LapRecord>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindUIComponent()

        lapAdapter = LapAdapter(this,lapTimes)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = lapAdapter

        handleClickListeners()

    }

    private fun bindUIComponent() {
        counterTextView = binding.counterTextView
        overallTextView = binding.overallTextView
        startStopResumeButton = binding.startAndStopOrResumeButton
        lapButton = binding.lapButton
        recyclerView = binding.recyclerView
        counterInfo = binding.counterInfo
        line = binding.line

        handler = Handler(Looper.getMainLooper())
    }

    private fun handleClickListeners() {

        lapButton.setOnClickListener {
            if (isRunning) {
                counterInfo.visibility = View.VISIBLE
                line.visibility = View.VISIBLE
                counterTextView.visibility = View.VISIBLE
                addLapTime()
                lapStartTime = 0

            } else if (isPaused)    {
                resetTimer()
            }
        }

        startStopResumeButton.setOnClickListener {
            if (!isRunning) {
                startTimer()
            }else {
                if (isPaused) {
                    resumeTimer()
                }else {
                    stopTimer()
                }
            }
        }
    }

    private fun stopTimer() {
        isRunning = false
        isPaused = true
        startStopResumeButton.text = getString(R.string.resume)
        startStopResumeButton.backgroundTintList = AppCompatResources.getColorStateList(this,R.color.baby_blue)
        lapButton.text = getString(R.string.reset)
        handler.removeCallbacksAndMessages(null)

    }

    private fun resumeTimer() {
        isPaused = false
        startStopResumeButton.text = getString(R.string.stop)
        startStopResumeButton.backgroundTintList =  AppCompatResources.getColorStateList(this,R.color.red)
        startTimer()

    }

    private fun startTimer() {
        isRunning = true
        isPaused = false
        startStopResumeButton.text = getString(R.string.stop)
        startStopResumeButton.backgroundTintList = AppCompatResources.getColorStateList(this,R.color.red)
        lapButton.isEnabled = true
        lapButton.text = getString(R.string.lap)

        handler.post(object : Runnable {
            override fun run() {
                if (isRunning && !isPaused) {
                    elapsedTime += 10
                    lapStartTime += 10
                    updateTimerText()
                    handler.postDelayed(this,10)
                }
            }
        })
    }

    private fun resetTimer() {
        isRunning = false
        isPaused = false
        startStopResumeButton.text = getString(R.string.start)
        startStopResumeButton.backgroundTintList =  AppCompatResources.getColorStateList(this, R.color.baby_blue)
        lapButton.isEnabled = false
        lapButton.text = getString(R.string.lap)
        counterInfo.visibility = View.INVISIBLE
        line.visibility = View.INVISIBLE
        counterTextView.visibility = View.INVISIBLE

        lapTimes.clear()
        lapAdapter.notifyDataSetChanged()
        elapsedTime = 0
        lapStartTime = 0
        lapCount = 0
        updateTimerText()

    }

    private fun updateTimerText() {
        val minutes = (elapsedTime / 60000).toString().padStart(2,'0')
        val seconds = ((elapsedTime % 60000) / 1000).toString().padStart(2,'0')
        val milliseconds = if (elapsedTime % 1000 == 0L) {
            "00"
        } else {
            (elapsedTime % 1000).toString().padStart(3,'0').removeSuffix("0")
        }

        overallTextView.text = "$minutes : $seconds.$milliseconds"

        val lapMinutes = (lapStartTime / 60000).toString().padStart(2, '0')
        val lapSeconds = ((lapStartTime % 60000) / 1000).toString().padStart(2, '0')
        val lapMillis = if (lapStartTime % 1000 == 0L) {
            "00"
        } else {
            (lapStartTime % 1000).toString().padStart(3,'0').removeSuffix("0")
        }

        counterTextView.text = "$lapMinutes : $lapSeconds.$lapMillis"
    }

    private fun addLapTime() {
        lapCount++

        val lapRecord = LapRecord(lapCount, lapStartTime, elapsedTime)
        lapTimes.add(lapRecord)
        lapAdapter.notifyDataSetChanged()

    }
}