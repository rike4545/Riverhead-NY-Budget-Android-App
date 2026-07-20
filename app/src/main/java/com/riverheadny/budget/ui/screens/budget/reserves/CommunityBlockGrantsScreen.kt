package com.riverheadny.budget.ui.screens.budget.reserves

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandGold
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.MutedText

// Illustrative one-time grant amounts, sized like Riverhead's own one-time-money surplus
// deployment plan on the other platforms — not an official Town commitment or budget line. The
// General Fund's unassigned reserve is well above policy target (see Fund Balance), which is
// the "Surplus funds" this list is framed against.
private data class CommunityGrant(val organization: String, val focus: String, val amount: Double)

private val communityBlockGrants = listOf(
    CommunityGrant("Legal Aid Society of Suffolk County", "Civil legal services for low-income Suffolk County residents", 15_000.0),
    CommunityGrant("Helping Hands of the East End", "Emergency assistance for East End families and individuals in crisis", 10_000.0),
    CommunityGrant("RISE", "Long Island community and social-services nonprofit", 10_000.0),
    CommunityGrant("Long Island Housing Partnership (LIHP)", "Regional affordable-housing development and homebuyer counseling", 15_000.0),
)

@Composable
fun CommunityBlockGrantsScreen() {
    val total = communityBlockGrants.sumOf { it.amount }

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "Community Block Grants",
            body = "A one-time round of grant applications to four nonprofits serving Riverhead and the East End, funded through the General Fund's surplus above its reserve policy target.",
        )

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Who would get funded", fontWeight = FontWeight.Bold, color = BrandNavy)
                Text(
                    "These amounts are this app's own illustrative sizing, not an official Town budget line or commitment.",
                    color = MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
                communityBlockGrants.forEachIndexed { index, grant ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(grant.organization, fontWeight = FontWeight.SemiBold)
                            Text(grant.focus, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
                        }
                        Text(currency(grant.amount), fontWeight = FontWeight.Bold, color = BrandGold)
                    }
                    if (index != communityBlockGrants.lastIndex) HorizontalDivider()
                }
                HorizontalDivider()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.Bold)
                    Text(currency(total), fontWeight = FontWeight.Bold, color = BrandNavy)
                }
            }
        }

        Text(
            "Reserve one-time grant applications to community-service nonprofits as targeted community-support investments " +
                "that do not create a recurring operating obligation — see Fund Balance for the reserve context this draws from.",
            color = MutedText,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
