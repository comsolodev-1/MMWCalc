package com.solodev.mmwcalc.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.*

@Composable
fun NormalCurveView(
    zScore: Double,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Normal Distribution Curve",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = accentColor)

        Spacer(Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val w     = size.width
            val h     = size.height
            val steps = 300

            // Z range to display: -4 to 4
            val zMin  = -4.0
            val zMax  =  4.0
            val zRange = zMax - zMin

            fun zToX(z: Double): Float = ((z - zMin) / zRange * w).toFloat()
            fun pdfY(z: Double): Float {
                val pdf = exp(-0.5 * z * z) / sqrt(2 * PI)
                // Scale: max pdf at z=0 is ~0.399, map to 85% of height
                return (h - (pdf / 0.4) * h * 0.85f).toFloat()
            }

            // ── Draw filled shaded area (left of z) ───────────────────────────
            val shadedPath = Path()
            val zClamped = zScore.coerceIn(zMin, zMax)
            shadedPath.moveTo(zToX(zMin), h)
            for (i in 0..steps) {
                val z = zMin + i * zRange / steps
                if (z > zClamped) break
                shadedPath.lineTo(zToX(z), pdfY(z))
            }
            shadedPath.lineTo(zToX(zClamped), h)
            shadedPath.close()

            drawPath(shadedPath, color = accentColor.copy(alpha = 0.25f), style = Fill)

            // ── Draw bell curve outline ───────────────────────────────────────
            val curvePath = Path()
            for (i in 0..steps) {
                val z = zMin + i * zRange / steps
                val x = zToX(z)
                val y = pdfY(z)
                if (i == 0) curvePath.moveTo(x, y)
                else curvePath.lineTo(x, y)
            }
            drawPath(curvePath,
                color     = accentColor,
                style     = Stroke(width = 2.5f))

            // ── Baseline ─────────────────────────────────────────────────────
            drawLine(
                color       = accentColor.copy(alpha = 0.3f),
                start       = Offset(0f, h),
                end         = Offset(w, h),
                strokeWidth = 1f)

            // ── Z-score vertical line ─────────────────────────────────────────
            val zX = zToX(zClamped)
            drawLine(
                color       = accentColor,
                start       = Offset(zX, pdfY(zClamped)),
                end         = Offset(zX, h),
                strokeWidth = 2f)

            // ── Mean line (z = 0) ─────────────────────────────────────────────
            val meanX = zToX(0.0)
            drawLine(
                color       = accentColor.copy(alpha = 0.4f),
                start       = Offset(meanX, pdfY(0.0)),
                end         = Offset(meanX, h),
                strokeWidth = 1f)

            // ── Tick marks at -3, -2, -1, 0, 1, 2, 3 ─────────────────────────
            listOf(-3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0).forEach { tick ->
                val tx = zToX(tick)
                drawLine(
                    color       = accentColor.copy(alpha = 0.3f),
                    start       = Offset(tx, h),
                    end         = Offset(tx, h - 8f),
                    strokeWidth = 1f)
            }
        }

        // Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("-3", "-2", "-1", "0", "1", "2", "3").forEach { label ->
                Text(label,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor.copy(alpha = 0.6f))
            }
        }

        Spacer(Modifier.height(4.dp))

        // Area info
        val areaLeft  = cumulativeNormalPublic(zScore)
        val areaRight = 1.0 - areaLeft
        Text(
            "z = ${"%.4f".format(zScore)}  |  " +
                    "P(Z < z) = ${"%.4f".format(areaLeft)}  |  " +
                    "P(Z > z) = ${"%.4f".format(areaRight)}",
            style = MaterialTheme.typography.labelSmall,
            color = accentColor,
            fontWeight = FontWeight.Medium)
    }
}

fun cumulativeNormalPublic(z: Double): Double {
    val t    = 1.0 / (1.0 + 0.2316419 * abs(z))
    val poly = t * (0.319381530
            + t * (-0.356563782
            + t * (1.781477937
            + t * (-1.821255978
            + t * 1.330274429))))
    val pdf  = exp(-0.5 * z * z) / sqrt(2 * PI)
    val area = 1.0 - pdf * poly
    return if (z >= 0) area else 1.0 - area
}