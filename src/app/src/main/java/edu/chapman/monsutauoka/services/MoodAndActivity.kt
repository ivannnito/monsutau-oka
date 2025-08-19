package edu.chapman.monsutauoka.services

import edu.chapman.monsutauoka.R
import android.widget.ImageView
import android.widget.TextView
import edu.chapman.monsutauoka.services.data.DataStore
import java.util.Calendar
import java.util.Date


class MoodAndActivity(val dataStore: DataStore) {
    private var currentMood: Int
    private var feedCount: Int //keeps track of how many times they have been fed since the last mood change
    private var previousFeedingDate: Date
    private val moodKey = "${this::class.simpleName}.currentMood"
    private val feedCountKey = "${this::class.simpleName}.feedCount"
    private val dateKey = "${this::class.simpleName}.previousFeedingDate"
    private var currentDate = Date()
    private var currentActivity = 0
    private val totalMoods = 7
    private val totalActivities = 9

    private val moodImages: Array<Array<Int>> by lazy {
        arrayOf(
            sadImages, madImages, tickedOffImages,
            neutralImages, boredImages, surprisedImages,
            happyImages
        )
    }

    private val sadImages = arrayOf(
        R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground
    )

    private val madImages = arrayOf(
        R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground
    )

    private val tickedOffImages = arrayOf(
        R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground
    )

    private val neutralImages = arrayOf(
        R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground
    )

    private val boredImages = arrayOf(
        R.drawable.ic_dashboard_black_24dp, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground
    )

    private val surprisedImages = arrayOf(
        R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground
    )

    private val happyImages = arrayOf(
        R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
        R.drawable.ic_launcher_foreground
    )

    private val moodTexts = arrayOf(
        "Sad", "Mad", "Ticked Off", "Neutral", "Bored",
        "Surprised", "Happy"
    )

    private val activityTexts: Array<Array<String>> by lazy {
        arrayOf(
            sadActivityTexts, madActivityTexts, tickedOffActivityTexts,
            neutralActivityTexts, boredActivityTexts, surprisedActivityTexts,
            happyActivityTexts
        )
    }

    private val sadActivityTexts = arrayOf(
        "sleeping", "sleeping", "sleeping", "eating", "drinking",
        "eating", "drinking", "sleeping", "very depressed"
    )

    private val madActivityTexts = arrayOf(
        "sleeping", "sleeping", "eating", "throwing a tantrum", "eating",
        "throwing a tantrum", "sleeping", "sleeping", "very upset"
    )

    private val tickedOffActivityTexts = arrayOf(
        "sleeping", "waking up", "eating", "throwing a tantrum", "eating",
        "getting ready for bed", "sleeping", "sleeping", "whining"
    )

    private val neutralActivityTexts = arrayOf(
        "waking up", "eating", "eating", "drawing", "eating",
        "getting ready for bed", "sleeping", "sleeping", "sitting around"
    )

    private val boredActivityTexts = arrayOf(
        "waking up", "eating", "eating", "doing puzzles", "eating",
        "getting ready for bed", "sleeping", "sleeping", "sitting around"
    )

    private val surprisedActivityTexts = arrayOf(
        "waking up", "eating", "eating", "doing puzzles", "eating",
        "getting ready for bed", "sleeping", "sleeping", "skittish"
    )

    private val happyActivityTexts = arrayOf(
        "waking up", "eating", "eating", "gardening", "eating",
        "getting ready for bed", "sleeping", "sleeping", "relaxing"
    )

    init {
        currentMood = dataStore.load(moodKey)?.toIntOrNull() ?: 4
        feedCount = dataStore.load(feedCountKey)?.toIntOrNull() ?: 0

        val dateString = dataStore.load(dateKey)
        previousFeedingDate = if (dateString != null) {
            try {
                Date(dateString.toLong()) // Assumes saved as timestamp
            } catch (e: Exception) {
                Date()
            }
        } else {
            Date()
        }
    }

    private fun persistState() {
        dataStore.save(moodKey, currentMood.toString())
        dataStore.save(feedCountKey, feedCount.toString())
        dataStore.save(dateKey, previousFeedingDate.time.toString()) // Save as timestamp
    }

    private fun getCurrentMood(): Int {
        return currentMood
    }

    private fun increaseMood() {
        if (currentMood < totalMoods - 1) {
            currentMood++
        }
        feedCount = 0
        setPreviousFeedingDateToNow()
        persistState()
    }

    private fun decreaseMood() {
        if (currentMood > 0) {
            currentMood--
        }
        feedCount = 0
        setPreviousFeedingDateToNow()
        persistState()
    }

    fun setPreviousFeedingDateToNow(newDate: Date = Date()) {
        previousFeedingDate = newDate
    }

    fun setCurrentDate(newDate: Date = Date()){
        currentDate = newDate
    }

    fun getTimePassedInHours(): Double {
        setCurrentDate()

        val diffInMillis = currentDate.time - previousFeedingDate.time

        val totalMinutes = diffInMillis / (1000.0 * 60)
        val hoursWithDecimal = totalMinutes / 60.0

        return hoursWithDecimal
    }

    fun feedWithoutWallet(imageView: ImageView, textView: TextView) {
        val timePassed = getTimePassedInHours()
        feedCount = if (timePassed > 2.0) 1 else (feedCount + 1)

        if (feedCount >= 4) {
            increaseMood()
            updateUI(imageView, textView)
        }
        updateUI(imageView, textView)
        persistState()
    }

    fun timeCheck(imageView: ImageView, textView: TextView) {
        //called every minute and when app is opened
        val timePassed = getTimePassedInHours()
        val moodsToDecrease = (timePassed / .01).toInt()
        repeat(moodsToDecrease) {
            decreaseMood()
        }
        updateUI(imageView, textView)
    }

    private fun updateImage(imageView: ImageView) {
        imageView.setImageResource(moodImages[currentMood][currentActivity])
    }

    private fun updateText(textView: TextView) {
        textView.text = "Turtwig's current mood is: " +
                moodTexts[currentMood] + " and they are " +
                activityTexts[currentMood][currentActivity] + "."
    }

    private fun updateUI(imageView: ImageView, textView: TextView) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        when (currentHour) {
            in 7..7 -> {
                currentActivity = 0
                updateImage(imageView)
                updateText(textView)
            }
            in 8..9 -> {
                currentActivity = 1
                updateImage(imageView)
                updateText(textView)
            }

            in 12..12 -> {
                currentActivity = 2
                updateImage(imageView)
                updateText(textView)
            }

            in 13..16 -> {
                currentActivity = 3
                updateImage(imageView)
                updateText(textView)
            }

            in 19..20 -> {
                currentActivity = 4
                updateImage(imageView)
                updateText(textView)
            }

            in 21..21 -> {
                currentActivity = 5
                updateImage(imageView)
                updateText(textView)
            }

            in 22..24 -> {
                currentActivity = 6
                updateImage(imageView)
                updateText(textView)
            }

            in 1..6 -> {
                currentActivity = 7
                updateImage(imageView)
                updateText(textView)
            }

            else -> {
                currentActivity = 8
                updateImage(imageView)
                updateText(textView)
            }
        }
    }
}