package com.solodev.mmwcalc.domain.calculators

import com.solodev.mmwcalc.domain.models.CalculationResult
import com.solodev.mmwcalc.domain.models.StepItem
import kotlin.math.floor

object NumberTheoryCalculator {

    // ─────────────────────────────────────────────────────────────────────────
    // MODULAR ARITHMETIC
    // a mod m = r
    // Solve for any one blank: a, m, or r
    // ─────────────────────────────────────────────────────────────────────────

    fun solveModular(inputs: Map<String, String>): CalculationResult {
        val mainVars   = listOf("a", "m", "r")
        val mainBlanks = mainVars.filter { inputs[it].isNullOrBlank() }

        if (mainBlanks.size > 1)
            return modError(inputs,
                "Leave exactly one of a, m, r blank to solve for it.")
        if (mainBlanks.isEmpty())
            return modError(inputs,
                "All fields are filled — nothing to solve. Leave one blank.")

        val unknown = mainBlanks.first()
        val get     = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }
        val steps   = mutableListOf<StepItem>()

        return try {
            when (unknown) {

                // ── Find remainder r ──────────────────────────────────────────
                "r" -> {
                    val a = get("a")!!.toLong()
                    val m = get("m")!!.toLong()

                    if (m <= 0) return modError(inputs,
                        "Modulus m must be a positive integer.")

                    if (a < 0)
                        return modError(inputs, "Dividend a must be non-negative for basic mod.")
                    if (m <= 0)
                        return modError(inputs, "Modulus m must be a positive integer.")

                    steps += StepItem(1, "Write the expression",
                        "$a mod $m = ?")
                    steps += StepItem(2, "Understand what mod means",
                        "a mod m = remainder when a is divided by m\n" +
                                "We repeatedly subtract m from a until the result is less than m.")

                    // Show repeated subtraction for small values
                    if (a >= 0 && a / m <= 15) {
                        val subSteps = mutableListOf<String>()
                        var current = a
                        var count   = 0
                        while (current >= m) {
                            current -= m
                            count++
                            subSteps += "$a − (${m} × $count) = $current"
                        }
                        steps += StepItem(3, "Repeated subtraction process",
                            subSteps.joinToString("\n"))
                    } else {
                        val q = a / m
                        steps += StepItem(3, "Compute quotient q = a ÷ m",
                            "$a ÷ $m = $q  (integer part only, ignore decimal)")
                        steps += StepItem(4, "Verify: a = m × q + r",
                            "$a = $m × $q + ?\n" +
                                    "$a = ${m * q} + ?\n" +
                                    "r = $a − ${m * q} = ${a - m * q}")
                    }

                    val r = ((a % m) + m) % m
                    steps += StepItem(steps.size + 1,
                        "Result",
                        "$a mod $m = $r",
                        result = "r = $r")

                    ntResult("modular_arithmetic", "Modular Arithmetic",
                        "r", "r — Remainder",
                        "$r", steps, inputs)
                }

                // ── Find dividend a ───────────────────────────────────────────
                "a" -> {
                    val m = get("m")!!.toLong()
                    val r = get("r")!!.toLong()

                    if (m <= 0) return modError(inputs,
                        "Modulus m must be a positive integer.")
                    if (r < 0 || r >= m) return modError(inputs,
                        "Remainder r must satisfy 0 ≤ r < m. Got r=$r, m=$m.")

                    steps += StepItem(1, "Write the relationship",
                        "a mod $m = $r\n" +
                                "This means: a = m × q + r  for some integer q ≥ 0")
                    steps += StepItem(2, "General form",
                        "a = $m × q + $r\n" +
                                "where q = 0, 1, 2, 3, ...")
                    steps += StepItem(3, "Possible values of a",
                        (0..5).joinToString("\n") { q ->
                            "q = $q → a = $m × $q + $r = ${m * q + r}"
                        } + "\n...")
                    steps += StepItem(4, "Smallest positive solution",
                        "When q = 0: a = $r\n" +
                                "When q = 1: a = ${m + r}",
                        result = "Smallest a = $r  (general: a = ${m}q + $r)")

                    ntResult("modular_arithmetic", "Modular Arithmetic",
                        "a", "a — Dividend",
                        "a = ${m}q + $r  (smallest: $r)", steps, inputs)
                }

                // ── Find modulus m ────────────────────────────────────────────
                "m" -> {
                    val a = get("a")!!.toLong()
                    val r = get("r")!!.toLong()

                    if (r < 0) return modError(inputs,
                        "Remainder r must be non-negative.")
                    if (r >= a) return modError(inputs,
                        "Remainder r must be less than a.")

                    steps += StepItem(1, "Write the relationship",
                        "a mod m = r\n" +
                                "$a mod m = $r\n" +
                                "This means: m divides (a − r) exactly, and m > r")
                    steps += StepItem(2, "Compute a − r",
                        "$a − $r = ${a - r}")

                    // Find all factors of (a - r) that are > r
                    val diff    = a - r
                    val factors = (1..diff).filter { diff % it == 0L && it > r }

                    steps += StepItem(3, "Find all divisors of ${a - r} that are greater than r = $r",
                        "Divisors of $diff: ${(1..diff).filter { diff % it == 0L }.joinToString(", ")}\n" +
                                "Must be > $r: ${factors.joinToString(", ")}")

                    if (factors.isEmpty())
                        return modError(inputs,
                            "No valid modulus found. Check that r < a and (a−r) has a divisor > r.")

                    steps += StepItem(4, "Verify each candidate",
                        factors.joinToString("\n") { m ->
                            "$a mod $m = ${((a % m) + m) % m}  ✓"
                        },
                        result = "Valid m: ${factors.joinToString(", ")}")

                    ntResult("modular_arithmetic", "Modular Arithmetic",
                        "m", "m — Modulus",
                        "m ∈ {${factors.joinToString(", ")}}", steps, inputs)
                }

                else -> modError(inputs, "Unknown variable.")
            }
        } catch (e: Exception) {
            modError(inputs, "Invalid input. Please check your values.")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
// MODULAR ARITHMETIC — OPERATIONS
// ─────────────────────────────────────────────────────────────────────────

    fun solveModularOperation(
        operation: String,
        inputs: Map<String, String>
    ): CalculationResult {
        return try {
            when (operation) {
                "addition"       -> modularAddition(inputs)
                "subtraction"    -> modularSubtraction(inputs)
                "multiplication" -> modularMultiplication(inputs)
                "division"       -> modularDivision(inputs)
                else             -> modError(inputs, "Unknown operation.")
            }
        } catch (e: Exception) {
            modError(inputs, "Invalid input. Please check your values.")
        }
    }

    private fun modularAddition(inputs: Map<String, String>): CalculationResult {
        val mainVars   = listOf("a", "b", "m", "r")
        val mainBlanks = mainVars.filter { inputs[it].isNullOrBlank() }

        if (mainBlanks.size > 1)
            return modError(inputs, "Leave exactly one field blank to solve for it.")
        if (mainBlanks.isEmpty())
            return modError(inputs, "All fields are filled — nothing to solve.")

        val get     = { key: String -> inputs[key]?.trim()?.toLongOrNull() }
        val steps   = mutableListOf<StepItem>()
        val unknown = mainBlanks.first()

        when (unknown) {
            "r" -> {
                val a = get("a")!!; val b = get("b")!!; val m = get("m")!!
                if (m <= 0) return modError(inputs, "Modulus m must be positive.")
                steps += StepItem(1, "Write the expression", "(a + b) mod m = r")
                steps += StepItem(2, "Substitute known values",
                    "($a + $b) mod $m")
                val sum = a + b
                steps += StepItem(3, "Compute a + b",
                    "$a + $b = $sum")
                steps += appendModSteps(sum, m, steps.size + 1)
                val r = ((sum % m) + m) % m
                steps += StepItem(steps.size + 1, "Result",
                    "($a + $b) mod $m = $r",
                    result = "r = $r")
                return modOpResult("addition", "r", "r — Result", "$r", steps, inputs)
            }
            "a" -> {
                val b = get("b")!!; val m = get("m")!!; val r = get("r")!!
                if (m <= 0) return modError(inputs, "Modulus m must be positive.")
                steps += StepItem(1, "Write the relationship",
                    "(a + $b) mod $m = $r")
                steps += StepItem(2, "Rearrange",
                    "a mod $m = ($r − $b) mod $m\n" +
                            "a mod $m = ${(r - b)} mod $m\n" +
                            "a mod $m = ${((( r - b) % m + m) % m)}")
                val aVal = (((r - b) % m) + m) % m
                steps += StepItem(3, "General solution",
                    "a = $aVal + $m × k,  k = 0, 1, 2, ...\n" +
                            "Smallest non-negative a = $aVal",
                    result = "a = $aVal  (general: $aVal + ${m}k)")
                return modOpResult("addition", "a", "a — First operand",
                    "a = $aVal (+ multiples of $m)", steps, inputs)
            }
            "b" -> {
                val a = get("a")!!; val m = get("m")!!; val r = get("r")!!
                if (m <= 0) return modError(inputs, "Modulus m must be positive.")
                steps += StepItem(1, "Write the relationship",
                    "($a + b) mod $m = $r")
                steps += StepItem(2, "Rearrange",
                    "b mod $m = ($r − $a) mod $m\n" +
                            "b mod $m = ${(r - a)} mod $m\n" +
                            "b mod $m = ${(((r - a) % m + m) % m)}")
                val bVal = (((r - a) % m) + m) % m
                steps += StepItem(3, "General solution",
                    "b = $bVal + $m × k,  k = 0, 1, 2, ...\n" +
                            "Smallest non-negative b = $bVal",
                    result = "b = $bVal  (general: $bVal + ${m}k)")
                return modOpResult("addition", "b", "b — Second operand",
                    "b = $bVal (+ multiples of $m)", steps, inputs)
            }
            "m" -> {
                val a = get("a")!!; val b = get("b")!!; val r = get("r")!!
                val sum = a + b
                steps += StepItem(1, "Write the relationship",
                    "($a + $b) mod m = $r")
                steps += StepItem(2, "Compute a + b",
                    "$a + $b = $sum")
                steps += StepItem(3, "Find m such that $sum mod m = $r",
                    "m must divide ($sum − $r) = ${sum - r}\n" +
                            "and m > $r")
                val diff    = sum - r
                if (diff <= 0) return modError(inputs,
                    "No valid modulus — (a+b) must be greater than r.")
                val factors = (1..diff).filter { diff % it == 0L && it > r }
                steps += StepItem(4, "Divisors of $diff greater than $r",
                    factors.joinToString(", ").ifEmpty { "None found" },
                    result = if (factors.isEmpty()) "No valid m"
                    else "m ∈ {${factors.joinToString(", ")}}")
                return modOpResult("addition", "m", "m — Modulus",
                    if (factors.isEmpty()) "No valid m"
                    else "m ∈ {${factors.joinToString(", ")}}", steps, inputs)
            }
            else -> return modError(inputs, "Unknown variable.")
        }
    }

    private fun modularSubtraction(inputs: Map<String, String>): CalculationResult {
        val mainVars   = listOf("a", "b", "m", "r")
        val mainBlanks = mainVars.filter { inputs[it].isNullOrBlank() }

        if (mainBlanks.size > 1)
            return modError(inputs, "Leave exactly one field blank to solve for it.")
        if (mainBlanks.isEmpty())
            return modError(inputs, "All fields are filled — nothing to solve.")

        val get     = { key: String -> inputs[key]?.trim()?.toLongOrNull() }
        val steps   = mutableListOf<StepItem>()
        val unknown = mainBlanks.first()

        when (unknown) {
            "r" -> {
                val a = get("a")!!; val b = get("b")!!; val m = get("m")!!
                if (m <= 0) return modError(inputs, "Modulus m must be positive.")
                steps += StepItem(1, "Write the expression", "(a − b) mod m = r")
                steps += StepItem(2, "Substitute known values",
                    "($a − $b) mod $m")
                val diff = a - b
                steps += StepItem(3, "Compute a − b",
                    "$a − $b = $diff")
                steps += appendModSteps(diff, m, steps.size + 1)
                val r = ((diff % m) + m) % m
                steps += StepItem(steps.size + 1, "Result",
                    "($a − $b) mod $m = $r",
                    result = "r = $r")
                return modOpResult("subtraction", "r", "r — Result", "$r", steps, inputs)
            }
            "a" -> {
                val b = get("b")!!; val m = get("m")!!; val r = get("r")!!
                if (m <= 0) return modError(inputs, "Modulus m must be positive.")
                steps += StepItem(1, "Write the relationship", "(a − $b) mod $m = $r")
                steps += StepItem(2, "Rearrange", "a mod $m = ($r + $b) mod $m")
                val aVal = (((r + b) % m) + m) % m
                steps += StepItem(3, "Compute ($r + $b) mod $m",
                    "($r + $b) = ${r + b}\n" +
                            "${r + b} mod $m = $aVal",
                    result = "a = $aVal  (general: $aVal + ${m}k)")
                return modOpResult("subtraction", "a", "a — First operand",
                    "a = $aVal (+ multiples of $m)", steps, inputs)
            }
            "b" -> {
                val a = get("a")!!; val m = get("m")!!; val r = get("r")!!
                if (m <= 0) return modError(inputs, "Modulus m must be positive.")
                steps += StepItem(1, "Write the relationship", "($a − b) mod $m = $r")
                steps += StepItem(2, "Rearrange", "b mod $m = ($a − $r) mod $m")
                val bVal = ((( a - r) % m) + m) % m
                steps += StepItem(3, "Compute ($a − $r) mod $m",
                    "($a − $r) = ${a - r}\n" +
                            "${a - r} mod $m = $bVal",
                    result = "b = $bVal  (general: $bVal + ${m}k)")
                return modOpResult("subtraction", "b", "b — Second operand",
                    "b = $bVal (+ multiples of $m)", steps, inputs)
            }
            "m" -> {
                val a = get("a")!!; val b = get("b")!!; val r = get("r")!!
                val diff = a - b
                steps += StepItem(1, "Write the relationship", "($a − $b) mod m = $r")
                steps += StepItem(2, "Compute a − b", "$a − $b = $diff")
                steps += StepItem(3, "Find m such that $diff mod m = $r",
                    "m must divide ($diff − $r) = ${diff - r} and m > $r")
                val target = diff - r
                if (target <= 0) return modError(inputs,
                    "No valid modulus — (a−b) must be greater than r.")
                val factors = (1..target).filter { target % it == 0L && it > r }
                steps += StepItem(4, "Valid moduli",
                    factors.joinToString(", ").ifEmpty { "None" },
                    result = if (factors.isEmpty()) "No valid m"
                    else "m ∈ {${factors.joinToString(", ")}}")
                return modOpResult("subtraction", "m", "m — Modulus",
                    if (factors.isEmpty()) "No valid m"
                    else "m ∈ {${factors.joinToString(", ")}}", steps, inputs)
            }
            else -> return modError(inputs, "Unknown variable.")
        }
    }

    private fun modularMultiplication(inputs: Map<String, String>): CalculationResult {
        val mainVars   = listOf("a", "b", "m", "r")
        val mainBlanks = mainVars.filter { inputs[it].isNullOrBlank() }

        if (mainBlanks.size > 1)
            return modError(inputs, "Leave exactly one field blank to solve for it.")
        if (mainBlanks.isEmpty())
            return modError(inputs, "All fields are filled — nothing to solve.")

        val get     = { key: String -> inputs[key]?.trim()?.toLongOrNull() }
        val steps   = mutableListOf<StepItem>()
        val unknown = mainBlanks.first()

        when (unknown) {
            "r" -> {
                val a = get("a")!!; val b = get("b")!!; val m = get("m")!!
                if (m <= 0) return modError(inputs, "Modulus m must be positive.")
                steps += StepItem(1, "Write the expression", "(a × b) mod m = r")
                steps += StepItem(2, "Substitute known values",
                    "($a × $b) mod $m")
                val product = a * b
                // Show repeated addition for small values
                if (b <= 10) {
                    steps += StepItem(3, "Compute a × b as repeated addition",
                        (1..b).joinToString(" + ") { "$a" } + " = $product")
                } else {
                    steps += StepItem(3, "Compute a × b",
                        "$a × $b = $product")
                }
                steps += appendModSteps(product, m, steps.size + 1)
                val r = ((product % m) + m) % m
                steps += StepItem(steps.size + 1, "Result",
                    "($a × $b) mod $m = $r",
                    result = "r = $r")
                return modOpResult("multiplication", "r", "r — Result", "$r", steps, inputs)
            }
            "a" -> {
                val b = get("b")!!; val m = get("m")!!; val r = get("r")!!
                if (m <= 0) return modError(inputs, "Modulus m must be positive.")
                if (b == 0L) return modError(inputs, "b cannot be 0 when solving for a.")
                steps += StepItem(1, "Write the relationship", "(a × $b) mod $m = $r")
                steps += StepItem(2, "Find all a in [0, m−1] satisfying the equation",
                    "Try each a from 0 to ${m - 1}:")
                val solutions = (0 until m).filter { ((it * b) % m + m) % m == r }
                steps += StepItem(3, "Candidates",
                    solutions.joinToString("\n") { a ->
                        "a = $a: ($a × $b) mod $m = ${((a * b) % m + m) % m} ✓"
                    }.ifEmpty { "No solution found in [0, ${m-1}]" },
                    result = if (solutions.isEmpty()) "No solution"
                    else "a ∈ {${solutions.joinToString(", ")}}")
                return modOpResult("multiplication", "a", "a — First operand",
                    if (solutions.isEmpty()) "No solution"
                    else "a ∈ {${solutions.joinToString(", ")}}", steps, inputs)
            }
            "b" -> {
                val a = get("a")!!; val m = get("m")!!; val r = get("r")!!
                if (m <= 0) return modError(inputs, "Modulus m must be positive.")
                if (a == 0L) return modError(inputs, "a cannot be 0 when solving for b.")
                steps += StepItem(1, "Write the relationship", "($a × b) mod $m = $r")
                steps += StepItem(2, "Find all b in [0, m−1] satisfying the equation",
                    "Try each b from 0 to ${m - 1}:")
                val solutions = (0 until m).filter { ((a * it) % m + m) % m == r }
                steps += StepItem(3, "Candidates",
                    solutions.joinToString("\n") { b ->
                        "b = $b: ($a × $b) mod $m = ${((a * b) % m + m) % m} ✓"
                    }.ifEmpty { "No solution found in [0, ${m-1}]" },
                    result = if (solutions.isEmpty()) "No solution"
                    else "b ∈ {${solutions.joinToString(", ")}}")
                return modOpResult("multiplication", "b", "b — Second operand",
                    if (solutions.isEmpty()) "No solution"
                    else "b ∈ {${solutions.joinToString(", ")}}", steps, inputs)
            }
            "m" -> {
                val a = get("a")!!; val b = get("b")!!; val r = get("r")!!
                val product = a * b
                steps += StepItem(1, "Write the relationship", "($a × $b) mod m = $r")
                steps += StepItem(2, "Compute a × b", "$a × $b = $product")
                steps += StepItem(3, "Find m such that $product mod m = $r",
                    "m must divide ($product − $r) = ${product - r} and m > $r")
                val diff = product - r
                if (diff <= 0) return modError(inputs,
                    "No valid modulus — (a×b) must be greater than r.")
                val factors = (1..diff).filter { diff % it == 0L && it > r }
                steps += StepItem(4, "Valid moduli",
                    factors.joinToString(", ").ifEmpty { "None" },
                    result = if (factors.isEmpty()) "No valid m"
                    else "m ∈ {${factors.joinToString(", ")}}")
                return modOpResult("multiplication", "m", "m — Modulus",
                    if (factors.isEmpty()) "No valid m"
                    else "m ∈ {${factors.joinToString(", ")}}", steps, inputs)
            }
            else -> return modError(inputs, "Unknown variable.")
        }
    }

    private fun modularDivision(inputs: Map<String, String>): CalculationResult {
        val mainVars   = listOf("a", "b", "m", "r")
        val mainBlanks = mainVars.filter { inputs[it].isNullOrBlank() }

        if (mainBlanks.size > 1)
            return modError(inputs, "Leave exactly one field blank to solve for it.")
        if (mainBlanks.isEmpty())
            return modError(inputs, "All fields are filled — nothing to solve.")

        val get     = { key: String -> inputs[key]?.trim()?.toLongOrNull() }
        val steps   = mutableListOf<StepItem>()
        val unknown = mainBlanks.first()

        // Modular division: (a / b) mod m = (a × b⁻¹) mod m
        // b⁻¹ exists only if gcd(b, m) = 1

        when (unknown) {
            "r" -> {
                val a = get("a")!!; val b = get("b")!!; val m = get("m")!!
                if (m <= 0) return modError(inputs, "Modulus m must be positive.")
                if (b == 0L) return modError(inputs, "b cannot be 0 in division.")

                steps += StepItem(1, "Write the expression",
                    "(a / b) mod m = (a × b⁻¹) mod m")
                steps += StepItem(2, "Substitute known values",
                    "($a / $b) mod $m = ($a × $b⁻¹) mod $m")

                val g = gcd(b, m)
                steps += StepItem(3, "Check if modular inverse of b exists",
                    "gcd($b, $m) = $g\n" +
                            if (g == 1L)
                                "gcd = 1 ✓ — inverse exists"
                            else
                                "gcd ≠ 1 ✗ — modular inverse does not exist")

                if (g != 1L) return modError(inputs,
                    "Modular inverse of $b mod $m does not exist because gcd($b, $m) = $g ≠ 1.")

                val inv = modInverse(b, m)
                steps += StepItem(4, "Find b⁻¹ mod $m using extended Euclidean algorithm",
                    extendedEuclideanSteps(b, m) +
                            "\n\nb⁻¹ mod $m = $inv")

                val product = ((a % m) * inv) % m
                val r = ((product % m) + m) % m
                steps += StepItem(5, "Compute (a × b⁻¹) mod m",
                    "($a × $inv) mod $m\n" +
                            "= ${a * inv} mod $m\n" +
                            "= $r",
                    result = "r = $r")

                return modOpResult("division", "r", "r — Result", "$r", steps, inputs)
            }
            "a" -> {
                val b = get("b")!!; val m = get("m")!!; val r = get("r")!!
                if (m <= 0) return modError(inputs, "Modulus m must be positive.")
                steps += StepItem(1, "Write the relationship",
                    "(a / $b) mod $m = $r\n" +
                            "→ a ≡ r × b (mod m)")
                steps += StepItem(2, "Rearrange",
                    "a mod $m = ($r × $b) mod $m\n" +
                            "= ${r * b} mod $m")
                val aVal = ((r * b) % m + m) % m
                steps += StepItem(3, "Compute",
                    "${r * b} mod $m = $aVal",
                    result = "a = $aVal  (general: $aVal + ${m}k)")
                return modOpResult("division", "a", "a — Dividend",
                    "a = $aVal (+ multiples of $m)", steps, inputs)
            }
            "b" -> {
                val a = get("a")!!; val m = get("m")!!; val r = get("r")!!
                if (m <= 0) return modError(inputs, "Modulus m must be positive.")
                steps += StepItem(1, "Write the relationship",
                    "($a / b) mod $m = $r")
                steps += StepItem(2, "Find b such that ($a / b) mod $m = $r",
                    "Try all b in [1, m−1] where gcd(b,m)=1:")
                val solutions = (1 until m).filter { b ->
                    gcd(b.toLong(), m) == 1L &&
                            ((a * modInverse(b.toLong(), m)) % m + m) % m == r
                }
                steps += StepItem(3, "Valid values of b",
                    solutions.joinToString("\n") { b ->
                        "b = $b: ($a × ${modInverse(b.toLong(), m)}) mod $m = " +
                                "${((a * modInverse(b.toLong(), m)) % m + m) % m} ✓"
                    }.ifEmpty { "No solution found" },
                    result = if (solutions.isEmpty()) "No solution"
                    else "b ∈ {${solutions.joinToString(", ")}}")
                return modOpResult("division", "b", "b — Divisor",
                    if (solutions.isEmpty()) "No solution"
                    else "b ∈ {${solutions.joinToString(", ")}}", steps, inputs)
            }
            "m" -> {
                return modError(inputs,
                    "Solving for m in modular division is not supported — " +
                            "too many possible values. Try specifying m.")
            }
            else -> return modError(inputs, "Unknown variable.")
        }
    }

// ─── Repeated subtraction steps helper ───────────────────────────────────────

    private fun appendModSteps(value: Long, m: Long, startStep: Int): StepItem {
        return if (value >= 0 && value / m <= 15) {
            val subSteps = mutableListOf<String>()
            var current = value
            var count   = 0
            while (current >= m) {
                current -= m
                count++
                subSteps += "$value − ($m × $count) = $current"
            }
            if (subSteps.isEmpty())
                StepItem(startStep, "Value is already less than m", "$value < $m, so remainder = $value")
            else
                StepItem(startStep, "Repeated subtraction until result < m",
                    subSteps.joinToString("\n"))
        } else {
            val q = value / m
            val r = ((value % m) + m) % m
            StepItem(startStep, "Compute using division",
                "$value = $m × $q + $r\n" +
                        "Remainder = $r")
        }
    }

// ─── GCD (Euclidean algorithm) ────────────────────────────────────────────────

    private fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

// ─── Modular inverse (extended Euclidean) ────────────────────────────────────

    private fun modInverse(a: Long, m: Long): Long {
        var (old_r, r) = Pair(a, m)
        var (old_s, s) = Pair(1L, 0L)
        while (r != 0L) {
            val q = old_r / r
            val tmp_r = r; r = old_r - q * r; old_r = tmp_r
            val tmp_s = s; s = old_s - q * s; old_s = tmp_s
        }
        return ((old_s % m) + m) % m
    }

// ─── Extended Euclidean steps (for display) ───────────────────────────────────

    private fun extendedEuclideanSteps(a: Long, m: Long): String {
        val lines = mutableListOf<String>()
        var (old_r, r) = Pair(a, m)
        var (old_s, s) = Pair(1L, 0L)
        lines += "Extended Euclidean Algorithm for $a⁻¹ mod $m:"
        lines += "─".repeat(35)
        while (r != 0L) {
            val q = old_r / r
            lines += "$old_r = $q × $r + ${old_r - q * r}"
            val tmp_r = r; r = old_r - q * r; old_r = tmp_r
            val tmp_s = s; s = old_s - q * s; old_s = tmp_s
        }
        lines += "─".repeat(35)
        lines += "gcd = $old_r"
        return lines.joinToString("\n")
    }

    private fun modOpResult(
        operation: String,
        solvedFor: String,
        solvedForLabel: String,
        answer: String,
        steps: List<StepItem>,
        inputs: Map<String, String>
    ) = CalculationResult(
        topicId        = "modular_arithmetic",
        topicName      = "Modular Arithmetic — ${operation.replaceFirstChar { it.uppercase() }}",
        solvedFor      = solvedFor,
        solvedForLabel = solvedForLabel,
        answer         = answer,
        answerWithUnit = answer,
        steps          = steps,
        inputs         = inputs.mapKeys { (key, _) ->
            when (key) {
                "a" -> "a — First operand"
                "b" -> "b — Second operand"
                "m" -> "m — Modulus"
                "r" -> "r — Result"
                else -> key
            }
        }
    )

    // ─────────────────────────────────────────────────────────────────────────
    // ZELLER'S CONGRUENCE
    // Input: day, month, year
    // Output: day of week
    // ─────────────────────────────────────────────────────────────────────────

    fun solveZeller(inputs: Map<String, String>): CalculationResult {
        val day   = inputs["day"]?.trim()?.toIntOrNull()
            ?: return zellerError("Please enter a valid day (1–31).")
        val month = inputs["month"]?.trim()?.toIntOrNull()
            ?: return zellerError("Please enter a valid month (1–12).")
        val year  = inputs["year"]?.trim()?.toIntOrNull()
            ?: return zellerError("Please enter a valid year (e.g. 2025).")

        // Validate day against month
        val daysInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11            -> 30
            2 -> if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) 29 else 28
            else -> 31
        }
        if (day > daysInMonth)
            return zellerError("Invalid date: $month/$day/$year. Month $month only has $daysInMonth days.")

