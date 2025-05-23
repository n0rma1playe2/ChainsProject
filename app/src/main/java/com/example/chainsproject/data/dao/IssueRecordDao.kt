package com.example.chainsproject.data.dao

import androidx.room.*
import com.example.chainsproject.data.model.IssueRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface IssueRecordDao {
    @Query("SELECT * FROM issue_records WHERE productId = :productId ORDER BY createdAt DESC")
    fun getIssueRecordsByProductId(productId: Long): Flow<List<IssueRecord>>

    @Query("SELECT * FROM issue_records WHERE id = :id")
    fun getIssueRecordById(id: Long): Flow<IssueRecord?>

    @Insert
    suspend fun insertIssueRecord(issueRecord: IssueRecord): Long

    @Update
    suspend fun updateIssueRecord(issueRecord: IssueRecord)

    @Delete
    suspend fun deleteIssueRecord(issueRecord: IssueRecord)

    @Query("DELETE FROM issue_records WHERE productId = :productId")
    suspend fun deleteIssueRecordsByProductId(productId: Long)

    @Query("SELECT * FROM issue_records WHERE status = :status ORDER BY createdAt DESC")
    fun getIssueRecordsByStatus(status: String): Flow<List<IssueRecord>>

    @Query("SELECT * FROM issue_records WHERE severity = :severity ORDER BY createdAt DESC")
    fun getIssueRecordsBySeverity(severity: String): Flow<List<IssueRecord>>
} 