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
import com.example.ui.PortalType
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun ClinicsScreen(
    clinics: List<ClinicEntity>,
    onAddClinic: (String, String, String, String, String, String, String) -> Unit,
    onToggleClinicStatus: (ClinicEntity) -> Unit,
    onSwitchToClinicPortal: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredClinics = clinics.filter {
        searchQuery.isEmpty() ||
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.ownerName.contains(searchQuery, ignoreCase = true) ||
        it.location.contains(searchQuery, ignoreCase = true) ||
        it.clinicType.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SectionHeader(
            title = "Registered Clinic Organizations",
            subtitle = "${clinics.size} active SaaS customer tenants",
            actionButtonText = "Add Clinic",
            onActionClick = { showAddDialog = true }
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by clinic name, doctor, location...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredClinics) { clinic ->
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
                                    text = clinic.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${clinic.ownerName} • ${clinic.clinicType}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            StatusBadge(text = clinic.status)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "📍 ${clinic.location} • 📞 ${clinic.phone}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Plan: ${clinic.subscriptionPlan} • Features: ${clinic.enabledFeatures}",
                            fontSize = 11.sp,
                            color = ClinispirePrimaryBlue
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Controls: Jump to Tenant Portal, or Suspend/Activate
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { onToggleClinicStatus(clinic) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (clinic.status == "Active") "Suspend Access" else "Reactivate Access",
                                    fontSize = 11.sp,
                                    color = if (clinic.status == "Active") ClinispireDanger else ClinispireSuccess
                                )
                            }

                            Button(
                                onClick = { onSwitchToClinicPortal(clinic.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = ClinispireTeal),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp), tint = ClinispireDeepNavy)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Open Clinic View", fontSize = 11.sp, color = ClinispireDeepNavy, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Clinic Modal
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var owner by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("+91 ") }
        var location by remember { mutableStateOf("Bengaluru") }
        var type by remember { mutableStateOf("Dental") }
        var plan by remember { mutableStateOf("Growth") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Register New Clinic Client") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Clinic Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = owner,
                        onValueChange = { owner = it },
                        label = { Text("Doctor / Owner Name *") },
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
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location (Area, City)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = type,
                            onValueChange = { type = it },
                            label = { Text("Specialty") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = plan,
                            onValueChange = { plan = it },
                            label = { Text("Plan (Starter/Growth/Pro)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && owner.isNotBlank()) {
                            onAddClinic(
                                name,
                                owner,
                                email.ifEmpty { "admin@${name.lowercase().replace(" ", "")}.in" },
                                phone,
                                location,
                                type,
                                plan
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinispirePrimaryBlue)
                ) {
                    Text("Register Clinic")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