        if (day !in 1..31)   return zellerError("Day must be between 1 and 31.")
        if (month !in 1..12) return zellerError("Month must be between 1 and 12.")
        if (year < 1)        return zellerError("Year must be a positive number.")

        val steps = mutableListOf<StepItem>()

        // Zeller adjustment: Jan and Feb are months 13 and 14 of previous year
        val adjustedMonth: Int
        val adjustedYear: Int
        if (month <= 2) {
            adjustedMonth = month + 12
            adjustedYear  = year - 1
        } else {
            adjustedMonth = month
            adjustedYear  = year
        }

        steps += StepItem(1, "Write the date",
            "Date: $day / $month / $year")

        steps += StepItem(2, "Adjust month and year (Zeller's rule)",
            "January and February are treated as months 13 and 14 of the previous year.\n" +
                    if (month <= 2)
                        "Month $month → adjusted month = ${adjustedMonth}\n" +
                                "Year $year → adjusted year = ${adjustedYear}"
                    else
                        "Month $month is March or later — no adjustment needed.\n" +
                                "Adjusted month = $adjustedMonth,  Adjusted year = $adjustedYear")

        val q = day
        val m = adjustedMonth
        val k = adjustedYear % 100
        val j = adjustedYear / 100

        steps += StepItem(3, "Identify all variables",
            "q = day of month = $q\n" +
                    "m = adjusted month = $m\n" +
                    "K = year of century = $adjustedYear mod 100 = $k\n" +
                    "J = zero-based century = ⌊$adjustedYear / 100⌋ = $j")

