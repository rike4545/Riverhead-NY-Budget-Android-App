package com.riverheadny.budget.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandGold
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.BrandSky
import com.riverheadny.budget.ui.theme.BrandTeal
import com.riverheadny.budget.ui.theme.CardSurface
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

data class ToolLink(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val url: String? = null,
)

@Composable
fun PageColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
fun HeroCard(eyebrow: String, title: String, body: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BrandNavy),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(BrandNavy, BrandSky, BrandTeal, BrandGold)))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(eyebrow, color = Color.White.copy(alpha = 0.84f), style = MaterialTheme.typography.labelLarge)
            Text(title, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(body, color = Color.White.copy(alpha = 0.92f), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandNavy)
}

@Composable
fun ToolCard(link: ToolLink) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(link.icon, contentDescription = null, tint = BrandBlue)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(link.title, fontWeight = FontWeight.SemiBold)
                Text(link.subtitle, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun LinkCard(link: ToolLink) {
    val context = LocalContext.current
    ElevatedCard(
        onClick = {
            link.url?.let { context.startActivity(Intent(Intent.ACTION_VIEW, it.toUri())) }
        },
        colors = CardDefaults.elevatedCardColors(containerColor = CardSurface),
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(link.icon, contentDescription = null, tint = BrandBlue)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(link.title, fontWeight = FontWeight.SemiBold)
                Text(link.subtitle, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Filled.Link, contentDescription = null, tint = BrandGold)
        }
    }
}

@Composable
fun MetricRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.DarkGray)
        Text(value, fontWeight = FontWeight.SemiBold, color = BrandNavy)
    }
}

@Composable
fun DisclaimerCard(text: String = "Not an official Town app. Always verify with the Town website, adopted budget, and official records.") {
    Card(colors = CardDefaults.cardColors(containerColor = BrandCoral.copy(alpha = 0.10f))) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Filled.Info, contentDescription = null, tint = BrandCoral)
            Spacer(Modifier.width(10.dp))
            Text(text, color = Color(0xFF5F2B23))
        }
    }
}

fun currency(value: Double): String =
    NumberFormat.getCurrencyInstance(Locale.US).format(value.roundToInt())

fun currencyPrecise(value: Double): String =
    NumberFormat.getCurrencyInstance(Locale.US).format(value)
