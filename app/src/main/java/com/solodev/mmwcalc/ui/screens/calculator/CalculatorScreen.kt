package com.solodev.mmwcalc.ui.screens.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.solodev.mmwcalc.domain.models.TopicRegistry
import com.solodev.mmwcalc.ui.components.FormulaInputField
import com.solodev.mmwcalc.ui.screens.home.categoryColor
import com.solodev.mmwcalc.ui.components.DatasetInputField
import com.solodev.mmwcalc.ui.components.parseDataset
import com.solodev.mmwcalc.ui.components.FrequencyRow
import com.solodev.mmwcalc.ui.components.FrequencyTableInput
import com.solodev.mmwcalc.ui.components.validateFrequencyTable
import com.solodev.mmwcalc.ui.SharedViewModel
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    topicId: String,
    onBack: () -> Unit,
    onResult: () -> Unit,
    viewModel: CalculatorViewModel,
    sharedViewModel: SharedViewModel
) {
    val topic        = TopicRegistry.findById(topicId)
    val inputs       by viewModel.inputs.collectAsState()
    val result       by viewModel.result.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val fibMode      by viewModel.fibMode.collectAsState()
    val fibSeedType  by viewModel.fibSeedType.collectAsState()
    val fibRows      by viewModel.fibRows.collectAsState()
    val freqRows by viewModel.freqRows.collectAsState()
    val stocksFormula by viewModel.stocksFormula.collectAsState()
    val bondsFormula  by viewModel.bondsFormula.collectAsState()
    val mfFormula     by viewModel.mfFormula.collectAsState()

    val pendingInputs by sharedViewModel.pendingInputs.collectAsState()

    LaunchedEffect(topicId) {
        viewModel.initTopic(topicId)
    }

    LaunchedEffect(pendingInputs) {
        val pending = pendingInputs ?: return@LaunchedEffect
        if (pending.first == topicId) {
            viewModel.preloadInputs(pending.first, pending.second)
            sharedViewModel.consumePendingInputs()
        }
    }

    LaunchedEffect(result) {
        if (result != null && result?.isError == false) onResult()
    }

    val accentColor = topic?.category?.let { categoryColor(it) }
        ?: MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(topic?.name ?: "Calculator",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold)
                        Text(topic?.category?.label ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (topicId) {
                "fibonacci" -> FibonacciSection(
                    fibMode         = fibMode,
                    fibSeedType     = fibSeedType,
                    fibRows         = fibRows,
                    accentColor     = accentColor,
                    onModeChange    = { viewModel.setFibMode(it) },
                    onSeedChange    = { viewModel.setFibSeedType(it) },
                    onRowChange     = { i, p, v -> viewModel.updateFibRow(i, p, v) }
                )
                "central_tendency_ungrouped",
                "dispersion_ungrouped" -> {
                    DatasetOnlyScreen(
                        topicId     = topicId,
                        inputs      = inputs,
                        accentColor = accentColor,
                        onInputChange = { k, v -> viewModel.updateInput(k, v) }
                    )
                }

                "relative_position_ungrouped" -> {
                    RelativePositionScreen(
                        inputs      = inputs,
                        accentColor = accentColor,
                        onInputChange = { k, v -> viewModel.updateInput(k, v) }
                    )
                }

                "central_tendency_grouped",
                "dispersion_grouped" -> {
                    GroupedDataScreen(
                        freqRows    = freqRows,
                        accentColor = accentColor,
                        onRowChange = { i, r -> viewModel.updateFreqRow(i, r) },
                        onAddRow    = { viewModel.addFreqRow() },
                        onRemoveRow = { viewModel.removeFreqRow(it) },
                        showMeasurePicker = false,
                        inputs      = inputs,
                        onInputChange = { k, v -> viewModel.updateInput(k, v) }
                    )
                }

                "relative_position_grouped" -> {
                    GroupedDataScreen(
                        freqRows    = freqRows,
                        accentColor = accentColor,
                        onRowChange = { i, r -> viewModel.updateFreqRow(i, r) },
                        onAddRow    = { viewModel.addFreqRow() },
                        onRemoveRow = { viewModel.removeFreqRow(it) },
                        showMeasurePicker = true,
                        inputs      = inputs,
                        onInputChange = { k, v -> viewModel.updateInput(k, v) }
                    )
                }

                "normal_distribution" -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = accentColor.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Fill in the known values. Leave exactly one field blank — that is what will be solved. " +
                                    "Area under the curve will always be computed.",
                            style = MaterialTheme.typography.bodySmall,
                            color = accentColor,
                            modifier = Modifier.padding(12.dp))
                    }

                    val mainVars = listOf("z", "x", "mean", "sd")
                    val blanks   = mainVars.filter { inputs[it].isNullOrBlank() }
                    val detected = if (blanks.size == 1) blanks.first() else null

                    listOf(
                        "z"    to "z — Z-score",
                        "x"    to "x — Raw score",
                        "mean" to "μ — Mean",
                        "sd"   to "σ — Standard deviation"
                    ).forEach { (key, label) ->
                        FormulaInputField(
                            variableKey   = key,
                            label         = label,
                            hint          = when (key) {
                                "z"    -> "e.g. 1.5"
                                "x"    -> "e.g. 85"
                                "mean" -> "e.g. 75"
                                "sd"   -> "e.g. 10"
                                else   -> ""
                            },
                            value         = inputs[key] ?: "",
                            onValueChange = { viewModel.updateInput(key, it) },
                            isUnknown     = detected == key
                        )
                    }
                }

                "linear_regression",
                "correlation" -> {
                    val xRaw   = inputs["xData"] ?: ""
                    val yRaw   = inputs["yData"] ?: ""
                    val xParsed = parseDataset(xRaw)
                    val yParsed = parseDataset(yRaw)

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = accentColor.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Enter X and Y datasets as comma-separated numbers. " +
                                    "Both must have the same count.",
                            style = MaterialTheme.typography.bodySmall,
                            color = accentColor,
                            modifier = Modifier.padding(12.dp))
                    }

                    DatasetInputField(
                        label            = "X values",
                        rawInput         = xRaw,
                        onRawInputChange = { viewModel.updateInput("xData", it) },
                        parsedValues     = xParsed,
                        accentColor      = accentColor,
                        hint             = "e.g. 1, 2, 3, 4, 5"
                    )

                    DatasetInputField(
                        label            = "Y values",
                        rawInput         = yRaw,
                        onRawInputChange = { viewModel.updateInput("yData", it) },
                        parsedValues     = yParsed,
                        accentColor      = accentColor,
                        hint             = "e.g. 2, 4, 5, 4, 5"
                    )

                    // Count mismatch warning
                    if (xParsed.isNotEmpty() && yParsed.isNotEmpty() && xParsed.size != yParsed.size) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("X has ${xParsed.size} values, Y has ${yParsed.size}. They must match.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(10.dp))
                        }
                    }

                    // Prediction fields (regression only)
                    if (topicId == "linear_regression") {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text("Optional — Prediction",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        FormulaInputField(
                            variableKey   = "predictY",
                            label         = "Given x — predict ŷ",
                            hint          = "e.g. 6",
                            value         = inputs["predictY"] ?: "",
                            onValueChange = { viewModel.updateInput("predictY", it) },
                            isUnknown     = false
                        )
                        FormulaInputField(
                            variableKey   = "predictX",
                            label         = "Given ŷ — predict x",
                            hint          = "e.g. 10",
                            value         = inputs["predictX"] ?: "",
                            onValueChange = { viewModel.updateInput("predictX", it) },
                            isUnknown     = false
                        )
                    }
                }

                "zellers_congruence" -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = accentColor.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Enter a date to find the day of the week using Zeller's Congruence formula.",
                            style = MaterialTheme.typography.bodySmall,
                            color = accentColor,
                            modifier = Modifier.padding(12.dp))
                    }

                    listOf(
                        "day"   to "Day — Day of month",
                        "month" to "Month — Month number (1–12)",
                        "year"  to "Year — Full year (e.g. 2025)"
                    ).forEach { (key, label) ->
                        FormulaInputField(
                            variableKey   = key,
                            label         = label,
                            hint          = when (key) {
                                "day"   -> "e.g. 25"
                                "month" -> "e.g. 12"
                                "year"  -> "e.g. 2025"
                                else    -> ""
                            },
                            value         = inputs[key] ?: "",
                            onValueChange = { viewModel.updateInput(key, it) },
                            isUnknown     = false
                        )
                    }
                }

                "modular_arithmetic" -> {
                    val modOperation by viewModel.modOperation.collectAsState()

                    // Operation selector
                    Text("Operation",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = accentColor)

                    // Row 1: default + addition + subtraction
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            "default"      to "a mod m",
                            "addition"     to "Add",
                            "subtraction"  to "Sub"
                        ).forEach { (op, label) ->
                            FilterChip(
                                selected = modOperation == op,
                                onClick  = { viewModel.setModOperation(op) },
                                label    = {
                                    Text(label,
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 1)
                                },
                                colors  = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = accentColor.copy(alpha = 0.2f),
                                    selectedLabelColor     = accentColor),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    // Row 2: multiplication + division
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            "multiplication" to "Multiply",
                            "division"       to "Divide"
                        ).forEach { (op, label) ->
                            FilterChip(
                                selected = modOperation == op,
                                onClick  = { viewModel.setModOperation(op) },
                                label    = {
                                    Text(label,
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 1)
                                },
                                colors  = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = accentColor.copy(alpha = 0.2f),
                                    selectedLabelColor     = accentColor),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    HorizontalDivider()

                    // Instruction card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = accentColor.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = when (modOperation) {
                                "default"        -> "a mod m = r\nLeave one field blank to solve for it."
                                "addition"       -> "(a + b) mod m = r\nLeave one field blank to solve for it."
                                "subtraction"    -> "(a − b) mod m = r\nLeave one field blank to solve for it."
                                "multiplication" -> "(a × b) mod m = r\nLeave one field blank to solve for it."
                                "division"       -> "(a / b) mod m = r\nUses modular inverse. b must be coprime to m."
                                else             -> ""
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = accentColor,
                            modifier = Modifier.padding(12.dp))
                    }

                    // Input fields
                    val modVars = if (modOperation == "default")
                        listOf("a" to "a — Dividend", "m" to "m — Modulus", "r" to "r — Remainder")
                    else
                        listOf(
                            "a" to "a — First operand",
                            "b" to "b — Second operand",
                            "m" to "m — Modulus",
                            "r" to "r — Result"
                        )

                    val blanks   = modVars.map { it.first }.filter { inputs[it].isNullOrBlank() }
                    val detected = if (blanks.size == 1) blanks.first() else null

                    modVars.forEach { (key, label) ->
                        FormulaInputField(
                            variableKey   = key,
                            label         = label,
                            hint          = when (key) {
                                "a" -> "e.g. 17"; "b" -> "e.g. 5"
                                "m" -> "e.g. 7";  "r" -> "e.g. 2"
                                else -> ""
                            },
                            value         = inputs[key] ?: "",
                            onValueChange = { viewModel.updateInput(key, it) },
                            isUnknown     = detected == key
                        )
                    }
                }

                "simple_interest" -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = accentColor.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("I = P × r × t  |  F = P(1 + rt)\n" +
                                "Fill P, r, and t → I and F computed automatically.\n" +
                                "Or fill any 3 values including I or F → solve for the missing one.\n" +
                                "Rate in % per year.",
                            style = MaterialTheme.typography.bodySmall,
                            color = accentColor, modifier = Modifier.padding(12.dp))
                    }
                    val siVars = listOf(
                        "p" to "P — Principal (₱)",
                        "r" to "r — Annual rate (%)",
                        "t" to "t — Time (years)",
                        "i" to "I — Interest earned (₱)",
                        "f" to "F — Maturity value (₱)")
                    val siBlanks   = siVars.map { it.first }.filter { inputs[it].isNullOrBlank() }
                    val siDetected = if (siBlanks.size == 1) siBlanks.first() else null
                    siVars.forEach { (key, label) ->
                        FormulaInputField(
                            variableKey = key, label = label,
                            hint = topic?.variableHints?.get(key) ?: "",
                            value = inputs[key] ?: "",
                            onValueChange = { viewModel.updateInput(key, it) },
                            isUnknown = siDetected == key)
                    }
                }

                "compound_interest" -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = accentColor.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("F = P(1 + j/m)^(mt)\n" +
                                "Leave one field blank to solve for it.\n" +
                                "Note: m (compounding periods) cannot be solved for algebraically.",
                            style = MaterialTheme.typography.bodySmall,
                            color = accentColor, modifier = Modifier.padding(12.dp))
                    }
                    val ciVars = listOf(
                        "p" to "P — Principal (₱)",
                        "j" to "j — Nominal annual rate (%)",
                        "m" to "m — Compounding periods/year",
                        "t" to "t — Time (years)",
                        "f" to "F — Future value (₱)")
                    val ciBlanks   = ciVars.map { it.first }.filter { inputs[it].isNullOrBlank() }
                    val ciDetected = if (ciBlanks.size == 1) ciBlanks.first() else null
                    ciVars.forEach { (key, label) ->
                        FormulaInputField(
                            variableKey = key, label = label,
                            hint = topic?.variableHints?.get(key) ?: "",
                            value = inputs[key] ?: "",
                            onValueChange = { viewModel.updateInput(key, it) },
                            isUnknown = ciDetected == key)
                    }
                }

                "stocks" -> {
                    FormulaPickerScreen(
                        formulas = listOf(
                            "dividend"     to "Dividend",
                            "yield"        to "Div. Yield",
                            "eps"          to "EPS",
                            "pe"           to "P/E Ratio",
                            "total_return" to "Total Return"
                        ),
                        selectedFormula = stocksFormula,
                        onFormulaChange = { viewModel.setStocksFormula(it) },
                        inputs          = inputs,
                        accentColor     = accentColor,
                        onInputChange   = { k, v -> viewModel.updateInput(k, v) },
                        labelMap        = stocksLabelMap(stocksFormula),
                        hintMap         = stocksHintMap(stocksFormula)
                    )
                }

                "bonds" -> {
                    FormulaPickerScreen(
                        formulas = listOf(
                            "coupon"         to "Coupon",
                            "current_yield"  to "Curr. Yield",
                            "bond_price"     to "Bond Price",
                            "total_interest" to "Total Int."
                        ),
                        selectedFormula = bondsFormula,
                        onFormulaChange = { viewModel.setBondsFormula(it) },
                        inputs          = inputs,
                        accentColor     = accentColor,
                        onInputChange   = { k, v -> viewModel.updateInput(k, v) },
                        labelMap        = bondsLabelMap(bondsFormula),
                        hintMap         = bondsHintMap(bondsFormula)
                    )
                }

                "mutual_funds" -> {
                    FormulaPickerScreen(
                        formulas = listOf(
                            "navps"       to "NAVPS",
                            "shares"      to "Shares",
                            "returns"     to "Returns",
                            "total_value" to "Total Value"
                        ),
                        selectedFormula = mfFormula,
                        onFormulaChange = { viewModel.setMFFormula(it) },
                        inputs          = inputs,
                        accentColor     = accentColor,
                        onInputChange   = { k, v -> viewModel.updateInput(k, v) },
                        labelMap        = mfLabelMap(mfFormula),
                        hintMap         = mfHintMap(mfFormula)
                    )
                }

                "loans" -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = accentColor.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("M = P[r(1+r)^n] / [(1+r)^n − 1]\n" +
                                "Annual rate will be converted to monthly automatically.\n" +
                                "Note: r (interest rate) cannot be solved for algebraically.",
                            style = MaterialTheme.typography.bodySmall,
                            color = accentColor, modifier = Modifier.padding(12.dp))
                    }
                    val lVars = listOf(
                        "p" to "P — Loan principal (₱)",
                        "r" to "r — Annual interest rate (%)",
                        "n" to "n — Total monthly payments",
                        "m" to "M — Monthly payment (₱)")
                    val lBlanks   = lVars.map { it.first }.filter { inputs[it].isNullOrBlank() }
                    val lDetected = if (lBlanks.size == 1) lBlanks.first() else null
                    lVars.forEach { (key, label) ->
                        FormulaInputField(
                            variableKey = key, label = label,
                            hint = topic?.variableHints?.get(key) ?: "",
                            value = inputs[key] ?: "",
                            onValueChange = { viewModel.updateInput(key, it) },
                            isUnknown = lDetected == key)
                    }
                }

                "credit_cards" -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = accentColor.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("BSP Circular 1098: Monthly rate cap is 2%.\n" +
                                "Enter your card's actual rate — must not exceed 2%.",
                            style = MaterialTheme.typography.bodySmall,
                            color = accentColor, modifier = Modifier.padding(12.dp))
                    }
                    listOf(
                        "balance"         to "Outstanding Balance (₱)",
                        "monthlyRate"     to "Monthly Interest Rate (%)",
                        "minPaymentPct"   to "Min. Payment % of Balance",
                        "minPaymentFloor" to "Min. Payment Floor (₱)"
                    ).forEach { (key, label) ->
                        FormulaInputField(
                            variableKey = key, label = label,
                            hint = topic?.variableHints?.get(key) ?: "",
                            value = inputs[key] ?: "",
                            onValueChange = { viewModel.updateInput(key, it) },
                            isUnknown = false)
                    }
                }

                else -> {
                    // Instruction card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = accentColor.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Fill in the known values. " +
                                    "Leave exactly one field blank — that is what will be solved.",
                            style = MaterialTheme.typography.bodySmall,
                            color = accentColor,
                            modifier = Modifier.padding(12.dp))
                    }

                    val mainVars = when (topicId) {
                        "arithmetic_sequence" -> listOf("a1", "d", "n", "an")
                        "geometric_sequence"  -> listOf("a1", "r", "n", "an")
                        else -> topic?.variables ?: emptyList()
                    }
                    val blanks = mainVars.filter { inputs[it].isNullOrBlank() }
                    val detectedUnknown = if (blanks.size == 1) blanks.first() else null

                    mainVars.forEach { variable ->
                        FormulaInputField(
                            variableKey   = variable,
                            label         = topic?.variableLabels?.get(variable) ?: variable,
                            hint          = topic?.variableHints?.get(variable) ?: "",
                            value         = inputs[variable] ?: "",
                            onValueChange = { viewModel.updateInput(variable, it) },
                            isUnknown     = detectedUnknown == variable
                        )
                    }

                    // Optional Sₙ
                    if (topicId in listOf("arithmetic_sequence", "geometric_sequence")) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text("Optional",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        FormulaInputField(
                            variableKey   = "sn",
                            label         = "Sₙ — Sum of n terms",
                            hint          = "Leave blank to auto-compute, or fill if given",
                            value         = inputs["sn"] ?: "",
                            onValueChange = { viewModel.updateInput("sn", it) },
                            isUnknown     = false
                        )
                    }
                }
            }

            // Error
            if (!errorMessage.isNullOrBlank()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(errorMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.clearAll(topicId) },
                    modifier = Modifier.weight(1f)
                ) { Text("Clear") }
                Button(
                    onClick = { viewModel.calculate(topicId) },
                    modifier = Modifier.weight(2f),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) { Text("Calculate", fontWeight = FontWeight.SemiBold) }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Fibonacci Section ────────────────────────────────────────────────────────