        steps += StepItem(4, "Write Zeller's formula",
            "h = (q + ⌊13(m+1)/5⌋ + K + ⌊K/4⌋ + ⌊J/4⌋ − 2J) mod 7")

        // Compute each part
        val part1 = q
        val part2 = floor(13.0 * (m + 1) / 5).toInt()
        val part3 = k
        val part4 = k / 4
        val part5 = j / 4
        val part6 = 2 * j

        steps += StepItem(5, "Compute each part of the formula",
            "q                = $part1\n" +
                    "⌊13(m+1)/5⌋      = ⌊13 × (${m}+1) / 5⌋\n" +
                    "                 = ⌊13 × ${m + 1} / 5⌋\n" +
                    "                 = ⌊${13.0 * (m + 1)} / 5⌋\n" +
                    "                 = ⌊${13.0 * (m + 1) / 5}⌋\n" +
                    "                 = $part2\n" +
                    "K                = $part3\n" +
                    "⌊K/4⌋            = ⌊$k / 4⌋ = $part4\n" +
                    "⌊J/4⌋            = ⌊$j / 4⌋ = $part5\n" +
                    "2J               = 2 × $j = $part6")

        val sum = part1 + part2 + part3 + part4 + part5 - part6
        steps += StepItem(6, "Sum all parts",
            "$part1 + $part2 + $part3 + $part4 + $part5 − $part6\n" +
                    "= $sum")

