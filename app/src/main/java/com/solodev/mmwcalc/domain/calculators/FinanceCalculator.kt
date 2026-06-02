package com.solodev.mmwcalc.domain.calculators

import com.solodev.mmwcalc.domain.models.CalculationResult
import com.solodev.mmwcalc.domain.models.StepItem
import kotlin.math.*

object FinanceCalculator {

    // ─────────────────────────────────────────────────────────────────────────
    // SIMPLE INTEREST
    // I = Prt,  F = P + I = P(1 + rt)
    // Solve for any one blank: P, r, t, I, F
    // ─────────────────────────────────────────────────────────────────────────

    fun solveSimpleInterest(inputs: Map<String, String>): CalculationResult {
        val get = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }

        val p = get("p"); val r = get("r"); val t = get("t")
        val i = get("i"); val f = get("f")

        val steps = mutableListOf<StepItem>()
        steps += StepItem(1, "Write the Simple Interest formulas",
            "I = P × r × t\n" +
                    "F = P + I = P(1 + rt)\n" +
                    "where r is entered as % (e.g. 5 means 5%)")

        return try {
            // Guard: rate must be positive
            if (r != null && r <= 0)
                return finError("simple_interest", "Simple Interest", inputs,
                    "Interest rate r must be positive.")
            // Guard: time must be positive
            if (t != null && t <= 0)
                return finError("simple_interest", "Simple Interest", inputs,
                    "Time t must be positive.")
            // Guard: principal must be positive
            if (p != null && p <= 0)
                return finError("simple_interest", "Simple Interest", inputs,
                    "Principal P must be positive.")

            // ── Case: P, r, t all given → compute I and F as bonus ───────────────
            if (p != null && r != null && t != null) {
                val rDec = r / 100.0
                steps += StepItem(2, "All core values given — compute I and F",
                    "P = ₱${p.fmt()},  r = ${r.fmt()}% = ${rDec.fmt()},  t = ${t.fmt()} years")
                val pr = p * rDec
                steps += StepItem(3, "Compute P × r",
                    "${p.fmt()} × ${rDec.fmt()} = ${pr.fmt()}")
                val computedI = pr * t
                steps += StepItem(4, "Compute I = P × r × t",
                    "${pr.fmt()} × ${t.fmt()} = ${computedI.fmt()}",
                    result = "I = ₱${computedI.fmt()}")
                val computedF = p + computedI
                steps += StepItem(5, "Compute F = P + I",
                    "${p.fmt()} + ${computedI.fmt()} = ${computedF.fmt()}",
                    result = "F = ₱${computedF.fmt()}")

                // Validate against given I or F if provided
                if (i != null && kotlin.math.abs(i - computedI) > 0.01)
                    return finError("simple_interest", "Simple Interest", inputs,
                        "Contradiction: Given I = ₱${i.fmt()} but computed I = ₱${computedI.fmt()}. Check your values.")
                if (f != null && kotlin.math.abs(f - computedF) > 0.01)
                    return finError("simple_interest", "Simple Interest", inputs,
                        "Contradiction: Given F = ₱${f.fmt()} but computed F = ₱${computedF.fmt()}. Check your values.")

                return finResult("simple_interest", "Simple Interest",
                    "all", "Interest and Maturity Value",
                    "I = ₱${computedI.fmt()}  |  F = ₱${computedF.fmt()}",
                    steps, inputs)
            }

            // ── Case: solve for P ─────────────────────────────────────────────────
            if (p == null) {
                if (r == null || t == null)
                    return finError("simple_interest", "Simple Interest", inputs,
                        "To find P, please provide r and t, plus either I or F.")
                val rDec = r / 100.0
                if (i != null) {
                    steps += StepItem(2, "Rearrange I = Prt for P", "P = I / (r × t)")
                    steps += StepItem(3, "Substitute",
                        "P = ${i.fmt()} / (${rDec.fmt()} × ${t.fmt()})")
                    val rt = rDec * t
                    steps += StepItem(4, "Compute r × t", "${rDec.fmt()} × ${t.fmt()} = ${rt.fmt()}")
                    val computedP = i / rt
                    steps += StepItem(5, "Divide", "${i.fmt()} ÷ ${rt.fmt()} = ${computedP.fmt()}",
                        result = "P = ₱${computedP.fmt()}")
                    // Bonus F
                    val computedF = computedP + i
                    steps += StepItem(6, "─── Bonus: F = P + I ───",
                        "${computedP.fmt()} + ${i.fmt()} = ${computedF.fmt()}",
                        result = "F = ₱${computedF.fmt()}")
                    return finResult("simple_interest", "Simple Interest",
                        "p", "P — Principal", "₱${computedP.fmt()}", steps, inputs)
                } else if (f != null) {
                    steps += StepItem(2, "Rearrange F = P(1+rt) for P", "P = F / (1 + rt)")
                    val rt = rDec * t
                    steps += StepItem(3, "Compute (1 + rt)", "1 + ${rt.fmt()} = ${(1+rt).fmt()}")
                    val computedP = f / (1 + rt)
                    steps += StepItem(4, "Divide", "${f.fmt()} ÷ ${(1+rt).fmt()} = ${computedP.fmt()}",
                        result = "P = ₱${computedP.fmt()}")
                    // Bonus I
                    val computedI = f - computedP
                    steps += StepItem(5, "─── Bonus: I = F − P ───",
                        "${f.fmt()} − ${computedP.fmt()} = ${computedI.fmt()}",
                        result = "I = ₱${computedI.fmt()}")
                    return finResult("simple_interest", "Simple Interest",
                        "p", "P — Principal", "₱${computedP.fmt()}", steps, inputs)
                } else {
                    return finError("simple_interest", "Simple Interest", inputs,
                        "To find P, provide either I or F along with r and t.")
                }
            }

            // ── Case: solve for r ─────────────────────────────────────────────────
            if (r == null) {
                if (p == null || t == null)
                    return finError("simple_interest", "Simple Interest", inputs,
                        "To find r, please provide P and t, plus either I or F.")
                val actualI = i ?: (f?.minus(p))
                ?: return finError("simple_interest", "Simple Interest", inputs,
                    "To find r, provide either I or F.")
                if (i == null)
                    steps += StepItem(2, "Compute I = F − P",
                        "${f!!.fmt()} − ${p.fmt()} = ${actualI.fmt()}")
                steps += StepItem(steps.size + 1, "Rearrange I = Prt for r", "r = I / (P × t)")
                val pt = p * t
                steps += StepItem(steps.size + 1, "Compute P × t",
                    "${p.fmt()} × ${t.fmt()} = ${pt.fmt()}")
                val computedR = (actualI / pt) * 100
                steps += StepItem(steps.size + 1, "Divide and convert to %",
                    "${actualI.fmt()} ÷ ${pt.fmt()} × 100 = ${computedR.fmt()}%",
                    result = "r = ${computedR.fmt()}% per year")
                // Bonus F
                val computedF = p + actualI
                steps += StepItem(steps.size + 1, "─── Bonus: F = P + I ───",
                    "${p.fmt()} + ${actualI.fmt()} = ${computedF.fmt()}",
                    result = "F = ₱${computedF.fmt()}")
                return finResult("simple_interest", "Simple Interest",
                    "r", "r — Annual rate", "${computedR.fmt()}%", steps, inputs)
            }

            // ── Case: solve for t ─────────────────────────────────────────────────
            if (t == null) {
                if (p == null || r == null)
                    return finError("simple_interest", "Simple Interest", inputs,
                        "To find t, please provide P and r, plus either I or F.")
                val rDec = r / 100.0
                val actualI = i ?: (f?.minus(p))
                ?: return finError("simple_interest", "Simple Interest", inputs,
                    "To find t, provide either I or F.")
                if (i == null)
                    steps += StepItem(2, "Compute I = F − P",
                        "${f!!.fmt()} − ${p.fmt()} = ${actualI.fmt()}")
                steps += StepItem(steps.size + 1, "Rearrange I = Prt for t", "t = I / (P × r)")
                val pr = p * rDec
                steps += StepItem(steps.size + 1, "Compute P × r",
                    "${p.fmt()} × ${rDec.fmt()} = ${pr.fmt()}")
                val computedT = actualI / pr
                steps += StepItem(steps.size + 1, "Divide",
                    "${actualI.fmt()} ÷ ${pr.fmt()} = ${computedT.fmt()} years",
                    result = "t = ${computedT.fmt()} years")
                // Bonus F
                val computedF = p + actualI
                steps += StepItem(steps.size + 1, "─── Bonus: F = P + I ───",
                    "${p.fmt()} + ${actualI.fmt()} = ${computedF.fmt()}",
                    result = "F = ₱${computedF.fmt()}")
                return finResult("simple_interest", "Simple Interest",
                    "t", "t — Time (years)", "${computedT.fmt()} years", steps, inputs)
            }

            // ── Case: solve for I ─────────────────────────────────────────────────
            if (i == null) {
                if (f != null) {
                    // I = F - P
                    steps += StepItem(2, "Rearrange F = P + I for I", "I = F − P")
                    steps += StepItem(3, "Substitute",
                        "I = ${f.fmt()} − ${p.fmt()}")
                    val computedI = f - p
                    steps += StepItem(4, "Subtract",
                        "${f.fmt()} − ${p.fmt()} = ${computedI.fmt()}",
                        result = "I = ₱${computedI.fmt()}")
                    return finResult("simple_interest", "Simple Interest",
                        "i", "I — Interest earned", "₱${computedI.fmt()}", steps, inputs)
                }
                return finError("simple_interest", "Simple Interest", inputs,
                    "Please provide P, r, and t to compute I (or provide F to find I = F − P).")
            }

            // ── Case: solve for F ─────────────────────────────────────────────────
            if (f == null) {
                if (i != null) {
                    steps += StepItem(2, "F = P + I", "F = ${p.fmt()} + ${i.fmt()}")
                    val computedF = p + i
                    steps += StepItem(3, "Add",
                        "${p.fmt()} + ${i.fmt()} = ${computedF.fmt()}",
                        result = "F = ₱${computedF.fmt()}")
                    return finResult("simple_interest", "Simple Interest",
                        "f", "F — Maturity value", "₱${computedF.fmt()}", steps, inputs)
                }
                return finError("simple_interest", "Simple Interest", inputs,
                    "Please provide P, r, and t to compute F.")
            }

            finError("simple_interest", "Simple Interest", inputs,
                "Could not determine what to solve. Check your inputs.")

        } catch (e: Exception) {
            finError("simple_interest", "Simple Interest", inputs,
                "Invalid input. Please check your values.")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // COMPOUND INTEREST
    // F = P(1 + j/m)^(mt)
    // ─────────────────────────────────────────────────────────────────────────

    fun solveCompoundInterest(inputs: Map<String, String>): CalculationResult {
        val mainVars   = listOf("p", "j", "m", "t", "f")
        val mainBlanks = mainVars.filter { inputs[it].isNullOrBlank() }

        if (mainBlanks.size > 1)
            return finError("compound_interest", "Compound Interest", inputs,
                "Leave exactly one field blank to solve for it.")
        if (mainBlanks.isEmpty())
            return finError("compound_interest", "Compound Interest", inputs,
                "All fields are filled — nothing to solve.")

        val get     = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }
        val unknown = mainBlanks.first()
        val steps   = mutableListOf<StepItem>()

        steps += StepItem(1, "Write the Compound Interest formula",
            "F = P × (1 + j/m)^(m×t)\n" +
                    "where:\n" +
                    "  P = principal\n" +
                    "  j = nominal annual rate (as decimal)\n" +
                    "  m = compounding periods per year\n" +
                    "  t = time in years\n" +
                    "  F = future value")

        return try {
            val pv  = get("p");  val jv = get("j")
            val mv  = get("m");  val tv = get("t"); val fv2 = get("f")
            if (pv != null && pv <= 0)
                return finError("compound_interest", "Compound Interest", inputs,
                    "Principal P must be positive.")
            if (jv != null && jv <= 0)
                return finError("compound_interest", "Compound Interest", inputs,
                    "Nominal rate j must be positive.")
            if (mv != null && (mv < 1 || mv != kotlin.math.floor(mv)))
                return finError("compound_interest", "Compound Interest", inputs,
                    "Compounding periods m must be a positive whole number (1, 2, 4, 12, 365...).")
            if (tv != null && tv <= 0)
                return finError("compound_interest", "Compound Interest", inputs,
                    "Time t must be positive.")

            val answer: Double
            when (unknown) {
                "f" -> {
                    val p = get("p")!!
                    val j = get("j")!! / 100.0
                    val m = get("m")!!
                    val t = get("t")!!
                    steps += StepItem(2, "Substitute known values",
                        "F = ${p.fmt()} × (1 + ${j.fmt()}/${m.fmt()})^(${m.fmt()}×${t.fmt()})")
                    val rate = j / m
                    steps += StepItem(3, "Compute periodic rate j/m",
                        "${j.fmt()} / ${m.fmt()} = ${rate.fmt()}")
                    val onePlusRate = 1 + rate
                    steps += StepItem(4, "Compute (1 + j/m)",
                        "1 + ${rate.fmt()} = ${onePlusRate.fmt()}")
                    val exp = m * t
                    steps += StepItem(5, "Compute exponent m×t",
                        "${m.fmt()} × ${t.fmt()} = ${exp.fmt()}")
                    val factor = onePlusRate.pow(exp)
                    steps += StepItem(6, "Raise to power m×t",
                        "${onePlusRate.fmt()}^${exp.fmt()} = ${factor.fmt()}")
                    val f = p * factor
                    steps += StepItem(7, "Multiply by P",
                        "${p.fmt()} × ${factor.fmt()} = ${f.fmt()}",
                        result = "F = ₱${f.fmt()}")
                    val interest = f - p
                    steps += StepItem(8, "Total interest earned = F − P",
                        "${f.fmt()} − ${p.fmt()} = ${interest.fmt()}",
                        result = "Interest = ₱${interest.fmt()}")
                    // Effective rate
                    val er = (onePlusRate.pow(m) - 1) * 100
                    steps += StepItem(9, "Effective annual rate ER = (1 + j/m)^m − 1",
                        "(${onePlusRate.fmt()})^${m.fmt()} − 1 = ${er.fmt()}%",
                        result = "ER = ${er.fmt()}%")
                    answer = f
                }
                "p" -> {
                    val f = get("f")!!; val j = get("j")!! / 100.0
                    val m = get("m")!!; val t = get("t")!!
                    steps += StepItem(2, "Rearrange for P",
                        "P = F / (1 + j/m)^(mt)")
                    val rate   = j / m
                    val exp    = m * t
                    val factor = (1 + rate).pow(exp)
                    steps += StepItem(3, "Compute (1 + j/m)^(mt)",
                        "(1 + ${rate.fmt()})^${exp.fmt()} = ${factor.fmt()}")
                    val p = f / factor
                    steps += StepItem(4, "Divide F by the factor",
                        "${f.fmt()} / ${factor.fmt()} = ${p.fmt()}",
                        result = "P = ₱${p.fmt()}")
                    answer = p
                    val interest = f - p
                    steps += StepItem(5, "─── Bonus: Interest earned I = F − P ───",
                        "${f.fmt()} − ${p.fmt()} = ${interest.fmt()}",
                        result = "I = ₱${interest.fmt()}")
                }
                "j" -> {
                    val p = get("p")!!; val f = get("f")!!
                    val m = get("m")!!; val t = get("t")!!
                    steps += StepItem(2, "Rearrange for j",
                        "j = m × [(F/P)^(1/mt) − 1]")
                    val ratio = f / p
                    steps += StepItem(3, "Compute F/P",
                        "${f.fmt()} / ${p.fmt()} = ${ratio.fmt()}")
                    val exp = 1.0 / (m * t)
                    steps += StepItem(4, "Compute exponent 1/(mt)",
                        "1 / (${m.fmt()} × ${t.fmt()}) = ${exp.fmt()}")
                    val root = ratio.pow(exp)
                    steps += StepItem(5, "Take the root",
                        "${ratio.fmt()}^${exp.fmt()} = ${root.fmt()}")
                    val j = m * (root - 1) * 100
                    steps += StepItem(6, "Compute j = m × (root − 1)",
                        "${m.fmt()} × (${root.fmt()} − 1)\n" +
                                "= ${m.fmt()} × ${(root - 1).fmt()}\n" +
                                "= ${j.fmt()}%",
                        result = "j = ${j.fmt()}% per year")
                    answer = j
                }
                "t" -> {
                    val p = get("p")!!; val f = get("f")!!
                    val j = get("j")!! / 100.0; val m = get("m")!!
                    steps += StepItem(2, "Rearrange for t using logarithms",
                        "t = ln(F/P) / (m × ln(1 + j/m))")
                    val ratio = f / p
                    steps += StepItem(3, "Compute F/P",
                        "${f.fmt()} / ${p.fmt()} = ${ratio.fmt()}")
                    val lnRatio = ln(ratio)
                    steps += StepItem(4, "Compute ln(F/P)",
                        "ln(${ratio.fmt()}) = ${lnRatio.fmt()}")
                    val rate = j / m
                    val lnRate = ln(1 + rate)
                    steps += StepItem(5, "Compute m × ln(1 + j/m)",
                        "${m.fmt()} × ln(1 + ${rate.fmt()})\n" +
                                "= ${m.fmt()} × ln(${(1 + rate).fmt()})\n" +
                                "= ${m.fmt()} × ${lnRate.fmt()}\n" +
                                "= ${(m * lnRate).fmt()}")
                    val t = lnRatio / (m * lnRate)
                    steps += StepItem(6, "Divide",
                        "${lnRatio.fmt()} / ${(m * lnRate).fmt()} = ${t.fmt()} years",
                        result = "t = ${t.fmt()} years")
                    answer = t
                }
                "m" -> {
                    return finError("compound_interest", "Compound Interest", inputs,
                        "Solving for m (compounding periods) algebraically is not supported. " +
                                "Common values: 1=annually, 2=semi-annually, 4=quarterly, 12=monthly, 365=daily.")
                }
                else -> return finError("compound_interest", "Compound Interest",
                    inputs, "Unknown variable.")
            }

            val solvedLabel = when (unknown) {
                "f" -> "F — Future value"
                "p" -> "P — Principal"
                "j" -> "j — Nominal annual rate"
                "t" -> "t — Time (years)"
                else -> unknown
            }
            val answerDisplay = when (unknown) {
                "j"  -> "${answer.fmt()}%"
                "t"  -> "${answer.fmt()} years"
                else -> "₱${answer.fmt()}"
            }
            // Bonus: show interest for all cases where we have both P and F
            val pFinal = inputs.toMutableMap().also { it[unknown] = answer.fmt() }
            val pVal   = pFinal["p"]?.toDoubleOrNull()
            val fVal   = pFinal["f"]?.toDoubleOrNull()
            if (pVal != null && fVal != null && unknown != "f") {
                val interest = fVal - pVal
                val s = steps.size + 1
                steps += StepItem(s, "─── Bonus: Total Interest I = F − P ───",
                    "${fVal.fmt()} − ${pVal.fmt()} = ${interest.fmt()}",
                    result = "I = ₱${interest.fmt()}")
            }
            finResult("compound_interest", "Compound Interest",
                unknown, solvedLabel, answerDisplay, steps, inputs)

        } catch (e: Exception) {
            finError("compound_interest", "Compound Interest", inputs,
                "Invalid input. Please check your values.")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STOCKS — formula picker
    // ─────────────────────────────────────────────────────────────────────────

    fun solveStocks(formula: String, inputs: Map<String, String>): CalculationResult {
        return try {
            when (formula) {
                "dividend"     -> stocksDividend(inputs)
                "yield"        -> stocksYield(inputs)
                "eps"          -> stocksEPS(inputs)
                "pe"           -> stocksPE(inputs)
                "total_return" -> stocksTotalReturn(inputs)
                else -> finError("stocks", "Stocks", inputs, "Unknown formula.")
            }
        } catch (e: Exception) {
            finError("stocks", "Stocks", inputs,
                "Invalid input. Please check your values.")
        }
    }

    private fun stocksDividend(inputs: Map<String, String>): CalculationResult {
        val vars   = listOf("shares", "dividendPerShare", "totalDividend")
        val blanks = vars.filter { inputs[it].isNullOrBlank() }
        if (blanks.size != 1) return finError("stocks", "Stocks", inputs,
            "Leave exactly one field blank.")
        val get     = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }
        val unknown = blanks.first()
        val steps   = mutableListOf<StepItem>()
        steps += StepItem(1, "Formula: Total Dividend = Shares × Dividend per Share", "")
        val answer: Double
        when (unknown) {
            "totalDividend" -> {
                val s = get("shares")!!; val d = get("dividendPerShare")!!
                steps += StepItem(2, "Substitute", "Total Dividend = $s × ₱$d")
                answer = s * d
                steps += StepItem(3, "Multiply", "= ₱${answer.fmt()}", result = "Total Dividend = ₱${answer.fmt()}")
            }
            "shares" -> {
                val td = get("totalDividend")!!; val d = get("dividendPerShare")!!
                steps += StepItem(2, "Rearrange", "Shares = Total Dividend / Dividend per Share")
                steps += StepItem(3, "Substitute", "Shares = ${td.fmt()} / ${d.fmt()}")
                answer = td / d
                steps += StepItem(4, "Divide", "= ${answer.fmt()} shares", result = "Shares = ${answer.fmt()}")
            }
            "dividendPerShare" -> {
                val td = get("totalDividend")!!; val s = get("shares")!!
                steps += StepItem(2, "Rearrange", "Dividend per Share = Total Dividend / Shares")
                steps += StepItem(3, "Substitute", "= ${td.fmt()} / ${s.fmt()}")
                answer = td / s
                steps += StepItem(4, "Divide", "= ₱${answer.fmt()} per share", result = "Dividend/Share = ₱${answer.fmt()}")
            }
            else -> return finError("stocks", "Stocks", inputs, "Unknown variable.")
        }
        return finResult("stocks", "Stocks — Dividend",
            unknown, unknown, "₱${answer.fmt()}", steps, inputs)
    }

    private fun stocksYield(inputs: Map<String, String>): CalculationResult {
        val vars   = listOf("annualDividend", "marketPrice", "dividendYield")
        val blanks = vars.filter { inputs[it].isNullOrBlank() }
        if (blanks.size != 1) return finError("stocks", "Stocks", inputs, "Leave exactly one field blank.")
        val get     = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }
        val unknown = blanks.first()
        val steps   = mutableListOf<StepItem>()
        steps += StepItem(1, "Formula: Dividend Yield = (Annual Dividend / Market Price) × 100", "")
        val answer: Double
        when (unknown) {
            "dividendYield" -> {
                val d = get("annualDividend")!!; val mp = get("marketPrice")!!
                steps += StepItem(2, "Substitute", "Yield = (${d.fmt()} / ${mp.fmt()}) × 100")
                answer = (d / mp) * 100
                steps += StepItem(3, "Compute", "= ${answer.fmt()}%", result = "Dividend Yield = ${answer.fmt()}%")
            }
            "annualDividend" -> {
                val dy = get("dividendYield")!! / 100.0; val mp = get("marketPrice")!!
                steps += StepItem(2, "Rearrange", "Annual Dividend = Yield × Market Price")
                answer = dy * mp
                steps += StepItem(3, "Compute", "= ₱${answer.fmt()}", result = "Annual Dividend = ₱${answer.fmt()}")
            }
            "marketPrice" -> {
                val d = get("annualDividend")!!; val dy = get("dividendYield")!! / 100.0
                steps += StepItem(2, "Rearrange", "Market Price = Annual Dividend / Yield")
                answer = d / dy
                steps += StepItem(3, "Compute", "= ₱${answer.fmt()}", result = "Market Price = ₱${answer.fmt()}")
            }
            else -> return finError("stocks", "Stocks", inputs, "Unknown variable.")
        }
        return finResult("stocks", "Stocks — Dividend Yield",
            unknown, unknown, "${answer.fmt()}%", steps, inputs)
    }

