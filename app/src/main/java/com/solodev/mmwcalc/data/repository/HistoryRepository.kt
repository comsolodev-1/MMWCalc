package com.solodev.mmwcalc.data.repository

import com.solodev.mmwcalc.data.db.HistoryDao
import com.solodev.mmwcalc.data.db.HistoryEntity
import com.solodev.mmwcalc.domain.models.CalculationResult
import com.solodev.mmwcalc.domain.models.HistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val dao: HistoryDao
) {
    val allHistory: Flow<List<HistoryItem>> = dao.getAllHistory().map { entities ->
        entities.map { it.toHistoryItem() }
    }

    suspend fun save(result: CalculationResult) {
        if (result.isError) return
        dao.insert(
            HistoryEntity(
                topicId        = result.topicId,
                topicName      = result.topicName,
                solvedFor      = result.solvedFor,
                solvedForLabel = result.solvedForLabel,
                answer         = result.answerWithUnit,
                steps          = result.steps,
                inputs         = result.inputs,
                timestampMs    = System.currentTimeMillis()
            )
        )
    }

    suspend fun getById(id: Int): HistoryItem? =
        dao.getById(id)?.toHistoryItem()

    suspend fun deleteById(id: Int) = dao.deleteById(id)

    suspend fun clearAll() = dao.clearAll()

    private fun HistoryEntity.toHistoryItem() = HistoryItem(
        id             = id,
        topicId        = topicId,
        topicName      = topicName,
        solvedFor      = solvedFor,
        solvedForLabel = solvedForLabel,
        answer         = answer,
        steps          = steps,
        inputs         = inputs,
        timestampMs    = timestampMs
    )
}