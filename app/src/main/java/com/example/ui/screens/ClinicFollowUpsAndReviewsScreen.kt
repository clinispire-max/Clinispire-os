package com.example.ui.screens

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
import com.example.data.model.FollowUpEntity
import com.example.data.model.ReviewRequestEntity
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun ClinicFollowUpsAndReviewsScreen(
    selectedClinicId: String,
    followUps: List<FollowUpEntity>,
    reviewRequests: List<ReviewRequestEntity>,
    isReviewsView: Boolean,
    onAddFollowUp: (String, String, String, String, String, String) -> Unit,
    onUpdateFollowUpStatus: (FollowUpEntity, String) -> Unit,
    onSendReviewRequest: (String, String, String) -> Unit
) {
    val scopedFollowUps = followUps.filter { it.clinicId == selectedClinicId }
    val scopedReviews = reviewRequests.filter { it.clinicId == selectedClinicId }

    var showAddFollowUpDialog by remember { mutableStateOf(false) }
    var showSendReviewDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (!isReviewsView) {
            // FOLLOW-UPS VIEW
            SectionHeader(
                title = "Patient Follow-Up Care",
                subtitle = "${scopedFollowUps.count { it.status == "Pending" }} pending recovery checks",
                actionButtonText = "New Follow-Up",
                onActionClick = { showAddFollowUpDialog = true }
            )

            if (scopedFollowUps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No follow-ups scheduled for this clinic.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(scopedFollowUps) { fup ->
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(fup.patientName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("📞 ${fup.patientPhone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    StatusBadge(text = fup.status)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Reason: ${fup.reason}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                if (fup.notes.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Notes: ${fup.notes}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Due Date: ${fup.dueDate}", fontSize = 11.sp, color = ClinispirePrimaryBlue)

                                    if (fup.status != "Completed") {
                                        Button(
                                            onClick = { onUpdateFollowUpStatus(fup, "Completed") },
                                            colors = ButtonDefaults.buttonColors(containerColor = ClinispireSuccess),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("Mark Completed", fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // REVIEWS VIEW
            SectionHeader(
                title = "Patient Reviews & Google Reputation",
                subtitle = "${scopedReviews.size} review requests sent",
                actionButtonText = "Send Review Invite",
                onActionClick = { showSendReviewDialog = true }
            )

            if (scopedReviews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No review requests found for this clinic.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(scopedReviews) { rev ->
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(rev.patientName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Channel: ${rev.channel} • ${rev.patientPhone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    StatusBadge(text = rev.status)
                                }

                                if (rev.rating > 0) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        repeat(rev.rating) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = ClinispireWarning, modifier = Modifier.size(16.dp))
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("${rev.rating} / 5 Stars", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }

                                if (rev.feedback.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "\"${rev.feedback}\"",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Follow Up Dialog
    if (showAddFollowUpDialog) {
        var patientName by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("+91 ") }
        var reason by remember { mutableStateOf("Post-treatment recovery review") }
        var dueDate by remember { mutableStateOf("2026-11-14") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddFollowUpDialog = false },
            title = { Text("Schedule Patient Follow-Up") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = patientName,
                        onValueChange = { patientName = it },
                        label = { Text("Patient Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Follow-Up Clinical Reason *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Due Date (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Doctor Instructions") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (patientName.isNotBlank() && phone.isNotBlank()) {
                            onAddFollowUp(selectedClinicId, patientName, phone, reason, dueDate, notes)
                            showAddFollowUpDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinispirePrimaryBlue)
                ) {
                    Text("Schedule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFollowUpDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Send Review Request Dialog
    if (showSendReviewDialog) {
        var patientName by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("+91 ") }

        AlertDialog(
            onDismissRequest = { showSendReviewDialog = false },
            title = { Text("Dispatch WhatsApp Review Request") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Sends a polite automated WhatsApp message asking the patient to rate their recent clinic visit with a 1-tap Google Review link.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = patientName,
                        onValueChange = { patientName = it },
                        label = { Text("Patient Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("WhatsApp Phone (+91...) *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (patientName.isNotBlank() && phone.isNotBlank()) {
                            onSendReviewRequest(selectedClinicId, patientName, phone)
                            showSendReviewDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinispireTeal)
                ) {
                    Text("Send Review Invite", color = ClinispireDeepNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSendReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
