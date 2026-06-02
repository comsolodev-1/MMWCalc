package com.solodev.mmwcalc.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solodev.mmwcalc.data.repository.HistoryRepository
import com.solodev.mmwcalc.domain.models.HistoryItem
import com.solodev.mmwcalc.domain.models.StepItem
import com.solodev.mmwcalc.domain.models.TopicRegistry
import com.solodev.mmwcalc.ui.screens.home.categoryColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// ─── ViewModel ────────────────────────────────────────────────────────────────

@HiltViewModel
class HistoryDetailViewModel @Inject constructor(
    private val repository: HistoryRepository
) : ViewModel() {

    private val _item = MutableStateFlow<HistoryItem?>(null)
    val item: StateFlow<HistoryItem?> = _item.asStateFlow()

    fun loadItem(id: Int) {
        viewModelScope.launch {
            _item.value = repository.getById(id)
        }
    }
}

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    historyId: Int,
    onBack: () -> Unit,
    onReopenInCalculator: (topicId: String, inputs: Map<String, String>) -> Unit,
    viewModel: HistoryDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(historyId) { viewModel.loadItem(historyId) }

    val item by viewModel.item.collectAsState()

    if (item == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val historyItem = item!!
    val topic       = TopicRegistry.findById(historyItem.topicId)
    val accentColor = topic?.category?.let { categoryColor(it) }
        ?: MaterialTheme.colorScheme.primary
    val dateFormat  = SimpleDateFormat("MMMM dd, yyyy  h:mm a", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(historyItem.topicName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold)
                        Text(dateFormat.format(Date(historyItem.timestampMs)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
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
                    Text(historyItem.solvedForLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = accentColor.copy(alpha = 0.2f))
                    Spacer(Modifier.height(12.dp))
                    Text("Answer",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor)
                    Spacer(Modifier.height(4.dp))
                    Text(historyItem.answer,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = accentColor)
                }
            }

            // ── Given values ──────────────────────────────────────────────────
            if (historyItem.inputs.isNotEmpty()) {
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
                        historyItem.inputs
                            .filter { it.value.isNotBlank() }
                            .forEach { (key, value) ->
                                val label = topic?.variableLabels?.get(key) ?: friendlyLabelHistory(key)
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

            // ── Step-by-step ──────────────────────────────────────────────────
            Text("Step-by-step Solution",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)

            historyItem.steps.forEachIndexed { index, step ->
                HistoryStepCard(step = step, accentColor = accentColor)
                if (index < historyItem.steps.size - 1) {
                    HorizontalDivider(
                        modifier  = Modifier.padding(start = 38.dp),
                        thickness = 0.5.dp,
                        color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Re-open in calculator ─────────────────────────────────────────
            Button(
                onClick  = { onReopenInCalculator(historyItem.topicId, historyItem.inputs) },
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Icon(Icons.Default.Replay,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Re-open in Calculator", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Step card (same as ResultScreen but self-contained) ──────────────────────

@Composable
private fun HistoryStepCard(
    step: StepItem,
    accentColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
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
            Text(step.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
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
                        text     = step.expression,
                        style    = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace),
                        color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        softWrap = false
                    )
                }
            }
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
private fun friendlyLabelHistory(key: String): String = when (key) {
    "xData"           -> "X values"
    "yData"           -> "Y values"
    "dataset"         -> "Dataset"
    "measureType"     -> "Measure type"
    "k"               -> "k"
    "x"               -> "x — Raw score"
    "predictX"        -> "Predict x (given ŷ)"
    "predictY"        -> "Predict ŷ (given x)"
    "balance"         -> "Outstanding Balance (₱)"
    "monthlyRate"     -> "Monthly Rate (%)"
    "minPaymentPct"   -> "Min. Payment %"
    "minPaymentFloor" -> "Min. Payment Floor (₱)"
    else              -> key
}