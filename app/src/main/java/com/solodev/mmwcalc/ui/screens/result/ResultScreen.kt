package com.solodev.mmwcalc.ui.screens.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.solodev.mmwcalc.domain.models.StepItem
import com.solodev.mmwcalc.domain.models.TopicRegistry
import com.solodev.mmwcalc.ui.screens.calculator.CalculatorViewModel
import com.solodev.mmwcalc.ui.screens.home.categoryColor
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import com.solodev.mmwcalc.ui.components.NormalCurveView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    topicId: String,
    onBack: () -> Unit,
    onRecalculate: () -> Unit,
    viewModel: CalculatorViewModel
) {
    val result by viewModel.result.collectAsState()
    val topic  = TopicRegistry.findById(topicId)

    val accentColor = topic?.category?.let { categoryColor(it) }
        ?: MaterialTheme.colorScheme.primary

    // If result is null (e.g. user navigated here directly), go back
    LaunchedEffect(result) {
        if (result == null) onBack()
    }

    val calcResult = result ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(topic?.name ?: "Result",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold)
                        Text("Solution",
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

            // ── Answer card ───────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = accentColor.copy(alpha = 0.12f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Solved for",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor)
                    Spacer(Modifier.height(2.dp))
                    Text(calcResult.solvedForLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = accentColor.copy(alpha = 0.2f))
                    Spacer(Modifier.height(12.dp))
                    Text("Answer",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor)
                    Spacer(Modifier.height(4.dp))
                    Text(calcResult.answer,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = accentColor)
                }
            }

            // ── Inputs summary ────────────────────────────────────────────────
            if (calcResult.inputs.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                            .copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Given values",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(Modifier.height(6.dp))
                        val topic2 = TopicRegistry.findById(topicId)
                        calcResult.inputs
                            .filter { it.value.isNotBlank() }
                            .forEach { (key, value) ->
                                val label = topic?.variableLabels?.get(key) ?: friendlyLabel(key)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(label,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                            .copy(alpha = 0.7f))
                                    Text(value,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium)
                                }
                            }
                    }
                }
            }

            // ── Normal distribution curve ─────────────────────────────────────────────
            if (topicId == "normal_distribution") {
                val zValue = calcResult.inputs["z"]?.toDoubleOrNull()
                    ?: run {
                        // if z was the solved variable, get it from answer
                        if (calcResult.solvedFor == "z")
                            calcResult.answer.toDoubleOrNull()
                        else null
                    }
                if (zValue != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        NormalCurveView(
                            zScore      = zValue,
                            accentColor = accentColor,
                            modifier    = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // ── Step-by-step ──────────────────────────────────────────────────
            Text("Step-by-step Solution",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)

            calcResult.steps.forEachIndexed { index, step ->
                StepCard(step = step, accentColor = accentColor)
                if (index < calcResult.steps.size - 1) {
                    StepDivider()
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Buttons ───────────────────────────────────────────────────────
            // Auto-saved indicator
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(8.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                        .copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("✓  Saved to history",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium)
                }
            }

            Button(
                onClick  = {
                    viewModel.clearResult()
                    onRecalculate()
                },
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Text("Recalculate", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Step Card ────────────────────────────────────────────────────────────────

@Composable
private fun StepCard(
    step: StepItem,
    accentColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Step number bubble
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(50))
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text("${step.stepNumber}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = accentColor)
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Title
            Text(step.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold)

            Spacer(Modifier.height(4.dp))

            // Expression box — horizontally scrollable for wide tables
            val hScroll = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.06f))
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(hScroll)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text  = step.expression,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        softWrap = false
                    )
                }
            }

            // Result highlight
            if (!step.result.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(3.dp, 16.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(accentColor)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(step.result,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = accentColor)
                }
            }
        }
    }
}

@Composable
private fun StepDivider() {
    HorizontalDivider(
        modifier  = Modifier.padding(start = 38.dp),
        thickness = 0.5.dp,
        color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

private fun friendlyLabel(key: String): String = when (key) {
    "xData"           -> "X values"
    "yData"           -> "Y values"
    "dataset"         -> "Dataset"
    "measureType"     -> "Measure type"
    "k"               -> "k"
    "x"               -> "x — Raw score"
    "predictX"        -> "Predict x (given ŷ)"
    "predictY"        -> "Predict ŷ (given x)"
    "annualDividend"  -> "Annual Dividend (₱)"
    "marketPrice"     -> "Market Price (₱)"
    "dividendYield"   -> "Dividend Yield (%)"
    "netIncome"       -> "Net Income (₱)"
    "totalShares"     -> "Total Shares"
    "eps"             -> "EPS (₱)"
    "peRatio"         -> "P/E Ratio"
    "shares"          -> "Shares"
    "dividendPerShare"-> "Dividend per Share (₱)"
    "totalDividend"   -> "Total Dividend (₱)"
    "dividends"       -> "Dividends (₱)"
    "capitalGain"     -> "Capital Gain (₱)"
    "purchasePrice"   -> "Purchase Price (₱)"
    "totalReturn"     -> "Total Return (%)"
    "faceValue"       -> "Face Value (₱)"
    "couponRate"      -> "Coupon Rate (%)"
    "couponPayment"   -> "Coupon Payment (₱)"
    "annualCoupon"    -> "Annual Coupon (₱)"
    "currentYield"    -> "Current Yield (%)"
    "coupon"          -> "Coupon (₱)"
    "rate"            -> "Yield Rate (%)"
    "periods"         -> "Number of Periods"
    "bondPrice"       -> "Bond Price (₱)"
    "totalInterest"   -> "Total Interest (₱)"
    "nav"             -> "NAV (₱)"
    "navps"           -> "NAVPS (₱)"
    "amountInvested"  -> "Amount Invested (₱)"
    "currentNAVPS"    -> "Current NAVPS (₱)"
    "purchaseNAVPS"   -> "Purchase NAVPS (₱)"
    "returns"         -> "Returns (%)"
    "totalValue"      -> "Total Value (₱)"
    "balance"         -> "Outstanding Balance (₱)"
    "monthlyRate"     -> "Monthly Rate (%)"
    "minPaymentPct"   -> "Min. Payment %"
    "minPaymentFloor" -> "Min. Payment Floor (₱)"
    "n"               -> "n — dataset"
    else              -> key
}