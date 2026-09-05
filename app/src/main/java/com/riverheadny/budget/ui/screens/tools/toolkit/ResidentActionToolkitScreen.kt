package com.riverheadny.budget.ui.screens.tools.toolkit

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.data.models.ResidentActionToolkit
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.MutedText

private fun copy(context: Context, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
    cm.setPrimaryClip(ClipData.newPlainText("Riverhead budget question", text))
    Toast.makeText(context, "Question copied", Toast.LENGTH_SHORT).show()
}

@Composable
fun ResidentActionToolkitScreen() {
    val context = LocalContext.current

    PageColumn {
        HeroCard(eyebrow = "Tools", title = "Resident Action Toolkit", body = ResidentActionToolkit.intro)

        SectionTitle("Question templates")
        Text(
            "Tap to copy. Each one names something specific enough that a general answer will not satisfy it.",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
        )
        ResidentActionToolkit.templates.forEach { t ->
            ElevatedCard(
                onClick = { copy(context, t) },
                colors = CardDefaults.elevatedCardColors(),
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(t, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    Text("Copy", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = BrandNavy)
                }
            }
        }

        SectionTitle("How to use one")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ResidentActionToolkit.firstPass.forEachIndexed { i, s ->
                    Text("${i + 1}. $s", style = MaterialTheme.typography.bodySmall, color = MutedText)
                }
                Text(
                    "Saved meeting notes are an iOS-only feature by design, so questions here copy to the clipboard rather than into a note.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
        }
    }
}
