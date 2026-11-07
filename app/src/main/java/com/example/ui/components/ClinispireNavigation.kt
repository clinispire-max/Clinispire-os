package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NavigationTab
import com.example.ui.PortalType
import com.example.ui.theme.*

@Composable
fun ClinispireTabRow(
    currentPortal: PortalType,
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val tabs = if (currentPortal == PortalType.SUPER_ADMIN) {
        listOf(
            NavigationTab.OVERVIEW,
            NavigationTab.LEADS_CRM,
            NavigationTab.CLINICS,
            NavigationTab.APPOINTMENTS,
            NavigationTab.AUTOMATIONS,
            NavigationTab.WHATSAPP,
            NavigationTab.BILLING,
            NavigationTab.STAFF,
            NavigationTab.SUPPORT,
            NavigationTab.INTEGRATIONS,
            NavigationTab.REPORTS,
            NavigationTab.AUDIT_LOGS,
            NavigationTab.BRAND_SETTINGS
        )
    } else {
        listOf(
            NavigationTab.OVERVIEW,
            NavigationTab.APPOINTMENTS,
            NavigationTab.FOLLOW_UPS,
            NavigationTab.REVIEWS,
            NavigationTab.BILLING,
            NavigationTab.SUPPORT,
            NavigationTab.AUTOMATIONS,
            NavigationTab.WHATSAPP,
            NavigationTab.REPORTS
        )
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == selectedTab
                FilterChip(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    label = {
                        Text(
                            text = if (tab == NavigationTab.OVERVIEW && currentPortal == PortalType.CLINIC_CLIENT) "Clinic Hub" else tab.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    leadingIcon = {
                        val icon = when (tab) {
                            NavigationTab.OVERVIEW -> Icons.Default.Dashboard
                            NavigationTab.LEADS_CRM -> Icons.Default.People
                            NavigationTab.CLINICS -> Icons.Default.Business
                            NavigationTab.APPOINTMENTS -> Icons.Default.CalendarMonth
                            NavigationTab.FOLLOW_UPS -> Icons.Default.AssignmentLate
                            NavigationTab.REVIEWS -> Icons.Default.Star
                            NavigationTab.AUTOMATIONS -> Icons.Default.AutoMode
                            NavigationTab.WHATSAPP -> Icons.Default.Chat
                            NavigationTab.BILLING -> Icons.Default.ReceiptLong
                            NavigationTab.STAFF -> Icons.Default.Badge
                            NavigationTab.SUPPORT -> Icons.Default.SupportAgent
                            NavigationTab.INTEGRATIONS -> Icons.Default.Extension
                            NavigationTab.REPORTS -> Icons.Default.BarChart
                            NavigationTab.AUDIT_LOGS -> Icons.Default.Security
                            NavigationTab.BRAND_SETTINGS -> Icons.Default.Palette
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isSelected) ClinispirePrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ClinispirePrimaryBlue.copy(alpha = 0.12f),
                        selectedLabelColor = ClinispirePrimaryBlue,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) ClinispirePrimaryBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        selectedBorderColor = ClinispirePrimaryBlue
                    )
                )
            }
        }
    }
}