@Composable
private fun FibonacciSection(
    fibMode: String,
    fibSeedType: String,
    fibRows: List<Pair<String, String>>,
    accentColor: Color,
    onModeChange: (String) -> Unit,
    onSeedChange: (String) -> Unit,
    onRowChange: (Int, String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // Mode toggle
        Text("Mode", style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold, color = accentColor)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("default" to "Default Seeds", "custom" to "Custom Seeds").forEach { (m, label) ->
                FilterChip(
                    selected = fibMode == m,
                    onClick  = { onModeChange(m) },
                    label    = { Text(label) },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.2f),
                        selectedLabelColor     = accentColor)
                )
            }
        }

        // Seed toggle (default mode only)
        if (fibMode == "default") {
            Text("Seed variant",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("1,1", "0,1").forEach { seed ->
                    FilterChip(
                        selected = fibSeedType == seed,
                        onClick  = { onSeedChange(seed) },
                        label    = { Text("F(1,2) = $seed") },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor.copy(alpha = 0.2f),
                            selectedLabelColor     = accentColor)
                    )
                }
            }
        }

        HorizontalDivider()

        // Instruction
        Card(
            colors = CardDefaults.cardColors(
                containerColor = accentColor.copy(alpha = 0.08f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = when (fibMode) {
                    "default" ->
                        "Enter either a Position or a Value — leave the other blank.\n" +
                                "• Position blank → find position(s) of the value\n" +
                                "• Value blank → find value at that position"
                    else ->
                        "Fill 5 rows with known terms. Leave one field blank in exactly one row — that is the unknown.\n" +
                                "• Value blank → find value at that position\n" +
                                "• Position blank → find position(s) of that value"
                },
                style = MaterialTheme.typography.bodySmall,
                color = accentColor,
                modifier = Modifier.padding(12.dp)
            )
        }

        // Column headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Row", style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(32.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Text("Position (n)",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Text("Value F(n)",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }

        // Rows
        fibRows.forEachIndexed { index, (posStr, valStr) ->
            val posBlank = posStr.isBlank()
            val valBlank = valStr.isBlank()
            val isUnknownRow = (posBlank && !valBlank) || (!posBlank && valBlank)
            val rowLabel = if (fibMode == "custom") {
                if (index < 2) "K${index + 1}" else "?"
            } else "→"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Row label
                Text(rowLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isUnknownRow) FontWeight.Bold else FontWeight.Normal,
                    color = if (isUnknownRow) accentColor
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.width(32.dp))

                // Position field
                OutlinedTextField(
                    value = posStr,
                    onValueChange = { onRowChange(index, it, valStr) },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text("blank = find pos",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f))
                    },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (posBlank && !valBlank) accentColor
                        else MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = if (posBlank && !valBlank) accentColor.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                // Value field
                OutlinedTextField(
                    value = valStr,
                    onValueChange = { onRowChange(index, posStr, it) },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text("blank = find val",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f))
                    },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (valBlank && !posBlank) accentColor
                        else MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = if (valBlank && !posBlank) accentColor.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
