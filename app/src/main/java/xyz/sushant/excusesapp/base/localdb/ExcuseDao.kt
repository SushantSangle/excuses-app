package xyz.sushant.excusesapp.base.localdb

import androidx.lifecycle.LiveData
import androidx.room.*
import xyz.sushant.excusesapp.domain.entities.Excuse

@Dao
interface ExcuseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addExcuses(vararg excuse: Excuse)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addExcusesList(excuseList: List<Excuse>)

    @Update
    suspend fun updateExcuse(excuse: Excuse)

    @Delete
    suspend fun deleteExcuse(excuse: Excuse)

    @Query("DELETE FROM excuses")
    suspend fun deleteAll()

    @Query("SELECT * FROM excuses ORDER BY time_stamp DESC")
    suspend fun getAll() : List<Excuse>

    @Query("SELECT * FROM excuses ORDER BY time_stamp DESC")
    fun getAllAsLiveData() : LiveData<List<Excuse>>

    @Query("SELECT * FROM excuses WHERE category LIKE :category ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomExcuse(category: String = "%%") : Excuse?


    @Query("SELECT * FROM excuses WHERE category LIKE :category ORDER BY RANDOM() LIMIT 1")
    fun getRandomExcuseAsLiveData(category: String = "%%") : LiveData<Excuse?>
}