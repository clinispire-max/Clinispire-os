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
import com.example.data.model.ClinicEntity
import com.example.data.model.InvoiceEntity
import com.example.data.model.SubscriptionEntity
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun BillingScreen(
    subscriptions: List<SubscriptionEntity>,
    invoices: List<InvoiceEntity>,
    clinics: List<ClinicEntity>,
    onCreateInvoice: (String, String, Double, String, String) -> Unit,
    onMarkInvoicePaid: (InvoiceEntity) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Subscriptions & Plans, 1 = Invoices
    var showCreateInvoiceDialog by remember { mutableStateOf(false) }

    val totalMrr = subscriptions.filter { it.status == "Active" }.sumOf { it.monthlyPrice }
    val totalCollected = invoices.filter { it.status == "Paid" }.sumOf { it.totalAmount }
    val outstanding = invoices.filter { it.status == "Unpaid" || it.status == "Overdue" }.sumOf { it.totalAmount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SectionHeader(
            title = "Subscriptions & Invoicing",
            subtitle = "MRR: ₹${totalMrr.toInt()}/mo • Outstanding: ₹${outstanding.toInt()}",
            actionButtonText = "New Invoice",
            onActionClick = { showCreateInvoiceDialog = true }
        )

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = ClinispirePrimaryBlue
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Subscriptions & Plans", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Invoices & Dues (${invoices.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedTab == 0) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Configurable SaaS Plans Showcase
                item {
                    Text(
                        text = "Configurable SaaS Plans",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("STARTER", "₹1,499", "Single Doctor • Core Scheduling • WhatsApp Reminders"),
                            Triple("GROWTH", "₹2,999", "Up to 5 Doctors • Automated Follow-Ups • Review Booster"),
                            Triple("PRO", "₹4,999", "Unlimited Doctors • Full Automation Suite • Custom Branding & API")
                        ).forEach { (plan, price, desc) ->
                            Card(
                                modifier = Modifier.width(220.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(plan, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = ClinispirePrimaryBlue)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("$price / month", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(desc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Active Clinic Subscriptions (${subscriptions.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(subscriptions) { sub ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(sub.clinicName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    "Plan: ${sub.planName} • ₹${sub.monthlyPrice.toInt()}/mo (${sub.billingCycle})",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Renews on: ${sub.renewalDate}",
                                    fontSize = 11.sp,
                                    color = ClinispirePrimaryBlue
                                )
                            }
                            StatusBadge(text = sub.status)
                        }
                    }
                }
            }
        } else {
            // Invoices List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(invoices) { inv ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = inv.invoiceNumber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = inv.clinicName,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                StatusBadge(text = inv.status)
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = inv.description,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Due: ${inv.dueDate} • (Subtotal: ₹${inv.subtotal.toInt()} + 18% GST)",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "₹${inv.totalAmount.toInt()}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (inv.status == "Paid") ClinispireSuccess else ClinispireDanger
                                )
                            }

                            if (inv.status != "Paid") {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = { onMarkInvoicePaid(inv) },
                                        colors = ButtonDefaults.buttonColors(containerColor = ClinispireSuccess),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Record Payment", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Create Invoice Dialog
    if (showCreateInvoiceDialog) {
        var selectedClinic by remember { mutableStateOf(clinics.firstOrNull()?.id ?: "") }
        var amountStr by remember { mutableStateOf("2999") }
        var dueDate by remember { mutableStateOf("2026-11-20") }
        var description by remember { mutableStateOf("Clinispire Monthly Platform Subscription") }

        val clinicObj = clinics.find { it.id == selectedClinic } ?: clinics.firstOrNull()

        AlertDialog(
            onDismissRequest = { showCreateInvoiceDialog = false },
            title = { Text("Generate Customer Invoice") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Select Clinic Organization:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        clinics.forEach { cl ->
                            FilterChip(
                                selected = cl.id == selectedClinic,
                                onClick = { selectedClinic = cl.id },
                                label = { Text(cl.name, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Base Amount (₹ before tax) *") },
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
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Service Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    val tax = amount * 0.18
                    Text(
                        text = "Total Payable (with 18% GST): ₹${(amount + tax).toInt()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClinispirePrimaryBlue
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountStr.toDoubleOrNull()
                        if (amount != null && amount > 0 && clinicObj != null) {
                            onCreateInvoice(clinicObj.id, clinicObj.name, amount, dueDate, description)
                            showCreateInvoiceDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinispirePrimaryBlue)
                ) {
                    Text("Generate & Send")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateInvoiceDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