// ─── Dataset only screen (Central Tendency, Dispersion) ──────────────────────

@Composable
private fun DatasetOnlyScreen(
    topicId: String,
    inputs: Map<String, String>,
    accentColor: Color,
    onInputChange: (String, String) -> Unit
) {
    val raw    = inputs["dataset"] ?: ""
    val parsed = parseDataset(raw)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = accentColor.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "Enter your dataset as comma-separated numbers. " +
                    "All measures will be computed and shown with full steps.",
            style = MaterialTheme.typography.bodySmall,
            color = accentColor,
            modifier = Modifier.padding(12.dp))
    }

    DatasetInputField(
        label            = "Dataset",
        rawInput         = raw,
        onRawInputChange = { onInputChange("dataset", it) },
        parsedValues     = parsed,
        accentColor      = accentColor
    )
}

// ─── Relative Position screen ─────────────────────────────────────────────────

@Composable
private fun RelativePositionScreen(
    inputs: Map<String, String>,
    accentColor: Color,
    onInputChange: (String, String) -> Unit
) {
    val raw         = inputs["dataset"] ?: ""
    val parsed      = parseDataset(raw)
    val measureType = inputs["measureType"] ?: "quartile"
    val k           = inputs["k"] ?: ""
    val x           = inputs["x"] ?: ""

    Card(
        colors = CardDefaults.cardColors(
            containerColor = accentColor.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Enter dataset and select the measure to compute.",
            style = MaterialTheme.typography.bodySmall,
            color = accentColor,
            modifier = Modifier.padding(12.dp))
    }

    DatasetInputField(
        label            = "Dataset",
        rawInput         = raw,
        onRawInputChange = { onInputChange("dataset", it) },
        parsedValues     = parsed,
        accentColor      = accentColor
    )

    // Measure type selector
    Text("Measure",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = accentColor)

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        listOf(
            "quartile"   to "Qₖ",
            "decile"     to "Dₖ",
            "percentile" to "Pₖ",
            "zscore"     to "Z"
        ).forEach { (type, label) ->
            FilterChip(
                selected = measureType == type,
                onClick  = { onInputChange("measureType", type) },
                label    = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                colors  = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = accentColor.copy(alpha = 0.2f),
                    selectedLabelColor     = accentColor),
                modifier = Modifier.weight(1f)
            )
        }
    }

    // k input (not for z-score)
    if (measureType != "zscore") {
        val kLabel = when (measureType) {
            "quartile"   -> "k — Which quartile? (1, 2, or 3)"
            "decile"     -> "k — Which decile? (1–9)"
            "percentile" -> "k — Which percentile? (1–99)"
            else         -> "k"
        }
        FormulaInputField(
            variableKey   = "k",
            label         = kLabel,
            hint          = when (measureType) {
                "quartile" -> "e.g. 1"; "decile" -> "e.g. 3"
                else -> "e.g. 25"
            },
            value         = k,
            onValueChange = { onInputChange("k", it) },
            isUnknown     = false
        )
    }

    // x input (z-score only)
    if (measureType == "zscore") {
        FormulaInputField(
            variableKey   = "x",
            label         = "x — Raw score",
            hint          = "e.g. 85",
            value         = x,
            onValueChange = { onInputChange("x", it) },
            isUnknown     = false
        )
    }
}

