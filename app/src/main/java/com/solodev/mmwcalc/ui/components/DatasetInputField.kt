package com.solodev.mmwcalc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DatasetInputField(
    label: String,
    rawInput: String,
    onRawInputChange: (String) -> Unit,
    parsedValues: List<Double>,
    accentColor: Color,
    modifier: Modifier = Modifier,
    hint: String = "e.g. 2, 4, 6, 8, 10"
) {
    Column(modifier = modifier.fillMaxWidth()) {

        Text(label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = accentColor)

        Spacer(Modifier.height(6.dp))

        OutlinedTextField(
            value = rawInput,
            onValueChange = onRawInputChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(hint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            },
            trailingIcon = {
                if (rawInput.isNotBlank()) {
                    IconButton(onClick = { onRawInputChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = accentColor,
                unfocusedBorderColor = if (parsedValues.isNotEmpty())
                    accentColor.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            ),
            minLines = 2
        )

        // Parsed values preview
        if (parsedValues.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.06f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column {
                    Text("${parsedValues.size} value(s) parsed:",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor)
                    Spacer(Modifier.height(2.dp))
                    Text(parsedValues.joinToString(", ") { it.fmt() },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                }
            }
        }

        // Parse error hint
        // Detect invalid tokens
        val tokens = rawInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
        val invalidTokens = tokens.filter { it.toDoubleOrNull() == null }

        if (rawInput.isNotBlank() && parsedValues.isEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text("Could not parse any values. Use comma-separated numbers: 2, 4, 6, 8",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error)
        } else if (invalidTokens.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text("⚠ Ignored invalid entries: ${invalidTokens.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error)
        }
    }
}

fun Double.fmt(): String =
    if (this == kotlin.math.floor(this) && !this.isInfinite())
        this.toLong().toString()
    else "%.4f".format(this).trimEnd('0').trimEnd('.')

fun parseDataset(raw: String): List<Double> =
    raw.split(",")
        .mapNotNull { it.trim().toDoubleOrNull() }
        .filter { it.isFinite() }