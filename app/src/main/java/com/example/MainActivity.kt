package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.ClinispireViewModel
import com.example.ui.NavigationTab
import com.example.ui.PortalType
import com.example.ui.components.ClinispireTabRow
import com.example.ui.components.ClinispireTopBar
import com.example.ui.components.DemoModeBanner
import com.example.ui.screens.*
import com.example.ui.theme.ClinispireTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: ClinispireViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ClinispireTheme {
                ClinispireApp(viewModel)
            }
        }
    }
}

@Composable
fun ClinispireApp(viewModel: ClinispireViewModel) {
    val currentPortal by viewModel.currentPortal.collectAsStateWithLifecycle()
    val selectedClinicId by viewModel.selectedClinicId.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val dateFilter by viewModel.dateFilter.collectAsStateWithLifecycle()

    val clinics by viewModel.clinics.collectAsStateWithLifecycle()
    val leads by viewModel.leads.collectAsStateWithLifecycle()
    val appointments by viewModel.appointments.collectAsStateWithLifecycle()
    val followUps by viewModel.followUps.collectAsStateWithLifecycle()
    val reviewRequests by viewModel.reviewRequests.collectAsStateWithLifecycle()
    val workflows by viewModel.workflows.collectAsStateWithLifecycle()
    val executions by viewModel.executions.collectAsStateWithLifecycle()
    val messageJobs by viewModel.messageJobs.collectAsStateWithLifecycle()
    val subscriptions by viewModel.subscriptions.collectAsStateWithLifecycle()
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()
    val staffTasks by viewModel.staffTasks.collectAsStateWithLifecycle()
    val supportTickets by viewModel.supportTickets.collectAsStateWithLifecycle()
    val integrations by viewModel.integrations.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val brandSettings by viewModel.brandSettings.collectAsStateWithLifecycle()
    val snackbarMsg by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Show toast / snackbar on event
    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Android back button handling for secondary screens & portals
    BackHandler(enabled = currentTab != NavigationTab.OVERVIEW || currentPortal == PortalType.CLINIC_CLIENT) {
        if (currentTab != NavigationTab.OVERVIEW) {
            viewModel.selectTab(NavigationTab.OVERVIEW)
        } else if (currentPortal == PortalType.CLINIC_CLIENT) {
            viewModel.switchPortal(PortalType.SUPER_ADMIN)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                ClinispireTopBar(
                    currentPortal = currentPortal,
                    onPortalSwitch = { viewModel.switchPortal(it) },
                    clinics = clinics,
                    selectedClinicId = selectedClinicId,
                    onSelectClinic = { viewModel.selectClinic(it) },
                    onOpenBrandSettings = { viewModel.selectTab(NavigationTab.BRAND_SETTINGS) },
                    onResetDemo = { viewModel.resetDemoData() }
                )
                DemoModeBanner(
                    onResetDemo = { viewModel.resetDemoData() }
                )
                ClinispireTabRow(
                    currentPortal = currentPortal,
                    selectedTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentTab) {
                NavigationTab.OVERVIEW -> {
                    OverviewScreen(
                        currentPortal = currentPortal,
                        selectedClinicId = selectedClinicId,
                        dateFilter = dateFilter,
                        onDateFilterChange = { viewModel.setDateFilter(it) },
                        clinics = clinics,
                        leads = leads,
                        appointments = appointments,
                        followUps = followUps,
                        reviewRequests = reviewRequests,
                        workflows = workflows,
                        executions = executions,
                        subscriptions = subscriptions,
                        invoices = invoices,
                        tickets = supportTickets,
                        integrations = integrations,
                        auditLogs = auditLogs,
                        onNavigateToTab = { viewModel.selectTab(it) },
                        onQuickAddLead = { viewModel.selectTab(NavigationTab.LEADS_CRM) },
                        onQuickBookAppointment = { viewModel.selectTab(NavigationTab.APPOINTMENTS) }
                    )
                }

                NavigationTab.LEADS_CRM -> {
                    LeadsCrmScreen(
                        leads = leads,
                        onAddLead = { name, person, phone, email, loc, type, src, req, valEst, staff, date, notes ->
                            viewModel.addLead(name, person, phone, email, loc, type, src, req, valEst, staff, date, notes)
                        },
                        onUpdateLeadStatus = { lead, status ->
                            viewModel.updateLeadStatus(lead, status)
                        },
                        onConvertLeadToClinic = { lead, plan ->
                            viewModel.convertLeadToClinic(lead, plan)
                        }
                    )
                }

                NavigationTab.CLINICS -> {
                    ClinicsScreen(
                        clinics = clinics,
                        onAddClinic = { name, owner, email, phone, loc, type, plan ->
                            viewModel.addClinic(name, owner, email, phone, loc, type, plan)
                        },
                        onToggleClinicStatus = { clinic ->
                            viewModel.toggleClinicStatus(clinic)
                        },
                        onSwitchToClinicPortal = { clinicId ->
                            viewModel.selectClinic(clinicId)
                            viewModel.switchPortal(PortalType.CLINIC_CLIENT)
                        }
                    )
                }

                NavigationTab.APPOINTMENTS -> {
                    AppointmentsScreen(
                        currentPortal = currentPortal,
                        selectedClinicId = selectedClinicId,
                        clinics = clinics,
                        appointments = appointments,
                        onAddAppointment = { clinicId, patient, phone, doctor, dept, date, time, notes ->
                            viewModel.addAppointment(clinicId, patient, phone, doctor, dept, date, time, notes)
                        },
                        onUpdateAppointmentStatus = { apt, status ->
                            viewModel.updateAppointmentStatus(apt, status)
                        },
                        onRescheduleAppointment = { apt, newDate, newTime ->
                            viewModel.rescheduleAppointment(apt, newDate, newTime)
                        },
                        onRequestReminder = { apt ->
                            viewModel.sendCustomWhatsAppMessage(
                                apt.clinicId,
                                apt.patientName,
                                apt.patientPhone,
                                "appointment_reminder_v2",
                                "Reminder: Your appointment with ${apt.doctorName} is confirmed for ${apt.appointmentDate} at ${apt.appointmentTime}."
                            )
                        }
                    )
                }

                NavigationTab.FOLLOW_UPS -> {
                    ClinicFollowUpsAndReviewsScreen(
                        selectedClinicId = selectedClinicId,
                        followUps = followUps,
                        reviewRequests = reviewRequests,
                        isReviewsView = false,
                        onAddFollowUp = { clinicId, patient, phone, reason, date, notes ->
                            viewModel.addFollowUp(clinicId, patient, phone, reason, date, notes)
                        },
                        onUpdateFollowUpStatus = { fup, status ->
                            viewModel.updateFollowUpStatus(fup, status)
                        },
                        onSendReviewRequest = { clinicId, patient, phone ->
                            viewModel.sendReviewRequest(clinicId, patient, phone)
                        }
                    )
                }

                NavigationTab.REVIEWS -> {
                    ClinicFollowUpsAndReviewsScreen(
                        selectedClinicId = selectedClinicId,
                        followUps = followUps,
                        reviewRequests = reviewRequests,
                        isReviewsView = true,
                        onAddFollowUp = { clinicId, patient, phone, reason, date, notes ->
                            viewModel.addFollowUp(clinicId, patient, phone, reason, date, notes)
                        },
                        onUpdateFollowUpStatus = { fup, status ->
                            viewModel.updateFollowUpStatus(fup, status)
                        },
                        onSendReviewRequest = { clinicId, patient, phone ->
                            viewModel.sendReviewRequest(clinicId, patient, phone)
                        }
                    )
                }

                NavigationTab.AUTOMATIONS -> {
                    AutomationCenterScreen(
                        workflows = workflows,
                        executions = executions,
                        onToggleWorkflow = { viewModel.toggleWorkflow(it) },
                        onTriggerManually = { viewModel.triggerWorkflowManually(it) },
                        onRetryExecution = { viewModel.retryExecution(it) }
                    )
                }

                NavigationTab.WHATSAPP -> {
                    WhatsAppScreen(
                        messageJobs = messageJobs,
                        selectedClinicId = selectedClinicId,
                        onSendMessage = { clinicId, name, phone, tmpl, msg ->
                            viewModel.sendCustomWhatsAppMessage(clinicId, name, phone, tmpl, msg)
                        }
                    )
                }

                NavigationTab.BILLING -> {
                    BillingScreen(
                        subscriptions = subscriptions,
                        invoices = invoices,
                        clinics = clinics,
                        onCreateInvoice = { clinicId, clinicName, amount, dueDate, desc ->
                            viewModel.createInvoice(clinicId, clinicName, amount, dueDate, desc)
                        },
                        onMarkInvoicePaid = { inv ->
                            viewModel.markInvoicePaid(inv)
                        }
                    )
                }

                NavigationTab.STAFF -> {
                    StaffAndSupportScreen(
                        currentPortal = currentPortal,
                        selectedClinicId = selectedClinicId,
                        clinics = clinics,
                        staffTasks = staffTasks,
                        supportTickets = supportTickets,
                        onAddStaffTask = { title, desc, role, staff, dueDate, priority ->
                            viewModel.addStaffTask(title, desc, role, staff, dueDate, priority)
                        },
                        onUpdateStaffTaskStatus = { task, status ->
                            viewModel.updateStaffTaskStatus(task, status)
                        },
                        onAddSupportTicket = { clinicId, clinicName, subj, desc, priority, cat ->
                            viewModel.addSupportTicket(clinicId, clinicName, subj, desc, priority, cat)
                        },
                        onUpdateTicketStatus = { ticket, status, notes ->
                            viewModel.updateTicketStatus(ticket, status, notes)
                        }
                    )
                }

                NavigationTab.SUPPORT -> {
                    StaffAndSupportScreen(
                        currentPortal = currentPortal,
                        selectedClinicId = selectedClinicId,
                        clinics = clinics,
                        staffTasks = staffTasks,
                        supportTickets = supportTickets,
                        onAddStaffTask = { title, desc, role, staff, dueDate, priority ->
                            viewModel.addStaffTask(title, desc, role, staff, dueDate, priority)
                        },
                        onUpdateStaffTaskStatus = { task, status ->
                            viewModel.updateStaffTaskStatus(task, status)
                        },
                        onAddSupportTicket = { clinicId, clinicName, subj, desc, priority, cat ->
                            viewModel.addSupportTicket(clinicId, clinicName, subj, desc, priority, cat)
                        },
                        onUpdateTicketStatus = { ticket, status, notes ->
                            viewModel.updateTicketStatus(ticket, status, notes)
                        }
                    )
                }

                NavigationTab.INTEGRATIONS -> {
                    IntegrationsAndReportsScreen(
                        integrations = integrations,
                        auditLogs = auditLogs,
                        leads = leads,
                        subscriptions = subscriptions,
                        onTestIntegration = { viewModel.testIntegration(it) },
                        onToggleIntegration = { viewModel.toggleIntegration(it) },
                        onExportCsv = { viewModel.showMessage("Data exported: Clinispire_Report_2026.csv saved to downloads.") },
                        initialTab = 0
                    )
                }

                NavigationTab.REPORTS -> {
                    IntegrationsAndReportsScreen(
                        integrations = integrations,
                        auditLogs = auditLogs,
                        leads = leads,
                        subscriptions = subscriptions,
                        onTestIntegration = { viewModel.testIntegration(it) },
                        onToggleIntegration = { viewModel.toggleIntegration(it) },
                        onExportCsv = { viewModel.showMessage("Data exported: Clinispire_Report_2026.csv saved to downloads.") },
                        initialTab = 1
                    )
                }

                NavigationTab.AUDIT_LOGS -> {
                    IntegrationsAndReportsScreen(
                        integrations = integrations,
                        auditLogs = auditLogs,
                        leads = leads,
                        subscriptions = subscriptions,
                        onTestIntegration = { viewModel.testIntegration(it) },
                        onToggleIntegration = { viewModel.toggleIntegration(it) },
                        onExportCsv = { viewModel.showMessage("Data exported: Clinispire_AuditLogs_2026.csv saved to downloads.") },
                        initialTab = 2
                    )
                }

                NavigationTab.BRAND_SETTINGS -> {
                    BrandSettingsScreen(
                        brandSettings = brandSettings,
                        onSaveSettings = { viewModel.updateBrandSettings(it) },
                        onResetDemoData = { viewModel.resetDemoData() }
                    )
                }
            }
        }
    }
}