@Composable
private fun GroupedDataScreen(
    freqRows: List<FrequencyRow>,
    accentColor: Color,
    onRowChange: (Int, FrequencyRow) -> Unit,
    onAddRow: () -> Unit,
    onRemoveRow: (Int) -> Unit,
    showMeasurePicker: Boolean,
    inputs: Map<String, String>,
    onInputChange: (String, String) -> Unit
) {
    val validation = remember(freqRows) { validateFrequencyTable(freqRows) }
    val measureType = inputs["measureType"] ?: "quartile"
    val k           = inputs["k"] ?: "1"

    Card(
        colors = CardDefaults.cardColors(
            containerColor = accentColor.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "Enter your frequency distribution table. " +
                    "Lower and upper limits are class boundaries (e.g. 10–19).",
            style = MaterialTheme.typography.bodySmall,
            color = accentColor,
            modifier = Modifier.padding(12.dp))
    }

    FrequencyTableInput(
        rows             = freqRows,
        onRowChange      = onRowChange,
        onAddRow         = onAddRow,
        onRemoveRow      = onRemoveRow,
        accentColor      = accentColor,
        validationResult = validation
    )

    if (showMeasurePicker) {
        Text("Measure",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = accentColor)

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(
                "quartile"   to "Qₖ",
                "decile"     to "Dₖ",
                "percentile" to "Pₖ"
            ).forEach { (type, label) ->
                FilterChip(
                    selected = measureType == type,
                    onClick  = { onInputChange("measureType", type) },
                    label    = {
                        Text(label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1)
                    },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.2f),
                        selectedLabelColor     = accentColor),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        val kLabel = when (measureType) {
            "quartile"   -> "k — Which quartile? (1, 2, or 3)"
            "decile"     -> "k — Which decile? (1–9)"
            "percentile" -> "k — Which percentile? (1–99)"
            else         -> "k"
        }
        FormulaInputField(
            variableKey   = "k",
            label         = kLabel,
            hint          = when (measureType) {
                "quartile" -> "e.g. 1"
                "decile"   -> "e.g. 3"
                else       -> "e.g. 25"
            },
            value         = k,
            onValueChange = { onInputChange("k", it) },
            isUnknown     = false
        )
    }
}

