package com.solodev.mmwcalc.domain.calculators

import com.solodev.mmwcalc.domain.models.CalculationResult
import com.solodev.mmwcalc.domain.models.StepItem
import kotlin.math.*

object AdvancedStatisticsCalculator {

    // ─────────────────────────────────────────────────────────────────────────
    // NORMAL DISTRIBUTION
    // z = (x - μ) / σ  — solve for any one blank variable
    // Plus area under curve using error function approximation
    // ─────────────────────────────────────────────────────────────────────────

    fun solveNormalDistribution(inputs: Map<String, String>): CalculationResult {
        val mainVars   = listOf("z", "x", "mean", "sd")
        val mainBlanks = mainVars.filter { inputs[it].isNullOrBlank() }
        val get        = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }

        if (mainBlanks.size > 1)
            return ndError("Leave exactly one field blank to solve for it.")
        if (mainBlanks.isEmpty())
            return ndError("All fields are filled — nothing to solve. Leave one blank.")

        val steps   = mutableListOf<StepItem>()
        val unknown = mainBlanks.first()
        val answer: Double

        steps += StepItem(1, "Write the Z-score formula",
            "z = (x − μ) / σ")

        // Guard: SD must be positive
        val sdVal = get("sd")
        if (sdVal != null && sdVal <= 0)
            return ndError("Standard deviation σ must be greater than 0.")

        // Guard: z cannot be 0 when solving for sd
        if (unknown == "sd") {
            val zVal = get("z")
            if (zVal != null && zVal == 0.0)
                return ndError("Cannot solve for σ when z = 0 (division by zero).")
        }

        when (unknown) {
            "z" -> {
                val x    = get("x")!!
                val mean = get("mean")!!
                val sd   = get("sd")!!
                if (sd <= 0) return ndError("Standard deviation must be greater than 0.")
                steps += StepItem(2, "Substitute known values",
                    "z = (${x.fmtN()} − ${mean.fmtN()}) / ${sd.fmtN()}")
                val numerator = x - mean
                steps += StepItem(3, "Compute numerator (x − μ)",
                    "${x.fmtN()} − ${mean.fmtN()} = ${numerator.fmtN()}")
                val z = numerator / sd
                steps += StepItem(4, "Divide by σ",
                    "${numerator.fmtN()} ÷ ${sd.fmtN()} = ${z.fmtN()}",
                    result = "z = ${z.fmtN()}")
                answer = z
                appendAreaSteps(steps, z)
            }
            "x" -> {
                val z    = get("z")!!
                val mean = get("mean")!!
                val sd   = get("sd")!!
                if (sd <= 0) return ndError("Standard deviation must be greater than 0.")
                steps += StepItem(2, "Rearrange for x",
                    "x = μ + (z × σ)")
                steps += StepItem(3, "Substitute known values",
                    "x = ${mean.fmtN()} + (${z.fmtN()} × ${sd.fmtN()})")
                val product = z * sd
                steps += StepItem(4, "Compute z × σ",
                    "${z.fmtN()} × ${sd.fmtN()} = ${product.fmtN()}")
                val x = mean + product
                steps += StepItem(5, "Add μ",
                    "${mean.fmtN()} + ${product.fmtN()} = ${x.fmtN()}",
                    result = "x = ${x.fmtN()}")
                answer = x
                appendAreaSteps(steps, z)
            }
            "mean" -> {
                val z  = get("z")!!
                val x  = get("x")!!
                val sd = get("sd")!!
                if (sd <= 0) return ndError("Standard deviation must be greater than 0.")
                steps += StepItem(2, "Rearrange for μ",
                    "μ = x − (z × σ)")
                steps += StepItem(3, "Substitute known values",
                    "μ = ${x.fmtN()} − (${z.fmtN()} × ${sd.fmtN()})")
                val product = z * sd
                steps += StepItem(4, "Compute z × σ",
                    "${z.fmtN()} × ${sd.fmtN()} = ${product.fmtN()}")
                val mean = x - product
                steps += StepItem(5, "Subtract from x",
                    "${x.fmtN()} − ${product.fmtN()} = ${mean.fmtN()}",
                    result = "μ = ${mean.fmtN()}")
                answer = mean
                appendAreaSteps(steps, z)
            }
            "sd" -> {
                val z    = get("z")!!
                val x    = get("x")!!
                val mean = get("mean")!!
                steps += StepItem(2, "Rearrange for σ",
                    "σ = (x − μ) / z")
                if (z == 0.0) return ndError("z cannot be 0 when solving for σ.")
                steps += StepItem(3, "Substitute known values",
                    "σ = (${x.fmtN()} − ${mean.fmtN()}) / ${z.fmtN()}")
                val numerator = x - mean
                steps += StepItem(4, "Compute numerator (x − μ)",
                    "${x.fmtN()} − ${mean.fmtN()} = ${numerator.fmtN()}")
                val sd = numerator / z
                if (sd <= 0) return ndError("Result gives σ ≤ 0, which is invalid.")
                steps += StepItem(5, "Divide by z",
                    "${numerator.fmtN()} ÷ ${z.fmtN()} = ${sd.fmtN()}",
                    result = "σ = ${sd.fmtN()}")
                answer = sd
                appendAreaSteps(steps, z)
            }
            else -> return ndError("Unknown variable.")
        }

