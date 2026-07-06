package com.solodev.mmwcalc.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// ─────────────────────────────────────────────────────────────────────────────
// COPYRIGHT MODAL
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CopyrightModal(onDismiss: () -> Unit) {
    AppModal(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModalHeader(title = "© Copyright", onDismiss = onDismiss)

            Text("©", style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary)

            Text("MMWCalc",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center)

            Text("Copyright © 2026 Reyny Mark Mabeza\nsoloDev",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            NoticeSection(
                title = "All Rights Reserved",
                body  = "This application, including all its source code, design, " +
                        "formulas, step-by-step logic, educational content, and visual " +
                        "assets, is the exclusive intellectual property of Reyny Mark " +
                        "Mabeza (soloDev)."
            )

            NoticeSection(
                title = "Prohibited Activities",
                body  = "You may NOT copy, reproduce, redistribute, modify, reverse " +
                        "engineer, sell, or claim ownership of any part of this " +
                        "application without explicit written permission from the developer."
            )

            NoticeSection(
                title = "Educational Use",
                body  = "MMWCalc is developed for educational purposes to assist " +
                        "first-year college students or freshmen in their " +
                        "Mathematics in the Modern World (GEC 3) course. " +
                        "It is provided free of charge and must remain so."
            )

            NoticeSection(
                title = "Disclaimer",
                body  = "While every effort has been made to ensure accuracy, " +
                        "the developer is not liable for any errors in calculations " +
                        "or academic consequences arising from the use of this app. " +
                        "Always verify results with your professor."
            )

            NoticeSection(
                title = "Reporting Violations",
                body  = "If you believe your intellectual property rights have been " +
                        "violated, or if you encounter an unauthorized copy of this app, " +
                        "please contact the developer immediately at callixasta@gmail.com"
            )

            Spacer(Modifier.height(8.dp))

            Text("Version 1.0.0 · Built with ❤️ for all students",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                textAlign = TextAlign.Center)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DONATE / SUPPORT MODAL
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DonateModal(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var copiedText by remember { mutableStateOf("") }

    AppModal(onDismiss = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModalHeader(title = "Support the Developer", onDismiss = onDismiss)

            Text("☕", style = MaterialTheme.typography.displaySmall)

            Text("Buy me a coffee?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center)

            Text("MMWCalc is free and always will be.\n" +
                    "If it helped you pass your MMW subject,\n" +
                    "consider supporting its development! 😊",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // GCash
            PaymentCard(
                platform    = "GCash",
                emoji       = "💙",
                number      = "09970726073",
                name        = "Reyny Mark Mabeza",
                color       = Color(0xFF0066CC),
                copiedText  = copiedText,
                onCopy      = {
                    copyToClipboard(context, "GCash", "09970726073")
                    copiedText = "GCash"
                }
            )

            // Maya
            PaymentCard(
                platform    = "Maya",
                emoji       = "💚",
                number      = "09926617956",
                name        = "Reyny Mark Mabeza",
                color       = Color(0xFF00A859),
                copiedText  = copiedText,
                onCopy      = {
                    copyToClipboard(context, "Maya", "09926617956")
                    copiedText = "Maya"
                }
            )

            Spacer(Modifier.height(4.dp))

            Text("I will soon release an ai powered web app version. Every peso helps keep this app updated and free! 🙏",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CONTACT MODAL
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ContactModal(onDismiss: () -> Unit) {
    val context = LocalContext.current

    AppModal(onDismiss = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModalHeader(title = "Contact Developer", onDismiss = onDismiss)

            Text("👨‍💻", style = MaterialTheme.typography.displaySmall)

            Text("Reyny Mark Mabeza",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center)

            Text("soloDev · MMWCalc",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text("For bug reports, inquiries, or custom app development:",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

            // Email
            ContactCard(
                icon    = "📧",
                label   = "Email",
                value   = "com.solodev@gmail.com",
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:com.solodev@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "MMWCalc — Inquiry")
                    }
                    context.startActivity(Intent.createChooser(intent, "Send Email"))
                }
            )

            // Facebook
            ContactCard(
                icon    = "👥",
                label   = "Facebook",
                value   = "Reyny Mark",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://www.facebook.com/search/top?q=Reyny%20Mark")
                    }
                    context.startActivity(intent)
                }
            )

            // Phone
            ContactCard(
                icon    = "📱",
                label   = "Phone / SMS",
                value   = "09926617956",
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:09926617956")
                    }
                    context.startActivity(intent)
                }
            )

            Spacer(Modifier.height(4.dp))

            Text("Response time: within 24–48 hours",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                textAlign = TextAlign.Center)

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                        .copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "💡 For custom app development inquiries,\n" +
                            "please include your project details and budget.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AppModal(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape  = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun ModalHeader(title: String, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold)
        IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
        }
    }
}

@Composable
private fun NoticeSection(title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary)
        Text(body,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
    }
}

@Composable
private fun PaymentCard(
    platform: String,
    emoji: String,
    number: String,
    name: String,
    color: Color,
    copiedText: String,
    onCopy: () -> Unit
) {
    val isCopied = copiedText == platform
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(emoji, style = MaterialTheme.typography.headlineSmall)
                Column {
                    Text(platform,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = color)
                    Text(number,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold)
                    Text(name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
            // Copy button
            FilledTonalButton(
                onClick = onCopy,
                colors  = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (isCopied) color.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (isCopied) "Copied!" else "Copy",
                    style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun ContactCard(
    icon: String,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(icon, style = MaterialTheme.typography.headlineSmall)
            Column(modifier = Modifier.weight(1f)) {
                Text(label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Text(value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium)
            }
            Text("Tap →",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
}