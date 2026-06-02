package com.solodev.mmwcalc.ui.screens.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solodev.mmwcalc.data.repository.HistoryRepository
import com.solodev.mmwcalc.domain.calculators.FibRow
import com.solodev.mmwcalc.domain.calculators.SequenceCalculator
import com.solodev.mmwcalc.domain.models.CalculationResult
import com.solodev.mmwcalc.domain.models.TopicRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.solodev.mmwcalc.domain.calculators.StatisticsCalculator
import com.solodev.mmwcalc.ui.components.parseDataset
import com.solodev.mmwcalc.domain.calculators.GroupedStatisticsCalculator
import com.solodev.mmwcalc.ui.components.FrequencyRow
import com.solodev.mmwcalc.ui.components.validateFrequencyTable
import com.solodev.mmwcalc.domain.calculators.AdvancedStatisticsCalculator
import com.solodev.mmwcalc.domain.calculators.NumberTheoryCalculator
import com.solodev.mmwcalc.domain.calculators.FinanceCalculator

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _inputs = MutableStateFlow<Map<String, String>>(emptyMap())
    val inputs: StateFlow<Map<String, String>> = _inputs.asStateFlow()

    private val _result = MutableStateFlow<CalculationResult?>(null)
    val result: StateFlow<CalculationResult?> = _result.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Fibonacci
    private val _fibMode = MutableStateFlow("default")
    val fibMode: StateFlow<String> = _fibMode.asStateFlow()

    private val _fibSeedType = MutableStateFlow("1,1")
    val fibSeedType: StateFlow<String> = _fibSeedType.asStateFlow()

    private val _modOperation = MutableStateFlow("default")
    val modOperation: StateFlow<String> = _modOperation.asStateFlow()

    private val _stocksFormula = MutableStateFlow("dividend")
    val stocksFormula: StateFlow<String> = _stocksFormula.asStateFlow()

    private val _bondsFormula = MutableStateFlow("coupon")
    val bondsFormula: StateFlow<String> = _bondsFormula.asStateFlow()

    private val _mfFormula = MutableStateFlow("navps")
    val mfFormula: StateFlow<String> = _mfFormula.asStateFlow()

    // Fibonacci rows: list of (positionString, valueString)
    private val _fibRows = MutableStateFlow<List<Pair<String, String>>>(
        listOf("" to "")
    )
    val fibRows: StateFlow<List<Pair<String, String>>> = _fibRows.asStateFlow()

    private val _freqRows = MutableStateFlow<List<FrequencyRow>>(List(5) { FrequencyRow() })
    val freqRows: StateFlow<List<FrequencyRow>> = _freqRows.asStateFlow()

    fun updateFreqRow(index: Int, row: FrequencyRow) {
        val current = _freqRows.value.toMutableList()
        if (index in current.indices) {
            current[index] = row
            _freqRows.value = current
        }
    }

    fun addFreqRow() {
        _freqRows.value = _freqRows.value + FrequencyRow()
    }

    fun removeFreqRow(index: Int) {
        if (_freqRows.value.size > 2) {
            _freqRows.value = _freqRows.value.toMutableList().also { it.removeAt(index) }
        }
    }

    fun setModOperation(op: String) {
        _modOperation.value = op
        // Reset inputs based on operation
        _inputs.value = when (op) {
            "default" -> mapOf("a" to "", "m" to "", "r" to "")
            else      -> mapOf("a" to "", "b" to "", "m" to "", "r" to "")
        }
        _result.value = null
        _errorMessage.value = null
    }

    fun setStocksFormula(f: String) {
        _stocksFormula.value = f
        _inputs.value = stocksInputsFor(f)
        _result.value = null; _errorMessage.value = null
    }

    fun setBondsFormula(f: String) {
        _bondsFormula.value = f
        _inputs.value = bondsInputsFor(f)
        _result.value = null; _errorMessage.value = null
    }

    fun setMFFormula(f: String) {
        _mfFormula.value = f
        _inputs.value = mfInputsFor(f)
        _result.value = null; _errorMessage.value = null
    }

    fun preloadInputs(topicId: String, inputs: Map<String, String>) {
        initTopic(topicId)

        when (topicId) {
            "fibonacci" -> {
                // Restore fib mode and seed type
                val mode     = inputs["mode"] ?: "default"
                val seedType = inputs["seedType"] ?: "1,1"
                _fibMode.value     = mode
                _fibSeedType.value = seedType

                // Restore fib rows
                val restoredRows = when (mode) {
                    "default" -> {
                        val n = inputs["n"] ?: ""
                        listOf(n to (inputs["value"] ?: ""))
                    }
                    "custom" -> {
                        // rows were stored as row0_pos, row0_val, row1_pos, row1_val...
                        val rows = mutableListOf<Pair<String, String>>()
                        var i = 0
                        while (inputs.containsKey("row${i}_pos") || inputs.containsKey("row${i}_val")) {
                            val pos = inputs["row${i}_pos"] ?: ""
                            val v   = inputs["row${i}_val"] ?: ""
                            rows.add(pos to v)
                            i++
                        }
                        if (rows.isEmpty()) List(3) { "" to "" } else rows
                    }
                    else -> listOf("" to "")
                }
                _fibRows.value = restoredRows
            }

            "central_tendency_grouped",
            "dispersion_grouped",
            "relative_position_grouped" -> {
                // Restore measure type and k
                val current = _inputs.value.toMutableMap()
                inputs.forEach { (k, v) -> if (v.isNotBlank()) current[k] = v }
                _inputs.value = current

                // Restore freq rows from stored data
                val rows = mutableListOf<FrequencyRow>()
                var i = 0
                while (inputs.containsKey("row${i}_lower")) {
                    rows.add(FrequencyRow(
                        lower = inputs["row${i}_lower"] ?: "",
                        upper = inputs["row${i}_upper"] ?: "",
                        freq  = inputs["row${i}_freq"]  ?: ""
                    ))
                    i++
                }
                if (rows.isNotEmpty()) _freqRows.value = rows
            }

            "stocks" -> {
                val formula = inputs["formula"] ?: "dividend"
                _stocksFormula.value = formula
                _inputs.value = stocksInputsFor(formula).toMutableMap().also { map ->
                    inputs.forEach { (k, v) -> if (map.containsKey(k) && v.isNotBlank()) map[k] = v }
                }
            }
            "bonds" -> {
                val formula = inputs["formula"] ?: "coupon"
                _bondsFormula.value = formula
                _inputs.value = bondsInputsFor(formula).toMutableMap().also { map ->
                    inputs.forEach { (k, v) -> if (map.containsKey(k) && v.isNotBlank()) map[k] = v }
                }
            }
            "mutual_funds" -> {
                val formula = inputs["formula"] ?: "navps"
                _mfFormula.value = formula
                _inputs.value = mfInputsFor(formula).toMutableMap().also { map ->
                    inputs.forEach { (k, v) -> if (map.containsKey(k) && v.isNotBlank()) map[k] = v }
                }
            }

            else -> {
                // All other topics — restore inputs directly
                val current = _inputs.value.toMutableMap()
                inputs.forEach { (k, v) -> if (v.isNotBlank()) current[k] = v }
                _inputs.value = current
            }
        }
    }

    private fun stocksInputsFor(f: String) = when (f) {
        "dividend"     -> mapOf("shares" to "", "dividendPerShare" to "", "totalDividend" to "")
        "yield"        -> mapOf("annualDividend" to "", "marketPrice" to "", "dividendYield" to "")
        "eps"          -> mapOf("netIncome" to "", "totalShares" to "", "eps" to "")
        "pe"           -> mapOf("marketPrice" to "", "eps" to "", "peRatio" to "")
        "total_return" -> mapOf("dividends" to "", "capitalGain" to "", "purchasePrice" to "", "totalReturn" to "")
        else           -> emptyMap()
    }

    private fun bondsInputsFor(f: String) = when (f) {
        "coupon"         -> mapOf("faceValue" to "", "couponRate" to "", "couponPayment" to "")
        "current_yield"  -> mapOf("annualCoupon" to "", "marketPrice" to "", "currentYield" to "")
        "bond_price"     -> mapOf("coupon" to "", "rate" to "", "periods" to "", "faceValue" to "", "bondPrice" to "")
        "total_interest" -> mapOf("couponPayment" to "", "periods" to "", "totalInterest" to "")
        else             -> emptyMap()
    }

    private fun mfInputsFor(f: String) = when (f) {
        "navps"       -> mapOf("nav" to "", "totalShares" to "", "navps" to "")
        "shares"      -> mapOf("amountInvested" to "", "navps" to "", "shares" to "")
        "returns"     -> mapOf("currentNAVPS" to "", "purchaseNAVPS" to "", "returns" to "")
        "total_value" -> mapOf("shares" to "", "currentNAVPS" to "", "totalValue" to "")
        else          -> emptyMap()
    }

    fun initTopic(topicId: String) {
        _result.value = null
        _errorMessage.value = null
        if (topicId == "fibonacci") {
            _fibMode.value = "default"
            _fibSeedType.value = "1,1"
            _fibRows.value = listOf("" to "")
            _inputs.value = emptyMap()
        } else if (topicId in listOf(
    "central_tendency_ungrouped",
    "dispersion_ungrouped",
    "relative_position_ungrouped"
    )) {
        _inputs.value = mapOf(
            "dataset"     to "",
            "measureType" to "quartile",
            "k"           to "",
            "x"           to ""
        )
    } else if (topicId in listOf(
    "central_tendency_grouped",
    "dispersion_grouped",
    "relative_position_grouped"
    )) {
        _inputs.value = mapOf(
            "measureType" to "quartile",
            "k"           to "1"
        )
        _freqRows.value = List(5) { FrequencyRow() }
    } else if (topicId in listOf("normal_distribution")) {
        _inputs.value = mapOf("z" to "", "x" to "", "mean" to "", "sd" to "")
    } else if (topicId in listOf("linear_regression", "correlation")) {
        _inputs.value = mapOf(
            "xData"    to "",
            "yData"    to "",
            "predictY" to "",
            "predictX" to ""
        )
    } else if (topicId == "zellers_congruence") {
        _inputs.value = mapOf("day" to "", "month" to "", "year" to "")
    } else if (topicId == "modular_arithmetic") {
        _modOperation.value = "default"
        _inputs.value = mapOf("a" to "", "m" to "", "r" to "")
    } else if (topicId == "simple_interest") {
            _inputs.value = mapOf("p" to "", "r" to "", "t" to "", "i" to "", "f" to "")
        } else if (topicId == "compound_interest") {
            _inputs.value = mapOf("p" to "", "j" to "", "m" to "", "t" to "", "f" to "")
        } else if (topicId == "stocks") {
            _stocksFormula.value = "dividend"
            _inputs.value = stocksInputsFor("dividend")
        } else if (topicId == "bonds") {
            _bondsFormula.value = "coupon"
            _inputs.value = bondsInputsFor("coupon")
        } else if (topicId == "mutual_funds") {
            _mfFormula.value = "navps"
            _inputs.value = mfInputsFor("navps")
        } else if (topicId == "loans") {
            _inputs.value = mapOf("p" to "", "r" to "", "n" to "", "m" to "")
        } else if (topicId == "credit_cards") {
            _inputs.value = mapOf(
                "balance"         to "",
                "monthlyRate"     to "",
                "minPaymentPct"   to "",
                "minPaymentFloor" to ""
            )
        } else {
            val topic = TopicRegistry.findById(topicId) ?: return
            _inputs.value = topic.variables.associateWith { "" }
        }
    }

    fun updateInput(key: String, value: String) {
        _inputs.value = _inputs.value.toMutableMap().also { it[key] = value }
        _errorMessage.value = null
    }

    fun setFibMode(mode: String) {
        _fibMode.value = mode
        _fibRows.value = when (mode) {
            "default" -> listOf("" to "")
            "custom"  -> List(3) { "" to "" }
            else      -> listOf("" to "")
        }
        _result.value = null
        _errorMessage.value = null
    }

    fun setFibSeedType(seedType: String) {
        _fibSeedType.value = seedType
    }

    fun updateFibRow(index: Int, pos: String, value: String) {
        val current = _fibRows.value.toMutableList()
        if (index in current.indices) {
            current[index] = pos to value
            _fibRows.value = current
        }
        _errorMessage.value = null
    }

    fun clearAll(topicId: String) = initTopic(topicId)

    fun clearResult() { _result.value = null }

    fun calculate(topicId: String) {
        _errorMessage.value = null

        val calcResult = when (topicId) {
            "arithmetic_sequence" -> {
                val mainVars   = listOf("a1", "d", "n", "an")
                val mainBlanks = mainVars.filter { _inputs.value[it].isNullOrBlank() }
                val snBlank    = _inputs.value["sn"].isNullOrBlank()
                when {
                    mainBlanks.size > 1 -> {
                        _errorMessage.value = "Leave exactly one of a₁, d, n, aₙ blank."
                        return
                    }
                    mainBlanks.isEmpty() && !snBlank -> {
                        _errorMessage.value = "All fields are filled — nothing to solve."
                        return
                    }
                    else -> SequenceCalculator.solveArithmetic(_inputs.value)
                }
            }
            "geometric_sequence" -> {
                val mainVars   = listOf("a1", "r", "n", "an")
                val mainBlanks = mainVars.filter { _inputs.value[it].isNullOrBlank() }
                val snBlank    = _inputs.value["sn"].isNullOrBlank()
                when {
                    mainBlanks.size > 1 -> {
                        _errorMessage.value = "Leave exactly one of a₁, r, n, aₙ blank."
                        return
                    }
                    mainBlanks.isEmpty() && !snBlank -> {
                        _errorMessage.value = "All fields are filled — nothing to solve."
                        return
                    }
                    else -> SequenceCalculator.solveGeometric(_inputs.value)
                }
            }
            "fibonacci" -> {
                val rows = _fibRows.value.map { (posStr, valStr) ->
                    FibRow(
                        position = posStr.trim().toIntOrNull(),
                        value    = valStr.trim().toLongOrNull()
                    )
                }
                SequenceCalculator.solveFibonacci(
                    mode     = _fibMode.value,
                    seedType = _fibSeedType.value,
                    rows     = rows
                )
            }
            "central_tendency_ungrouped" -> {
                val data = parseDataset(_inputs.value["dataset"] ?: "")
                if (data.isEmpty()) {
                    _errorMessage.value = "Please enter comma-separated values."
                    return
                }
                StatisticsCalculator.solveCentralTendencyUngrouped(data)
            }

            "dispersion_ungrouped" -> {
                val data = parseDataset(_inputs.value["dataset"] ?: "")
                if (data.size < 2) {
                    _errorMessage.value = "Please enter at least 2 comma-separated values."
                    return
                }
                StatisticsCalculator.solveDispersionUngrouped(data)
            }

            "relative_position_ungrouped" -> {
                val data = parseDataset(_inputs.value["dataset"] ?: "")
                if (data.size < 2) {
                    _errorMessage.value = "Please enter at least 2 values."
                    return
                }
                val measureType = _inputs.value["measureType"] ?: "quartile"
                val k = _inputs.value["k"]?.trim()?.toIntOrNull() ?: 1
                val x = _inputs.value["x"]?.trim()?.toDoubleOrNull()
                StatisticsCalculator.solveRelativePositionUngrouped(data, k, measureType, x)
            }

            "central_tendency_grouped" -> {
                val validation = validateFrequencyTable(_freqRows.value)
                if (!validation.isValid) {
                    _errorMessage.value = validation.errors.joinToString("\n")
                    return
                }
                GroupedStatisticsCalculator.solveCentralTendencyGrouped(validation.rows)
            }

            "dispersion_grouped" -> {
                val validation = validateFrequencyTable(_freqRows.value)
                if (!validation.isValid) {
                    _errorMessage.value = validation.errors.joinToString("\n")
                    return
                }
                GroupedStatisticsCalculator.solveDispersionGrouped(validation.rows)
            }

            "relative_position_grouped" -> {
                val validation = validateFrequencyTable(_freqRows.value)
                if (!validation.isValid) {
                    _errorMessage.value = validation.errors.joinToString("\n")
                    return
                }
                val measureType = _inputs.value["measureType"] ?: "quartile"
                val k = _inputs.value["k"]?.trim()?.toIntOrNull() ?: 1
                GroupedStatisticsCalculator.solveRelativePositionGrouped(
                    validation.rows, measureType, k)
            }

            "normal_distribution" -> {
                AdvancedStatisticsCalculator.solveNormalDistribution(_inputs.value)
            }

            "linear_regression" -> {
                val xData = parseDataset(_inputs.value["xData"] ?: "")
                val yData = parseDataset(_inputs.value["yData"] ?: "")
                if (xData.isEmpty() || yData.isEmpty()) {
                    _errorMessage.value = "Please enter both X and Y datasets."
                    return
                }
                if (xData.size != yData.size) {
                    _errorMessage.value = "X and Y must have the same number of values."
                    return
                }
                val predictY = _inputs.value["predictY"] ?: ""
                val predictX = _inputs.value["predictX"] ?: ""
                AdvancedStatisticsCalculator.solveLinearRegression(
                    xData    = xData,
                    yData    = yData,
                    predictX = predictX,
                    predictY = predictY,
                    hasPrediction = predictX.isNotBlank() || predictY.isNotBlank()
                )
            }

            "correlation" -> {
                val xData = parseDataset(_inputs.value["xData"] ?: "")
                val yData = parseDataset(_inputs.value["yData"] ?: "")
                if (xData.isEmpty() || yData.isEmpty()) {
                    _errorMessage.value = "Please enter both X and Y datasets."
                    return
                }
                if (xData.size != yData.size) {
                    _errorMessage.value = "X and Y must have the same number of values."
                    return
                }
                AdvancedStatisticsCalculator.solveCorrelation(xData, yData)
            }

            "modular_arithmetic" -> {
                if (_modOperation.value == "default") {
                    NumberTheoryCalculator.solveModular(_inputs.value)
                } else {
                    NumberTheoryCalculator.solveModularOperation(
                        _modOperation.value, _inputs.value)
                }
            }

            "zellers_congruence" -> {
                NumberTheoryCalculator.solveZeller(_inputs.value)
            }

            "simple_interest" -> {
                FinanceCalculator.solveSimpleInterest(_inputs.value)
            }
            "compound_interest" -> {
                FinanceCalculator.solveCompoundInterest(_inputs.value)
            }
            "stocks" -> {
                FinanceCalculator.solveStocks(_stocksFormula.value, _inputs.value)
            }
            "bonds" -> {
                FinanceCalculator.solveBonds(_bondsFormula.value, _inputs.value)
            }
            "mutual_funds" -> {
                FinanceCalculator.solveMutualFunds(_mfFormula.value, _inputs.value)
            }
            "loans" -> {
                FinanceCalculator.solveLoans(_inputs.value)
            }
            "credit_cards" -> {
                FinanceCalculator.solveCreditCards(_inputs.value)
            }

            else -> CalculationResult(
                topicId = topicId, topicName = topicId,
                solvedFor = "", solvedForLabel = "", answer = "",
                steps = emptyList(), inputs = _inputs.value,
                isError = true,
                errorMessage = "Calculator for this topic coming soon.")
        }

        _result.value = calcResult
        if (!calcResult.isError) {
            viewModelScope.launch { historyRepository.save(calcResult) }
        } else {
            _errorMessage.value = calcResult.errorMessage
            _result.value = null
        }
    }
}