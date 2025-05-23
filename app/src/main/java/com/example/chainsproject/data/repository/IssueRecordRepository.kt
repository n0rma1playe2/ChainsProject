package com.example.chainsproject.data.repository

import com.example.chainsproject.data.dao.IssueRecordDao
import com.example.chainsproject.data.model.IssueRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IssueRecordRepository @Inject constructor(
    private val issueRecordDao: IssueRecordDao
) {
    fun getIssueRecordsByProductId(productId: Long): Flow<List<IssueRecord>> =
        issueRecordDao.getIssueRecordsByProductId(productId)

    fun getIssueRecordById(id: Long): Flow<IssueRecord?> =
        issueRecordDao.getIssueRecordById(id)

    suspend fun insertIssueRecord(issueRecord: IssueRecord): Long =
        issueRecordDao.insertIssueRecord(issueRecord)

    suspend fun updateIssueRecord(issueRecord: IssueRecord) =
        issueRecordDao.updateIssueRecord(issueRecord)

    suspend fun deleteIssueRecord(issueRecord: IssueRecord) =
        issueRecordDao.deleteIssueRecord(issueRecord)

    suspend fun deleteIssueRecordsByProductId(productId: Long) =
        issueRecordDao.deleteIssueRecordsByProductId(productId)

    fun getIssueRecordsByStatus(status: String): Flow<List<IssueRecord>> =
        issueRecordDao.getIssueRecordsByStatus(status)

    fun getIssueRecordsBySeverity(severity: String): Flow<List<IssueRecord>> =
        issueRecordDao.getIssueRecordsBySeverity(severity)
}
 