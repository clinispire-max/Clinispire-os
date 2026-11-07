package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppointmentEntity
import com.example.data.model.ClinicEntity
import com.example.ui.PortalType
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun AppointmentsScreen(
    currentPortal: PortalType,
    selectedClinicId: String,
    clinics: List<ClinicEntity>,
    appointments: List<AppointmentEntity>,
    onAddAppointment: (String, String, String, String, String, String, String, String) -> Unit,
    onUpdateAppointmentStatus: (AppointmentEntity, String) -> Unit,
    onRescheduleAppointment: (AppointmentEntity, String, String) -> Unit,
    onRequestReminder: (AppointmentEntity) -> Unit
) {
    var statusFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showBookDialog by remember { mutableStateOf(false) }
    var appointmentToReschedule by remember { mutableStateOf<AppointmentEntity?>(null) }

    // Enforce tenant isolation when in Clinic Client portal
    val scopedAppointments = if (currentPortal == PortalType.CLINIC_CLIENT) {
        appointments.filter { it.clinicId == selectedClinicId }
    } else {
        appointments
    }

    val filteredAppointments = scopedAppointments.filter { apt ->
        (statusFilter == "All" || apt.status == statusFilter) &&
        (searchQuery.isEmpty() ||
         apt.patientName.contains(searchQuery, ignoreCase = true) ||
         apt.doctorName.contains(searchQuery, ignoreCase = true) ||
         apt.patientPhone.contains(searchQuery, ignoreCase = true) ||
         apt.department.contains(searchQuery, ignoreCase = true))
    }

    val activeClinic = clinics.find { it.id == selectedClinicId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SectionHeader(
            title = if (currentPortal == PortalType.CLINIC_CLIENT) "${activeClinic?.name} Schedule" else "Master Appointments Manager",
            subtitle = "${scopedAppointments.size} appointments recorded",
            actionButtonText = "Book Patient",
            onActionClick = { showBookDialog = true }
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search patient, doctor, phone, specialty...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        // Status Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "Scheduled", "Completed", "NoShow", "Cancelled", "Rescheduled").forEach { st ->
                val count = if (st == "All") scopedAppointments.size else scopedAppointments.count { it.status == st }
                FilterChip(
                    selected = statusFilter == st,
                    onClick = { statusFilter = st },
                    label = { Text("$st ($count)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ClinispirePrimaryBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        if (filteredAppointments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No appointments matching filter criteria",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredAppointments) { apt ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                        text = apt.patientName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "📞 ${apt.patientPhone} • ${apt.department}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                StatusBadge(text = apt.status)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = ClinispirePrimaryBlue, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${apt.appointmentDate} at ${apt.appointmentTime}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = ClinispireTeal, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = apt.doctorName,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (apt.notes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Notes: ${apt.notes}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action buttons: Complete, No-Show, Reschedule
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (apt.status == "Scheduled" || apt.status == "Rescheduled") {
                                    OutlinedButton(
                                        onClick = { appointmentToReschedule = apt },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Reschedule", fontSize = 10.sp)
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Button(
                                        onClick = { onUpdateAppointmentStatus(apt, "NoShow") },
                                        colors = ButtonDefaults.buttonColors(containerColor = ClinispireDanger),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("No-Show", fontSize = 10.sp)
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Button(
                                        onClick = { onUpdateAppointmentStatus(apt, "Completed") },
                                        colors = ButtonDefaults.buttonColors(containerColor = ClinispireSuccess),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Complete", fontSize = 10.sp)
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { onUpdateAppointmentStatus(apt, "Scheduled") },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Re-open", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Book Appointment Dialog
    if (showBookDialog) {
        var patientName by remember { mutableStateOf("") }
        var patientPhone by remember { mutableStateOf("+91 ") }
        var doctorName by remember { mutableStateOf(activeClinic?.ownerName ?: "Dr. Rohan Shenoy") }
        var department by remember { mutableStateOf(activeClinic?.clinicType ?: "Consultation") }
        var appointmentDate by remember { mutableStateOf("2026-11-09") }
        var appointmentTime by remember { mutableStateOf("11:00 AM") }
        var notes by remember { mutableStateOf("") }
        var targetClinicId by remember { mutableStateOf(selectedClinicId) }

        AlertDialog(
            onDismissRequest = { showBookDialog = false },
            title = { Text("Book Patient Appointment") },
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
                        value = patientPhone,
                        onValueChange = { patientPhone = it },
                        label = { Text("Patient Phone *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = doctorName,
                            onValueChange = { doctorName = it },
                            label = { Text("Doctor *") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = department,
                            onValueChange = { department = it },
                            label = { Text("Department") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = appointmentDate,
                            onValueChange = { appointmentDate = it },
                            label = { Text("Date (YYYY-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = appointmentTime,
                            onValueChange = { appointmentTime = it },
                            label = { Text("Time (e.g. 10:30 AM)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Clinical / Visit Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (patientName.isNotBlank() && patientPhone.isNotBlank()) {
                            onAddAppointment(
                                targetClinicId,
                                patientName,
                                patientPhone,
                                doctorName,
                                department,
                                appointmentDate,
                                appointmentTime,
                                notes
                            )
                            showBookDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinispirePrimaryBlue)
                ) {
                    Text("Confirm Appointment")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBookDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reschedule Dialog
    if (appointmentToReschedule != null) {
        val apt = appointmentToReschedule!!
        var newDate by remember { mutableStateOf(apt.appointmentDate) }
        var newTime by remember { mutableStateOf(apt.appointmentTime) }

        AlertDialog(
            onDismissRequest = { appointmentToReschedule = null },
            title = { Text("Reschedule: ${apt.patientName}") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Doctor: ${apt.doctorName} (${apt.department})", fontSize = 12.sp)
                    OutlinedTextField(
                        value = newDate,
                        onValueChange = { newDate = it },
                        label = { Text("New Date (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTime,
                        onValueChange = { newTime = it },
                        label = { Text("New Slot (e.g. 03:00 PM)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRescheduleAppointment(apt, newDate, newTime)
                        appointmentToReschedule = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinispirePrimaryBlue)
                ) {
                    Text("Save & Reschedule")
                }
            },
            dismissButton = {
                TextButton(onClick = { appointmentToReschedule = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
