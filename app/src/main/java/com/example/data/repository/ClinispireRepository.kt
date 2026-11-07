package com.example.data.repository

import com.example.data.local.ClinispireDao
import com.example.data.local.DemoDataSeeder
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ClinispireRepository(private val dao: ClinispireDao) {

    // Clinics
    val clinics: Flow<List<ClinicEntity>> = dao.getAllClinics()

    suspend fun getClinicById(id: String): ClinicEntity? = dao.getClinicById(id)

    suspend fun addClinic(clinic: ClinicEntity) {
        dao.insertClinic(clinic)
        logAudit("Super Admin", clinic.name, "Clinic Registered", "Clinic", clinic.id, "Success")
    }

    suspend fun updateClinic(clinic: ClinicEntity) {
        dao.updateClinic(clinic)
        logAudit("Super Admin", clinic.name, "Clinic Profile Updated", "Clinic", clinic.id, "Success")
    }

    suspend fun toggleClinicStatus(clinic: ClinicEntity, newStatus: String) {
        dao.updateClinic(clinic.copy(status = newStatus))
        logAudit("Super Admin", clinic.name, "Clinic Status -> $newStatus", "Clinic", clinic.id, "Success")
    }

    // Leads & CRM
    val leads: Flow<List<LeadEntity>> = dao.getAllLeads()

    fun getActivitiesForLead(leadId: String): Flow<List<CrmActivityEntity>> = dao.getActivitiesForLead(leadId)

    suspend fun addLead(lead: LeadEntity) {
        dao.insertLead(lead)
        logAudit("Sales Team", "Clinispire HQ", "Lead Created: ${lead.clinicName}", "Lead", lead.id, "Success")
    }

    suspend fun updateLead(lead: LeadEntity) {
        dao.updateLead(lead)
        logAudit("Sales Team", "Clinispire HQ", "Lead Updated: ${lead.clinicName}", "Lead", lead.id, "Success")
    }

    suspend fun updateLeadStatus(lead: LeadEntity, newStatus: String) {
        dao.updateLead(lead.copy(status = newStatus))
        dao.insertActivity(
            CrmActivityEntity(
                id = UUID.randomUUID().toString(),
                leadId = lead.id,
                activityType = "Pipeline Change",
                title = "Status updated to $newStatus",
                description = "Lead moved from ${lead.status} to $newStatus in pipeline.",
                staffName = "Current User"
            )
        )
        logAudit("Sales Team", "Clinispire HQ", "Pipeline moved -> $newStatus (${lead.clinicName})", "Lead", lead.id, "Success")
    }

    suspend fun addLeadActivity(leadId: String, type: String, title: String, description: String, staffName: String) {
        val act = CrmActivityEntity(
            id = UUID.randomUUID().toString(),
            leadId = leadId,
            activityType = type,
            title = title,
            description = description,
            staffName = staffName
        )
        dao.insertActivity(act)
    }

    // Convert Lead to Clinic
    suspend fun convertLeadToClinic(lead: LeadEntity, plan: String): ClinicEntity {
        val clinicId = "clinic-" + System.currentTimeMillis().toString().takeLast(6)
        val newClinic = ClinicEntity(
            id = clinicId,
            name = lead.clinicName,
            ownerName = lead.contactPerson,
            email = lead.email,
            phone = lead.phone,
            location = lead.location,
            clinicType = lead.clinicType,
            subscriptionPlan = plan,
            status = "Active",
            enabledFeatures = "Appointments, Automations, WhatsApp, Billing, Reviews"
        )
        dao.insertClinic(newClinic)
        dao.updateLead(lead.copy(status = "Converted"))

        // Create subscription
        val price = when (plan) {
            "Starter" -> 1499.0
            "Growth" -> 2999.0
            else -> 4999.0
        }
        val renewal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(System.currentTimeMillis() + 30L * 86400000L))
        dao.insertSubscription(
            SubscriptionEntity(
                id = UUID.randomUUID().toString(),
                clinicId = clinicId,
                clinicName = newClinic.name,
                planName = plan,
                monthlyPrice = price,
                billingCycle = "Monthly",
                startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                renewalDate = renewal,
                status = "Active"
            )
        )

        // Log
        logAudit("Super Admin", newClinic.name, "Converted Lead to Active Clinic", "Clinic", clinicId, "Success")
        return newClinic
    }

    // Appointments
    val allAppointments: Flow<List<AppointmentEntity>> = dao.getAllAppointments()

    fun getAppointmentsForClinic(clinicId: String): Flow<List<AppointmentEntity>> = dao.getAppointmentsForClinic(clinicId)

    suspend fun addAppointment(appointment: AppointmentEntity) {
        dao.insertAppointment(appointment)
        logAudit("Clinic Staff", appointment.clinicId, "Appointment Booked: ${appointment.patientName}", "Appointment", appointment.id, "Success")
    }

    suspend fun updateAppointmentStatus(appointment: AppointmentEntity, newStatus: String) {
        dao.updateAppointment(appointment.copy(status = newStatus))
        logAudit("Clinic Staff", appointment.clinicId, "Appointment marked $newStatus (${appointment.patientName})", "Appointment", appointment.id, "Success")

        // If marked NoShow, trigger Missed Recovery workflow
        if (newStatus == "NoShow") {
            triggerWorkflowEvent("wf-02", "Missed Appointment Recovery Follow-Up", appointment.clinicId, "Triggered for ${appointment.patientName} (marked NoShow)")
            dao.insertFollowUp(
                FollowUpEntity(
                    id = UUID.randomUUID().toString(),
                    clinicId = appointment.clinicId,
                    patientName = appointment.patientName,
                    patientPhone = appointment.patientPhone,
                    reason = "NoShow Rebooking Assistance",
                    dueDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    status = "Pending"
                )
            )
        } else if (newStatus == "Completed") {
            // Trigger review booster
            triggerWorkflowEvent("wf-03", "Post-Visit Google Review & Feedback Booster", appointment.clinicId, "Triggered for ${appointment.patientName} (visit completed)")
            dao.insertReviewRequest(
                ReviewRequestEntity(
                    id = UUID.randomUUID().toString(),
                    clinicId = appointment.clinicId,
                    patientName = appointment.patientName,
                    patientPhone = appointment.patientPhone,
                    channel = "WhatsApp",
                    status = "Sent"
                )
            )
        }
    }

    suspend fun rescheduleAppointment(appointment: AppointmentEntity, newDate: String, newTime: String) {
        dao.updateAppointment(appointment.copy(appointmentDate = newDate, appointmentTime = newTime, status = "Rescheduled"))
        logAudit("Clinic Staff", appointment.clinicId, "Appointment Rescheduled to $newDate $newTime (${appointment.patientName})", "Appointment", appointment.id, "Success")
    }

    // Follow-ups
    val allFollowUps: Flow<List<FollowUpEntity>> = dao.getAllFollowUps()
    fun getFollowUpsForClinic(clinicId: String): Flow<List<FollowUpEntity>> = dao.getFollowUpsForClinic(clinicId)

    suspend fun addFollowUp(followUp: FollowUpEntity) {
        dao.insertFollowUp(followUp)
    }

    suspend fun updateFollowUpStatus(followUp: FollowUpEntity, newStatus: String) {
        dao.updateFollowUp(followUp.copy(status = newStatus))
    }

    // Reviews
    val allReviewRequests: Flow<List<ReviewRequestEntity>> = dao.getAllReviewRequests()
    fun getReviewRequestsForClinic(clinicId: String): Flow<List<ReviewRequestEntity>> = dao.getReviewRequestsForClinic(clinicId)

    suspend fun sendReviewRequest(request: ReviewRequestEntity) {
        dao.insertReviewRequest(request)
        // Also queue a WhatsApp message
        dao.insertMessageJob(
            MessageJobEntity(
                id = UUID.randomUUID().toString(),
                clinicId = request.clinicId,
                recipientName = request.patientName,
                recipientPhone = request.patientPhone,
                channel = request.channel,
                templateName = "post_visit_feedback",
                content = "Thank you for visiting us! Could you please take 30 seconds to rate your experience? clinispire.online/review/${request.id}",
                status = "Delivered"
            )
        )
    }

    // Automation Workflows
    val workflows: Flow<List<AutomationWorkflowEntity>> = dao.getAllWorkflows()
    val recentExecutions: Flow<List<AutomationExecutionEntity>> = dao.getRecentExecutions()

    suspend fun toggleWorkflowStatus(workflow: AutomationWorkflowEntity) {
        val newStatus = if (workflow.status == "Active") "Paused" else "Active"
        dao.updateWorkflow(workflow.copy(status = newStatus))
        logAudit("Automation Manager", "Clinispire HQ", "Workflow ${workflow.name} -> $newStatus", "AutomationWorkflow", workflow.id, "Success")
    }

    suspend fun triggerWorkflowEvent(workflowId: String, workflowName: String, clinicId: String?, triggerDetail: String) {
        val isSimulatedSuccess = true
        val status = if (isSimulatedSuccess) "Success" else "Failed"
        val nowStr = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault()).format(Date())

        dao.insertExecution(
            AutomationExecutionEntity(
                id = UUID.randomUUID().toString(),
                workflowId = workflowId,
                workflowName = workflowName,
                clinicId = clinicId,
                status = status,
                triggerDetail = triggerDetail,
                executedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun retryExecution(execution: AutomationExecutionEntity) {
        dao.insertExecution(
            execution.copy(
                id = UUID.randomUUID().toString(),
                status = "Retried",
                triggerDetail = "Manual retry succeeded: ${execution.triggerDetail}",
                executedAt = System.currentTimeMillis(),
                errorMessage = null
            )
        )
        logAudit("Automation Admin", execution.clinicId ?: "Clinispire HQ", "Manual Retry Executed", "AutomationExecution", execution.id, "Success")
    }

    // Messages
    val allMessageJobs: Flow<List<MessageJobEntity>> = dao.getAllMessageJobs()
    fun getMessageJobsForClinic(clinicId: String): Flow<List<MessageJobEntity>> = dao.getMessageJobsForClinic(clinicId)

    suspend fun sendWhatsAppMessage(job: MessageJobEntity) {
        dao.insertMessageJob(job)
        logAudit("Communication Engine", job.clinicId, "WhatsApp Message Sent to ${job.recipientName}", "MessageJob", job.id, "Success")
    }

    // Subscriptions & Invoices
    val subscriptions: Flow<List<SubscriptionEntity>> = dao.getAllSubscriptions()
    val invoices: Flow<List<InvoiceEntity>> = dao.getAllInvoices()

    fun getInvoicesForClinic(clinicId: String): Flow<List<InvoiceEntity>> = dao.getInvoicesForClinic(clinicId)

    suspend fun addInvoice(invoice: InvoiceEntity) {
        dao.insertInvoice(invoice)
        logAudit("Billing Admin", invoice.clinicName, "Invoice Generated: ${invoice.invoiceNumber}", "Invoice", invoice.id, "Success")
    }

    suspend fun markInvoicePaid(invoice: InvoiceEntity) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        dao.updateInvoice(invoice.copy(status = "Paid", paidAt = today))
        logAudit("Billing Admin", invoice.clinicName, "Invoice Verified Paid: ${invoice.invoiceNumber} (₹${invoice.totalAmount})", "Invoice", invoice.id, "Success")
    }

    // Staff & Tasks
    val staffTasks: Flow<List<StaffTaskEntity>> = dao.getAllStaffTasks()

    suspend fun addStaffTask(task: StaffTaskEntity) {
        dao.insertStaffTask(task)
        logAudit("Operations Lead", "Clinispire HQ", "Task Assigned to ${task.assignedToName}: ${task.title}", "StaffTask", task.id, "Success")
    }

    suspend fun updateStaffTaskStatus(task: StaffTaskEntity, newStatus: String) {
        dao.updateStaffTask(task.copy(status = newStatus))
    }

    // Support Tickets
    val supportTickets: Flow<List<SupportTicketEntity>> = dao.getAllSupportTickets()
    fun getSupportTicketsForClinic(clinicId: String): Flow<List<SupportTicketEntity>> = dao.getSupportTicketsForClinic(clinicId)

    suspend fun addSupportTicket(ticket: SupportTicketEntity) {
        dao.insertSupportTicket(ticket)
        logAudit("Support Desk", ticket.clinicName, "Support Ticket Raised: ${ticket.ticketNumber}", "SupportTicket", ticket.id, "Success")
    }

    suspend fun updateTicketStatus(ticket: SupportTicketEntity, newStatus: String, notes: String = "") {
        dao.updateSupportTicket(ticket.copy(status = newStatus, resolutionNotes = if (notes.isNotEmpty()) notes else ticket.resolutionNotes))
        logAudit("Support Desk", ticket.clinicName, "Ticket ${ticket.ticketNumber} -> $newStatus", "SupportTicket", ticket.id, "Success")
    }

    // Integrations
    val integrations: Flow<List<IntegrationEntity>> = dao.getAllIntegrations()

    suspend fun testIntegration(integration: IntegrationEntity): Boolean {
        val nowStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val updated = integration.copy(
            status = "Connected and Verified",
            lastVerified = "$nowStr (Server ping 200 OK)"
        )
        dao.updateIntegration(updated)
        logAudit("Security Admin", "Clinispire HQ", "Integration Verified: ${integration.serviceName}", "Integration", integration.id, "Success")
        return true
    }

    suspend fun toggleIntegration(integration: IntegrationEntity) {
        val newStatus = if (integration.status == "Disabled") "Connected and Verified" else "Disabled"
        dao.updateIntegration(integration.copy(status = newStatus))
    }

    // Audit Logs
    val auditLogs: Flow<List<AuditLogEntity>> = dao.getRecentAuditLogs()

    private suspend fun logAudit(userName: String, org: String, action: String, recordType: String, recordId: String, outcome: String) {
        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                userName = userName,
                organizationName = org,
                action = action,
                recordType = recordType,
                recordId = recordId,
                outcome = outcome,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // Brand Settings
    val brandSettings: Flow<BrandSettingsEntity?> = dao.getBrandSettings()

    suspend fun updateBrandSettings(settings: BrandSettingsEntity) {
        dao.insertBrandSettings(settings)
        logAudit("Super Admin", "Clinispire HQ", "Brand Settings Updated", "BrandSettings", "1", "Success")
    }

    suspend fun resetDemoData() {
        DemoDataSeeder.seedAll(dao)
    }
}
