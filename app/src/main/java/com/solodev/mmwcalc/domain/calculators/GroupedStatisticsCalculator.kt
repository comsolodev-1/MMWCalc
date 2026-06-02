package com.solodev.mmwcalc.domain.calculators

import com.solodev.mmwcalc.domain.models.CalculationResult
import com.solodev.mmwcalc.domain.models.StepItem
import com.solodev.mmwcalc.ui.components.ValidatedRow
import kotlin.math.floor
import kotlin.math.sqrt

object GroupedStatisticsCalculator {

    // ─────────────────────────────────────────────────────────────────────────
    // CENTRAL TENDENCY — GROUPED
    // ─────────────────────────────────────────────────────────────────────────

    fun solveCentralTendencyGrouped(rows: List<ValidatedRow>): CalculationResult {
        if (rows.size < 2)
            return groupedError("central_tendency_grouped",
                "Central Tendency (Grouped)", "Need at least 2 class intervals.")

        val steps = mutableListOf<StepItem>()
        val n     = rows.sumOf { it.freq }

        // ── Step 1: Build table ───────────────────────────────────────────────
        steps += StepItem(1, "Identify class intervals and boundaries",
            buildClassTable(rows))

        // ── MEAN ─────────────────────────────────────────────────────────────
        steps += StepItem(2, "─── GROUPED MEAN ───",
            "Formula: x̄ = Σ(f × xₘ) / n\n" +
                    "where xₘ = class midpoint = (LB + UB) / 2")

        val fxmRows = rows.map { r ->
            "  [${r.lower}–${r.upper}]: " +
                    "xₘ = (${r.lowerBoundary.fmtG()}+${r.upperBoundary.fmtG()})/2 = ${r.midpoint.fmtG()}, " +
                    "f = ${r.freq}, " +
                    "f×xₘ = ${r.freq}×${r.midpoint.fmtG()} = ${(r.freq * r.midpoint).fmtG()}"
        }.joinToString("\n")
        steps += StepItem(3, "Compute midpoint (xₘ) and f×xₘ for each class", fxmRows)

        val sumFxm = rows.sumOf { it.freq * it.midpoint }
        val fxmExpression = rows.joinToString(" + ") {
            "(${it.freq}×${it.midpoint.fmtG()})"
        }
        steps += StepItem(4, "Sum all f×xₘ values",
            "$fxmExpression\n= ${sumFxm.fmtG()}")

        steps += StepItem(5, "Divide by n (total frequency)",
            "n = ${rows.joinToString(" + ") { "${it.freq}" }} = $n\n" +
                    "x̄ = ${sumFxm.fmtG()} / $n\n" +
                    "x̄ = ${(sumFxm / n).fmtG()}",
            result = "x̄ = ${(sumFxm / n).fmtG()}")

        val mean = sumFxm / n

        // ── MEDIAN ───────────────────────────────────────────────────────────
        steps += StepItem(6, "─── GROUPED MEDIAN ───",
            "Formula: Md = LB + [(n/2 − cf) / f] × i\n" +
                    "where:\n" +
                    "  LB = lower boundary of median class\n" +
                    "  cf = cumulative frequency BEFORE median class\n" +
                    "  f  = frequency of median class\n" +
                    "  i  = class width")

        val halfN = n / 2.0
        steps += StepItem(7, "Compute n/2",
            "n/2 = $n / 2 = ${halfN.fmtG()}")

        // Build cumulative frequency table
        val cfTable = buildCumulativeFreqTable(rows)
        steps += StepItem(8, "Build cumulative frequency (cf) table", cfTable)

        // Find median class: first class where cf >= n/2
        var cfRunning = 0
        var medianClass: ValidatedRow? = null
        var cfBefore = 0
        for (r in rows) {
            cfBefore = cfRunning
            cfRunning += r.freq
            if (cfRunning >= halfN && medianClass == null) {
                medianClass = r
            }
        }

        if (medianClass == null)
            return groupedError("central_tendency_grouped",
                "Central Tendency (Grouped)", "Could not determine median class.")

        steps += StepItem(9, "Identify median class",
            "Median class = first class where cumulative frequency ≥ n/2 = ${halfN.fmtG()}\n" +
                    "Median class: [${medianClass.lower}–${medianClass.upper}]\n" +
                    "  LB = ${medianClass.lowerBoundary.fmtG()}\n" +
                    "  cf (before) = $cfBefore\n" +
                    "  f = ${medianClass.freq}\n" +
                    "  i = ${medianClass.classWidth.fmtG()}")

        val median = medianClass.lowerBoundary +
                ((halfN - cfBefore) / medianClass.freq) * medianClass.classWidth

        steps += StepItem(10, "Substitute into median formula",
            "Md = ${medianClass.lowerBoundary.fmtG()} + " +
                    "[(${halfN.fmtG()} − $cfBefore) / ${medianClass.freq}] × ${medianClass.classWidth.fmtG()}\n" +
                    "   = ${medianClass.lowerBoundary.fmtG()} + " +
                    "[${(halfN - cfBefore).fmtG()} / ${medianClass.freq}] × ${medianClass.classWidth.fmtG()}\n" +
                    "   = ${medianClass.lowerBoundary.fmtG()} + " +
                    "${((halfN - cfBefore) / medianClass.freq).fmtG()} × ${medianClass.classWidth.fmtG()}\n" +
                    "   = ${medianClass.lowerBoundary.fmtG()} + " +
                    "${(((halfN - cfBefore) / medianClass.freq) * medianClass.classWidth).fmtG()}",
            result = "Md = ${median.fmtG()}")

        // ── MODE ─────────────────────────────────────────────────────────────
        steps += StepItem(11, "─── GROUPED MODE ───",
            "Formula: Mo = LB + [d₁ / (d₁ + d₂)] × i\n" +
                    "where:\n" +
                    "  LB = lower boundary of modal class\n" +
                    "  d₁ = f_modal − f_before\n" +
                    "  d₂ = f_modal − f_after\n" +
                    "  i  = class width")

        val maxFreq    = rows.maxOf { it.freq }
        val modalIndex = rows.indexOfFirst { it.freq == maxFreq }
        val modalClass = rows[modalIndex]

        val fBefore = if (modalIndex > 0) rows[modalIndex - 1].freq else 0
        val fAfter  = if (modalIndex < rows.size - 1) rows[modalIndex + 1].freq else 0
        val d1 = modalClass.freq - fBefore
        val d2 = modalClass.freq - fAfter

        steps += StepItem(12, "Identify modal class (highest frequency)",
            "Modal class: [${modalClass.lower}–${modalClass.upper}]  f = ${modalClass.freq}\n" +
                    "  LB = ${modalClass.lowerBoundary.fmtG()}\n" +
                    "  f before = $fBefore  →  d₁ = ${modalClass.freq} − $fBefore = $d1\n" +
                    "  f after  = $fAfter   →  d₂ = ${modalClass.freq} − $fAfter = $d2\n" +
                    "  i = ${modalClass.classWidth.fmtG()}")

        val mode = if (d1 + d2 == 0) modalClass.lowerBoundary
        else modalClass.lowerBoundary + (d1.toDouble() / (d1 + d2)) * modalClass.classWidth

        steps += StepItem(13, "Substitute into mode formula",
            "Mo = ${modalClass.lowerBoundary.fmtG()} + [$d1 / ($d1 + $d2)] × ${modalClass.classWidth.fmtG()}\n" +
                    "   = ${modalClass.lowerBoundary.fmtG()} + [${d1.toDouble().fmtG()} / ${(d1 + d2).toDouble().fmtG()}] × ${modalClass.classWidth.fmtG()}\n" +
                    "   = ${modalClass.lowerBoundary.fmtG()} + ${(d1.toDouble() / (d1 + d2)).fmtG()} × ${modalClass.classWidth.fmtG()}\n" +
                    "   = ${modalClass.lowerBoundary.fmtG()} + ${((d1.toDouble() / (d1 + d2)) * modalClass.classWidth).fmtG()}",
            result = "Mo = ${mode.fmtG()}")

        val answer = "x̄ = ${mean.fmtG()} | Md = ${median.fmtG()} | Mo = ${mode.fmtG()}"

        return CalculationResult(
            topicId        = "central_tendency_grouped",
            topicName      = "Central Tendency (Grouped)",
            solvedFor      = "all",
            solvedForLabel = "Grouped Mean, Median, and Mode",
            answer         = answer,
            answerWithUnit = answer,
            steps          = steps,
            inputs         = mapOf("n" to "$n")
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DISPERSION — GROUPED
    // ─────────────────────────────────────────────────────────────────────────

    fun solveDispersionGrouped(rows: List<ValidatedRow>): CalculationResult {
        if (rows.size < 2)
            return groupedError("dispersion_grouped",
                "Measures of Dispersion (Grouped)", "Need at least 2 class intervals.")

        val steps = mutableListOf<StepItem>()
        val n     = rows.sumOf { it.freq }
        val mean  = rows.sumOf { it.freq * it.midpoint } / n

        steps += StepItem(1, "Identify class intervals",
            buildClassTable(rows))

        steps += StepItem(2, "Compute grouped mean first",
            "x̄ = Σ(f × xₘ) / n\n" +
                    "x̄ = ${rows.joinToString(" + ") { "(${it.freq}×${it.midpoint.fmtG()})" }} / $n\n" +
                    "x̄ = ${rows.sumOf { it.freq * it.midpoint }.fmtG()} / $n\n" +
                    "x̄ = ${mean.fmtG()}")

        // ── VARIANCE ─────────────────────────────────────────────────────────
        steps += StepItem(3, "─── GROUPED VARIANCE & SD ───",
            "Formula: s² = Σf(xₘ − x̄)² / (n − 1)")

        val devTable = rows.map { r ->
            val dev   = r.midpoint - mean
            val devSq = dev * dev
            val fDevSq = r.freq * devSq
            "  [${r.lower}–${r.upper}]: " +
                    "xₘ=${r.midpoint.fmtG()}, " +
                    "xₘ−x̄=${r.midpoint.fmtG()}−${mean.fmtG()}=${dev.fmtG()}, " +
                    "(xₘ−x̄)²=${devSq.fmtG()}, " +
                    "f(xₘ−x̄)²=${r.freq}×${devSq.fmtG()}=${fDevSq.fmtG()}"
        }.joinToString("\n")

        steps += StepItem(4, "Compute f(xₘ − x̄)² for each class", devTable)

        val sumFDevSq = rows.sumOf { it.freq * (it.midpoint - mean) * (it.midpoint - mean) }
        steps += StepItem(5, "Sum all f(xₘ − x̄)² values",
            rows.joinToString(" + ") {
                "${(it.freq * (it.midpoint - mean) * (it.midpoint - mean)).fmtG()}"
            } + "\n= ${sumFDevSq.fmtG()}")

        val variance = sumFDevSq / (n - 1)
        steps += StepItem(6, "Divide by (n − 1)",
            "s² = ${sumFDevSq.fmtG()} / ($n − 1)\n" +
                    "   = ${sumFDevSq.fmtG()} / ${n - 1}\n" +
                    "   = ${variance.fmtG()}",
            result = "s² = ${variance.fmtG()}")

        val sd = sqrt(variance)
        steps += StepItem(7, "Standard Deviation = √Variance",
            "s = √${variance.fmtG()}\n" +
                    "s = ${sd.fmtG()}",
            result = "s = ${sd.fmtG()}")

        val answer = "s² = ${variance.fmtG()} | s = ${sd.fmtG()}"

        return CalculationResult(
            topicId        = "dispersion_grouped",
            topicName      = "Measures of Dispersion (Grouped)",
            solvedFor      = "all",
            solvedForLabel = "Grouped Variance and Standard Deviation",
            answer         = answer,
            answerWithUnit = answer,
            steps          = steps,
            inputs         = mapOf("n" to "$n")
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RELATIVE POSITION — GROUPED
    // Qk, Dk, Pk using interpolation formula
    // ─────────────────────────────────────────────────────────────────────────

    fun solveRelativePositionGrouped(
        rows: List<ValidatedRow>,
        measureType: String,  // "quartile", "decile", "percentile"
        k: Int
    ): CalculationResult {
        if (rows.size < 2)
            return groupedError("relative_position_grouped",
                "Relative Position (Grouped)", "Need at least 2 class intervals.")

        val steps  = mutableListOf<StepItem>()
        val n      = rows.sumOf { it.freq }

        val (symbol, divisor, kRange) = when (measureType) {
            "quartile"   -> Triple("Q", 4,   1..3)
            "decile"     -> Triple("D", 10,  1..9)
            "percentile" -> Triple("P", 100, 1..99)
            else         -> return groupedError("relative_position_grouped",
                "Relative Position (Grouped)", "Unknown measure type.")
        }

        if (k !in kRange)
            return groupedError("relative_position_grouped",
                "Relative Position (Grouped)",
                "$symbol$k: k must be in range ${kRange.first}–${kRange.last}.")

        steps += StepItem(1, "Identify class intervals",
            buildClassTable(rows))

        val cfTable = buildCumulativeFreqTable(rows)
        steps += StepItem(2, "Build cumulative frequency (cf) table", cfTable)

        val target = k.toDouble() * n / divisor
        steps += StepItem(3, "Compute target position for $symbol$k",
            "Target = k × n / $divisor\n" +
                    "       = $k × $n / $divisor\n" +
                    "       = ${target.fmtG()}")

        // Find the class where cumulative frequency first reaches target
        var cfRunning = 0
        var targetClass: ValidatedRow? = null
        var cfBefore = 0
        for (r in rows) {
            cfBefore = cfRunning
            cfRunning += r.freq
            if (cfRunning >= target && targetClass == null) {
                targetClass = r
            }
        }

        if (targetClass == null)
            return groupedError("relative_position_grouped",
                "Relative Position (Grouped)",
                "Could not determine $symbol$k class.")

        steps += StepItem(4, "Identify $symbol$k class",
            "$symbol$k class = first class where cf ≥ ${target.fmtG()}\n" +
                    "$symbol$k class: [${targetClass.lower}–${targetClass.upper}]\n" +
                    "  LB = ${targetClass.lowerBoundary.fmtG()}\n" +
                    "  cf (before) = $cfBefore\n" +
                    "  f = ${targetClass.freq}\n" +
                    "  i = ${targetClass.classWidth.fmtG()}")

        steps += StepItem(5, "Write the formula",
            "$symbol$k = LB + [(kn/$divisor − cf) / f] × i")

        val result = targetClass.lowerBoundary +
                ((target - cfBefore) / targetClass.freq) * targetClass.classWidth

        steps += StepItem(6, "Substitute all values",
            "$symbol$k = ${targetClass.lowerBoundary.fmtG()} + " +
                    "[(${target.fmtG()} − $cfBefore) / ${targetClass.freq}] × ${targetClass.classWidth.fmtG()}\n" +
                    "     = ${targetClass.lowerBoundary.fmtG()} + " +
                    "[${(target - cfBefore).fmtG()} / ${targetClass.freq}] × ${targetClass.classWidth.fmtG()}\n" +
                    "     = ${targetClass.lowerBoundary.fmtG()} + " +
                    "${((target - cfBefore) / targetClass.freq).fmtG()} × ${targetClass.classWidth.fmtG()}\n" +
                    "     = ${targetClass.lowerBoundary.fmtG()} + " +
                    "${(((target - cfBefore) / targetClass.freq) * targetClass.classWidth).fmtG()}",
            result = "$symbol$k = ${result.fmtG()}")

        val answer = "$symbol$k = ${result.fmtG()}"

        return CalculationResult(
            topicId        = "relative_position_grouped",
            topicName      = "Relative Position (Grouped)",
            solvedFor      = "result",
            solvedForLabel = answer,
            answer         = answer,
            answerWithUnit = answer,
            steps          = steps,
            inputs         = mapOf("n" to "$n", "k" to "$k", "type" to measureType)
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Table builders
    // ─────────────────────────────────────────────────────────────────────────

    private fun buildClassTable(rows: List<ValidatedRow>): String {
        // Pre-compute all values as strings
        val intervals = rows.map { "[${it.lower.fmtG()}–${it.upper.fmtG()}]" }
        val lbs       = rows.map { it.lowerBoundary.fmtG() }
        val ubs       = rows.map { it.upperBoundary.fmtG() }
        val mids      = rows.map { it.midpoint.fmtG() }
        val freqs     = rows.map { "${it.freq}" }

        // Column widths = max of header or any value
        val w0 = maxOf(8,  intervals.maxOf { it.length })
        val w1 = maxOf(4,  lbs.maxOf  { it.length })
        val w2 = maxOf(4,  ubs.maxOf  { it.length })
        val w3 = maxOf(4,  mids.maxOf { it.length })
        val w4 = maxOf(4,  freqs.maxOf{ it.length })

        fun pad(s: String, w: Int) = s.padEnd(w)

        val header  = "${pad("Interval", w0)}  ${pad("LB", w1)}  ${pad("UB", w2)}  ${pad("xₘ", w3)}  ${pad("f", w4)}"
        val divider = "─".repeat(w0 + w1 + w2 + w3 + w4 + 8)
        val body    = rows.mapIndexed { i, r ->
            "${pad(intervals[i], w0)}  ${pad(lbs[i], w1)}  ${pad(ubs[i], w2)}  ${pad(mids[i], w3)}  ${pad(freqs[i], w4)}"
        }.joinToString("\n")
        val total   = "Total n = ${rows.sumOf { it.freq }}"

        return "$header\n$divider\n$body\n$divider\n$total"
    }

    private fun buildCumulativeFreqTable(rows: List<ValidatedRow>): String {
        val intervals = rows.map { "[${it.lower.fmtG()}–${it.upper.fmtG()}]" }
        val freqs     = rows.map { "${it.freq}" }
        var cf        = 0
        val cfs       = rows.map { cf += it.freq; "$cf" }

        val w0 = maxOf(8,  intervals.maxOf { it.length })
        val w1 = maxOf(2,  freqs.maxOf { it.length })
        val w2 = maxOf(2,  cfs.maxOf  { it.length })

        fun pad(s: String, w: Int) = s.padEnd(w)

        val header  = "${pad("Interval", w0)}  ${pad("f", w1)}  ${pad("cf", w2)}"
        val divider = "─".repeat(w0 + w1 + w2 + 4)
        val body    = rows.mapIndexed { i, _ ->
            "${pad(intervals[i], w0)}  ${pad(freqs[i], w1)}  ${pad(cfs[i], w2)}"
        }.joinToString("\n")

        return "$header\n$divider\n$body"
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    fun groupedError(topicId: String, topicName: String, message: String) =
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

    private fun Double.fmtG(): String =
        if (this == floor(this) && !this.isInfinite())
            this.toLong().toString()
        else "%.4f".format(this).trimEnd('0').trimEnd('.')
}