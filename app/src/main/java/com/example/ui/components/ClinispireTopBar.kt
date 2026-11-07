package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ClinicEntity
import com.example.ui.NavigationTab
import com.example.ui.PortalType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinispireTopBar(
    currentPortal: PortalType,
    onPortalSwitch: (PortalType) -> Unit,
    clinics: List<ClinicEntity>,
    selectedClinicId: String,
    onSelectClinic: (String) -> Unit,
    onOpenBrandSettings: () -> Unit,
    onResetDemo: () -> Unit
) {
    var clinicDropdownExpanded by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    val selectedClinic = clinics.find { it.id == selectedClinicId } ?: clinics.firstOrNull()

    Surface(
        color = ClinispireDeepNavy,
        contentColor = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            // Main App Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo & Brand Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ClinispireDarkSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.clinispire_logo),
                            contentDescription = "Clinispire OS Logo",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CLINISPIRE",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                letterSpacing = 1.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ClinispireTeal)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "OS",
                                    color = ClinispireDeepNavy,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                        Text(
                            text = if (currentPortal == PortalType.SUPER_ADMIN) "Super Admin HQ Command Centre" else (selectedClinic?.name ?: "Clinic Portal"),
                            fontSize = 11.sp,
                            color = ClinispireTealLight.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Actions: Settings & Menu
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenBrandSettings,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Brand & System Settings",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Reset Demo Data") },
                                onClick = {
                                    menuExpanded = false
                                    onResetDemo()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = ClinispireWarning)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Brand Settings") },
                                onClick = {
                                    menuExpanded = false
                                    onOpenBrandSettings()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Palette, contentDescription = null, tint = ClinispirePrimaryBlue)
                                }
                            )
                        }
                    }
                }
            }

            // Portal Switcher Tabs (Super Admin vs Clinic Client)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Super Admin Tab Button
                val isAdminActive = currentPortal == PortalType.SUPER_ADMIN
                Button(
                    onClick = { onPortalSwitch(PortalType.SUPER_ADMIN) },
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAdminActive) ClinispirePrimaryBlue else ClinispireDarkSurface,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isAdminActive) Color.White else ClinispireTeal
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Super Admin HQ",
                        fontSize = 12.sp,
                        fontWeight = if (isAdminActive) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Clinic Client Tab Button
                val isClinicActive = currentPortal == PortalType.CLINIC_CLIENT
                Button(
                    onClick = { onPortalSwitch(PortalType.CLINIC_CLIENT) },
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isClinicActive) ClinispireTeal else ClinispireDarkSurface,
                        contentColor = if (isClinicActive) ClinispireDeepNavy else Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isClinicActive) ClinispireDeepNavy else ClinispireTeal
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Clinic Portal",
                        fontSize = 12.sp,
                        fontWeight = if (isClinicActive) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            // If in Clinic Client mode, render Tenant Organization Selector Dropdown
            if (currentPortal == PortalType.CLINIC_CLIENT) {
                Surface(
                    color = ClinispireDarkCard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { clinicDropdownExpanded = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = ClinispireTeal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Active Clinic Organization:",
                                    fontSize = 10.sp,
                                    color = Color.LightGray
                                )
                                Text(
                                    text = selectedClinic?.name ?: "Select Clinic",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    DropdownMenu(
                        expanded = clinicDropdownExpanded,
                        onDismissRequest = { clinicDropdownExpanded = false }
                    ) {
                        clinics.forEach { clinic ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(clinic.name, fontWeight = FontWeight.Bold)
                                        Text(
                                            "${clinic.clinicType} • Plan: ${clinic.subscriptionPlan} • ${clinic.status}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    onSelectClinic(clinic.id)
                                    clinicDropdownExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (clinic.id == selectedClinicId) ClinispirePrimaryBlue else Color.Transparent
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