// ─── Formula Picker Screen (Stocks, Bonds, Mutual Funds) ──────────────────────

@Composable
private fun FormulaPickerScreen(
    formulas: List<Pair<String, String>>,
    selectedFormula: String,
    onFormulaChange: (String) -> Unit,
    inputs: Map<String, String>,
    accentColor: Color,
    onInputChange: (String, String) -> Unit,
    labelMap: Map<String, String>,
    hintMap: Map<String, String>
) {
    Text("Select formula",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = accentColor)

    // Wrap chips in two rows if more than 3
    val row1 = formulas.take(3)
    val row2 = formulas.drop(3)

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()) {
        row1.forEach { (formula, label) ->
            FilterChip(
                selected = selectedFormula == formula,
                onClick  = { onFormulaChange(formula) },
                label    = { Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1) },
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = accentColor.copy(alpha = 0.2f),
                    selectedLabelColor     = accentColor),
                modifier = Modifier.weight(1f)
            )
        }
    }
    if (row2.isNotEmpty()) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()) {
            row2.forEach { (formula, label) ->
                FilterChip(
                    selected = selectedFormula == formula,
                    onClick  = { onFormulaChange(formula) },
                    label    = { Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1) },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.2f),
                        selectedLabelColor     = accentColor),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    HorizontalDivider()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = accentColor.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Leave exactly one field blank to solve for it.",
            style = MaterialTheme.typography.bodySmall,
            color = accentColor, modifier = Modifier.padding(12.dp))
    }

    val blanks   = inputs.keys.filter { inputs[it].isNullOrBlank() }
    val detected = if (blanks.size == 1) blanks.first() else null

    inputs.keys.forEach { key ->
        FormulaInputField(
            variableKey   = key,
            label         = labelMap[key] ?: key,
            hint          = hintMap[key] ?: "",
            value         = inputs[key] ?: "",
            onValueChange = { onInputChange(key, it) },
            isUnknown     = detected == key
        )
    }
}

