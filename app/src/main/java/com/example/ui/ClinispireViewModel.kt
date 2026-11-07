package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ClinispireDatabase
import com.example.data.model.*
import com.example.data.repository.ClinispireRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class PortalType {
    SUPER_ADMIN,
    CLINIC_CLIENT
}

enum class NavigationTab(val title: String, val superAdminOnly: Boolean = false) {
    OVERVIEW("Overview"),
    LEADS_CRM("Leads & CRM", true),
    CLINICS("Clinics", true),
    APPOINTMENTS("Appointments"),
    FOLLOW_UPS("Follow-Ups"),
    REVIEWS("Reviews"),
    AUTOMATIONS("Automations"),
    WHATSAPP("WhatsApp"),
    BILLING("Billing & MRR"),
    STAFF("Staff & Tasks", true),
    SUPPORT("Support Tickets"),
    INTEGRATIONS("Integrations", true),
    REPORTS("Reports & Analytics"),
    AUDIT_LOGS("Audit Logs", true),
    BRAND_SETTINGS("Brand Settings", true)
}

enum class DateFilter(val label: String) {
    TODAY("Today"),
    LAST_7_DAYS("Last 7 Days"),
    LAST_30_DAYS("Last 30 Days"),
    THIS_MONTH("This Month"),
    ALL_TIME("All Time")
}

class ClinispireViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ClinispireDatabase.getDatabase(application)
    val repository = ClinispireRepository(db.clinispireDao())

    // Active Portal & Navigation
    private val _currentPortal = MutableStateFlow(PortalType.SUPER_ADMIN)
    val currentPortal: StateFlow<PortalType> = _currentPortal.asStateFlow()

    private val _selectedClinicId = MutableStateFlow("clinic-001")
    val selectedClinicId: StateFlow<String> = _selectedClinicId.asStateFlow()

    private val _currentTab = MutableStateFlow(NavigationTab.OVERVIEW)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _dateFilter = MutableStateFlow(DateFilter.THIS_MONTH)
    val dateFilter: StateFlow<DateFilter> = _dateFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Data streams from Repository
    val clinics: StateFlow<List<ClinicEntity>> = repository.clinics.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val leads: StateFlow<List<LeadEntity>> = repository.leads.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val appointments: StateFlow<List<AppointmentEntity>> = repository.allAppointments.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val followUps: StateFlow<List<FollowUpEntity>> = repository.allFollowUps.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val reviewRequests: StateFlow<List<ReviewRequestEntity>> = repository.allReviewRequests.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val workflows: StateFlow<List<AutomationWorkflowEntity>> = repository.workflows.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val executions: StateFlow<List<AutomationExecutionEntity>> = repository.recentExecutions.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val messageJobs: StateFlow<List<MessageJobEntity>> = repository.allMessageJobs.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val subscriptions: StateFlow<List<SubscriptionEntity>> = repository.subscriptions.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val invoices: StateFlow<List<InvoiceEntity>> = repository.invoices.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val staffTasks: StateFlow<List<StaffTaskEntity>> = repository.staffTasks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val supportTickets: StateFlow<List<SupportTicketEntity>> = repository.supportTickets.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val integrations: StateFlow<List<IntegrationEntity>> = repository.integrations.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.auditLogs.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val brandSettings: StateFlow<BrandSettingsEntity?> = repository.brandSettings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    // User message feedback
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // Portal & Navigation Setters
    fun switchPortal(portal: PortalType) {
        _currentPortal.value = portal
        _currentTab.value = NavigationTab.OVERVIEW
    }

    fun selectClinic(clinicId: String) {
        _selectedClinicId.value = clinicId
    }

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun setDateFilter(filter: DateFilter) {
        _dateFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Business Actions: Leads
    fun addLead(
        clinicName: String,
        contactPerson: String,
        phone: String,
        email: String,
        location: String,
        clinicType: String,
        leadSource: String,
        requirements: String,
        estimatedValue: Double,
        assignedStaff: String,
        nextFollowUpDate: String,
        notes: String
    ) {
        viewModelScope.launch {
            val lead = LeadEntity(
                id = "lead-" + UUID.randomUUID().toString().take(6),
                clinicName = clinicName,
                contactPerson = contactPerson,
                phone = phone,
                email = email,
                location = location,
                clinicType = clinicType,
                leadSource = leadSource,
                requirements = requirements,
                estimatedMonthlyValue = estimatedValue,
                status = "New",
                assignedStaff = assignedStaff,
                nextFollowUpDate = nextFollowUpDate,
                notes = notes
            )
            repository.addLead(lead)
            showMessage("Lead '${clinicName}' successfully created in pipeline.")
        }
    }

    fun updateLeadStatus(lead: LeadEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateLeadStatus(lead, newStatus)
            showMessage("Lead '${lead.clinicName}' moved to '$newStatus'.")
        }
    }

    fun convertLeadToClinic(lead: LeadEntity, plan: String) {
        viewModelScope.launch {
            val newClinic = repository.convertLeadToClinic(lead, plan)
            showMessage("Success! ${newClinic.name} onboarded onto $plan plan.")
        }
    }

    fun addLeadActivity(leadId: String, type: String, title: String, desc: String, staff: String) {
        viewModelScope.launch {
            repository.addLeadActivity(leadId, type, title, desc, staff)
            showMessage("Activity recorded for lead.")
        }
    }

    // Business Actions: Clinics
    fun addClinic(
        name: String,
        owner: String,
        email: String,
        phone: String,
        location: String,
        type: String,
        plan: String
    ) {
        viewModelScope.launch {
            val clinic = ClinicEntity(
                id = "clinic-" + UUID.randomUUID().toString().take(6),
                name = name,
                ownerName = owner,
                email = email,
                phone = phone,
                location = location,
                clinicType = type,
                subscriptionPlan = plan,
                status = "Active",
                enabledFeatures = "Appointments, Automations, WhatsApp, Billing, Reviews"
            )
            repository.addClinic(clinic)
            showMessage("Clinic '$name' created successfully.")
        }
    }

    fun toggleClinicStatus(clinic: ClinicEntity) {
        viewModelScope.launch {
            val newStatus = if (clinic.status == "Active") "Suspended" else "Active"
            repository.toggleClinicStatus(clinic, newStatus)
            showMessage("Clinic '${clinic.name}' status changed to $newStatus.")
        }
    }

    // Business Actions: Appointments
    fun addAppointment(
        clinicId: String,
        patientName: String,
        patientPhone: String,
        doctorName: String,
        department: String,
        date: String,
        time: String,
        notes: String
    ) {
        viewModelScope.launch {
            val apt = AppointmentEntity(
                id = "apt-" + UUID.randomUUID().toString().take(6),
                clinicId = clinicId,
                patientName = patientName,
                patientPhone = patientPhone,
                doctorName = doctorName,
                department = department,
                appointmentDate = date,
                appointmentTime = time,
                status = "Scheduled",
                reminderSent = false,
                notes = notes
            )
            repository.addAppointment(apt)
            // Trigger 24h reminder workflow
            repository.triggerWorkflowEvent("wf-01", "24-Hour Pre-Appointment WhatsApp Reminder", clinicId, "Auto-queued 24h reminder for $patientName")
            showMessage("Appointment confirmed for $patientName on $date at $time.")
        }
    }

    fun updateAppointmentStatus(appointment: AppointmentEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateAppointmentStatus(appointment, newStatus)
            showMessage("Appointment for ${appointment.patientName} marked as $newStatus.")
        }
    }

    fun rescheduleAppointment(appointment: AppointmentEntity, newDate: String, newTime: String) {
        viewModelScope.launch {
            repository.rescheduleAppointment(appointment, newDate, newTime)
            showMessage("Rescheduled ${appointment.patientName}'s appointment to $newDate at $newTime.")
        }
    }

    // Business Actions: Follow-ups & Reviews
    fun addFollowUp(clinicId: String, patientName: String, patientPhone: String, reason: String, dueDate: String, notes: String) {
        viewModelScope.launch {
            val fup = FollowUpEntity(
                id = "fup-" + UUID.randomUUID().toString().take(6),
                clinicId = clinicId,
                patientName = patientName,
                patientPhone = patientPhone,
                reason = reason,
                dueDate = dueDate,
                status = "Pending",
                notes = notes
            )
            repository.addFollowUp(fup)
            showMessage("Follow-up task scheduled for $patientName on $dueDate.")
        }
    }

    fun updateFollowUpStatus(followUp: FollowUpEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateFollowUpStatus(followUp, newStatus)
            showMessage("Follow-up for ${followUp.patientName} updated to $newStatus.")
        }
    }

    fun sendReviewRequest(clinicId: String, patientName: String, phone: String) {
        viewModelScope.launch {
            val rev = ReviewRequestEntity(
                id = "rev-" + UUID.randomUUID().toString().take(6),
                clinicId = clinicId,
                patientName = patientName,
                patientPhone = phone,
                channel = "WhatsApp",
                status = "Sent"
            )
            repository.sendReviewRequest(rev)
            showMessage("WhatsApp review invite dispatched to $patientName.")
        }
    }

    // Business Actions: Automations
    fun toggleWorkflow(workflow: AutomationWorkflowEntity) {
        viewModelScope.launch {
            repository.toggleWorkflowStatus(workflow)
            showMessage("Workflow '${workflow.name}' is now ${if (workflow.status == "Active") "Paused" else "Active"}.")
        }
    }

    fun triggerWorkflowManually(workflow: AutomationWorkflowEntity) {
        viewModelScope.launch {
            repository.triggerWorkflowEvent(
                workflowId = workflow.id,
                workflowName = workflow.name,
                clinicId = _selectedClinicId.value,
                triggerDetail = "Manual test execution triggered by admin"
            )
            showMessage("Workflow '${workflow.name}' executed successfully.")
        }
    }

    fun retryExecution(execution: AutomationExecutionEntity) {
        viewModelScope.launch {
            repository.retryExecution(execution)
            showMessage("Retry dispatched for ${execution.workflowName}.")
        }
    }

    // Business Actions: WhatsApp Messages
    fun sendCustomWhatsAppMessage(clinicId: String, recipientName: String, phone: String, template: String, text: String) {
        viewModelScope.launch {
            val job = MessageJobEntity(
                id = "msg-" + UUID.randomUUID().toString().take(6),
                clinicId = clinicId,
                recipientName = recipientName,
                recipientPhone = phone,
                channel = "WhatsApp",
                templateName = template,
                content = text,
                status = "Delivered"
            )
            repository.sendWhatsAppMessage(job)
            showMessage("WhatsApp message sent to $recipientName ($phone).")
        }
    }

    // Business Actions: Invoices & Payments
    fun createInvoice(clinicId: String, clinicName: String, amount: Double, dueDate: String, desc: String) {
        viewModelScope.launch {
            val tax = amount * 0.18 // 18% GST
            val total = amount + tax
            val inv = InvoiceEntity(
                id = "inv-" + UUID.randomUUID().toString().take(6),
                invoiceNumber = "CLIN-2026-" + (100..999).random(),
                clinicId = clinicId,
                clinicName = clinicName,
                subtotal = amount,
                tax = tax,
                totalAmount = total,
                dueDate = dueDate,
                status = "Unpaid",
                description = desc
            )
            repository.addInvoice(inv)
            showMessage("Invoice ${inv.invoiceNumber} created for $clinicName.")
        }
    }

    fun markInvoicePaid(invoice: InvoiceEntity) {
        viewModelScope.launch {
            repository.markInvoicePaid(invoice)
            showMessage("Payment confirmed! Invoice ${invoice.invoiceNumber} marked as Paid.")
        }
    }

    // Business Actions: Staff Tasks
    fun addStaffTask(title: String, desc: String, role: String, staff: String, dueDate: String, priority: String) {
        viewModelScope.launch {
            val task = StaffTaskEntity(
                id = "task-" + UUID.randomUUID().toString().take(6),
                title = title,
                description = desc,
                assignedToRole = role,
                assignedToName = staff,
                dueDate = dueDate,
                priority = priority,
                status = "Pending"
            )
            repository.addStaffTask(task)
            showMessage("Task '$title' assigned to $staff.")
        }
    }

    fun updateStaffTaskStatus(task: StaffTaskEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateStaffTaskStatus(task, newStatus)
            showMessage("Task status updated to $newStatus.")
        }
    }

    // Business Actions: Support Tickets
    fun addSupportTicket(clinicId: String, clinicName: String, subject: String, desc: String, priority: String, cat: String) {
        viewModelScope.launch {
            val tkt = SupportTicketEntity(
                id = "tkt-" + UUID.randomUUID().toString().take(6),
                ticketNumber = "TICK-2026-" + (100..999).random(),
                clinicId = clinicId,
                clinicName = clinicName,
                subject = subject,
                description = desc,
                priority = priority,
                category = cat,
                status = "Open",
                assignedStaff = "Support Team"
            )
            repository.addSupportTicket(tkt)
            showMessage("Support Ticket ${tkt.ticketNumber} opened.")
        }
    }

    fun updateTicketStatus(ticket: SupportTicketEntity, status: String, notes: String) {
        viewModelScope.launch {
            repository.updateTicketStatus(ticket, status, notes)
            showMessage("Ticket ${ticket.ticketNumber} marked as $status.")
        }
    }

    // Business Actions: Integrations
    fun testIntegration(integration: IntegrationEntity) {
        viewModelScope.launch {
            repository.testIntegration(integration)
            showMessage("Connected & Verified: ${integration.serviceName} ping succeeded.")
        }
    }

    fun toggleIntegration(integration: IntegrationEntity) {
        viewModelScope.launch {
            repository.toggleIntegration(integration)
            showMessage("${integration.serviceName} status updated.")
        }
    }

    // Business Actions: Brand Settings
    fun updateBrandSettings(settings: BrandSettingsEntity) {
        viewModelScope.launch {
            repository.updateBrandSettings(settings)
            showMessage("Brand & Organization settings saved.")
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetDemoData()
            showMessage("Demo data successfully reset to original showcase state.")
        }
    }
}
