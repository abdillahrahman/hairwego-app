package com.app.hairwego.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = [FaceScanEntity::class, RecommendationEntity::class], version = 1, exportSchema = false)

abstract class HairWeGoDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: HairWeGoDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context, applicationScope: CoroutineScope): HairWeGoDatabase {
            if (INSTANCE == null) {
                synchronized(HairWeGoDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        HairWeGoDatabase::class.java, "hairwego_database"
                    )
                        .fallbackToDestructiveMigration()
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                INSTANCE?.let { database ->
                                    applicationScope.launch {

                                    }
                                }
                            }
                        })
                        .build()
                }
            }
            return INSTANCE as HairWeGoDatabase
        }
    }


}