// ─── Label and hint maps ──────────────────────────────────────────────────────

private fun stocksLabelMap(formula: String) = when (formula) {
    "dividend"     -> mapOf("shares" to "Shares", "dividendPerShare" to "Dividend per Share (₱)", "totalDividend" to "Total Dividend (₱)")
    "yield"        -> mapOf("annualDividend" to "Annual Dividend (₱)", "marketPrice" to "Market Price (₱)", "dividendYield" to "Dividend Yield (%)")
    "eps"          -> mapOf("netIncome" to "Net Income (₱)", "totalShares" to "Total Shares", "eps" to "EPS (₱)")
    "pe"           -> mapOf("marketPrice" to "Market Price (₱)", "eps" to "EPS (₱)", "peRatio" to "P/E Ratio")
    "total_return" -> mapOf("dividends" to "Dividends (₱)", "capitalGain" to "Capital Gain (₱)", "purchasePrice" to "Purchase Price (₱)", "totalReturn" to "Total Return (%)")
    else           -> emptyMap()
}

private fun stocksHintMap(formula: String) = when (formula) {
    "dividend"     -> mapOf("shares" to "e.g. 1000", "dividendPerShare" to "e.g. 2.50", "totalDividend" to "e.g. 2500")
    "yield"        -> mapOf("annualDividend" to "e.g. 5", "marketPrice" to "e.g. 100", "dividendYield" to "e.g. 5")
    "eps"          -> mapOf("netIncome" to "e.g. 1000000", "totalShares" to "e.g. 500000", "eps" to "e.g. 2")
    "pe"           -> mapOf("marketPrice" to "e.g. 50", "eps" to "e.g. 5", "peRatio" to "e.g. 10")
    "total_return" -> mapOf("dividends" to "e.g. 500", "capitalGain" to "e.g. 2000", "purchasePrice" to "e.g. 10000", "totalReturn" to "e.g. 25")
    else           -> emptyMap()
}