    private fun stocksEPS(inputs: Map<String, String>): CalculationResult {
        val vars   = listOf("netIncome", "totalShares", "eps")
        val blanks = vars.filter { inputs[it].isNullOrBlank() }
        if (blanks.size != 1) return finError("stocks", "Stocks", inputs, "Leave exactly one field blank.")
        val get     = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }
        val unknown = blanks.first()
        val steps   = mutableListOf<StepItem>()
        steps += StepItem(1, "Formula: EPS = Net Income / Total Shares", "")
        val answer: Double
        when (unknown) {
            "eps" -> {
                val ni = get("netIncome")!!; val ts = get("totalShares")!!
                steps += StepItem(2, "Substitute", "EPS = ${ni.fmt()} / ${ts.fmt()}")
                answer = ni / ts
                steps += StepItem(3, "Divide", "= ₱${answer.fmt()} per share", result = "EPS = ₱${answer.fmt()}")
            }
            "netIncome" -> {
                val eps = get("eps")!!; val ts = get("totalShares")!!
                steps += StepItem(2, "Rearrange", "Net Income = EPS × Total Shares")
                answer = eps * ts
                steps += StepItem(3, "Compute", "= ₱${answer.fmt()}", result = "Net Income = ₱${answer.fmt()}")
            }
            "totalShares" -> {
                val eps = get("eps")!!; val ni = get("netIncome")!!
                steps += StepItem(2, "Rearrange", "Total Shares = Net Income / EPS")
                answer = ni / eps
                steps += StepItem(3, "Divide", "= ${answer.fmt()} shares", result = "Total Shares = ${answer.fmt()}")
            }
            else -> return finError("stocks", "Stocks", inputs, "Unknown variable.")
        }
        return finResult("stocks", "Stocks — EPS",
            unknown, unknown, "${answer.fmt()}", steps, inputs)
    }

    private fun stocksPE(inputs: Map<String, String>): CalculationResult {
        val vars   = listOf("marketPrice", "eps", "peRatio")
        val blanks = vars.filter { inputs[it].isNullOrBlank() }
        if (blanks.size != 1) return finError("stocks", "Stocks", inputs, "Leave exactly one field blank.")
        val get     = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }
        val unknown = blanks.first()
        val steps   = mutableListOf<StepItem>()
        steps += StepItem(1, "Formula: P/E Ratio = Market Price / EPS", "")
        val answer: Double
        when (unknown) {
            "peRatio" -> {
                val mp = get("marketPrice")!!; val eps = get("eps")!!
                steps += StepItem(2, "Substitute", "P/E = ${mp.fmt()} / ${eps.fmt()}")
                answer = mp / eps
                steps += StepItem(3, "Divide", "= ${answer.fmt()}x", result = "P/E Ratio = ${answer.fmt()}x")
            }
            "marketPrice" -> {
                val pe = get("peRatio")!!; val eps = get("eps")!!
                steps += StepItem(2, "Rearrange", "Market Price = P/E × EPS")
                answer = pe * eps
                steps += StepItem(3, "Compute", "= ₱${answer.fmt()}", result = "Market Price = ₱${answer.fmt()}")
            }
            "eps" -> {
                val mp = get("marketPrice")!!; val pe = get("peRatio")!!
                steps += StepItem(2, "Rearrange", "EPS = Market Price / P/E")
                answer = mp / pe
                steps += StepItem(3, "Divide", "= ₱${answer.fmt()}", result = "EPS = ₱${answer.fmt()}")
            }
            else -> return finError("stocks", "Stocks", inputs, "Unknown variable.")
        }
        return finResult("stocks", "Stocks — P/E Ratio",
            unknown, unknown, "${answer.fmt()}", steps, inputs)
    }

    private fun stocksTotalReturn(inputs: Map<String, String>): CalculationResult {
        val vars   = listOf("dividends", "capitalGain", "purchasePrice", "totalReturn")
        val blanks = vars.filter { inputs[it].isNullOrBlank() }
        if (blanks.size != 1) return finError("stocks", "Stocks", inputs, "Leave exactly one field blank.")
        val get     = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }
        val unknown = blanks.first()
        val steps   = mutableListOf<StepItem>()
        steps += StepItem(1, "Formula: Total Return = (Dividends + Capital Gain) / Purchase Price × 100", "")
        val answer: Double
        when (unknown) {
            "totalReturn" -> {
                val d = get("dividends")!!; val cg = get("capitalGain")!!; val pp = get("purchasePrice")!!
                steps += StepItem(2, "Substitute",
                    "Total Return = (${d.fmt()} + ${cg.fmt()}) / ${pp.fmt()} × 100")
                val num = d + cg
                steps += StepItem(3, "Compute numerator", "${d.fmt()} + ${cg.fmt()} = ${num.fmt()}")
                answer = (num / pp) * 100
                steps += StepItem(4, "Divide and multiply by 100",
                    "(${num.fmt()} / ${pp.fmt()}) × 100 = ${answer.fmt()}%",
                    result = "Total Return = ${answer.fmt()}%")
            }
            "purchasePrice" -> {
                val d = get("dividends")!!; val cg = get("capitalGain")!!; val tr = get("totalReturn")!! / 100.0
                val num = d + cg
                steps += StepItem(2, "Rearrange", "Purchase Price = (Dividends + Capital Gain) / Total Return")
                answer = num / tr
                steps += StepItem(3, "Compute", "= ₱${answer.fmt()}", result = "Purchase Price = ₱${answer.fmt()}")
            }
            "dividends" -> {
                val cg = get("capitalGain")!!; val pp = get("purchasePrice")!!; val tr = get("totalReturn")!! / 100.0
                steps += StepItem(2, "Rearrange", "Dividends = (Total Return × Purchase Price) − Capital Gain")
                answer = (tr * pp) - cg
                steps += StepItem(3, "Compute", "= ₱${answer.fmt()}", result = "Dividends = ₱${answer.fmt()}")
            }
            "capitalGain" -> {
                val d = get("dividends")!!; val pp = get("purchasePrice")!!; val tr = get("totalReturn")!! / 100.0
                steps += StepItem(2, "Rearrange", "Capital Gain = (Total Return × Purchase Price) − Dividends")
                answer = (tr * pp) - d
                steps += StepItem(3, "Compute", "= ₱${answer.fmt()}", result = "Capital Gain = ₱${answer.fmt()}")
            }
            else -> return finError("stocks", "Stocks", inputs, "Unknown variable.")
        }
        return finResult("stocks", "Stocks — Total Return",
            unknown, unknown, "${answer.fmt()}%", steps, inputs)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BONDS
    // ─────────────────────────────────────────────────────────────────────────

    fun solveBonds(formula: String, inputs: Map<String, String>): CalculationResult {
        return try {
            when (formula) {
                "coupon"        -> bondsCoupon(inputs)
                "current_yield" -> bondsCurrentYield(inputs)
                "bond_price"    -> bondsBondPrice(inputs)
                "total_interest"-> bondsTotalInterest(inputs)
                else -> finError("bonds", "Bonds", inputs, "Unknown formula.")
            }
        } catch (e: Exception) {
            finError("bonds", "Bonds", inputs, "Invalid input.")
        }
    }

    private fun bondsCoupon(inputs: Map<String, String>): CalculationResult {
        val vars   = listOf("faceValue", "couponRate", "couponPayment")
        val blanks = vars.filter { inputs[it].isNullOrBlank() }
        if (blanks.size != 1) return finError("bonds", "Bonds", inputs, "Leave exactly one field blank.")
        val get     = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }
        val unknown = blanks.first()
        val steps   = mutableListOf<StepItem>()
        steps += StepItem(1, "Formula: Coupon = Face Value × Coupon Rate", "")
        val answer: Double
        when (unknown) {
            "couponPayment" -> {
                val fv = get("faceValue")!!; val cr = get("couponRate")!! / 100.0
                steps += StepItem(2, "Substitute", "Coupon = ${fv.fmt()} × ${cr.fmt()}")
                answer = fv * cr
                steps += StepItem(3, "Multiply", "= ₱${answer.fmt()}", result = "Coupon = ₱${answer.fmt()}")
            }
            "faceValue" -> {
                val cp = get("couponPayment")!!; val cr = get("couponRate")!! / 100.0
                steps += StepItem(2, "Rearrange", "Face Value = Coupon / Coupon Rate")
                answer = cp / cr
                steps += StepItem(3, "Compute", "= ₱${answer.fmt()}", result = "Face Value = ₱${answer.fmt()}")
            }
            "couponRate" -> {
                val cp = get("couponPayment")!!; val fv = get("faceValue")!!
                steps += StepItem(2, "Rearrange", "Coupon Rate = Coupon / Face Value × 100")
                answer = (cp / fv) * 100
                steps += StepItem(3, "Compute", "= ${answer.fmt()}%", result = "Coupon Rate = ${answer.fmt()}%")
            }
            else -> return finError("bonds", "Bonds", inputs, "Unknown variable.")
        }
        return finResult("bonds", "Bonds — Coupon Payment",
            unknown, unknown, "${answer.fmt()}", steps, inputs)
    }

    private fun bondsCurrentYield(inputs: Map<String, String>): CalculationResult {
        val vars   = listOf("annualCoupon", "marketPrice", "currentYield")
        val blanks = vars.filter { inputs[it].isNullOrBlank() }
        if (blanks.size != 1) return finError("bonds", "Bonds", inputs, "Leave exactly one field blank.")
        val get     = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }
        val unknown = blanks.first()
        val steps   = mutableListOf<StepItem>()
        steps += StepItem(1, "Formula: Current Yield = (Annual Coupon / Market Price) × 100", "")
        val answer: Double
        when (unknown) {
            "currentYield" -> {
                val ac = get("annualCoupon")!!; val mp = get("marketPrice")!!
                steps += StepItem(2, "Substitute", "Current Yield = (${ac.fmt()} / ${mp.fmt()}) × 100")
                answer = (ac / mp) * 100
                steps += StepItem(3, "Compute", "= ${answer.fmt()}%", result = "Current Yield = ${answer.fmt()}%")
            }
            "annualCoupon" -> {
                val cy = get("currentYield")!! / 100.0; val mp = get("marketPrice")!!
                steps += StepItem(2, "Rearrange", "Annual Coupon = Current Yield × Market Price")
                answer = cy * mp
                steps += StepItem(3, "Compute", "= ₱${answer.fmt()}", result = "Annual Coupon = ₱${answer.fmt()}")
            }
            "marketPrice" -> {
                val ac = get("annualCoupon")!!; val cy = get("currentYield")!! / 100.0
                steps += StepItem(2, "Rearrange", "Market Price = Annual Coupon / Current Yield")
                answer = ac / cy
                steps += StepItem(3, "Compute", "= ₱${answer.fmt()}", result = "Market Price = ₱${answer.fmt()}")
            }
            else -> return finError("bonds", "Bonds", inputs, "Unknown variable.")
        }
        return finResult("bonds", "Bonds — Current Yield",
            unknown, unknown, "${answer.fmt()}", steps, inputs)
    }

    private fun bondsBondPrice(inputs: Map<String, String>): CalculationResult {
        val vars   = listOf("coupon", "rate", "periods", "faceValue", "bondPrice")
        val blanks = vars.filter { inputs[it].isNullOrBlank() }
        if (blanks.size != 1) return finError("bonds", "Bonds", inputs, "Leave exactly one field blank.")
        val get     = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }
        val unknown = blanks.first()
        val steps   = mutableListOf<StepItem>()
        steps += StepItem(1, "Formula: P = C/r × [1 − 1/(1+r)^n] + F/(1+r)^n",
            "where C=coupon, r=yield rate, n=periods, F=face value")
        val answer: Double
        when (unknown) {
            "bondPrice" -> {
                val c  = get("coupon")!!
                val r  = get("rate")!! / 100.0
                val n  = get("periods")!!
                val fv = get("faceValue")!!
                val discountFactor = 1 - 1.0 / (1 + r).pow(n)
                val pvCoupons = (c / r) * discountFactor
                val pvFace    = fv / (1 + r).pow(n)
                steps += StepItem(2, "Compute PV of coupons: C/r × [1 − 1/(1+r)^n]",
                    "= (${c.fmt()} / ${r.fmt()}) × [1 − 1/(${(1+r).fmt()})^${n.fmt()}]\n" +
                            "= ${(c/r).fmt()} × ${discountFactor.fmt()}\n" +
                            "= ${pvCoupons.fmt()}")
                steps += StepItem(3, "Compute PV of face value: F/(1+r)^n",
                    "= ${fv.fmt()} / (${(1+r).fmt()})^${n.fmt()}\n" +
                            "= ${fv.fmt()} / ${(1+r).pow(n).fmt()}\n" +
                            "= ${pvFace.fmt()}")
                answer = pvCoupons + pvFace
                steps += StepItem(4, "Add both components",
                    "${pvCoupons.fmt()} + ${pvFace.fmt()} = ${answer.fmt()}",
                    result = "Bond Price = ₱${answer.fmt()}")
            }
            "faceValue" -> {
                val c  = get("coupon")!!; val r = get("rate")!! / 100.0
                val n  = get("periods")!!; val bp = get("bondPrice")!!
                val pvCoupons = (c / r) * (1 - 1.0 / (1 + r).pow(n))
                val pvFaceDiscount = 1.0 / (1 + r).pow(n)
                answer = (bp - pvCoupons) / pvFaceDiscount
                steps += StepItem(2, "Rearrange for F",
                    "F = (P − PV of coupons) / (1+r)^−n")
                steps += StepItem(3, "Compute PV of coupons", "PV of coupons = ${pvCoupons.fmt()}")
                steps += StepItem(4, "Compute",
                    "(${bp.fmt()} − ${pvCoupons.fmt()}) / ${pvFaceDiscount.fmt()} = ${answer.fmt()}",
                    result = "Face Value = ₱${answer.fmt()}")
            }
            else -> return finError("bonds", "Bonds", inputs,
                "Can only solve for Bond Price or Face Value in this formula.")
        }
        return finResult("bonds", "Bonds — Bond Price",
            unknown, unknown, "₱${answer.fmt()}", steps, inputs)
    }

    private fun bondsTotalInterest(inputs: Map<String, String>): CalculationResult {
        val vars   = listOf("couponPayment", "periods", "totalInterest")
        val blanks = vars.filter { inputs[it].isNullOrBlank() }
        if (blanks.size != 1) return finError("bonds", "Bonds", inputs, "Leave exactly one field blank.")
        val get     = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }
        val unknown = blanks.first()
        val steps   = mutableListOf<StepItem>()
        steps += StepItem(1, "Formula: Total Interest = Coupon × Number of Periods", "")
        val answer: Double
        when (unknown) {
            "totalInterest" -> {
                val c = get("couponPayment")!!; val n = get("periods")!!
                steps += StepItem(2, "Substitute", "Total Interest = ${c.fmt()} × ${n.fmt()}")
                answer = c * n
                steps += StepItem(3, "Multiply", "= ₱${answer.fmt()}", result = "Total Interest = ₱${answer.fmt()}")
            }
            "couponPayment" -> {
                val ti = get("totalInterest")!!; val n = get("periods")!!
                steps += StepItem(2, "Rearrange", "Coupon = Total Interest / Periods")
                answer = ti / n
                steps += StepItem(3, "Divide", "= ₱${answer.fmt()}", result = "Coupon = ₱${answer.fmt()}")
            }
            "periods" -> {
                val ti = get("totalInterest")!!; val c = get("couponPayment")!!
                steps += StepItem(2, "Rearrange", "Periods = Total Interest / Coupon")
                answer = ti / c
                steps += StepItem(3, "Divide", "= ${answer.fmt()} periods", result = "Periods = ${answer.fmt()}")
            }
            else -> return finError("bonds", "Bonds", inputs, "Unknown variable.")
        }
        return finResult("bonds", "Bonds — Total Interest",
            unknown, unknown, "${answer.fmt()}", steps, inputs)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MUTUAL FUNDS
    // ─────────────────────────────────────────────────────────────────────────

    fun solveMutualFunds(formula: String, inputs: Map<String, String>): CalculationResult {
        return try {
            when (formula) {
                "navps"        -> mfNAVPS(inputs)
                "shares"       -> mfShares(inputs)
                "returns"      -> mfReturns(inputs)
                "total_value"  -> mfTotalValue(inputs)
                else -> finError("mutual_funds", "Mutual Funds", inputs, "Unknown formula.")
            }
        } catch (e: Exception) {
            finError("mutual_funds", "Mutual Funds", inputs, "Invalid input.")
        }
    }

    private fun mfNAVPS(inputs: Map<String, String>): CalculationResult {
        val vars   = listOf("nav", "totalShares", "navps")
        val blanks = vars.filter { inputs[it].isNullOrBlank() }
        if (blanks.size != 1) return finError("mutual_funds", "Mutual Funds", inputs, "Leave exactly one field blank.")
        val get     = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }
        val unknown = blanks.first()
        val steps   = mutableListOf<StepItem>()
        steps += StepItem(1, "Formula: NAVPS = NAV / Total Shares Outstanding", "")
        val answer: Double
        when (unknown) {
            "navps" -> {
                val nav = get("nav")!!; val ts = get("totalShares")!!
                steps += StepItem(2, "Substitute", "NAVPS = ${nav.fmt()} / ${ts.fmt()}")
                answer = nav / ts
                steps += StepItem(3, "Divide", "= ₱${answer.fmt()}", result = "NAVPS = ₱${answer.fmt()}")
            }
            "nav" -> {
                val navps = get("navps")!!; val ts = get("totalShares")!!
                steps += StepItem(2, "Rearrange", "NAV = NAVPS × Total Shares")
                answer = navps * ts
                steps += StepItem(3, "Compute", "= ₱${answer.fmt()}", result = "NAV = ₱${answer.fmt()}")
            }
            "totalShares" -> {
                val nav = get("nav")!!; val navps = get("navps")!!
                steps += StepItem(2, "Rearrange", "Total Shares = NAV / NAVPS")
                answer = nav / navps
                steps += StepItem(3, "Divide", "= ${answer.fmt()} shares", result = "Total Shares = ${answer.fmt()}")
            }
            else -> return finError("mutual_funds", "Mutual Funds", inputs, "Unknown variable.")
        }
        return finResult("mutual_funds", "Mutual Funds — NAVPS",
            unknown, unknown, "${answer.fmt()}", steps, inputs)
    }

    private fun mfShares(inputs: Map<String, String>): CalculationResult {
        val vars   = listOf("amountInvested", "navps", "shares")
        val blanks = vars.filter { inputs[it].isNullOrBlank() }
        if (blanks.size != 1) return finError("mutual_funds", "Mutual Funds", inputs, "Leave exactly one field blank.")
        val get     = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }
        val unknown = blanks.first()
        val steps   = mutableListOf<StepItem>()
        steps += StepItem(1, "Formula: Shares = Amount Invested / NAVPS", "")
        val answer: Double
        when (unknown) {
            "shares" -> {
                val ai = get("amountInvested")!!; val navps = get("navps")!!
                steps += StepItem(2, "Substitute", "Shares = ${ai.fmt()} / ${navps.fmt()}")
                answer = ai / navps
                steps += StepItem(3, "Divide", "= ${answer.fmt()} shares", result = "Shares = ${answer.fmt()}")
            }
            "amountInvested" -> {
                val s = get("shares")!!; val navps = get("navps")!!
                steps += StepItem(2, "Rearrange", "Amount = Shares × NAVPS")
                answer = s * navps
                steps += StepItem(3, "Compute", "= ₱${answer.fmt()}", result = "Amount Invested = ₱${answer.fmt()}")
            }
            "navps" -> {
                val ai = get("amountInvested")!!; val s = get("shares")!!
                steps += StepItem(2, "Rearrange", "NAVPS = Amount / Shares")
                answer = ai / s
                steps += StepItem(3, "Divide", "= ₱${answer.fmt()}", result = "NAVPS = ₱${answer.fmt()}")
            }
            else -> return finError("mutual_funds", "Mutual Funds", inputs, "Unknown variable.")
        }
        return finResult("mutual_funds", "Mutual Funds — Shares",
            unknown, unknown, "${answer.fmt()}", steps, inputs)
    }

    private fun mfReturns(inputs: Map<String, String>): CalculationResult {
        val vars   = listOf("currentNAVPS", "purchaseNAVPS", "returns")
        val blanks = vars.filter { inputs[it].isNullOrBlank() }
        if (blanks.size != 1) return finError("mutual_funds", "Mutual Funds", inputs, "Leave exactly one field blank.")
        val get     = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }
        val unknown = blanks.first()
        val steps   = mutableListOf<StepItem>()
        steps += StepItem(1, "Formula: Returns = (Current NAVPS − Purchase NAVPS) / Purchase NAVPS × 100", "")
        val answer: Double
        when (unknown) {
            "returns" -> {
                val cn = get("currentNAVPS")!!; val pn = get("purchaseNAVPS")!!
                steps += StepItem(2, "Substitute",
                    "Returns = (${cn.fmt()} − ${pn.fmt()}) / ${pn.fmt()} × 100")
                val diff = cn - pn
                steps += StepItem(3, "Compute numerator", "${cn.fmt()} − ${pn.fmt()} = ${diff.fmt()}")
                answer = (diff / pn) * 100
                steps += StepItem(4, "Divide and multiply",
                    "(${diff.fmt()} / ${pn.fmt()}) × 100 = ${answer.fmt()}%",
                    result = "Returns = ${answer.fmt()}%")
            }
            "currentNAVPS" -> {
                val r = get("returns")!! / 100.0; val pn = get("purchaseNAVPS")!!
                steps += StepItem(2, "Rearrange", "Current NAVPS = Purchase NAVPS × (1 + Returns)")
                answer = pn * (1 + r)
                steps += StepItem(3, "Compute", "= ₱${answer.fmt()}", result = "Current NAVPS = ₱${answer.fmt()}")
            }
            "purchaseNAVPS" -> {
                val cn = get("currentNAVPS")!!; val r = get("returns")!! / 100.0
                steps += StepItem(2, "Rearrange", "Purchase NAVPS = Current NAVPS / (1 + Returns)")
                answer = cn / (1 + r)
                steps += StepItem(3, "Compute", "= ₱${answer.fmt()}", result = "Purchase NAVPS = ₱${answer.fmt()}")
            }
            else -> return finError("mutual_funds", "Mutual Funds", inputs, "Unknown variable.")
        }
        return finResult("mutual_funds", "Mutual Funds — Returns",
            unknown, unknown, "${answer.fmt()}%", steps, inputs)
    }

    private fun mfTotalValue(inputs: Map<String, String>): CalculationResult {
        val vars   = listOf("shares", "currentNAVPS", "totalValue")
        val blanks = vars.filter { inputs[it].isNullOrBlank() }
        if (blanks.size != 1) return finError("mutual_funds", "Mutual Funds", inputs, "Leave exactly one field blank.")
        val get     = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }
        val unknown = blanks.first()
        val steps   = mutableListOf<StepItem>()
        steps += StepItem(1, "Formula: Total Value = Shares × Current NAVPS", "")
        val answer: Double
        when (unknown) {
            "totalValue" -> {
                val s = get("shares")!!; val cn = get("currentNAVPS")!!
                steps += StepItem(2, "Substitute", "Total Value = ${s.fmt()} × ${cn.fmt()}")
                answer = s * cn
                steps += StepItem(3, "Multiply", "= ₱${answer.fmt()}", result = "Total Value = ₱${answer.fmt()}")
            }
            "shares" -> {
                val tv = get("totalValue")!!; val cn = get("currentNAVPS")!!
                steps += StepItem(2, "Rearrange", "Shares = Total Value / Current NAVPS")
                answer = tv / cn
                steps += StepItem(3, "Divide", "= ${answer.fmt()} shares", result = "Shares = ${answer.fmt()}")
            }
            "currentNAVPS" -> {
                val tv = get("totalValue")!!; val s = get("shares")!!
                steps += StepItem(2, "Rearrange", "Current NAVPS = Total Value / Shares")
                answer = tv / s
                steps += StepItem(3, "Divide", "= ₱${answer.fmt()}", result = "Current NAVPS = ₱${answer.fmt()}")
            }
            else -> return finError("mutual_funds", "Mutual Funds", inputs, "Unknown variable.")
        }
        return finResult("mutual_funds", "Mutual Funds — Total Value",
            unknown, unknown, "₱${answer.fmt()}", steps, inputs)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LOANS — Amortization
    // M = P[r(1+r)^n] / [(1+r)^n − 1]
    // ─────────────────────────────────────────────────────────────────────────

    fun solveLoans(inputs: Map<String, String>): CalculationResult {
        val mainVars   = listOf("p", "r", "n", "m")
        val mainBlanks = mainVars.filter { inputs[it].isNullOrBlank() }

        if (mainBlanks.size > 1)
            return finError("loans", "Loans (Amortization)", inputs,
                "Leave exactly one field blank to solve for it.")
        if (mainBlanks.isEmpty())
            return finError("loans", "Loans (Amortization)", inputs,
                "All fields are filled — nothing to solve.")

        val get     = { key: String -> inputs[key]?.trim()?.toDoubleOrNull() }
        val unknown = mainBlanks.first()
        val steps   = mutableListOf<StepItem>()

        steps += StepItem(1, "Write the Loan Payment formula",
            "M = P × [r(1+r)^n] / [(1+r)^n − 1]\n" +
                    "where:\n" +
                    "  P = principal\n" +
                    "  r = monthly interest rate (annual rate / 12 / 100)\n" +
                    "  n = total number of monthly payments\n" +
                    "  M = monthly payment")

        return try {
            val pv2 = get("p"); val rv = get("r"); val nv = get("n"); val mv2 = get("m")
            if (pv2 != null && pv2 <= 0)
                return finError("loans", "Loans (Amortization)", inputs,
                    "Principal P must be positive.")
            if (rv != null && rv <= 0)
                return finError("loans", "Loans (Amortization)", inputs,
                    "Interest rate must be positive.")
            if (nv != null && (nv < 1 || nv != kotlin.math.floor(nv)))
                return finError("loans", "Loans (Amortization)", inputs,
                    "Number of payments n must be a positive whole number.")
            if (mv2 != null && mv2 <= 0)
                return finError("loans", "Loans (Amortization)", inputs,
                    "Monthly payment M must be positive.")
            val answer: Double
            when (unknown) {
                "m" -> {
                    val p = get("p")!!
                    val annualRate = get("r")!!
                    val r = annualRate / 12.0 / 100.0
                    val n = get("n")!!

                    steps += StepItem(2, "Convert annual rate to monthly rate",
                        "r = ${annualRate.fmt()}% / 12 / 100\n" +
                                "r = ${r.fmt()}")
                    steps += StepItem(3, "Substitute into formula",
                        "M = ${p.fmt()} × [${r.fmt()}(1+${r.fmt()})^${n.fmt()}] / [(1+${r.fmt()})^${n.fmt()} − 1]")
                    val onePlusR = 1 + r
                    val onePlusRn = onePlusR.pow(n)
                    steps += StepItem(4, "Compute (1+r)^n",
                        "(${onePlusR.fmt()})^${n.fmt()} = ${onePlusRn.fmt()}")
                    val numerator = r * onePlusRn
                    steps += StepItem(5, "Compute numerator r×(1+r)^n",
                        "${r.fmt()} × ${onePlusRn.fmt()} = ${numerator.fmt()}")
                    val denominator = onePlusRn - 1
                    steps += StepItem(6, "Compute denominator (1+r)^n − 1",
                        "${onePlusRn.fmt()} − 1 = ${denominator.fmt()}")
                    val m = p * (numerator / denominator)
                    steps += StepItem(7, "Multiply P by the fraction",
                        "${p.fmt()} × (${numerator.fmt()} / ${denominator.fmt()})\n" +
                                "= ${p.fmt()} × ${(numerator / denominator).fmt()}\n" +
                                "= ${m.fmt()}",
                        result = "M = ₱${m.fmt()} per month")
                    val totalPayment  = m * n
                    val totalInterest = totalPayment - p
                    steps += StepItem(8, "Total payment and interest",
                        "Total Payment = M × n = ${m.fmt()} × ${n.fmt()} = ${totalPayment.fmt()}\n" +
                                "Total Interest = Total Payment − P\n" +
                                "              = ${totalPayment.fmt()} − ${p.fmt()}\n" +
                                "              = ${totalInterest.fmt()}",
                        result = "Total Interest = ₱${totalInterest.fmt()}")
                    steps += buildAmortizationTable(p, r, n.toInt(), m)
                    answer = m
                }
                "p" -> {
                    val annualRate = get("r")!!
                    val r = annualRate / 12.0 / 100.0
                    val n = get("n")!!
                    val m = get("m")!!
                    steps += StepItem(2, "Rearrange for P",
                        "P = M × [(1+r)^n − 1] / [r(1+r)^n]")
                    val onePlusRn = (1 + r).pow(n)
                    steps += StepItem(3, "Compute (1+r)^n",
                        "(${(1+r).fmt()})^${n.fmt()} = ${onePlusRn.fmt()}")
                    val num = onePlusRn - 1
                    val den = r * onePlusRn
                    val p = m * (num / den)
                    steps += StepItem(4, "Compute P",
                        "${m.fmt()} × (${num.fmt()} / ${den.fmt()}) = ${p.fmt()}",
                        result = "P = ₱${p.fmt()}")
                    answer = p
                }
                "n" -> {
                    val p = get("p")!!
                    val annualRate = get("r")!!
                    val r = annualRate / 12.0 / 100.0
                    val m = get("m")!!
                    steps += StepItem(2, "Rearrange for n using logarithms",
                        "n = −ln(1 − Pr/M) / ln(1+r)")
                    val inner = 1 - (p * r / m)
                    if (inner <= 0) return finError("loans", "Loans (Amortization)", inputs,
                        "Monthly payment M is too small to cover interest. Increase M.")
                    steps += StepItem(3, "Compute (1 − Pr/M)",
                        "1 − (${p.fmt()} × ${r.fmt()} / ${m.fmt()})\n" +
                                "= 1 − ${(p * r / m).fmt()}\n" +
                                "= ${inner.fmt()}")
                    val n = -ln(inner) / ln(1 + r)
                    steps += StepItem(4, "Apply formula",
                        "n = −ln(${inner.fmt()}) / ln(${(1+r).fmt()})\n" +
                                "  = ${(-ln(inner)).fmt()} / ${ln(1+r).fmt()}\n" +
                                "  = ${n.fmt()} months",
                        result = "n = ${ceil(n).toInt()} months (${(ceil(n).toInt()/12.0).fmt()} years)")
                    answer = n
                }
                "r" -> {
                    return finError("loans", "Loans (Amortization)", inputs,
                        "Solving for interest rate requires numerical methods. " +
                                "Please provide the rate and solve for another variable.")
                }
                else -> return finError("loans", "Loans (Amortization)", inputs, "Unknown variable.")
            }

            val solvedLabel = when (unknown) {
                "m" -> "M — Monthly payment"
                "p" -> "P — Principal"
                "n" -> "n — Number of payments"
                else -> unknown
            }
            finResult("loans", "Loans (Amortization)",
                unknown, solvedLabel, "₱${answer.fmt()}", steps, inputs)

        } catch (e: Exception) {
            finError("loans", "Loans (Amortization)", inputs,
                "Invalid input. Please check your values.")
        }
    }

    private fun buildAmortizationTable(
        p: Double, r: Double, n: Int, m: Double
    ): StepItem {
        val showRows = minOf(n, 5)
        val header = padR("Period", 8) + padR("Payment", 12) +
                padR("Principal", 12) + padR("Interest", 12) + padR("Balance", 14)
        val divider = "─".repeat(58)
        var balance = p
        val rows = mutableListOf<String>()

        for (period in 1..showRows) {
            val interest   = balance * r
            val principal  = m - interest
            balance       -= principal
            if (balance < 0.01) balance = 0.0
            rows += padR("$period", 8) +
                    padR("₱${m.fmt()}", 12) +
                    padR("₱${principal.fmt()}", 12) +
                    padR("₱${interest.fmt()}", 12) +
                    padR("₱${balance.fmt()}", 14)
        }

        val suffix = if (n > 5) "\n... (${n - 5} more periods)" else ""

        return StepItem(
            stepNumber = 9,
            title      = "Amortization Table (first $showRows of $n periods)",
            expression = "$header\n$divider\n${rows.joinToString("\n")}$suffix"
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CREDIT CARDS
    // ─────────────────────────────────────────────────────────────────────────

    fun solveCreditCards(inputs: Map<String, String>): CalculationResult {
        val balance          = inputs["balance"]?.trim()?.toDoubleOrNull()
            ?: return finError("credit_cards", "Credit Cards", inputs,
                "Please enter the outstanding balance.")
        val monthlyRate      = inputs["monthlyRate"]?.trim()?.toDoubleOrNull()
            ?: return finError("credit_cards", "Credit Cards", inputs,
                "Please enter the monthly interest rate (%).")
        val minPaymentPct    = inputs["minPaymentPct"]?.trim()?.toDoubleOrNull()
            ?: return finError("credit_cards", "Credit Cards", inputs,
                "Please enter the minimum payment percentage.")
        val minPaymentFloor  = inputs["minPaymentFloor"]?.trim()?.toDoubleOrNull()
            ?: return finError("credit_cards", "Credit Cards", inputs,
                "Please enter the minimum payment floor (₱).")

        if (monthlyRate <= 0) return finError("credit_cards", "Credit Cards", inputs,
            "Monthly rate must be positive.")
        if (monthlyRate > 2) return finError("credit_cards", "Credit Cards", inputs,
            "BSP Circular 1098 caps credit card monthly rate at 2%. Please check your rate.")

        val r   = monthlyRate / 100.0
        val steps = mutableListOf<StepItem>()

        steps += StepItem(1, "Given information",
            "Outstanding Balance: ₱${balance.fmt()}\n" +
                    "Monthly Rate: ${monthlyRate.fmt()}% (BSP cap: 2%)\n" +
                    "Min. Payment: ${minPaymentPct.fmt()}% of balance or ₱${minPaymentFloor.fmt()}, whichever is higher")

        val monthlyInterest = balance * r
        steps += StepItem(2, "Compute monthly interest",
            "Monthly Interest = Balance × Monthly Rate\n" +
                    "= ₱${balance.fmt()} × ${r.fmt()}\n" +
                    "= ₱${monthlyInterest.fmt()}",
            result = "Monthly Interest = ₱${monthlyInterest.fmt()}")

        val minPayment = maxOf(balance * (minPaymentPct / 100.0), minPaymentFloor)
        steps += StepItem(3, "Compute minimum payment",
            "${minPaymentPct.fmt()}% of ₱${balance.fmt()} = ₱${(balance * minPaymentPct / 100.0).fmt()}\n" +
                    "Floor = ₱${minPaymentFloor.fmt()}\n" +
                    "Min. Payment = max(₱${(balance * minPaymentPct / 100.0).fmt()}, ₱${minPaymentFloor.fmt()}) = ₱${minPayment.fmt()}",
            result = "Minimum Payment = ₱${minPayment.fmt()}")

        if (minPayment <= monthlyInterest)
            return finError("credit_cards", "Credit Cards", inputs,
                "Minimum payment (₱${minPayment.fmt()}) does not cover the monthly interest " +
                        "(₱${monthlyInterest.fmt()}). Balance will never be paid off.")

        // Build payoff table
        steps += buildCreditCardTable(balance, r, minPayment, minPaymentPct, minPaymentFloor)

        // Count total months
        var bal = balance
        var months = 0
        var totalInterestPaid = 0.0
        while (bal > 0.01 && months < 1200) {
            val interest = bal * r
            totalInterestPaid += interest
            val payment = maxOf(maxOf(bal * (minPaymentPct / 100.0), minPaymentFloor), bal + interest)
            bal = (bal + interest - payment).coerceAtLeast(0.0)
            months++
        }

        steps += StepItem(steps.size + 1, "Summary",
            "Total months to pay off: $months\n" +
                    "Total interest paid: ₱${totalInterestPaid.fmt()}\n" +
                    "Total amount paid: ₱${(balance + totalInterestPaid).fmt()}",
            result = "Paid off in $months months")

        return finResult("credit_cards", "Credit Cards",
            "result", "Payoff Schedule",
            "₱${minPayment.fmt()}/month — paid off in $months months",
            steps, inputs)
    }

    private fun buildCreditCardTable(
        balance: Double, r: Double,
        minPayment: Double, minPct: Double, minFloor: Double
    ): StepItem {
        val header  = padR("Month", 8) + padR("Payment", 12) +
                padR("Interest", 12) + padR("Principal", 12) + padR("Balance", 14)
        val divider = "─".repeat(58)
        var bal     = balance
        val rows    = mutableListOf<String>()
        var month   = 0

        while (bal > 0.01 && month < 5) {
            month++
            val interest  = bal * r
            val payment   = minOf(maxOf(bal * (minPct / 100.0), minFloor), bal + interest)
            val principal = payment - interest
            bal           = (bal + interest - payment).coerceAtLeast(0.0)
            rows += padR("$month", 8) +
                    padR("₱${payment.fmt()}", 12) +
                    padR("₱${interest.fmt()}", 12) +
                    padR("₱${principal.fmt()}", 12) +
                    padR("₱${bal.fmt()}", 14)
        }

        return StepItem(
            stepNumber = 4,
            title      = "Payoff Table (first 5 months)",
            expression = "$header\n$divider\n${rows.joinToString("\n")}\n..."
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun Double.pow(exp: Double): Double = Math.pow(this, exp)
    private fun Double.fmt(): String =
        if (this == kotlin.math.floor(this) && !this.isInfinite())
            this.toLong().toString()
        else "%.4f".format(this).trimEnd('0').trimEnd('.')

    private fun padR(s: String, w: Int) = s.padEnd(w)

    fun finResult(
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

    fun finError(
        topicId: String, topicName: String,
        inputs: Map<String, String>, message: String
    ) = CalculationResult(
        topicId        = topicId,
        topicName      = topicName,
        solvedFor      = "", solvedForLabel = "", answer = "",
        steps          = emptyList(), inputs = inputs,
        isError        = true, errorMessage = message
    )
}