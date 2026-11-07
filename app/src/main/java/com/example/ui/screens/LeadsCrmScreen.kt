package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.LeadEntity
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadsCrmScreen(
    leads: List<LeadEntity>,
    onAddLead: (String, String, String, String, String, String, String, String, Double, String, String, String) -> Unit,
    onUpdateLeadStatus: (LeadEntity, String) -> Unit,
    onConvertLeadToClinic: (LeadEntity, String) -> Unit
) {
    var selectedPipelineStage by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddLeadDialog by remember { mutableStateOf(false) }
    var selectedLeadForDetail by remember { mutableStateOf<LeadEntity?>(null) }
    var leadToConvert by remember { mutableStateOf<LeadEntity?>(null) }

    val pipelineStages = listOf(
        "All", "New", "Contacted", "Interested", "Demo Scheduled",
        "Demo Completed", "Proposal Sent", "Negotiation", "Converted", "Lost"
    )

    val filteredLeads = leads.filter { lead ->
        (selectedPipelineStage == "All" || lead.status == selectedPipelineStage) &&
        (searchQuery.isEmpty() ||
         lead.clinicName.contains(searchQuery, ignoreCase = true) ||
         lead.contactPerson.contains(searchQuery, ignoreCase = true) ||
         lead.location.contains(searchQuery, ignoreCase = true) ||
         lead.clinicType.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Section Header with Add Lead button
        SectionHeader(
            title = "Clinispire Sales CRM",
            subtitle = "${leads.size} total clinic leads in pipeline",
            actionButtonText = "New Lead",
            onActionClick = { showAddLeadDialog = true }
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by clinic name, doctor, location, specialty...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        // Pipeline Stage Horizontal Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            pipelineStages.forEach { stage ->
                val isSelected = stage == selectedPipelineStage
                val count = if (stage == "All") leads.size else leads.count { it.status == stage }
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedPipelineStage = stage },
                    label = { Text("$stage ($count)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ClinispirePrimaryBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Leads List
        if (filteredLeads.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No leads found matching '$searchQuery' in stage '$selectedPipelineStage'",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredLeads) { lead ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedLeadForDetail = lead },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = lead.clinicName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${lead.contactPerson} • ${lead.clinicType} • ${lead.location}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                StatusBadge(text = lead.status)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = ClinispireSuccess, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Est. Value: ₹${lead.estimatedMonthlyValue.toInt()}/mo",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Event, contentDescription = null, tint = ClinispirePrimaryBlue, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Next: ${lead.nextFollowUpDate}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (lead.notes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Notes: ${lead.notes}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (lead.status != "Converted" && lead.status != "Lost") {
                                    Button(
                                        onClick = { leadToConvert = lead },
                                        colors = ButtonDefaults.buttonColors(containerColor = ClinispireTeal),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp), tint = ClinispireDeepNavy)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Convert to Clinic", fontSize = 11.sp, color = ClinispireDeepNavy, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Lead Dialog
    if (showAddLeadDialog) {
        var clinicName by remember { mutableStateOf("") }
        var contactPerson by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("+91 ") }
        var email by remember { mutableStateOf("") }
        var location by remember { mutableStateOf("Bengaluru") }
        var clinicType by remember { mutableStateOf("Dental") }
        var leadSource by remember { mutableStateOf("Instagram") }
        var requirements by remember { mutableStateOf("WhatsApp appointment reminders & reviews") }
        var estValue by remember { mutableStateOf("2999") }
        var assignedStaff by remember { mutableStateOf("Vikram (Sales Exec)") }
        var nextDate by remember { mutableStateOf("2026-11-12") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddLeadDialog = false },
            title = { Text("New Clinic Lead") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = clinicName,
                        onValueChange = { clinicName = it },
                        label = { Text("Clinic Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = contactPerson,
                        onValueChange = { contactPerson = it },
                        label = { Text("Doctor / Contact Person *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone *") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = clinicType,
                            onValueChange = { clinicType = it },
                            label = { Text("Specialty") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location (Area, City)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = estValue,
                            onValueChange = { estValue = it },
                            label = { Text("Est Value (₹/mo)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = leadSource,
                            onValueChange = { leadSource = it },
                            label = { Text("Source") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes & Requirements") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (clinicName.isNotBlank() && contactPerson.isNotBlank()) {
                            onAddLead(
                                clinicName,
                                contactPerson,
                                phone,
                                email.ifEmpty { "contact@${clinicName.lowercase().replace(" ", "")}.in" },
                                location,
                                clinicType,
                                leadSource,
                                requirements,
                                estValue.toDoubleOrNull() ?: 2999.0,
                                assignedStaff,
                                nextDate,
                                notes
                            )
                            showAddLeadDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinispirePrimaryBlue)
                ) {
                    Text("Create Lead")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLeadDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Lead Detail & Stage Advance Dialog
    if (selectedLeadForDetail != null) {
        val lead = selectedLeadForDetail!!
        AlertDialog(
            onDismissRequest = { selectedLeadForDetail = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(lead.clinicName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    StatusBadge(text = lead.status)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Doctor: ${lead.contactPerson} (${lead.phone})", fontSize = 13.sp)
                    Text("Location: ${lead.location} • Source: ${lead.leadSource}", fontSize = 13.sp)
                    Text("Requirements: ${lead.requirements}", fontSize = 12.sp)
                    Text("Assigned to: ${lead.assignedStaff}", fontSize = 12.sp)

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    Text("Move Pipeline Stage:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Contacted", "Interested", "Demo Scheduled", "Demo Completed", "Proposal Sent", "Negotiation", "Lost").forEach { st ->
                            OutlinedButton(
                                onClick = {
                                    onUpdateLeadStatus(lead, st)
                                    selectedLeadForDetail = null
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(st, fontSize = 10.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        leadToConvert = lead
                        selectedLeadForDetail = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinispireTeal)
                ) {
                    Text("Convert to Customer", color = ClinispireDeepNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedLeadForDetail = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Guided Convert Lead to Clinic Wizard
    if (leadToConvert != null) {
        val lead = leadToConvert!!
        var selectedPlan by remember { mutableStateOf("Growth") }

        AlertDialog(
            onDismissRequest = { leadToConvert = null },
            title = { Text("Onboard Clinic: ${lead.clinicName}") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Converting this lead will automatically:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text("1. Create Organization & Tenant Profile", fontSize = 11.sp)
                    Text("2. Provision Active Monthly Subscription", fontSize = 11.sp)
                    Text("3. Enable Core WhatsApp & Appointment Modules", fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Select Subscription Tier:", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    listOf(
                        Triple("Starter", "₹1,499/mo", "Core Scheduling & WhatsApp"),
                        Triple("Growth", "₹2,999/mo", "Multi-Doctor, Reviews & Automations"),
                        Triple("Pro", "₹4,999/mo", "Full Automation Suite & Invoicing")
                    ).forEach { (plan, price, desc) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPlan = plan },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedPlan == plan) ClinispirePrimaryBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(plan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(desc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(price, fontWeight = FontWeight.Bold, color = ClinispirePrimaryBlue, fontSize = 13.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConvertLeadToClinic(lead, selectedPlan)
                        leadToConvert = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinispirePrimaryBlue)
                ) {
                    Text("Complete Onboarding")
                }
            },
            dismissButton = {
                TextButton(onClick = { leadToConvert = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
