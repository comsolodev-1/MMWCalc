package com.solodev.mmwcalc.domain.calculators

import com.solodev.mmwcalc.domain.models.CalculationResult
import com.solodev.mmwcalc.domain.models.StepItem
import kotlin.math.floor
import kotlin.math.sqrt

object StatisticsCalculator {

    // ─────────────────────────────────────────────────────────────────────────
    // CENTRAL TENDENCY — UNGROUPED
    // ─────────────────────────────────────────────────────────────────────────

    fun solveCentralTendencyUngrouped(data: List<Double>): CalculationResult {
        if (data.isEmpty())
            return statError("central_tendency_ungrouped",
                "Central Tendency (Ungrouped)", "Please enter at least one value.")
        if (data.size < 2)
            return statError("central_tendency_ungrouped",
                "Central Tendency (Ungrouped)", "Please enter at least 2 values.")
        // Guard: check for non-finite values
        if (data.any { !it.isFinite() })
            return statError("central_tendency_ungrouped",
                "Central Tendency (Ungrouped)", "Dataset contains invalid values.")

        val steps = mutableListOf<StepItem>()
        val n = data.size

        // ── Step 1: List values ───────────────────────────────────────────────
        steps += StepItem(1, "Identify all values",
            "Dataset: ${data.joinToString(", ") { it.fmt() }}\n" +
                    "n = $n")

        // ── MEAN ─────────────────────────────────────────────────────────────
        steps += StepItem(2, "─── MEAN ───", "Formula: x̄ = Σx / n")

        val sumExpression = data.joinToString(" + ") { it.fmt() }
        val sum = data.sum()
        steps += StepItem(3, "Sum all values (Σx)",
            "$sumExpression\n= ${sum.fmt()}")

        steps += StepItem(4, "Divide by n",
            "${sum.fmt()} ÷ $n = ${(sum / n).fmt()}",
            result = "x̄ = ${(sum / n).fmt()}")

        val mean = sum / n

        // ── MEDIAN ───────────────────────────────────────────────────────────
        steps += StepItem(5, "─── MEDIAN ───",
            "Formula: arrange in order, find middle value")

        val sorted = data.sorted()
        steps += StepItem(6, "Sort values in ascending order",
            sorted.joinToString(", ") { it.fmt() })

        val median: Double
        if (n % 2 == 1) {
            val midIndex = n / 2
            median = sorted[midIndex]
            steps += StepItem(7, "n = $n (odd) → middle position = (n+1)/2",
                "(${n}+1)/2 = ${(n + 1) / 2}\n" +
                        "Position ${(n + 1) / 2} in sorted list = ${sorted[midIndex].fmt()}",
                result = "Median = ${median.fmt()}")
        } else {
            val mid1 = n / 2 - 1
            val mid2 = n / 2
            median = (sorted[mid1] + sorted[mid2]) / 2.0
            steps += StepItem(7, "n = $n (even) → average of two middle values",
                "Positions ${mid1 + 1} and ${mid2 + 1} in sorted list:\n" +
                        "${sorted[mid1].fmt()} and ${sorted[mid2].fmt()}\n" +
                        "Median = (${sorted[mid1].fmt()} + ${sorted[mid2].fmt()}) / 2\n" +
                        "       = ${(sorted[mid1] + sorted[mid2]).fmt()} / 2",
                result = "Median = ${median.fmt()}")
        }

        // ── MODE ─────────────────────────────────────────────────────────────
        steps += StepItem(8, "─── MODE ───",
            "The value(s) that appear most frequently")

        val freqMap = data.groupBy { it }.mapValues { it.value.size }
        val freqTable = freqMap.entries
            .sortedBy { it.key }
            .joinToString("\n") { (v, f) -> "  ${v.fmt()} → appears $f time(s)" }
        steps += StepItem(9, "Count frequency of each value", freqTable)

        val maxFreq = freqMap.values.max()
        val modes   = freqMap.entries
            .filter { it.value == maxFreq }
            .map { it.key }
            .sorted()

        val modeResult = when {
            maxFreq == 1 ->
                "No mode — all values appear only once"
            modes.size == data.distinct().size ->
                "No mode — all values appear equally often"
            else ->
                "Mode = ${modes.joinToString(", ") { it.fmt() }} " +
                        "(each appears $maxFreq times)"
        }
        steps += StepItem(10, "Identify highest frequency",
            "Highest frequency = $maxFreq",
            result = modeResult)

        val modeDisplay = when {
            maxFreq == 1 -> "No mode"
            modes.size == data.distinct().size -> "No mode"
            else -> modes.joinToString(", ") { it.fmt() }
        }

        val answer = "Mean = ${mean.fmt()} | Median = ${median.fmt()} | Mode = $modeDisplay"

        return CalculationResult(
            topicId        = "central_tendency_ungrouped",
            topicName      = "Central Tendency (Ungrouped)",
            solvedFor      = "all",
            solvedForLabel = "Mean, Median, and Mode",
            answer         = answer,
            answerWithUnit = answer,
            steps          = steps,
            inputs         = mapOf("dataset" to data.joinToString(", ") { it.fmt() })
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MEASURES OF DISPERSION — UNGROUPED
    // ─────────────────────────────────────────────────────────────────────────

    fun solveDispersionUngrouped(data: List<Double>): CalculationResult {
        if (data.size < 2)
            return statError("dispersion_ungrouped",
                "Measures of Dispersion (Ungrouped)",
                "Please enter at least 2 values.")
        if (data.any { !it.isFinite() })
            return statError("dispersion_ungrouped",
                "Measures of Dispersion (Ungrouped)", "Dataset contains invalid values.")
        // Guard: variance requires at least 2 distinct values
        if (data.distinct().size < 2)
            return statError("dispersion_ungrouped",
                "Measures of Dispersion (Ungrouped)",
                "All values are identical — variance and SD are 0. Range = 0, IQR = 0.")

        val steps = mutableListOf<StepItem>()
        val n     = data.size
        val sorted = data.sorted()

        steps += StepItem(1, "Identify all values",
            "Dataset: ${data.joinToString(", ") { it.fmt() }}\n" +
                    "n = $n")

        // ── RANGE ─────────────────────────────────────────────────────────────
        steps += StepItem(2, "─── RANGE ───", "Formula: Range = Max − Min")
        val max = sorted.last()
        val min = sorted.first()
        steps += StepItem(3, "Identify Max and Min",
            "Sorted: ${sorted.joinToString(", ") { it.fmt() }}\n" +
                    "Max = ${max.fmt()},  Min = ${min.fmt()}")
        val range = max - min
        steps += StepItem(4, "Subtract",
            "${max.fmt()} − ${min.fmt()} = ${range.fmt()}",
            result = "Range = ${range.fmt()}")

        // ── MEAN (needed for variance) ────────────────────────────────────────
        steps += StepItem(5, "─── VARIANCE & STANDARD DEVIATION ───",
            "Formula: s² = Σ(x − x̄)² / (n − 1)")
        val mean = data.sum() / n
        steps += StepItem(6, "Compute mean first",
            "x̄ = (${data.joinToString(" + ") { it.fmt() }}) / $n\n" +
                    "x̄ = ${data.sum().fmt()} / $n\n" +
                    "x̄ = ${mean.fmt()}")

        // ── DEVIATIONS ────────────────────────────────────────────────────────
        val deviations    = data.map { it - mean }
        val deviationsSq  = deviations.map { it * it }

        val devTable = data.mapIndexed { i, x ->
            "  x=${x.fmt()}:  x−x̄ = ${x.fmt()}−${mean.fmt()} = ${deviations[i].fmt()}" +
                    ",  (x−x̄)² = ${deviations[i].fmt()}² = ${deviationsSq[i].fmt()}"
        }.joinToString("\n")

        steps += StepItem(7, "Compute each deviation (x − x̄) and square it", devTable)

        val sumSq = deviationsSq.sum()
        steps += StepItem(8, "Sum all (x − x̄)²",
            deviationsSq.joinToString(" + ") { it.fmt() } +
                    "\n= ${sumSq.fmt()}")

        val variance = sumSq / (n - 1)
        steps += StepItem(9, "Divide by (n − 1)",
            "${sumSq.fmt()} / (${n} − 1)\n" +
                    "= ${sumSq.fmt()} / ${n - 1}\n" +
                    "= ${variance.fmt()}",
            result = "s² = ${variance.fmt()}")

        val sd = sqrt(variance)
        steps += StepItem(10, "Standard Deviation = √Variance",
            "s = √${variance.fmt()}\n" +
                    "s = ${sd.fmt()}",
            result = "s = ${sd.fmt()}")

        // ── IQR ───────────────────────────────────────────────────────────────
        steps += StepItem(11, "─── INTERQUARTILE RANGE (IQR) ───",
            "Formula: IQR = Q3 − Q1")

        val q1 = computeQuartile(sorted, 1)
        val q3 = computeQuartile(sorted, 3)

        steps += StepItem(12, "Find Q1 (25th percentile)",
            computeQuartileSteps(sorted, 1))
        steps += StepItem(13, "Find Q3 (75th percentile)",
            computeQuartileSteps(sorted, 3))

        val iqr = q3 - q1
        steps += StepItem(14, "Compute IQR",
            "IQR = Q3 − Q1\n" +
                    "    = ${q3.fmt()} − ${q1.fmt()}\n" +
                    "    = ${iqr.fmt()}",
            result = "IQR = ${iqr.fmt()}")

        val answer = "Range=${range.fmt()} | s²=${variance.fmt()} | s=${sd.fmt()} | IQR=${iqr.fmt()}"

        return CalculationResult(
            topicId        = "dispersion_ungrouped",
            topicName      = "Measures of Dispersion (Ungrouped)",
            solvedFor      = "all",
            solvedForLabel = "Range, Variance, SD, and IQR",
            answer         = answer,
            answerWithUnit = answer,
            steps          = steps,
            inputs         = mapOf("dataset" to data.joinToString(", ") { it.fmt() })
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MEASURES OF RELATIVE POSITION — UNGROUPED
    // ─────────────────────────────────────────────────────────────────────────

    fun solveRelativePositionUngrouped(
        data: List<Double>,
        targetK: Int,           // k for Qk, Dk, Pk
        measureType: String,    // "quartile", "decile", "percentile", "zscore"
        specificValue: Double? = null  // for z-score: the raw score x
    ): CalculationResult {
        if (data.size < 2)
            return statError("relative_position_ungrouped",
                "Relative Position (Ungrouped)", "Please enter at least 2 values.")
        if (data.any { !it.isFinite() })
            return statError("relative_position_ungrouped",
                "Relative Position (Ungrouped)", "Dataset contains invalid values.")

        val steps  = mutableListOf<StepItem>()
        val n      = data.size
        val sorted = data.sorted()

        steps += StepItem(1, "Identify all values",
            "Dataset: ${data.joinToString(", ") { it.fmt() }}\n" +
                    "n = $n")

        steps += StepItem(2, "Sort values in ascending order",
            sorted.joinToString(", ") { it.fmt() })

        return when (measureType) {

            "quartile" -> {
                if (targetK !in 1..3)
                    return statError("relative_position_ungrouped",
                        "Relative Position (Ungrouped)", "Quartile k must be 1, 2, or 3.")
                val pos = targetK * (n + 1) / 4.0
                steps += StepItem(3, "Compute position of Q$targetK",
                    "Position = k(n+1)/4\n" +
                            "         = $targetK × (${n}+1) / 4\n" +
                            "         = $targetK × ${n + 1} / 4\n" +
                            "         = ${pos.fmt()}")
                val value = interpolate(sorted, pos)
                steps += StepItem(4, "Locate value at position ${pos.fmt()}",
                    interpolateSteps(sorted, pos),
                    result = "Q$targetK = ${value.fmt()}")
                buildResult("relative_position_ungrouped",
                    "Relative Position (Ungrouped)",
                    "Q$targetK = ${value.fmt()}", steps, data)
            }

            "decile" -> {
                if (targetK !in 1..9)
                    return statError("relative_position_ungrouped",
                        "Relative Position (Ungrouped)", "Decile k must be between 1 and 9.")
                val pos = targetK * (n + 1) / 10.0
                steps += StepItem(3, "Compute position of D$targetK",
                    "Position = k(n+1)/10\n" +
                            "         = $targetK × (${n}+1) / 10\n" +
                            "         = $targetK × ${n + 1} / 10\n" +
                            "         = ${pos.fmt()}")
                val value = interpolate(sorted, pos)
                steps += StepItem(4, "Locate value at position ${pos.fmt()}",
                    interpolateSteps(sorted, pos),
                    result = "D$targetK = ${value.fmt()}")
                buildResult("relative_position_ungrouped",
                    "Relative Position (Ungrouped)",
                    "D$targetK = ${value.fmt()}", steps, data)
            }

            "percentile" -> {
                if (targetK !in 1..99)
                    return statError("relative_position_ungrouped",
                        "Relative Position (Ungrouped)", "Percentile k must be between 1 and 99.")
                val pos = targetK * (n + 1) / 100.0
                steps += StepItem(3, "Compute position of P$targetK",
                    "Position = k(n+1)/100\n" +
                            "         = $targetK × (${n}+1) / 100\n" +
                            "         = $targetK × ${n + 1} / 100\n" +
                            "         = ${pos.fmt()}")
                val value = interpolate(sorted, pos)
                steps += StepItem(4, "Locate value at position ${pos.fmt()}",
                    interpolateSteps(sorted, pos),
                    result = "P$targetK = ${value.fmt()}")
                buildResult("relative_position_ungrouped",
                    "Relative Position (Ungrouped)",
                    "P$targetK = ${value.fmt()}", steps, data)
            }

            "zscore" -> {
                val x = specificValue
                    ?: return statError("relative_position_ungrouped",
                        "Relative Position (Ungrouped)", "Please enter a value for x.")
                val mean = data.sum() / n
                val variance = data.sumOf { (it - mean) * (it - mean) } / (n - 1)
                val sd = sqrt(variance)

                steps += StepItem(3, "Compute mean",
                    "x̄ = (${data.joinToString(" + ") { it.fmt() }}) / $n\n" +
                            "x̄ = ${data.sum().fmt()} / $n = ${mean.fmt()}")
                steps += StepItem(4, "Compute standard deviation",
                    "s = ${sd.fmt()}  (full steps shown in Dispersion calculator)")
                steps += StepItem(5, "Apply Z-score formula",
                    "z = (x − x̄) / s\n" +
                            "z = (${x.fmt()} − ${mean.fmt()}) / ${sd.fmt()}\n" +
                            "z = ${(x - mean).fmt()} / ${sd.fmt()}",
                    result = "z = ${((x - mean) / sd).fmt()}")

                buildResult("relative_position_ungrouped",
                    "Relative Position (Ungrouped)",
                    "z = ${((x - mean) / sd).fmt()}", steps, data)
            }

            else -> statError("relative_position_ungrouped",
                "Relative Position (Ungrouped)", "Unknown measure type.")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Quartile helper
    // ─────────────────────────────────────────────────────────────────────────

    private fun computeQuartile(sorted: List<Double>, k: Int): Double {
        val n   = sorted.size
        val pos = k * (n + 1) / 4.0
        return interpolate(sorted, pos)
    }

    private fun computeQuartileSteps(sorted: List<Double>, k: Int): String {
        val n   = sorted.size
        val pos = k * (n + 1) / 4.0
        return "Position = $k(n+1)/4 = $k×${n+1}/4 = ${pos.fmt()}\n" +
                interpolateSteps(sorted, pos) +
                "\nQ$k = ${interpolate(sorted, pos).fmt()}"
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Interpolation helper
    // ─────────────────────────────────────────────────────────────────────────

    private fun interpolate(sorted: List<Double>, position: Double): Double {
        val n = sorted.size
        if (position <= 1) return sorted.first()
        if (position >= n) return sorted.last()
        val lower = floor(position).toInt()
        val upper = lower + 1
        val frac  = position - lower
        return sorted[lower - 1] + frac * (sorted[upper - 1] - sorted[lower - 1])
    }

    private fun interpolateSteps(sorted: List<Double>, position: Double): String {
        val n = sorted.size
        if (position <= 1) return "Position ≤ 1 → value = ${sorted.first().fmt()}"
        if (position >= n) return "Position ≥ n → value = ${sorted.last().fmt()}"
        val lower = floor(position).toInt()
        val upper = lower + 1
        val frac  = position - lower
        val lv    = sorted[lower - 1]
        val uv    = sorted[upper - 1]
        val result = lv + frac * (uv - lv)
        return if (frac == 0.0) {
            "Position ${position.fmt()} is exact → value at position $lower = ${lv.fmt()}"
        } else {
            "Position ${position.fmt()} falls between positions $lower and $upper\n" +
                    "Value at position $lower = ${lv.fmt()}\n" +
                    "Value at position $upper = ${uv.fmt()}\n" +
                    "Interpolate: ${lv.fmt()} + ${frac.fmt()} × (${uv.fmt()} − ${lv.fmt()})\n" +
                    "           = ${lv.fmt()} + ${frac.fmt()} × ${(uv - lv).fmt()}\n" +
                    "           = ${lv.fmt()} + ${(frac * (uv - lv)).fmt()}\n" +
                    "           = ${result.fmt()}"
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun buildResult(
        topicId: String, topicName: String,
        answer: String, steps: List<StepItem>,
        data: List<Double>
    ) = CalculationResult(
        topicId        = topicId,
        topicName      = topicName,
        solvedFor      = "result",
        solvedForLabel = answer,
        answer         = answer,
        answerWithUnit = answer,
        steps          = steps,
        inputs         = mapOf("dataset" to data.joinToString(", ") { it.fmt() })
    )

    fun statError(topicId: String, topicName: String, message: String) =
        CalculationResult(
            topicId        = topicId,
            topicName      = topicName,
            solvedFor      = "",
            solvedForLabel = "",
            answer         = "",
            steps          = emptyList(),
            inputs         = emptyMap(),
            isError        = true,
            errorMessage   = message
        )

    private fun Double.fmt(): String =
        if (this == floor(this) && !this.isInfinite())
            this.toLong().toString()
        else "%.4f".format(this).trimEnd('0').trimEnd('.')
}