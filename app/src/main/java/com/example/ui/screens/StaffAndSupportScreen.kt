package com.example.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClinicEntity
import com.example.data.model.StaffTaskEntity
import com.example.data.model.SupportTicketEntity
import com.example.ui.PortalType
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun StaffAndSupportScreen(
    currentPortal: PortalType,
    selectedClinicId: String,
    clinics: List<ClinicEntity>,
    staffTasks: List<StaffTaskEntity>,
    supportTickets: List<SupportTicketEntity>,
    onAddStaffTask: (String, String, String, String, String, String) -> Unit,
    onUpdateStaffTaskStatus: (StaffTaskEntity, String) -> Unit,
    onAddSupportTicket: (String, String, String, String, String, String) -> Unit,
    onUpdateTicketStatus: (SupportTicketEntity, String, String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(if (currentPortal == PortalType.SUPER_ADMIN) 0 else 1) } // 0 = Staff Tasks, 1 = Support Tickets
    var showCreateTaskDialog by remember { mutableStateOf(false) }
    var showCreateTicketDialog by remember { mutableStateOf(false) }
    var ticketToResolve by remember { mutableStateOf<SupportTicketEntity?>(null) }

    // Enforce tenant isolation for tickets when in Clinic Client portal
    val scopedTickets = if (currentPortal == PortalType.CLINIC_CLIENT) {
        supportTickets.filter { it.clinicId == selectedClinicId }
    } else {
        supportTickets
    }

    val activeClinic = clinics.find { it.id == selectedClinicId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SectionHeader(
            title = if (selectedTab == 0) "Internal Operations & Staff Tasks" else "Healthcare Support Helpdesk",
            subtitle = if (selectedTab == 0) "${staffTasks.size} assigned tasks across team" else "${scopedTickets.size} customer support tickets",
            actionButtonText = if (selectedTab == 0) "Assign Task" else "Raise Ticket",
            onActionClick = {
                if (selectedTab == 0) showCreateTaskDialog = true else showCreateTicketDialog = true
            }
        )

        if (currentPortal == PortalType.SUPER_ADMIN) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = ClinispirePrimaryBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Staff Tasks (${staffTasks.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Support Tickets (${scopedTickets.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (selectedTab == 0 && currentPortal == PortalType.SUPER_ADMIN) {
            // Staff Tasks List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(staffTasks) { task ->
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
                                    text = task.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                StatusBadge(text = task.status)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = task.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "👤 ${task.assignedToName} (${task.assignedToRole})",
                                    fontSize = 11.sp,
                                    color = ClinispirePrimaryBlue
                                )
                                Text(
                                    text = "Priority: ${task.priority} • Due: ${task.dueDate}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (task.status != "Completed") {
                                    Button(
                                        onClick = { onUpdateStaffTaskStatus(task, "Completed") },
                                        colors = ButtonDefaults.buttonColors(containerColor = ClinispireSuccess),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Mark Done", fontSize = 10.sp)
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { onUpdateStaffTaskStatus(task, "Pending") },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Reopen", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Support Tickets List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(scopedTickets) { ticket ->
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
                                    text = "${ticket.ticketNumber} • ${ticket.subject}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                StatusBadge(text = ticket.status)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Clinic: ${ticket.clinicName} • Category: ${ticket.category} • Priority: ${ticket.priority}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = ticket.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (ticket.resolutionNotes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Resolution: ${ticket.resolutionNotes}",
                                    fontSize = 11.sp,
                                    color = ClinispireSuccess,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            if (ticket.status != "Resolved" && ticket.status != "Closed") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = { ticketToResolve = ticket },
                                        colors = ButtonDefaults.buttonColors(containerColor = ClinispirePrimaryBlue),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Update & Resolve", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Assign Task Dialog
    if (showCreateTaskDialog) {
        var title by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var role by remember { mutableStateOf("Business Manager") }
        var staffName by remember { mutableStateOf("Pooja Hegde") }
        var dueDate by remember { mutableStateOf("2026-11-12") }
        var priority by remember { mutableStateOf("High") }

        AlertDialog(
            onDismissRequest = { showCreateTaskDialog = false },
            title = { Text("Assign Staff Task") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Title *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Description & Instructions") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                    OutlinedTextField(
                        value = staffName,
                        onValueChange = { staffName = it },
                        label = { Text("Assignee Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = role,
                            onValueChange = { role = it },
                            label = { Text("Role") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = priority,
                            onValueChange = { priority = it },
                            label = { Text("Priority") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Due Date (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && staffName.isNotBlank()) {
                            onAddStaffTask(title, desc, role, staffName, dueDate, priority)
                            showCreateTaskDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinispirePrimaryBlue)
                ) {
                    Text("Assign Task")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTaskDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Raise Support Ticket Dialog
    if (showCreateTicketDialog) {
        var subject by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var priority by remember { mutableStateOf("Medium") }
        var category by remember { mutableStateOf("WhatsApp Integration") }

        AlertDialog(
            onDismissRequest = { showCreateTicketDialog = false },
            title = { Text("Raise Support Ticket") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Clinic: ${activeClinic?.name ?: "Clinispire Client"}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject / Issue Summary *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category (Billing, WhatsApp, Calendar, Bug)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = priority,
                        onValueChange = { priority = it },
                        label = { Text("Priority (Low, Medium, High, Urgent)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Detailed Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (subject.isNotBlank()) {
                            onAddSupportTicket(
                                selectedClinicId,
                                activeClinic?.name ?: "Clinic",
                                subject,
                                desc,
                                priority,
                                category
                            )
                            showCreateTicketDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinispirePrimaryBlue)
                ) {
                    Text("Submit Ticket")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTicketDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Resolve Ticket Dialog
    if (ticketToResolve != null) {
        val tkt = ticketToResolve!!
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { ticketToResolve = null },
            title = { Text("Resolve Ticket: ${tkt.ticketNumber}") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Subject: ${tkt.subject}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Resolution Notes & Action Taken *") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateTicketStatus(tkt, "Resolved", notes.ifEmpty { "Issue verified and resolved by support engineer." })
                        ticketToResolve = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinispireSuccess)
                ) {
                    Text("Mark Resolved")
                }
            },
            dismissButton = {
                TextButton(onClick = { ticketToResolve = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