        val h = ((sum % 7) + 7) % 7
        steps += StepItem(7, "Apply mod 7",
            "$sum mod 7 = $h",
            result = "h = $h")

        val dayName = when (h) {
            0 -> "Saturday"
            1 -> "Sunday"
            2 -> "Monday"
            3 -> "Tuesday"
            4 -> "Wednesday"
            5 -> "Thursday"
            6 -> "Friday"
            else -> "Unknown"
        }

        steps += StepItem(8, "Convert h to day name",
            "h = 0 → Saturday\n" +
                    "h = 1 → Sunday\n" +
                    "h = 2 → Monday\n" +
                    "h = 3 → Tuesday\n" +
                    "h = 4 → Wednesday\n" +
                    "h = 5 → Thursday\n" +
                    "h = 6 → Friday\n\n" +
                    "h = $h → $dayName",
            result = "$day/$month/$year falls on a $dayName")

        return ntResult(
            "zellers_congruence", "Zeller's Congruence",
            "day", "Day of the week",
            dayName, steps, inputs)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun ntResult(
        topicId: String, topicName: String,
        solvedFor: String, solvedForLabel: String,
        answer: String, steps: List<StepItem>,
        inputs: Map<String, String>
    ) = CalculationResult(
        topicId        = topicId,
        topicName      = topicName,
        solvedFor      = solvedFor,
        solvedForLabel = solvedForLabel,
        answer         = answer,
        answerWithUnit = answer,
        steps          = steps,
        inputs         = inputs
    )

    private fun modError(inputs: Map<String, String>, message: String) =
        CalculationResult(
            topicId = "modular_arithmetic", topicName = "Modular Arithmetic",
            solvedFor = "", solvedForLabel = "", answer = "",
            steps = emptyList(), inputs = inputs,
            isError = true, errorMessage = message)

    private fun zellerError(message: String) =
        CalculationResult(
            topicId = "zellers_congruence", topicName = "Zeller's Congruence",
            solvedFor = "", solvedForLabel = "", answer = "",
            steps = emptyList(), inputs = emptyMap(),
            isError = true, errorMessage = message)
}