package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BrandSettingsEntity
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun BrandSettingsScreen(
    brandSettings: BrandSettingsEntity?,
    onSaveSettings: (BrandSettingsEntity) -> Unit,
    onResetDemoData: () -> Unit
) {
    var companyName by remember(brandSettings) { mutableStateOf(brandSettings?.companyName ?: "Clinispire") }
    var tagline by remember(brandSettings) { mutableStateOf(brandSettings?.tagline ?: "Inspire. Connect. Learn. Grow.") }
    var website by remember(brandSettings) { mutableStateOf(brandSettings?.website ?: "www.clinispire.online") }
    var instagram by remember(brandSettings) { mutableStateOf(brandSettings?.instagram ?: "@clinispire") }
    var supportEmail by remember(brandSettings) { mutableStateOf(brandSettings?.supportEmail ?: "support@clinispire.online") }
    var phone by remember(brandSettings) { mutableStateOf(brandSettings?.phone ?: "+91 98765 43210") }
    var address by remember(brandSettings) { mutableStateOf(brandSettings?.address ?: "Bengaluru, Karnataka, India") }

    var showResetConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            SectionHeader(
                title = "Brand & SaaS Identity",
                subtitle = "Manage Clinispire company profile, logo, and platform controls"
            )
        }

        // Logo & Theme Preview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Official Brand Identity", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ClinispireDeepNavy)
                                .border(1.dp, ClinispireTeal, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.clinispire_logo),
                                contentDescription = "Clinispire Logo",
                                modifier = Modifier.size(54.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(companyName, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = ClinispireDeepNavy)
                            Text(tagline, fontSize = 12.sp, color = ClinispirePrimaryBlue, fontWeight = FontWeight.Medium)
                            Text(website, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Core Brand Color Tokens:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BrandColorPill("Deep Navy", "#10233F", ClinispireDeepNavy)
                        BrandColorPill("Primary Blue", "#2563EB", ClinispirePrimaryBlue)
                        BrandColorPill("Teal", "#14B8A6", ClinispireTeal)
                    }
                }
            }
        }

        // Editable Brand Information
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Company Profile & Channels", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("Company Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tagline,
                        onValueChange = { tagline = it },
                        label = { Text("Brand Tagline") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = website,
                            onValueChange = { website = it },
                            label = { Text("Website") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = instagram,
                            onValueChange = { instagram = it },
                            label = { Text("Instagram Handle") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = supportEmail,
                            onValueChange = { supportEmail = it },
                            label = { Text("Support Email") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("HQ Phone") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Registered Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            val updated = (brandSettings ?: BrandSettingsEntity()).copy(
                                companyName = companyName,
                                tagline = tagline,
                                website = website,
                                instagram = instagram,
                                supportEmail = supportEmail,
                                phone = phone,
                                address = address
                            )
                            onSaveSettings(updated)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ClinispirePrimaryBlue)
                    ) {
                        Text("Save Brand Settings")
                    }
                }
            }
        }

        // Demo Environment Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Demo Mode & Data Reset", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Resetting demo data clears test leads, appointments, and restores the full showcase dataset with 5 clinics, pipeline leads, and automations.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showResetConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ClinispireWarning),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset All Demo Data", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset Demo Data?") },
            text = {
                Text("This will restore the original pre-seeded clinics, CRM leads, appointments, workflows, and invoices. Any custom records created during this session will be refreshed.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetDemoData()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinispireWarning)
                ) {
                    Text("Confirm Reset", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun BrandColorPill(name: String, hex: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(name, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = color)
    }
}
