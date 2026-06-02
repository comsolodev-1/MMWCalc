package com.solodev.mmwcalc.domain.models

// ─── Topic categories ─────────────────────────────────────────────────────────

enum class TopicCategory(val label: String) {
    SEQUENCES("Sequences"),
    STATISTICS("Statistics"),
    NUMBER_THEORY("Number Theory"),
    FINANCE("Finance")
}

// ─── Topic definition ─────────────────────────────────────────────────────────

data class Topic(
    val id: String,
    val name: String,
    val category: TopicCategory,
    val description: String,
    val variables: List<String>,
    val variableLabels: Map<String, String>,
    val variableHints: Map<String, String> = emptyMap(),
    val hasDatasetInput: Boolean = false,
    val formulaPicker: Boolean = false
)

// ─── One step in the solution ─────────────────────────────────────────────────

data class StepItem(
    val stepNumber: Int,
    val title: String,
    val expression: String,
    val result: String? = null
)

// ─── Full calculation result ──────────────────────────────────────────────────

data class CalculationResult(
    val topicId: String,
    val topicName: String,
    val solvedFor: String,
    val solvedForLabel: String,
    val answer: String,
    val answerWithUnit: String = answer,
    val steps: List<StepItem>,
    val inputs: Map<String, String>,
    val isError: Boolean = false,
    val errorMessage: String = ""
)

// ─── History item ─────────────────────────────────────────────────────────────

data class HistoryItem(
    val id: Int = 0,
    val topicId: String,
    val topicName: String,
    val solvedFor: String,
    val solvedForLabel: String,
    val answer: String,
    val steps: List<StepItem>,
    val inputs: Map<String, String>,
    val timestampMs: Long = System.currentTimeMillis()
)

// ─── For grouped frequency table input ───────────────────────────────────────

data class FrequencyClass(
    val lowerLimit: Double,
    val upperLimit: Double,
    val frequency: Int
) {
    val lowerBoundary: Double get() = lowerLimit - 0.5
    val upperBoundary: Double get() = upperLimit + 0.5
    val midpoint: Double get() = (lowerBoundary + upperBoundary) / 2.0
    val classWidth: Double get() = upperBoundary - lowerBoundary
}