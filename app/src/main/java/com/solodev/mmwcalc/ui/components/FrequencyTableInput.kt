package com.solodev.mmwcalc.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class FrequencyRow(
    val lower: String = "",
    val upper: String = "",
    val freq:  String = ""
)

data class ValidatedFrequencyTable(
    val rows: List<ValidatedRow>,
    val errors: List<String>
) {
    val isValid: Boolean get() = errors.isEmpty() && rows.isNotEmpty()
}

data class ValidatedRow(
    val lower: Double,
    val upper: Double,
    val freq:  Int,
    val lowerBoundary: Double = lower - 0.5,
    val upperBoundary: Double = upper + 0.5,
    val midpoint: Double = (lower - 0.5 + upper + 0.5) / 2.0,
    val classWidth: Double = (upper + 0.5) - (lower - 0.5)
)

fun validateFrequencyTable(rows: List<FrequencyRow>): ValidatedFrequencyTable {
    val errors  = mutableListOf<String>()
    val valid   = mutableListOf<ValidatedRow>()

    val filledRows = rows.filter {
        it.lower.isNotBlank() || it.upper.isNotBlank() || it.freq.isNotBlank()
    }

    if (filledRows.isEmpty()) {
        errors += "Please enter at least one class interval."
        return ValidatedFrequencyTable(emptyList(), errors)
    }

    if (filledRows.size < 2) {
        errors += "Please enter at least 2 class intervals."
        return ValidatedFrequencyTable(emptyList(), errors)
    }

    filledRows.forEachIndexed { index, row ->
        val rowNum = index + 1
        val lower = row.lower.trim().toDoubleOrNull()
        val upper = row.upper.trim().toDoubleOrNull()
        val freq  = row.freq.trim().toIntOrNull()

        if (lower == null) errors += "Row $rowNum: Lower limit must be a number."
        if (upper == null) errors += "Row $rowNum: Upper limit must be a number."
        if (freq  == null) errors += "Row $rowNum: Frequency must be a whole number."

        if (lower != null && upper != null) {
            if (lower >= upper)
                errors += "Row $rowNum: Lower limit ($lower) must be less than upper limit ($upper)."
        }
        if (freq != null && freq < 0)
            errors += "Row $rowNum: Frequency cannot be negative."

        if (lower != null && upper != null && freq != null && lower < upper && freq >= 0) {
            valid += ValidatedRow(lower, upper, freq)
        }
    }

    // Check ascending order
    if (valid.size >= 2) {
        for (i in 1 until valid.size) {
            if (valid[i].lower <= valid[i - 1].lower)
                errors += "Row ${i + 1}: Intervals must be in ascending order " +
                        "(${valid[i].lower} ≤ ${valid[i-1].lower})."
        }
    }

    // Check overlaps
    if (valid.size >= 2) {
        for (i in 1 until valid.size) {
            if (valid[i].lower <= valid[i - 1].upper)
                errors += "Row ${i + 1}: Interval [${valid[i].lower}, ${valid[i].upper}] " +
                        "overlaps with previous [${valid[i-1].lower}, ${valid[i-1].upper}]."
        }
    }

    return ValidatedFrequencyTable(valid, errors)
}

