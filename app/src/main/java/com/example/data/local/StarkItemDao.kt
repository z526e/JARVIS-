package com.example.data.local

import androidx.room.*
import com.example.data.model.StarkItem
import kotlinx.coroutines.flow.Flow

@Dao
interface StarkItemDao {
    @Query("SELECT * FROM stark_items ORDER BY timestamp DESC")
    fun getAllItems(): Flow<List<StarkItem>>

    @Query("SELECT * FROM stark_items WHERE type = :type ORDER BY timestamp DESC")
    fun getItemsByType(type: String): Flow<List<StarkItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: StarkItem)

    @Update
    suspend fun updateItem(item: StarkItem)

    @Delete
    suspend fun deleteItem(item: StarkItem)

    @Query("DELETE FROM stark_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("DELETE FROM stark_items")
    suspend fun clearAll()
}
