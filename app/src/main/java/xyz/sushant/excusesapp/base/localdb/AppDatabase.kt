package xyz.sushant.excusesapp.base.localdb

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import xyz.sushant.excusesapp.BuildConfig
import xyz.sushant.excusesapp.base.ExcusesApp
import xyz.sushant.excusesapp.domain.entities.Excuse

@Database(entities = [Excuse::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun excuseDao() : ExcuseDao

    companion object {
        final val DB_NAME = "excuses_database"
        private var dbInstance: AppDatabase? = null

        fun getInstance(context: Context? = null): AppDatabase  {
            if(dbInstance == null && (context != null || ExcusesApp.getInstance() != null)) {
                (context ?: ExcusesApp.getInstance())?.applicationContext?.let {
                    dbInstance = Room.databaseBuilder(it, AppDatabase::class.java, DB_NAME)
                        .fallbackToDestructiveMigration()
                        .addCallback(object: RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                if(BuildConfig.DEBUG) {
                                    Log.i("APP_DATABASE","app excuses app database created")
                                }
                            }
                        }).build()
                }
            }
            return dbInstance!!
        }
    }
}