@Composable
fun FrequencyTableInput(
    rows: List<FrequencyRow>,
    onRowChange: (Int, FrequencyRow) -> Unit,
    onAddRow: () -> Unit,
    onRemoveRow: (Int) -> Unit,
    accentColor: Color,
    validationResult: ValidatedFrequencyTable? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        Text("Frequency Distribution Table",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = accentColor)

        Spacer(Modifier.height(2.dp))

        // Column headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Lower",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Text("Upper",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Text("Freq (f)",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(Modifier.width(36.dp))
        }

        // Rows
        rows.forEachIndexed { index, row ->
            val rowValidation = getRowValidationState(row)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lower
                FreqCell(
                    value       = row.lower,
                    onValueChange = { onRowChange(index, row.copy(lower = it)) },
                    placeholder = "e.g. 10",
                    isError     = rowValidation.lowerError,
                    modifier    = Modifier.weight(1f)
                )
                // Upper
                FreqCell(
                    value       = row.upper,
                    onValueChange = { onRowChange(index, row.copy(upper = it)) },
                    placeholder = "e.g. 19",
                    isError     = rowValidation.upperError,
                    modifier    = Modifier.weight(1f)
                )
                // Freq
                FreqCell(
                    value       = row.freq,
                    onValueChange = { onRowChange(index, row.copy(freq = it)) },
                    placeholder = "e.g. 5",
                    isError     = rowValidation.freqError,
                    keyboardType = KeyboardType.Number,
                    modifier    = Modifier.weight(1f)
                )
                // Remove button
                IconButton(
                    onClick  = { onRemoveRow(index) },
                    modifier = Modifier.size(36.dp),
                    enabled  = rows.size > 2
                ) {
                    Icon(Icons.Default.Remove,
                        contentDescription = "Remove row",
                        tint = if (rows.size > 2)
                            MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        modifier = Modifier.size(18.dp))
                }
            }
        }

        // Add row button
        OutlinedButton(
            onClick  = onAddRow,
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Add,
                contentDescription = "Add row",
                modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Add Class Interval",
                style = MaterialTheme.typography.labelMedium)
        }

        // Validation summary
        if (validationResult != null) {
            if (validationResult.isValid) {
                // Show computed class boundaries preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.06f))
                        .padding(10.dp)
                ) {
                    Column {
                        Text("✓ Table validated — class boundaries:",
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor,
                            fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        // Header
                        Row(Modifier.fillMaxWidth()) {
                            Text("Interval", Modifier.weight(1.5f),
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor.copy(alpha = 0.7f))
                            Text("LB", Modifier.weight(1f),
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center)
                            Text("UB", Modifier.weight(1f),
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center)
                            Text("xₘ", Modifier.weight(1f),
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center)
                            Text("f", Modifier.weight(0.7f),
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center)
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 2.dp),
                            color = accentColor.copy(alpha = 0.2f))
                        validationResult.rows.forEach { r ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 1.dp)
                            ) {
                                Text("${r.lower.fmtG()}–${r.upper.fmtG()}",
                                    Modifier.weight(1.5f),
                                    style = MaterialTheme.typography.bodySmall)
                                Text(r.lowerBoundary.fmtG(),
                                    Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center)
                                Text(r.upperBoundary.fmtG(),
                                    Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center)
                                Text(r.midpoint.fmtG(),
                                    Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center)
                                Text("${r.freq}",
                                    Modifier.weight(0.7f),
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center)
                            }
                        }
                        val totalF = validationResult.rows.sumOf { it.freq }
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 2.dp),
                            color = accentColor.copy(alpha = 0.2f))
                        Row(Modifier.fillMaxWidth()) {
                            Text("Total",
                                Modifier.weight(4.2f),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold)
                            Text("$totalF",
                                Modifier.weight(0.7f),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center)
                        }
                    }
                }
            } else if (validationResult.errors.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Please fix the following:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        validationResult.errors.forEach { err ->
                            Text("• $err",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FreqCell(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Decimal,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        modifier      = modifier.height(52.dp),
        placeholder   = {
            Text(placeholder,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f))
        },
        singleLine    = true,
        isError       = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape         = RoundedCornerShape(8.dp),
        textStyle     = MaterialTheme.typography.bodySmall,
        colors        = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = if (isError)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    )
}

private data class RowValidation(
    val lowerError: Boolean = false,
    val upperError: Boolean = false,
    val freqError:  Boolean = false
)

private fun getRowValidationState(row: FrequencyRow): RowValidation {
    if (row.lower.isBlank() && row.upper.isBlank() && row.freq.isBlank())
        return RowValidation()
    val lower = row.lower.trim().toDoubleOrNull()
    val upper = row.upper.trim().toDoubleOrNull()
    val freq  = row.freq.trim().toIntOrNull()
    return RowValidation(
        lowerError = row.lower.isNotBlank() && lower == null,
        upperError = row.upper.isNotBlank() &&
                (upper == null || (lower != null && upper <= lower)),
        freqError  = row.freq.isNotBlank() && (freq == null || freq < 0)
    )
}

fun Double.fmtG(): String =
    if (this == kotlin.math.floor(this) && !this.isInfinite())
        this.toLong().toString()
    else "%.2f".format(this)