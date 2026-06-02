package com.solodev.mmwcalc.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.solodev.mmwcalc.domain.models.Topic
import com.solodev.mmwcalc.domain.models.TopicCategory
import com.solodev.mmwcalc.domain.models.TopicRegistry
import com.solodev.mmwcalc.ui.theme.*
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Copyright
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.solodev.mmwcalc.ui.components.ContactModal
import com.solodev.mmwcalc.ui.components.CopyrightModal
import com.solodev.mmwcalc.ui.components.DonateModal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onTopicClick: (String) -> Unit) {
    val groupedTopics = TopicRegistry.ALL.groupBy { it.category }

    var showCopyright by remember { mutableStateOf(false) }
    var showDonate    by remember { mutableStateOf(false) }
    var showContact   by remember { mutableStateOf(false) }

    // Modals
    if (showCopyright) CopyrightModal(onDismiss = { showCopyright = false })
    if (showDonate)    DonateModal(onDismiss    = { showDonate    = false })
    if (showContact)   ContactModal(onDismiss   = { showContact   = false })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "MMWCalc",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Mathematics in the Modern World",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                actions = {
                    // Copyright
                    IconButton(onClick = { showCopyright = true }) {
                        Icon(Icons.Default.Copyright,
                            contentDescription = "Copyright",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                    // Donate
                    IconButton(onClick = { showDonate = true }) {
                        Icon(Icons.Default.AttachMoney,
                            contentDescription = "Support Developer",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                    // Contact
                    IconButton(onClick = { showContact = true }) {
                        Icon(
                            Icons.Default.ContactMail,
                            contentDescription = "Contact",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TopicCategory.entries.forEach { category ->
                val topics = groupedTopics[category] ?: return@forEach
                item { CategoryHeader(category) }
                items(topics) { topic ->
                    TopicCard(topic = topic, onClick = { onTopicClick(topic.id) })
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun CategoryHeader(category: TopicCategory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(4.dp, 18.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(categoryColor(category))
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = category.label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = categoryColor(category)
        )
    }
}

@Composable
private fun TopicCard(topic: Topic, onClick: () -> Unit) {
    val accent = categoryColor(topic.category)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = topic.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = topic.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            if (topic.hasDatasetInput) TopicBadge("Dataset", accent)
            else if (topic.formulaPicker) TopicBadge("Multi", accent)
        }
    }
}

@Composable
private fun TopicBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

fun categoryColor(category: TopicCategory): Color = when (category) {
    TopicCategory.SEQUENCES     -> CategorySequences
    TopicCategory.STATISTICS    -> CategoryStatistics
    TopicCategory.NUMBER_THEORY -> CategoryNumberTh
    TopicCategory.FINANCE       -> CategoryFinance
}