        val solvedLabel = when (unknown) {
            "z"    -> "z — Z-score"
            "x"    -> "x — Raw score"
            "mean" -> "μ — Mean"
            "sd"   -> "σ — Standard deviation"
            else   -> unknown
        }

        return CalculationResult(
            topicId        = "normal_distribution",
            topicName      = "Normal Distribution",
            solvedFor      = unknown,
            solvedForLabel = solvedLabel,
            answer         = answer.fmtN(),
            answerWithUnit = answer.fmtN(),
            steps          = steps,
            inputs = inputs.toMutableMap().also {
                if (unknown == "z") it["z"] = answer.fmtN()
            },
        )
    }

    private fun appendAreaSteps(steps: MutableList<StepItem>, z: Double) {
        val stepNum  = steps.size + 1
        val areaLeft = cumulativeNormal(z)
        val areaRight = 1.0 - areaLeft
        steps += StepItem(stepNum, "─── Area Under the Curve ───",
            "Using the standard normal distribution")
        steps += StepItem(stepNum + 1, "Area to the LEFT of z = ${z.fmtN()}",
            "P(Z < ${z.fmtN()}) = ${areaLeft.fmtArea()}",
            result = "P(Z < ${z.fmtN()}) = ${areaLeft.fmtArea()}")
        steps += StepItem(stepNum + 2, "Area to the RIGHT of z = ${z.fmtN()}",
            "P(Z > ${z.fmtN()}) = 1 − ${areaLeft.fmtArea()} = ${areaRight.fmtArea()}",
            result = "P(Z > ${z.fmtN()}) = ${areaRight.fmtArea()}")
        steps += StepItem(stepNum + 3, "Area BETWEEN z = 0 and z = ${z.fmtN()}",
            "P(0 < Z < ${z.fmtN()}) = |${areaLeft.fmtArea()} − 0.5| = ${abs(areaLeft - 0.5).fmtArea()}",
            result = "P(0 < Z < ${z.fmtN()}) = ${abs(areaLeft - 0.5).fmtArea()}")
    }

    // Error function approximation for Φ(z)
    private fun cumulativeNormal(z: Double): Double {
        // Abramowitz and Stegun approximation — accurate to 7 decimal places
        val t = 1.0 / (1.0 + 0.2316419 * abs(z))
        val poly = t * (0.319381530
                + t * (-0.356563782
                + t * (1.781477937
                + t * (-1.821255978
                + t * 1.330274429))))
        val pdf  = exp(-0.5 * z * z) / sqrt(2 * PI)
        val area = 1.0 - pdf * poly
        return if (z >= 0) area else 1.0 - area
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LINEAR REGRESSION
    // ─────────────────────────────────────────────────────────────────────────

    fun solveLinearRegression(
        xData: List<Double>,
        yData: List<Double>,
        predictX: String,
        predictY: String,
        hasPrediction: Boolean = false
    ): CalculationResult {
        if (xData.size != yData.size)
            return lrError("X and Y datasets must have the same number of values.")
        if (xData.size < 2)
            return lrError("Please enter at least 2 data points.")
        // Guard: all x values identical = no slope possible
        if (xData.distinct().size == 1)
            return lrError("All X values are identical — slope is undefined.")
        // Guard: check for non-finite values
        if (xData.any { !it.isFinite() } || yData.any { !it.isFinite() })
            return lrError("Dataset contains invalid values.")

        val steps = mutableListOf<StepItem>()
        val n     = xData.size

        steps += StepItem(1, "Identify data points",
            "n = $n\n" +
                    "X: ${xData.joinToString(", ") { it.fmtN() }}\n" +
                    "Y: ${yData.joinToString(", ") { it.fmtN() }}")

        // ── Build computation table ───────────────────────────────────────────
        val xy   = xData.zip(yData).map { (x, y) -> x * y }
        val xSq  = xData.map { it * it }
        val ySq  = yData.map { it * it }

        val tableHeader = padC("x", 8) + padC("y", 8) + padC("xy", 10) + padC("x²", 10) + padC("y²", 10)
        val tableDivider = "─".repeat(46)
        val tableBody = (0 until n).joinToString("\n") { i ->
            padC(xData[i].fmtN(), 8) +
                    padC(yData[i].fmtN(), 8) +
                    padC(xy[i].fmtN(), 10) +
                    padC(xSq[i].fmtN(), 10) +
                    padC(ySq[i].fmtN(), 10)
        }
        val sumX  = xData.sum()
        val sumY  = yData.sum()
        val sumXY = xy.sum()
        val sumX2 = xSq.sum()
        val sumY2 = ySq.sum()
        val tableSums = padC("Σx=${sumX.fmtN()}", 8) +
                padC("Σy=${sumY.fmtN()}", 8) +
                padC("Σxy=${sumXY.fmtN()}", 10) +
                padC("Σx²=${sumX2.fmtN()}", 10) +
                padC("Σy²=${sumY2.fmtN()}", 10)

        steps += StepItem(2, "Build computation table",
            "$tableHeader\n$tableDivider\n$tableBody\n$tableDivider\n$tableSums")

        // ── Slope m ───────────────────────────────────────────────────────────
        steps += StepItem(3, "Compute slope (m)",
            "Formula: m = (nΣxy − ΣxΣy) / (nΣx² − (Σx)²)")

        val mNumerator   = n * sumXY - sumX * sumY
        val mDenominator = n * sumX2 - sumX * sumX
        steps += StepItem(4, "Substitute into slope formula",
            "Numerator:   nΣxy − ΣxΣy\n" +
                    "           = $n × ${sumXY.fmtN()} − ${sumX.fmtN()} × ${sumY.fmtN()}\n" +
                    "           = ${(n * sumXY).fmtN()} − ${(sumX * sumY).fmtN()}\n" +
                    "           = ${mNumerator.fmtN()}\n\n" +
                    "Denominator: nΣx² − (Σx)²\n" +
                    "           = $n × ${sumX2.fmtN()} − (${sumX.fmtN()})²\n" +
                    "           = ${(n * sumX2).fmtN()} − ${(sumX * sumX).fmtN()}\n" +
                    "           = ${mDenominator.fmtN()}")

        if (mDenominator == 0.0) return lrError("Cannot compute slope — all x values are identical.")
        val m = mNumerator / mDenominator
        steps += StepItem(5, "Divide numerator by denominator",
            "${mNumerator.fmtN()} ÷ ${mDenominator.fmtN()} = ${m.fmtN()}",
            result = "m = ${m.fmtN()}")

        // ── Intercept b ───────────────────────────────────────────────────────
        steps += StepItem(6, "Compute intercept (b)",
            "Formula: b = (Σy − mΣx) / n")

        val bNumerator = sumY - m * sumX
        steps += StepItem(7, "Substitute into intercept formula",
            "b = (${sumY.fmtN()} − ${m.fmtN()} × ${sumX.fmtN()}) / $n\n" +
                    "  = (${sumY.fmtN()} − ${(m * sumX).fmtN()}) / $n\n" +
                    "  = ${bNumerator.fmtN()} / $n\n" +
                    "  = ${(bNumerator / n).fmtN()}",
            result = "b = ${(bNumerator / n).fmtN()}")

        val b = bNumerator / n

        steps += StepItem(8, "Regression line equation",
            "ŷ = mx + b\n" +
                    "ŷ = ${m.fmtN()}x + ${b.fmtN()}",
            result = "ŷ = ${m.fmtN()}x + ${b.fmtN()}")

        // ── Prediction ────────────────────────────────────────────────────────
        val pyVal = predictY.trim().toDoubleOrNull()
        val pxVal = predictX.trim().toDoubleOrNull()

        if (pyVal != null) {
            // predict ŷ given x
            val yHat = m * pyVal + b
            steps += StepItem(9, "Predict ŷ when x = ${pyVal.fmtN()}",
                "ŷ = ${m.fmtN()} × ${pyVal.fmtN()} + ${b.fmtN()}\n" +
                        "  = ${(m * pyVal).fmtN()} + ${b.fmtN()}\n" +
                        "  = ${yHat.fmtN()}",
                result = "ŷ = ${yHat.fmtN()} when x = ${pyVal.fmtN()}")
        }

        if (pxVal != null) {
            // predict x given ŷ: x = (ŷ - b) / m
            if (m == 0.0) {
                steps += StepItem(10, "Cannot predict x",
                    "Slope m = 0, so x cannot be determined from ŷ.")
            } else {
                val xPred = (pxVal - b) / m
                steps += StepItem(10, "Predict x when ŷ = ${pxVal.fmtN()}",
                    "x = (ŷ − b) / m\n" +
                            "  = (${pxVal.fmtN()} − ${b.fmtN()}) / ${m.fmtN()}\n" +
                            "  = ${(pxVal - b).fmtN()} / ${m.fmtN()}\n" +
                            "  = ${xPred.fmtN()}",
                    result = "x = ${xPred.fmtN()} when ŷ = ${pxVal.fmtN()}")
            }
        }

        val pyVal2 = predictY.trim().toDoubleOrNull()
        val pxVal2 = predictX.trim().toDoubleOrNull()
        val answer = when {
            pyVal2 != null -> {
                val yHat = m * pyVal2 + b
                "ŷ = ${yHat.fmtN()} when x = ${pyVal2.fmtN()}"
            }
            pxVal2 != null && m != 0.0 -> {
                val xPred = (pxVal2 - b) / m
                "x = ${xPred.fmtN()} when ŷ = ${pxVal2.fmtN()}"
            }
            else -> "ŷ = ${m.fmtN()}x + ${b.fmtN()}"
        }
        val solvedForLabel2 = when {
            pyVal2 != null -> "Predicted ŷ when x = ${pyVal2.fmtN()}"
            pxVal2 != null -> "Predicted x when ŷ = ${pxVal2.fmtN()}"
            else           -> "Slope, Intercept, and Regression Line"
        }

        return CalculationResult(
            topicId        = "linear_regression",
            topicName      = "Linear Regression",
            solvedFor      = "regression",
            solvedForLabel = solvedForLabel2,
            answer         = answer,
            answerWithUnit = answer,
            steps          = steps,
            inputs         = mapOf(
                "x" to xData.joinToString(", ") { it.fmtN() },
                "y" to yData.joinToString(", ") { it.fmtN() })
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CORRELATION — Pearson's r
    // ─────────────────────────────────────────────────────────────────────────

    fun solveCorrelation(
        xData: List<Double>,
        yData: List<Double>
    ): CalculationResult {
        if (xData.size != yData.size)
            return corrError("X and Y datasets must have the same number of values.")
        if (xData.size < 2)
            return corrError("Please enter at least 2 data points.")
        if (xData.distinct().size == 1 || yData.distinct().size == 1)
            return corrError("Cannot compute r — one variable has no variation (all values identical).")
        if (xData.any { !it.isFinite() } || yData.any { !it.isFinite() })
            return corrError("Dataset contains invalid values.")

        val steps = mutableListOf<StepItem>()
        val n     = xData.size

        steps += StepItem(1, "Identify data points",
            "n = $n\n" +
                    "X: ${xData.joinToString(", ") { it.fmtN() }}\n" +
                    "Y: ${yData.joinToString(", ") { it.fmtN() }}")

        // ── Build computation table ───────────────────────────────────────────
        val xy  = xData.zip(yData).map { (x, y) -> x * y }
        val xSq = xData.map { it * it }
        val ySq = yData.map { it * it }

        val tableHeader  = padC("x", 8) + padC("y", 8) + padC("xy", 10) + padC("x²", 10) + padC("y²", 10)
        val tableDivider = "─".repeat(46)
        val tableBody    = (0 until n).joinToString("\n") { i ->
            padC(xData[i].fmtN(), 8) +
                    padC(yData[i].fmtN(), 8) +
                    padC(xy[i].fmtN(), 10)   +
                    padC(xSq[i].fmtN(), 10)  +
                    padC(ySq[i].fmtN(), 10)
        }
        val sumX  = xData.sum()
        val sumY  = yData.sum()
        val sumXY = xy.sum()
        val sumX2 = xSq.sum()
        val sumY2 = ySq.sum()
        val tableSums =
            padC("${sumX.fmtN()}", 8) +
                    padC("${sumY.fmtN()}", 8) +
                    padC("${sumXY.fmtN()}", 10) +
                    padC("${sumX2.fmtN()}", 10) +
                    padC("${sumY2.fmtN()}", 10)

        steps += StepItem(2, "Build computation table",
            "$tableHeader\n$tableDivider\n$tableBody\n$tableDivider\n$tableSums")

        // ── Pearson's r ───────────────────────────────────────────────────────
        steps += StepItem(3, "Write Pearson's r formula",
            "r = (nΣxy − ΣxΣy) / √[(nΣx² − (Σx)²)(nΣy² − (Σy)²)]")

        val numerator    = n * sumXY - sumX * sumY
        val denomPartX   = n * sumX2 - sumX * sumX
        val denomPartY   = n * sumY2 - sumY * sumY
        val denominator  = sqrt(denomPartX * denomPartY)

        steps += StepItem(4, "Compute numerator: nΣxy − ΣxΣy",
            "= $n × ${sumXY.fmtN()} − ${sumX.fmtN()} × ${sumY.fmtN()}\n" +
                    "= ${(n * sumXY).fmtN()} − ${(sumX * sumY).fmtN()}\n" +
                    "= ${numerator.fmtN()}")

        steps += StepItem(5, "Compute denominator parts",
            "nΣx² − (Σx)² = $n × ${sumX2.fmtN()} − (${sumX.fmtN()})²\n" +
                    "              = ${(n * sumX2).fmtN()} − ${(sumX * sumX).fmtN()}\n" +
                    "              = ${denomPartX.fmtN()}\n\n" +
                    "nΣy² − (Σy)² = $n × ${sumY2.fmtN()} − (${sumY.fmtN()})²\n" +
                    "              = ${(n * sumY2).fmtN()} − ${(sumY * sumY).fmtN()}\n" +
                    "              = ${denomPartY.fmtN()}")

        steps += StepItem(6, "Multiply denominator parts and take square root",
            "√(${denomPartX.fmtN()} × ${denomPartY.fmtN()})\n" +
                    "= √${(denomPartX * denomPartY).fmtN()}\n" +
                    "= ${denominator.fmtN()}")

        if (denominator == 0.0)
            return corrError("Cannot compute r — no variation in one or both variables.")

        val r = numerator / denominator
        steps += StepItem(7, "Divide numerator by denominator",
            "${numerator.fmtN()} ÷ ${denominator.fmtN()} = ${r.fmtN()}",
            result = "r = ${r.fmtN()}")

        // ── Interpretation ────────────────────────────────────────────────────
        val interpretation = interpretR(r)
        steps += StepItem(8, "Interpret Pearson's r",
            "r = ${r.fmtN()}\n$interpretation",
            result = interpretation)

        val answer = "r = ${r.fmtN()} — $interpretation"

        return CalculationResult(
            topicId        = "correlation",
            topicName      = "Correlation (Pearson's r)",
            solvedFor      = "r",
            solvedForLabel = "Pearson's r",
            answer         = answer,
            answerWithUnit = answer,
            steps          = steps,
            inputs         = mapOf(
                "x" to xData.joinToString(", ") { it.fmtN() },
                "y" to yData.joinToString(", ") { it.fmtN() })
        )
    }

    private fun interpretR(r: Double): String {
        val abs = abs(r)
        val direction = if (r >= 0) "positive" else "negative"
        val strength  = when {
            abs >= 0.90 -> "very strong"
            abs >= 0.70 -> "strong"
            abs >= 0.50 -> "moderate"
            abs >= 0.30 -> "weak"
            else        -> "very weak / negligible"
        }
        return when {
            abs == 1.0  -> "Perfect $direction correlation"
            abs == 0.0  -> "No correlation"
            else        -> "${strength.replaceFirstChar { it.uppercase() }} $direction correlation"
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun padC(s: String, width: Int): String = s.padEnd(width)

    private fun Double.fmtN(): String =
        if (this == floor(this) && !this.isInfinite())
            this.toLong().toString()
        else "%.4f".format(this).trimEnd('0').trimEnd('.')

    private fun Double.fmtArea(): String = "%.4f".format(this)

    private fun ndError(message: String) = CalculationResult(
        topicId = "normal_distribution", topicName = "Normal Distribution",
        solvedFor = "", solvedForLabel = "", answer = "",
        steps = emptyList(), inputs = emptyMap(),
        isError = true, errorMessage = message)

    private fun lrError(message: String) = CalculationResult(
        topicId = "linear_regression", topicName = "Linear Regression",
        solvedFor = "", solvedForLabel = "", answer = "",
        steps = emptyList(), inputs = emptyMap(),
        isError = true, errorMessage = message)

    private fun corrError(message: String) = CalculationResult(
        topicId = "correlation", topicName = "Correlation (Pearson's r)",
        solvedFor = "", solvedForLabel = "", answer = "",
        steps = emptyList(), inputs = emptyMap(),
        isError = true, errorMessage = message)
}