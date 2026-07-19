package com.riverheadny.budget.ui.screens.civic

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

/** Shared building blocks for the static civic-accountability content screens (Procurement Watch, Campaign Ethics). */

@Composable
fun InfoSectionCard(title: String? = null, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            title?.let { Text(it, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandNavy) }
            content()
        }
    }
}

@Composable
fun BulletItem(text: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(modifier = Modifier.padding(top = 7.dp)) {
            Column(modifier = Modifier.size(6.dp).background(MutedText, CircleShape)) {}
        }
        Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }
}

@Composable
fun HighlightBox(title: String, message: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrandNavy.copy(alpha = 0.06f)),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(message, color = Color.DarkGray, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun FactRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = MutedText, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ComparisonRow(title: String, detail: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(detail, color = Color.DarkGray, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ClaimVsCodeRow(leftTitle: String, leftDetail: String, rightTitle: String, rightDetail: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(leftTitle.uppercase(), color = MutedText, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Text(leftDetail, style = MaterialTheme.typography.bodySmall)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(rightTitle.uppercase(), color = MutedText, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Text(rightDetail, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun SourceLinkRow(label: String, url: String) {
    val context = LocalContext.current
    Text(
        label,
        color = BrandBlue,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clickable(role = Role.Button) { context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }
            .wrapContentHeight(Alignment.CenterVertically)
            .padding(vertical = 6.dp),
    )
}
