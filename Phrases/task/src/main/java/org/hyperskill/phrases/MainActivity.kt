package org.hyperskill.phrases

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.hyperskill.phrases.databinding.ActivityMainBinding

const val CHANNEL_ID = "org.hyperskill.phrases"

@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var myAdapter: RecyclerAdapter
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = (application as MyApplication).database
        createNotificationChannel()

        val reminderView = binding.reminderTextView
        val addButton = binding.addButton

        myAdapter = RecyclerAdapter(database.getPhraseDao().getAll().toMutableList(),
            getString(R.string.delete_button), database)

        setRecyclerView()

        binding.recyclerView.setRecyclerListener {
            if (database.getPhraseDao().getAll().isEmpty()) {
                binding.reminderTextView.text = getString(R.string.textView_empty)
                setAlarm(0)
            }
        }

        reminderView.setOnClickListener { handleReminder() }
        addButton.setOnClickListener { addPhrase() }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "PhrasesChannel"
            val descriptionText = "Channel for daily phrases"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setAlarm(timeInMillis: Long) {
        val am: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, Receiver::class.java)
        intent.putExtra("phrase", database.getPhraseDao().getRandom())
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if (database.getPhraseDao().getAll().isEmpty()) {
            try {
                am.cancel(pendingIntent)
            }catch (_: Exception) {}
        } else {
            am.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        }
    }

    private fun addPhrase() {
        val contentView = LayoutInflater.from(this).inflate(R.layout.add_phrase_layout, null, false)
        AlertDialog.Builder(this)
            .setTitle("Add phrase")
            .setView(contentView)
            .setPositiveButton("ADD") { _, _ ->
                val phrase = Phrase(contentView.findViewById<EditText>(R.id.editText).text.toString())
                database.getPhraseDao().insert(phrase)
                myAdapter.updateData(database.getPhraseDao().getAll())
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun handleReminder() {
        val isDatabaseEmpty = database.getPhraseDao().getAll().isEmpty()
        if (isDatabaseEmpty) {
            Toast.makeText(this, "Error : The database is Empty", Toast.LENGTH_SHORT).show()
            binding.reminderTextView.text = getString(R.string.textView_empty)
        } else {
            addReminder()
        }
    }

    private fun addReminder() {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)

            if (cal < Calendar.getInstance()) cal.add(Calendar.DAY_OF_YEAR, 1)

            binding.reminderTextView.text = StringBuilder().append("Reminder set for %02d:%02d".format(hour, minute)).toString()
            setAlarm(cal.timeInMillis)
        }
        TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
            .show()
    }

    private fun setRecyclerView() {
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = myAdapter
        }
    }
}
