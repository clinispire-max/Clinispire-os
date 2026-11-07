package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.model.*
import com.example.ui.DateFilter
import com.example.ui.NavigationTab
import com.example.ui.PortalType
import com.example.ui.components.MetricSummaryCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OverviewScreen(
    currentPortal: PortalType,
    selectedClinicId: String,
    dateFilter: DateFilter,
    onDateFilterChange: (DateFilter) -> Unit,
    clinics: List<ClinicEntity>,
    leads: List<LeadEntity>,
    appointments: List<AppointmentEntity>,
    followUps: List<FollowUpEntity>,
    reviewRequests: List<ReviewRequestEntity>,
    workflows: List<AutomationWorkflowEntity>,
    executions: List<AutomationExecutionEntity>,
    subscriptions: List<SubscriptionEntity>,
    invoices: List<InvoiceEntity>,
    tickets: List<SupportTicketEntity>,
    integrations: List<IntegrationEntity>,
    auditLogs: List<AuditLogEntity>,
    onNavigateToTab: (NavigationTab) -> Unit,
    onQuickAddLead: () -> Unit,
    onQuickBookAppointment: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    if (currentPortal == PortalType.SUPER_ADMIN) {
        // SUPER ADMIN COMMAND CENTRE
        val activeClinicsCount = clinics.count { it.status == "Active" }
        val trialClinicsCount = clinics.count { it.status == "Trial" }
        val totalPipelineValue = leads.filter { it.status != "Lost" }.sumOf { it.estimatedMonthlyValue }
        val mrr = subscriptions.filter { it.status == "Active" }.sumOf { it.monthlyPrice }
        val successfulExecutions = executions.count { it.status == "Success" || it.status == "Retried" }
        val openTicketsCount = tickets.count { it.status == "Open" || it.status == "In Progress" }
        val connectedIntegrations = integrations.count { it.status == "Connected and Verified" }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Date Filter Selector
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DateFilter.values().forEach { filter ->
                        FilterChip(
                            selected = filter == dateFilter,
                            onClick = { onDateFilterChange(filter) },
                            label = { Text(filter.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ClinispirePrimaryBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Key SaaS Performance Metrics Grid
            item {
                Text(
                    text = "Executive Command Metrics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricSummaryCard(
                            title = "Total Clinics",
                            value = "${clinics.size}",
                            subtitle = "$activeClinicsCount Active • $trialClinicsCount Trial",
                            icon = Icons.Default.Business,
                            iconColor = ClinispirePrimaryBlue,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToTab(NavigationTab.CLINICS) }
                        )
                        MetricSummaryCard(
                            title = "Monthly Revenue",
                            value = "₹${mrr.toInt()}",
                            subtitle = "${subscriptions.count { it.status == "Active" }} active subs",
                            icon = Icons.Default.MonetizationOn,
                            iconColor = ClinispireSuccess,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToTab(NavigationTab.BILLING) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricSummaryCard(
                            title = "Active Leads",
                            value = "${leads.count { it.status != "Lost" && it.status != "Converted" }}",
                            subtitle = "₹${totalPipelineValue.toInt()}/mo pipeline",
                            icon = Icons.Default.TrendingUp,
                            iconColor = ClinispireTeal,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToTab(NavigationTab.LEADS_CRM) }
                        )
                        MetricSummaryCard(
                            title = "Automations",
                            value = "${workflows.count { it.status == "Active" }} Active",
                            subtitle = "$successfulExecutions successful runs",
                            icon = Icons.Default.AutoMode,
                            iconColor = ClinispirePurple,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToTab(NavigationTab.AUTOMATIONS) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricSummaryCard(
                            title = "Integrations",
                            value = "$connectedIntegrations/${integrations.size}",
                            subtitle = "Healthy & Verified",
                            icon = Icons.Default.Extension,
                            iconColor = ClinispireTealDark,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToTab(NavigationTab.INTEGRATIONS) }
                        )
                        MetricSummaryCard(
                            title = "Support Desk",
                            value = "$openTicketsCount Open",
                            subtitle = "${tickets.size} total tickets",
                            icon = Icons.Default.ConfirmationNumber,
                            iconColor = if (openTicketsCount > 0) ClinispireWarning else ClinispireSuccess,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToTab(NavigationTab.SUPPORT) }
                        )
                    }
                }
            }

            // Quick Actions Bar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Admin Quick Actions",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onQuickAddLead,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ClinispirePrimaryBlue),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Lead", fontSize = 11.sp)
                            }
                            Button(
                                onClick = onQuickBookAppointment,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ClinispireTeal),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = ClinispireDeepNavy)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Schedule", fontSize = 11.sp, color = ClinispireDeepNavy, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { onNavigateToTab(NavigationTab.AUTOMATIONS) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Run Flow", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Recent Audit Activity Feed
            item {
                SectionHeader(
                    title = "System Audit Trail",
                    subtitle = "Real-time compliance and tenant action history",
                    actionButtonText = "View All",
                    onActionClick = { onNavigateToTab(NavigationTab.AUDIT_LOGS) }
                )
            }

            items(auditLogs.take(4)) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = log.action,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${log.userName} • ${log.organizationName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        StatusBadge(text = log.outcome)
                    }
                }
            }
        }
    } else {
        // CLINIC CLIENT WORKSPACE
        val currentClinic = clinics.find { it.id == selectedClinicId } ?: clinics.firstOrNull()
        val clinicApts = appointments.filter { it.clinicId == selectedClinicId }
        val clinicFollowUps = followUps.filter { it.clinicId == selectedClinicId }
        val clinicReviews = reviewRequests.filter { it.clinicId == selectedClinicId }
        val clinicInvoices = invoices.filter { it.clinicId == selectedClinicId }

        val completedApts = clinicApts.count { it.status == "Completed" }
        val pendingApts = clinicApts.count { it.status == "Scheduled" }
        val avgRating = if (clinicReviews.any { it.rating > 0 }) {
            String.format(Locale.US, "%.1f", clinicReviews.filter { it.rating > 0 }.map { it.rating }.average())
        } else "5.0"

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Clinic Profile Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ClinispireDeepNavy)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentClinic?.name ?: "Clinic Portal",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${currentClinic?.clinicType} • ${currentClinic?.location}",
                                    fontSize = 12.sp,
                                    color = ClinispireTealLight
                                )
                            }
                            StatusBadge(text = currentClinic?.status ?: "Active")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ClinispireDarkSurface)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Plan: ${currentClinic?.subscriptionPlan}",
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ClinispireDarkSurface)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Dr: ${currentClinic?.ownerName}",
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Clinic Operational Metrics
            item {
                Text(
                    text = "Clinic Operational Pulse",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricSummaryCard(
                        title = "Appointments",
                        value = "${clinicApts.size}",
                        subtitle = "$completedApts Done • $pendingApts Upcoming",
                        icon = Icons.Default.CalendarToday,
                        iconColor = ClinispirePrimaryBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTab(NavigationTab.APPOINTMENTS) }
                    )
                    MetricSummaryCard(
                        title = "Follow-Ups Due",
                        value = "${clinicFollowUps.count { it.status == "Pending" }}",
                        subtitle = "${clinicFollowUps.size} total tasks",
                        icon = Icons.Default.AssignmentLate,
                        iconColor = ClinispireWarning,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTab(NavigationTab.FOLLOW_UPS) }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricSummaryCard(
                        title = "Patient Rating",
                        value = "$avgRating ⭐",
                        subtitle = "${clinicReviews.size} review requests",
                        icon = Icons.Default.Star,
                        iconColor = ClinispireTeal,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTab(NavigationTab.REVIEWS) }
                    )
                    MetricSummaryCard(
                        title = "Billing Dues",
                        value = "₹${clinicInvoices.filter { it.status == "Unpaid" }.sumOf { it.totalAmount }.toInt()}",
                        subtitle = "${clinicInvoices.count { it.status == "Unpaid" }} pending invoices",
                        icon = Icons.Default.Receipt,
                        iconColor = ClinispireSuccess,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTab(NavigationTab.BILLING) }
                    )
                }
            }

            // Quick Clinic Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onQuickBookAppointment,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ClinispirePrimaryBlue)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Book Patient", fontSize = 12.sp)
                    }
                    Button(
                        onClick = { onNavigateToTab(NavigationTab.WHATSAPP) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ClinispireTeal)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = ClinispireDeepNavy)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", fontSize = 12.sp, color = ClinispireDeepNavy, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Today's Scheduled Appointments
            item {
                SectionHeader(
                    title = "Scheduled Patient Visits",
                    subtitle = "Showing patient schedule for this clinic",
                    actionButtonText = "View All",
                    onActionClick = { onNavigateToTab(NavigationTab.APPOINTMENTS) }
                )
            }

            if (clinicApts.isEmpty()) {
                item {
                    Text(
                        text = "No appointments scheduled for this clinic yet. Tap 'Book Patient' to schedule.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(clinicApts.take(3)) { apt ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = apt.patientName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${apt.appointmentTime} • ${apt.doctorName} (${apt.department})",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            StatusBadge(text = apt.status)
                        }
                    }
                }
            }
        }
    }
}
