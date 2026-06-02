package com.solodev.mmwcalc.ui.screens.learn

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.solodev.mmwcalc.data.content.TopicContentRegistry
import com.solodev.mmwcalc.domain.models.TopicCategory
import com.solodev.mmwcalc.domain.models.TopicRegistry
import com.solodev.mmwcalc.ui.screens.home.categoryColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.automirrored.filled.ArrowForward

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    onTopicClick: (String) -> Unit
) {
    var searchQuery      by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<TopicCategory?>(null) }

    // Only show topics that have learn content
    val availableTopics = TopicRegistry.ALL.filter { topic ->
        TopicContentRegistry.findById(topic.id) != null
    }

    val filteredTopics = availableTopics.filter { topic ->
        val matchesSearch = searchQuery.isBlank() ||
                topic.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null ||
                topic.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Learn",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold)
                        Text("${availableTopics.size} topics",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search bar
            item {
                OutlinedTextField(
                    value         = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier      = Modifier.fillMaxWidth(),
                    placeholder   = { Text("Search topics...") },
                    leadingIcon   = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    singleLine = true,
                    shape      = RoundedCornerShape(12.dp)
                )
            }

            // Category filter chips
            item {
                val categories = listOf(null) + TopicCategory.entries
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick  = { selectedCategory = cat },
                            label    = {
                                Text(cat?.label ?: "All",
                                    style = MaterialTheme.typography.labelMedium)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor =
                                    if (cat != null) categoryColor(cat).copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor =
                                    if (cat != null) categoryColor(cat)
                                    else MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            // Topic cards
            items(filteredTopics) { topic ->
                val content     = TopicContentRegistry.findById(topic.id) ?: return@items
                val accentColor = categoryColor(topic.category)

                Card(
                    onClick   = { onTopicClick(topic.id) },
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp, 56.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(accentColor)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(topic.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold)
                            Text(topic.category.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor)
                            Spacer(Modifier.height(4.dp))
                            Text(content.whatIsIt.take(80) + "...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = accentColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}