package org.hyperskill.phrases

import android.app.Application
import androidx.room.Room

class MyApplication: Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "phrases.db"
        ).allowMainThreadQueries().build()
    }
}