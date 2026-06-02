package com.solodev.mmwcalc.ui.screens.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.solodev.mmwcalc.data.content.FormulaEntry
import com.solodev.mmwcalc.data.content.TopicContentRegistry
import com.solodev.mmwcalc.data.content.WorkedExample
import com.solodev.mmwcalc.domain.models.TopicRegistry
import com.solodev.mmwcalc.ui.screens.home.categoryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDetailScreen(
    topicId: String,
    onBack: () -> Unit,
    onOpenCalculator: (String) -> Unit
) {
    val topic   = TopicRegistry.findById(topicId)
    val content = TopicContentRegistry.findById(topicId)
    val accent  = topic?.category?.let { categoryColor(it) }
        ?: MaterialTheme.colorScheme.primary

    if (content == null || topic == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Content not found.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(topic.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold)
                        Text(topic.category.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = accent)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── What is it? ───────────────────────────────────────────────────
            LearnSection(title = "📖 What is it?", accentColor = accent) {
                Text(content.whatIsIt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
            }

            // ── Real-world analogy ────────────────────────────────────────────
            LearnSection(title = "🌍 Real-world Analogy", accentColor = accent) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent.copy(alpha = 0.06f))
                        .padding(12.dp)
                ) {
                    Text(content.analogy,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                }
            }

            // ── Formula reference ─────────────────────────────────────────────
            LearnSection(title = "📐 Formula Reference", accentColor = accent) {
                FormulaCard(formulas = content.formulas, accentColor = accent)
            }

            // ── Worked example ────────────────────────────────────────────────
            LearnSection(title = "✏️ Worked Example", accentColor = accent) {
                WorkedExampleCard(example = content.workedExample, accentColor = accent)
            }

            // ── Tips & Tricks ─────────────────────────────────────────────────
            LearnSection(title = "💡 Tips & Tricks", accentColor = accent) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    content.tipsAndTricks.forEachIndexed { index, tip ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(accent)
                            )
                            Text(tip,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // ── Did you know? ─────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = accent.copy(alpha = 0.10f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("🤔 Did You Know?",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = accent)
                    Spacer(Modifier.height(6.dp))
                    Text(content.didYouKnow,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                }
            }

            // ── Open Calculator button ────────────────────────────────────────
            Button(
                onClick  = { onOpenCalculator(topicId) },
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(containerColor = accent)
            ) {
                Icon(Icons.Default.Calculate,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Open Calculator", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Section wrapper ──────────────────────────────────────────────────────────

@Composable
private fun LearnSection(
    title: String,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = accentColor)
        content()
    }
}

// ─── Formula card ─────────────────────────────────────────────────────────────

@Composable
private fun FormulaCard(formulas: List<FormulaEntry>, accentColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            formulas.forEach { entry ->
                Column {
                    if (entry.label.isNotBlank()) {
                        Text(entry.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor,
                            fontWeight = FontWeight.SemiBold)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.08f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(entry.formula,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                    }
                }
            }
        }
    }
}

// ─── Worked example card ──────────────────────────────────────────────────────

@Composable
private fun WorkedExampleCard(example: WorkedExample, accentColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Problem
            Column {
                Text("Problem:",
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(example.problem,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
            }

            HorizontalDivider(color = accentColor.copy(alpha = 0.2f))

            // Solution
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Solution:",
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.06f))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        example.solution.forEach { line ->
                            Text(
                                text  = line,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = if (line.isBlank()) FontFamily.Default
                                    else FontFamily.Monospace),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = accentColor.copy(alpha = 0.2f))

            // Answer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 20.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(accentColor)
                )
                Column {
                    Text("Answer:",
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold)
                    Text(example.answer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = accentColor)
                }
            }
        }
    }
}