private fun bondsLabelMap(formula: String) = when (formula) {
    "coupon"         -> mapOf("faceValue" to "Face Value (₱)", "couponRate" to "Coupon Rate (%)", "couponPayment" to "Coupon Payment (₱)")
    "current_yield"  -> mapOf("annualCoupon" to "Annual Coupon (₱)", "marketPrice" to "Market Price (₱)", "currentYield" to "Current Yield (%)")
    "bond_price"     -> mapOf("coupon" to "Coupon Payment (₱)", "rate" to "Yield Rate (%)", "periods" to "Number of Periods", "faceValue" to "Face Value (₱)", "bondPrice" to "Bond Price (₱)")
    "total_interest" -> mapOf("couponPayment" to "Coupon Payment (₱)", "periods" to "Number of Periods", "totalInterest" to "Total Interest (₱)")
    else             -> emptyMap()
}

private fun bondsHintMap(formula: String) = when (formula) {
    "coupon"         -> mapOf("faceValue" to "e.g. 10000", "couponRate" to "e.g. 8", "couponPayment" to "e.g. 800")
    "current_yield"  -> mapOf("annualCoupon" to "e.g. 800", "marketPrice" to "e.g. 9500", "currentYield" to "e.g. 8.42")
    "bond_price"     -> mapOf("coupon" to "e.g. 800", "rate" to "e.g. 10", "periods" to "e.g. 5", "faceValue" to "e.g. 10000", "bondPrice" to "e.g. 9241")
    "total_interest" -> mapOf("couponPayment" to "e.g. 800", "periods" to "e.g. 5", "totalInterest" to "e.g. 4000")
    else             -> emptyMap()
}

