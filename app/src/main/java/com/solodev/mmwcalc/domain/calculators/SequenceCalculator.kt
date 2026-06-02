package com.solodev.mmwcalc.domain.calculators

import com.solodev.mmwcalc.domain.models.CalculationResult
import com.solodev.mmwcalc.domain.models.StepItem
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.abs

object SequenceCalculator {

    // ─────────────────────────────────────────────────────────────────────────
    // ARITHMETIC SEQUENCE
    // ─────────────────────────────────────────────────────────────────────────

    fun solveArithmetic(inputs: Map<String, String>): CalculationResult {
        val mainVars   = listOf("a1", "d", "n", "an")
        val mainBlanks = mainVars.filter { inputs[it].isNullOrBlank() }
        val snBlank    = inputs["sn"].isNullOrBlank()
        val get        = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }

        if (mainBlanks.size > 1)
            return error("arithmetic_sequence", "Arithmetic Sequence", inputs,
                "Leave exactly one of a₁, d, n, aₙ blank to solve for it.")
        if (mainBlanks.isEmpty() && !snBlank)
            return error("arithmetic_sequence", "Arithmetic Sequence", inputs,
                "All fields are filled — nothing to solve.")

        return try {
            if (mainBlanks.isEmpty() && snBlank) {
                val a1 = get("a1")!!; val an = get("an")!!; val n = get("n")!!
                val steps = mutableListOf<StepItem>()
                steps += StepItem(1, "Write the formula", "Sₙ = n/2 × (a₁ + aₙ)")
                steps += StepItem(2, "Substitute known values",
                    "Sₙ = ${n.fmt()}/2 × (${a1.fmt()} + ${an.fmt()})")
                val innerSum = a1 + an
                steps += StepItem(3, "Compute (a₁ + aₙ)",
                    "${a1.fmt()} + ${an.fmt()} = ${innerSum.fmt()}")
                val half = n / 2.0
                steps += StepItem(4, "Compute n/2", "${n.fmt()} ÷ 2 = ${half.fmt()}")
                val sn = half * innerSum
                steps += StepItem(5, "Multiply",
                    "${half.fmt()} × ${innerSum.fmt()} = ${sn.fmt()}",
                    result = "Sₙ = ${sn.fmt()}")
                return result("arithmetic_sequence", "Arithmetic Sequence",
                    "sn", "Sₙ — Sum of n terms", sn.fmt(), steps, inputs)
            }

            val unknown = mainBlanks.first()
            val steps   = mutableListOf<StepItem>()
            val answer: Double

            // Guard: n must be positive integer
            if (unknown != "n") {
                val nVal = get("n")?.toDouble()
                if (nVal != null && (nVal < 1 || nVal != kotlin.math.floor(nVal)))
                    return error("arithmetic_sequence", "Arithmetic Sequence", inputs,
                        "n must be a positive whole number (e.g. 1, 2, 3...).")
            }
            // Guard: d = 0 is valid but n must not cause division by zero
            if (unknown == "n" || unknown == "d") {
                val dVal = get("d")
                val nVal = get("n")
                if (unknown == "n" && dVal != null && dVal == 0.0)
                    return error("arithmetic_sequence", "Arithmetic Sequence", inputs,
                        "Cannot find n when d = 0 — all terms are equal, n is undefined.")
                if (unknown == "d" && nVal != null && nVal == 1.0)
                    return error("arithmetic_sequence", "Arithmetic Sequence", inputs,
                        "Cannot find d when n = 1 — only one term, difference is undefined.")
            }

            when (unknown) {
                "an" -> {
                    val a1 = get("a1")!!; val d = get("d")!!; val n = get("n")!!
                    steps += StepItem(1, "Write the formula", "aₙ = a₁ + (n − 1) × d")
                    steps += StepItem(2, "Substitute known values",
                        "aₙ = ${a1.fmt()} + (${n.fmt()} − 1) × ${d.fmt()}")
                    val nMinus1 = n - 1
                    steps += StepItem(3, "Compute (n − 1)", "${n.fmt()} − 1 = ${nMinus1.fmt()}")
                    val product = nMinus1 * d
                    steps += StepItem(4, "Compute (n−1) × d — repeated addition",
                        buildRepeatedAddition(d, nMinus1.toInt()) + " = ${product.fmt()}")
                    val an = a1 + product
                    steps += StepItem(5, "Add a₁",
                        "${a1.fmt()} + ${product.fmt()} = ${an.fmt()}",
                        result = "aₙ = ${an.fmt()}")
                    answer = an
                }
                "a1" -> {
                    val an = get("an")!!; val d = get("d")!!; val n = get("n")!!
                    steps += StepItem(1, "Write the formula", "a₁ = aₙ − (n − 1) × d")
                    steps += StepItem(2, "Substitute known values",
                        "a₁ = ${an.fmt()} − (${n.fmt()} − 1) × ${d.fmt()}")
                    val nMinus1 = n - 1
                    steps += StepItem(3, "Compute (n − 1)", "${n.fmt()} − 1 = ${nMinus1.fmt()}")
                    val product = nMinus1 * d
                    steps += StepItem(4, "Compute (n−1) × d",
                        "${nMinus1.fmt()} × ${d.fmt()} = ${product.fmt()}")
                    val a1 = an - product
                    steps += StepItem(5, "Subtract from aₙ",
                        "${an.fmt()} − ${product.fmt()} = ${a1.fmt()}",
                        result = "a₁ = ${a1.fmt()}")
                    answer = a1
                }
                "d" -> {
                    val a1 = get("a1")!!; val an = get("an")!!; val n = get("n")!!
                    steps += StepItem(1, "Write the formula", "d = (aₙ − a₁) / (n − 1)")
                    steps += StepItem(2, "Substitute known values",
                        "d = (${an.fmt()} − ${a1.fmt()}) / (${n.fmt()} − 1)")
                    val numerator = an - a1
                    steps += StepItem(3, "Compute numerator (aₙ − a₁)",
                        "${an.fmt()} − ${a1.fmt()} = ${numerator.fmt()}")
                    val denominator = n - 1
                    steps += StepItem(4, "Compute denominator (n − 1)",
                        "${n.fmt()} − 1 = ${denominator.fmt()}")
                    val d = numerator / denominator
                    steps += StepItem(5, "Divide",
                        "${numerator.fmt()} ÷ ${denominator.fmt()} = ${d.fmt()}",
                        result = "d = ${d.fmt()}")
                    answer = d
                }
                "n" -> {
                    val a1 = get("a1")!!; val an = get("an")!!; val d = get("d")!!
                    steps += StepItem(1, "Write the formula", "n = ((aₙ − a₁) / d) + 1")
                    steps += StepItem(2, "Substitute known values",
                        "n = ((${an.fmt()} − ${a1.fmt()}) / ${d.fmt()}) + 1")
                    val numerator = an - a1
                    steps += StepItem(3, "Compute (aₙ − a₁)",
                        "${an.fmt()} − ${a1.fmt()} = ${numerator.fmt()}")
                    val divided = numerator / d
                    steps += StepItem(4, "Divide by d",
                        "${numerator.fmt()} ÷ ${d.fmt()} = ${divided.fmt()}")
                    val n = divided + 1
                    steps += StepItem(5, "Add 1",
                        "${divided.fmt()} + 1 = ${n.fmt()}",
                        result = "n = ${n.fmt()}")
                    answer = n
                }
                else -> return error("arithmetic_sequence", "Arithmetic Sequence",
                    inputs, "Unknown variable.")
            }

            // Bonus Sₙ
            if (snBlank) {
                val fm = inputs.toMutableMap().also { it[unknown] = answer.fmt() }
                val a1 = fm["a1"]?.toDoubleOrNull()
                val an = fm["an"]?.toDoubleOrNull()
                val n  = fm["n"]?.toDoubleOrNull()
                if (a1 != null && an != null && n != null) {
                    val s = steps.size + 1
                    steps += StepItem(s, "─── Bonus: Also compute Sₙ ───",
                        "Sₙ was blank so we compute it too.")
                    steps += StepItem(s+1, "Formula", "Sₙ = n/2 × (a₁ + aₙ)")
                    val inner = a1 + an
                    steps += StepItem(s+2, "Substitute",
                        "Sₙ = ${n.fmt()}/2 × (${a1.fmt()} + ${an.fmt()})")
                    val sn = (n / 2.0) * inner
                    steps += StepItem(s+3, "Compute",
                        "${(n/2.0).fmt()} × ${inner.fmt()} = ${sn.fmt()}",
                        result = "Sₙ = ${sn.fmt()}")
                }
            }

            val solvedLabel = when (unknown) {
                "an" -> "aₙ — nth term"
                "a1" -> "a₁ — First term"
                "d"  -> "d — Common difference"
                "n"  -> "n — Position / Number of terms"
                else -> unknown
            }
            result("arithmetic_sequence", "Arithmetic Sequence",
                unknown, solvedLabel, answer.fmt(), steps, inputs)

        } catch (e: Exception) {
            error("arithmetic_sequence", "Arithmetic Sequence", inputs,
                "Invalid input. Please check your values.")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GEOMETRIC SEQUENCE
    // ─────────────────────────────────────────────────────────────────────────

    fun solveGeometric(inputs: Map<String, String>): CalculationResult {
        val mainVars   = listOf("a1", "r", "n", "an")
        val mainBlanks = mainVars.filter { inputs[it].isNullOrBlank() }
        val snBlank    = inputs["sn"].isNullOrBlank()
        val get        = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }

        if (mainBlanks.size > 1)
            return error("geometric_sequence", "Geometric Sequence", inputs,
                "Leave exactly one of a₁, r, n, aₙ blank to solve for it.")
        if (mainBlanks.isEmpty() && !snBlank)
            return error("geometric_sequence", "Geometric Sequence", inputs,
                "All fields are filled — nothing to solve.")

        return try {
            if (mainBlanks.isEmpty() && snBlank) {
                val a1 = get("a1")!!; val r = get("r")!!; val n = get("n")!!
                val steps = mutableListOf<StepItem>()
                if (r == 1.0) {
                    val sn = a1 * n
                    steps += StepItem(1, "Special case: r = 1", "Sₙ = a₁ × n")
                    steps += StepItem(2, "Substitute",
                        "${a1.fmt()} × ${n.fmt()} = ${sn.fmt()}",
                        result = "Sₙ = ${sn.fmt()}")
                    return result("geometric_sequence", "Geometric Sequence",
                        "sn", "Sₙ — Sum of n terms", sn.fmt(), steps, inputs)
                }
                steps += StepItem(1, "Write the formula",
                    "Sₙ = a₁ × (1 − rⁿ) / (1 − r)")
                steps += StepItem(2, "Substitute",
                    "Sₙ = ${a1.fmt()} × (1 − ${r.fmt()}^${n.fmt()}) / (1 − ${r.fmt()})")
                val rPow = r.pow(n)
                steps += StepItem(3, "Compute rⁿ",
                    "${r.fmt()}^${n.fmt()} = ${rPow.fmt()}")
                val num = 1 - rPow
                steps += StepItem(4, "Compute (1 − rⁿ)",
                    "1 − ${rPow.fmt()} = ${num.fmt()}")
                val den = 1 - r
                steps += StepItem(5, "Compute (1 − r)",
                    "1 − ${r.fmt()} = ${den.fmt()}")
                val sn = a1 * (num / den)
                steps += StepItem(6, "Multiply and divide",
                    "${a1.fmt()} × (${num.fmt()} / ${den.fmt()}) = ${sn.fmt()}",
                    result = "Sₙ = ${sn.fmt()}")
                return result("geometric_sequence", "Geometric Sequence",
                    "sn", "Sₙ — Sum of n terms", sn.fmt(), steps, inputs)
            }

            val unknown = mainBlanks.first()
            val steps   = mutableListOf<StepItem>()
            val answer: Double

            // Guard: n must be positive integer
            if (unknown != "n") {
                val nVal = get("n")?.toDouble()
                if (nVal != null && (nVal < 1 || nVal != kotlin.math.floor(nVal)))
                    return error("geometric_sequence", "Geometric Sequence", inputs,
                        "n must be a positive whole number.")
            }
            // Guard: r cannot be 0
            if (unknown != "r") {
                val rVal = get("r")
                if (rVal != null && rVal == 0.0)
                    return error("geometric_sequence", "Geometric Sequence", inputs,
                        "Common ratio r cannot be 0.")
            }
            // Guard: a1 cannot be 0 for most operations
            if (unknown != "a1") {
                val a1Val = get("a1")
                if (a1Val != null && a1Val == 0.0 && unknown == "r")
                    return error("geometric_sequence", "Geometric Sequence", inputs,
                        "Cannot find r when a₁ = 0.")
            }
            // Guard: infinite sum requires |r| < 1
            if (unknown == "sn") {
                val rVal = get("r")
                if (rVal != null && kotlin.math.abs(rVal) >= 1 && get("n") == null)
                    return error("geometric_sequence", "Geometric Sequence", inputs,
                        "Infinite sum only exists when |r| < 1.")
            }

            when (unknown) {
                "an" -> {
                    val a1 = get("a1")!!; val r = get("r")!!; val n = get("n")!!
                    steps += StepItem(1, "Write the formula", "aₙ = a₁ × r^(n−1)")
                    steps += StepItem(2, "Substitute",
                        "aₙ = ${a1.fmt()} × ${r.fmt()}^(${n.fmt()}−1)")
                    val exp = (n - 1).toInt()
                    steps += StepItem(3, "Compute (n − 1)", "${n.fmt()} − 1 = $exp")
                    val rPow = r.pow(exp.toDouble())
                    steps += StepItem(4, "Compute r^(n−1) — repeated multiplication",
                        buildRepeatedMultiplication(r, exp) + " = ${rPow.fmt()}")
                    val an = a1 * rPow
                    steps += StepItem(5, "Multiply by a₁",
                        "${a1.fmt()} × ${rPow.fmt()} = ${an.fmt()}",
                        result = "aₙ = ${an.fmt()}")
                    answer = an
                }
                "a1" -> {
                    val an = get("an")!!; val r = get("r")!!; val n = get("n")!!
                    steps += StepItem(1, "Write the formula", "a₁ = aₙ / r^(n−1)")
                    steps += StepItem(2, "Substitute",
                        "a₁ = ${an.fmt()} / ${r.fmt()}^(${n.fmt()}−1)")
                    val exp = (n - 1).toInt()
                    val rPow = r.pow(exp.toDouble())
                    steps += StepItem(3, "Compute r^(n−1)",
                        "${r.fmt()}^$exp = ${rPow.fmt()}")
                    val a1 = an / rPow
                    steps += StepItem(4, "Divide aₙ by r^(n−1)",
                        "${an.fmt()} ÷ ${rPow.fmt()} = ${a1.fmt()}",
                        result = "a₁ = ${a1.fmt()}")
                    answer = a1
                }
                "r" -> {
                    val a1 = get("a1")!!; val an = get("an")!!; val n = get("n")!!
                    steps += StepItem(1, "Write the formula", "r = (aₙ / a₁)^(1/(n−1))")
                    steps += StepItem(2, "Substitute",
                        "r = (${an.fmt()} / ${a1.fmt()})^(1/(${n.fmt()}−1))")
                    val ratio = an / a1
                    steps += StepItem(3, "Compute aₙ / a₁",
                        "${an.fmt()} ÷ ${a1.fmt()} = ${ratio.fmt()}")
                    val exp = 1.0 / (n - 1)
                    steps += StepItem(4, "Compute exponent 1/(n−1)",
                        "1 / (${n.fmt()} − 1) = ${exp.fmt()}")
                    val r = ratio.pow(exp)
                    steps += StepItem(5, "Take the nth root",
                        "${ratio.fmt()}^${exp.fmt()} = ${r.fmt()}",
                        result = "r = ${r.fmt()}")
                    answer = r
                }
                "n" -> {
                    val a1 = get("a1")!!; val an = get("an")!!; val r = get("r")!!
                    steps += StepItem(1, "Write the formula",
                        "n = log(aₙ / a₁) / log(r) + 1")
                    steps += StepItem(2, "Substitute",
                        "n = log(${an.fmt()} / ${a1.fmt()}) / log(${r.fmt()}) + 1")
                    val ratio = an / a1
                    steps += StepItem(3, "Compute aₙ / a₁",
                        "${an.fmt()} ÷ ${a1.fmt()} = ${ratio.fmt()}")
                    val logRatio = ln(ratio)
                    val logR = ln(r)
                    steps += StepItem(4, "Apply natural log",
                        "ln(${ratio.fmt()}) = ${logRatio.fmt()}\nln(${r.fmt()}) = ${logR.fmt()}")
                    val divided = logRatio / logR
                    steps += StepItem(5, "Divide log values",
                        "${logRatio.fmt()} ÷ ${logR.fmt()} = ${divided.fmt()}")
                    val n = divided + 1
                    steps += StepItem(6, "Add 1",
                        "${divided.fmt()} + 1 = ${n.fmt()}",
                        result = "n = ${n.fmt()}")
                    answer = n
                }
                else -> return error("geometric_sequence", "Geometric Sequence",
                    inputs, "Unknown variable.")
            }

            // Bonus Sₙ
            if (snBlank) {
                val fm = inputs.toMutableMap().also { it[unknown] = answer.fmt() }
                val a1 = fm["a1"]?.toDoubleOrNull()
                val r  = fm["r"]?.toDoubleOrNull()
                val n  = fm["n"]?.toDoubleOrNull()
                if (a1 != null && r != null && n != null) {
                    val s = steps.size + 1
                    steps += StepItem(s, "─── Bonus: Also compute Sₙ ───",
                        "Sₙ was blank so we compute it too.")
                    if (r == 1.0) {
                        val sn = a1 * n
                        steps += StepItem(s+1, "Special case r=1: Sₙ = a₁ × n",
                            "${a1.fmt()} × ${n.fmt()} = ${sn.fmt()}",
                            result = "Sₙ = ${sn.fmt()}")
                    } else {
                        steps += StepItem(s+1, "Formula",
                            "Sₙ = a₁ × (1 − rⁿ) / (1 − r)")
                        val rPow = r.pow(n)
                        steps += StepItem(s+2, "Compute rⁿ",
                            "${r.fmt()}^${n.fmt()} = ${rPow.fmt()}")
                        val sn = a1 * ((1 - rPow) / (1 - r))
                        steps += StepItem(s+3, "Compute Sₙ",
                            "${a1.fmt()} × (1 − ${rPow.fmt()}) / (1 − ${r.fmt()}) = ${sn.fmt()}",
                            result = "Sₙ = ${sn.fmt()}")
                    }
                }
            }

            val solvedLabel = when (unknown) {
                "an" -> "aₙ — nth term"; "a1" -> "a₁ — First term"
                "r"  -> "r — Common ratio"; "n"  -> "n — Position / Number of terms"
                else -> unknown
            }
            result("geometric_sequence", "Geometric Sequence",
                unknown, solvedLabel, answer.fmt(), steps, inputs)

        } catch (e: Exception) {
            error("geometric_sequence", "Geometric Sequence", inputs,
                "Invalid input. Please check your values.")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FIBONACCI — Constraint Solver
    //
    // Default mode: seeds fixed (toggle 0,1 or 1,1)
    //   1 row: position + value, one blank = unknown
    //   position blank → find position(s) of value
    //   value blank    → find value at position
    //   both blank     → error
    //   both filled    → error
    //
    // Custom mode: 6 rows total, 5 knowns + 1 unknown
    //   each row: position + value, one may be blank = unknown
    //   Solver: collect knowns → propagate → resolve unknown
    // ─────────────────────────────────────────────────────────────────────────

    fun solveFibonacci(
        mode: String,                          // "default" | "custom"
        seedType: String,                      // "1,1" | "0,1" (default mode only)
        rows: List<FibRow>                     // all rows from UI
    ): CalculationResult {
        return try {
            when (mode) {
                "default" -> fibDefault(seedType, rows)
                "custom"  -> fibCustom(rows)
                else      -> fibError("Invalid mode.")
            }
        } catch (e: Exception) {
            fibError("Unexpected error: ${e.message}")
        }
    }

    // ── Default mode ──────────────────────────────────────────────────────────

    private fun fibDefault(seedType: String, rows: List<FibRow>): CalculationResult {
        val f1 = if (seedType == "0,1") 0L else 1L
        val f2 = 1L

        val row = rows.firstOrNull()
            ?: return fibError("No input row found.")

        val posBlank = row.position == null
        val valBlank = row.value == null

        if (posBlank && valBlank)
            return fibError("Both fields are empty. Fill one and leave the other blank.")
        if (!posBlank && !valBlank)
            return fibError("Both fields are filled — nothing to solve. Leave one blank.")

        val steps = mutableListOf<StepItem>()
        steps += StepItem(1, "Default seeds",
            "F(1) = $f1,  F(2) = $f2  [$seedType]")

        // Build sequence
        val seq = buildSequence(f1, f2, maxTerms = 200)

        return if (valBlank) {
            // Find value at given position
            val pos = row.position!!
            if (pos < 1) return fibError("Position must be at least 1.")
            val seq2 = if (pos > seq.size) buildSequence(f1, f2, pos + 1) else seq
            steps += StepItem(2, "Generate sequence forward",
                "Each term = F(n−1) + F(n−2)")
            for (i in 3..minOf(pos, seq2.size)) {
                val p2 = seq2[i - 3]; val p1 = seq2[i - 2]; val c = seq2[i - 1]
                steps += StepItem(i, "Compute F($i)",
                    "F($i) = F(${i-2}) + F(${i-1}) = $p2 + $p1 = $c",
                    result = if (i == pos) "F($pos) = $c" else null)
            }
            val answer = seq2.getOrNull(pos - 1)
                ?: return fibError("Could not compute F($pos).")
            result("fibonacci", "Fibonacci Sequence",
                "value", "F($pos) — Value at position $pos",
                "$answer", steps, emptyMap())
        } else {
            // Find position(s) of given value
            val target = row.value!!
            steps += StepItem(2, "Search for value $target in sequence",
                "Scanning F(1), F(2), F(3)...")
            val positions = seq.mapIndexedNotNull { i, v ->
                if (v == target) i + 1 else null
            }
            val foundMax = positions.maxOrNull() ?: 12
            val showUpTo = maxOf(foundMax, 12)
            val preview = seq.take(showUpTo)
                .mapIndexed { i, v -> "F(${i+1})=$v" }.joinToString("\n") +
                    if (seq.size > showUpTo) "\n..." else ""
            steps += StepItem(3, "Sequence up to found position", preview)
            if (positions.isEmpty()) {
                steps += StepItem(4, "Not found",
                    "$target does not appear in this sequence (searched ${seq.size} terms).")
                return fibError("$target is not a term in this Fibonacci sequence.")
            }
            steps += StepItem(4, "Found ${positions.size} match(es)",
                positions.joinToString("\n") { "F($it) = $target" },
                result = if (positions.size == 1) "Position = ${positions.first()}"
                else "Positions = ${positions.joinToString(", ")}")
            result("fibonacci", "Fibonacci Sequence",
                "position", "Position(s) of $target",
                if (positions.size == 1) "F(${positions.first()}) = $target"
                else "Positions: ${positions.joinToString(", ")}",
                steps, emptyMap())
        }
    }

    // ── Custom mode ───────────────────────────────────────────────────────────

    private fun fibCustom(rows: List<FibRow>): CalculationResult {
        // Separate knowns from the unknown row
        val unknownRows = rows.filter { it.position == null || it.value == null }
        val knownRows   = rows.filter { it.position != null && it.value != null }

        // Validation
        if (unknownRows.isEmpty())
            return fibError("All rows are filled — nothing to solve. Leave one field blank.")
        if (unknownRows.size > 1)
            return fibError("Only one row can have a blank field (the unknown).")
        val unknownRow = unknownRows.first()
        if (unknownRow.position == null && unknownRow.value == null)
            return fibError("The unknown row has both fields empty. Fill one.")

        if (knownRows.size < 2)
            return fibError("Please provide at least 2 known terms to propagate the sequence.")

        // Check duplicate positions with conflicting values
        val posMap = mutableMapOf<Int, Long>()
        for (row in knownRows) {
            val existing = posMap[row.position!!]
            if (existing != null && existing != row.value!!)
                return fibError(
                    "Contradiction: F(${row.position}) cannot be both $existing and ${row.value}.")
            posMap[row.position] = row.value!!
        }

        val steps = mutableListOf<StepItem>()
        steps += StepItem(1, "Known terms provided",
            knownRows.sortedBy { it.position }
                .joinToString("\n") { "F(${it.position}) = ${it.value}" })

        // Propagate sequence from known terms
        // Strategy: find the lowest two consecutive knowns, use them as anchor
        val sortedPositions = posMap.keys.sorted()
        var anchorPos: Int? = null
        for (i in 0 until sortedPositions.size - 1) {
            if (sortedPositions[i+1] - sortedPositions[i] == 1) {
                anchorPos = sortedPositions[i]
                break
            }
        }

        if (anchorPos == null)
            return fibError(
                "No two consecutive known positions found. " +
                        "Please include at least two consecutive terms (e.g. F(3) and F(4)).")

        val anchorF1 = posMap[anchorPos]!!
        val anchorF2 = posMap[anchorPos + 1]!!

        steps += StepItem(2, "Anchor pair found",
            "F($anchorPos) = $anchorF1,  F(${anchorPos+1}) = $anchorF2\n" +
                    "Will propagate forward and backward from here.")

        // Build full sequence map by propagating in both directions
        val seqMap = mutableMapOf<Int, Long>()
        seqMap[anchorPos]     = anchorF1
        seqMap[anchorPos + 1] = anchorF2

        // Forward: F(n) = F(n-1) + F(n-2)
        val maxPos = maxOf(
            sortedPositions.max(),
            unknownRow.position ?: (sortedPositions.max() + 5)
        ) + 2
        var i = anchorPos + 2
        while (i <= maxPos) {
            seqMap[i] = (seqMap[i-1] ?: break) + (seqMap[i-2] ?: break)
            i++
        }

        // Backward: F(n-2) = F(n) - F(n-1)
        var j = anchorPos - 1
        while (j >= 1) {
            val fNext = seqMap[j+2] ?: break
            val fMid  = seqMap[j+1] ?: break
            seqMap[j] = fNext - fMid
            j--
        }

        // Validate all known terms against propagated sequence
        steps += StepItem(3, "Propagated sequence (key positions)",
            sortedPositions.mapNotNull { p ->
                seqMap[p]?.let { "F($p) = $it" }
            }.joinToString("\n"))

        for (row in knownRows) {
            val propagated = seqMap[row.position!!]
            if (propagated != null && propagated != row.value!!) {
                return fibError(
                    "Contradiction detected!\n" +
                            "F(${row.position}) should be $propagated based on the sequence,\n" +
                            "but you entered ${row.value}.\n" +
                            "Please check your known values.")
            }
        }

        steps += StepItem(4, "All known terms validated ✓",
            "No contradictions found.")

        // Resolve the unknown
        return when {
            unknownRow.value == null -> {
                // Find value at given position
                val pos = unknownRow.position!!
                val value = seqMap[pos]
                    ?: return fibError("Could not compute F($pos) from the given terms.")
                steps += StepItem(5, "Resolve F($pos)",
                    showPropagationPath(seqMap, pos),
                    result = "F($pos) = $value")
                result("fibonacci", "Fibonacci Sequence",
                    "value", "F($pos) — Value at position $pos",
                    "$value", steps, emptyMap())
            }
            unknownRow.position == null -> {
                // Find position(s) of given value
                val target = unknownRow.value!!
                val positions = seqMap.entries
                    .filter { it.value == target }
                    .map { it.key }
                    .sorted()
                steps += StepItem(5, "Search for value $target in propagated sequence",
                    seqMap.entries.sortedBy { it.key }.take(15)
                        .joinToString(", ") { "F(${it.key})=${it.value}" } +
                            if (seqMap.size > 15) "..." else "")
                if (positions.isEmpty()) {
                    steps += StepItem(6, "Not found",
                        "$target does not appear in the propagated sequence.")
                    return fibError("$target is not a term in this Fibonacci sequence.")
                }
                steps += StepItem(6, "Found ${positions.size} match(es)",
                    positions.joinToString("\n") { "F($it) = $target" },
                    result = if (positions.size == 1) "Position = ${positions.first()}"
                    else "Positions = ${positions.joinToString(", ")}")
                result("fibonacci", "Fibonacci Sequence",
                    "position", "Position(s) of $target",
                    if (positions.size == 1) "F(${positions.first()}) = $target"
                    else "Positions: ${positions.joinToString(", ")}",
                    steps, emptyMap())
            }
            else -> fibError("Unexpected state in unknown resolution.")
        }
    }

    // ── Show how a position was derived ──────────────────────────────────────

    private fun showPropagationPath(seqMap: Map<Int, Long>, pos: Int): String {
        val prev1 = seqMap[pos - 1]
        val prev2 = seqMap[pos - 2]
        return when {
            prev1 != null && prev2 != null ->
                "F($pos) = F(${pos-2}) + F(${pos-1})\n" +
                        "       = $prev2 + $prev1 = ${seqMap[pos]}"
            else -> "F($pos) = ${seqMap[pos]} (directly from propagation)"
        }
    }

    // ── Build a sequence from f1, f2 forward ─────────────────────────────────

    private fun buildSequence(f1: Long, f2: Long, maxTerms: Int = 200): List<Long> {
        val seq = mutableListOf(f1, f2)
        while (seq.size < maxTerms) {
            val next = seq[seq.size - 1] + seq[seq.size - 2]
            if (next < 0) break // overflow guard
            seq.add(next)
        }
        return seq
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun buildRepeatedAddition(value: Double, times: Int): String =
        if (times <= 10) (1..times).joinToString(" + ") { value.fmt() }
        else "$times × ${value.fmt()}"

    private fun buildRepeatedMultiplication(value: Double, times: Int): String =
        if (times <= 8) (1..times).joinToString(" × ") { value.fmt() }
        else "${value.fmt()}^$times"

    private fun Double.pow(exp: Double): Double = Math.pow(this, exp)

    fun Double.fmt(): String =
        if (this == kotlin.math.floor(this) && !this.isInfinite())
            this.toLong().toString()
        else "%.6f".format(this).trimEnd('0').trimEnd('.')

    private fun fibError(message: String) = CalculationResult(
        topicId = "fibonacci", topicName = "Fibonacci Sequence",
        solvedFor = "", solvedForLabel = "", answer = "",
        steps = emptyList(), inputs = emptyMap(),
        isError = true, errorMessage = message)

    private fun result(
        topicId: String, topicName: String,
        solvedFor: String, solvedForLabel: String,
        answer: String, steps: List<StepItem>,
        inputs: Map<String, String>
    ) = CalculationResult(
        topicId = topicId, topicName = topicName,
        solvedFor = solvedFor, solvedForLabel = solvedForLabel,
        answer = answer, answerWithUnit = answer,
        steps = steps, inputs = inputs)

    private fun error(
        topicId: String, topicName: String,
        inputs: Map<String, String>, message: String
    ) = CalculationResult(
        topicId = topicId, topicName = topicName,
        solvedFor = "", solvedForLabel = "", answer = "",
        steps = emptyList(), inputs = inputs,
        isError = true, errorMessage = message)
}

// ─── Data class for a Fibonacci row ──────────────────────────────────────────

data class FibRow(
    val position: Int?,   // null = blank (unknown position)
    val value: Long?      // null = blank (unknown value)
)