package com.ksajja.newone.room

import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

/**
 * Created by ksajja on 2/25/18.
 */
//@Database(entities = {Task.class, List.class}, version = 1)
//@TypeConverters({DateConverter.class})
abstract class newoneDatabase : RoomDatabase() {

    abstract val taskDao: TaskDao

    abstract val listDao: ListDao

    companion object {
        private val DB_NAME = "newoneDatabase.db"
        @Volatile
        private var instance: newoneDatabase? = null

        @Synchronized
        fun getInstance(context: Context): newoneDatabase? {
            if (instance == null) {
                instance = create(context)
            }
            return instance
        }

        private fun create(context: Context): newoneDatabase {
            return Room.databaseBuilder(
                    context,
                    newoneDatabase::class.java,
                    DB_NAME).build()
        }
    }
}
