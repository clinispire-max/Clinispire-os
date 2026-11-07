package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLogEntity
import com.example.data.model.IntegrationEntity
import com.example.data.model.LeadEntity
import com.example.data.model.SubscriptionEntity
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun IntegrationsAndReportsScreen(
    integrations: List<IntegrationEntity>,
    auditLogs: List<AuditLogEntity>,
    leads: List<LeadEntity>,
    subscriptions: List<SubscriptionEntity>,
    onTestIntegration: (IntegrationEntity) -> Unit,
    onToggleIntegration: (IntegrationEntity) -> Unit,
    onExportCsv: () -> Unit,
    initialTab: Int = 0 // 0 = Integrations, 1 = Reports, 2 = Audit Logs
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SectionHeader(
            title = when (selectedTab) {
                0 -> "Third-Party Integrations"
                1 -> "Business Analytics & Reports"
                else -> "Immutable Compliance Audit Trail"
            },
            subtitle = when (selectedTab) {
                0 -> "${integrations.count { it.status == "Connected and Verified" }} of ${integrations.size} services connected"
                1 -> "Conversion, MRR and Operational Funnels"
                else -> "${auditLogs.size} recorded audit events"
            },
            actionButtonText = if (selectedTab == 1) "Export CSV" else null,
            onActionClick = if (selectedTab == 1) onExportCsv else null
        )

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = ClinispirePrimaryBlue
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Integrations", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Reports", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Audit Logs", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedTab) {
            0 -> {
                // Integrations List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(integrations) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.serviceName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatusBadge(text = item.status)
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        .padding(8.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text("Endpoint: ${item.endpointUrl}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Last Verification: ${item.lastVerified}", fontSize = 10.sp, color = ClinispirePrimaryBlue)
                                        if (item.notes.isNotEmpty()) {
                                            Text("Notes: ${item.notes}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = { onToggleIntegration(item) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(if (item.status == "Disabled") "Enable" else "Disable", fontSize = 10.sp)
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Button(
                                        onClick = { onTestIntegration(item) },
                                        colors = ButtonDefaults.buttonColors(containerColor = ClinispirePrimaryBlue),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Test Ping", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Reports & Analytics
                val totalLeads = leads.size
                val convertedLeads = leads.count { it.status == "Converted" }
                val conversionRate = if (totalLeads > 0) (convertedLeads.toDouble() / totalLeads * 100).toInt() else 0

                val starterCount = subscriptions.count { it.planName == "Starter" && it.status == "Active" }
                val growthCount = subscriptions.count { it.planName == "Growth" && it.status == "Active" }
                val proCount = subscriptions.count { it.planName == "Pro" && it.status == "Active" }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Sales Pipeline Funnel", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Lead Conversion Rate:", fontSize = 12.sp)
                                    Text("$conversionRate%", fontWeight = FontWeight.Bold, color = ClinispireSuccess, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { conversionRate / 100f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = ClinispireSuccess
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "$convertedLeads of $totalLeads total leads successfully converted to paid subscriptions",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Active Subscriptions Distribution", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Pro Plan (₹4,999/mo):", fontSize = 12.sp)
                                    Text("$proCount clinics (₹${proCount * 4999}/mo)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Growth Plan (₹2,999/mo):", fontSize = 12.sp)
                                    Text("$growthCount clinics (₹${growthCount * 2999}/mo)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Starter Plan (₹1,499/mo):", fontSize = 12.sp)
                                    Text("$starterCount clinics (₹${starterCount * 1499}/mo)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Lead Channels Breakdown", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                listOf("Instagram", "Website", "Referral", "Cold Outreach", "Google").forEach { src ->
                                    val count = leads.count { it.leadSource == src }
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(src, fontSize = 12.sp)
                                        Text("$count leads", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = ClinispirePrimaryBlue)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Audit Logs List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(auditLogs) { log ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = log.action,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "${log.userName} • Org: ${log.organizationName} • ${log.recordType}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                StatusBadge(text = log.outcome)
                            }
                        }
                    }
                }
            }
        }
    }
}