private fun mfLabelMap(formula: String) = when (formula) {
    "navps"       -> mapOf("nav" to "NAV (₱)", "totalShares" to "Total Shares Outstanding", "navps" to "NAVPS (₱)")
    "shares"      -> mapOf("amountInvested" to "Amount Invested (₱)", "navps" to "NAVPS (₱)", "shares" to "Number of Shares")
    "returns"     -> mapOf("currentNAVPS" to "Current NAVPS (₱)", "purchaseNAVPS" to "Purchase NAVPS (₱)", "returns" to "Returns (%)")
    "total_value" -> mapOf("shares" to "Number of Shares", "currentNAVPS" to "Current NAVPS (₱)", "totalValue" to "Total Value (₱)")
    else          -> emptyMap()
}

private fun mfHintMap(formula: String) = when (formula) {
    "navps"       -> mapOf("nav" to "e.g. 5000000", "totalShares" to "e.g. 1000000", "navps" to "e.g. 5")
    "shares"      -> mapOf("amountInvested" to "e.g. 10000", "navps" to "e.g. 5", "shares" to "e.g. 2000")
    "returns"     -> mapOf("currentNAVPS" to "e.g. 6", "purchaseNAVPS" to "e.g. 5", "returns" to "e.g. 20")
    "total_value" -> mapOf("shares" to "e.g. 2000", "currentNAVPS" to "e.g. 6", "totalValue" to "e.g. 12000")
    else          -> emptyMap()
}