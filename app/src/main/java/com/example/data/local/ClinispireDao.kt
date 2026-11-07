package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClinispireDao {
    // Clinics
    @Query("SELECT * FROM clinics ORDER BY name ASC")
    fun getAllClinics(): Flow<List<ClinicEntity>>

    @Query("SELECT * FROM clinics WHERE id = :id LIMIT 1")
    suspend fun getClinicById(id: String): ClinicEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClinic(clinic: ClinicEntity)

    @Update
    suspend fun updateClinic(clinic: ClinicEntity)

    @Query("DELETE FROM clinics WHERE id = :id")
    suspend fun deleteClinic(id: String)

    // Leads
    @Query("SELECT * FROM leads ORDER BY createdAt DESC")
    fun getAllLeads(): Flow<List<LeadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity)

    @Update
    suspend fun updateLead(lead: LeadEntity)

    @Query("DELETE FROM leads WHERE id = :id")
    suspend fun deleteLead(id: String)

    // CRM Activities
    @Query("SELECT * FROM crm_activities WHERE leadId = :leadId ORDER BY timestamp DESC")
    fun getActivitiesForLead(leadId: String): Flow<List<CrmActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: CrmActivityEntity)

    // Appointments
    @Query("SELECT * FROM appointments ORDER BY appointmentDate ASC, appointmentTime ASC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE clinicId = :clinicId ORDER BY appointmentDate ASC, appointmentTime ASC")
    fun getAppointmentsForClinic(clinicId: String): Flow<List<AppointmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteAppointment(id: String)

    // Follow-ups
    @Query("SELECT * FROM follow_ups ORDER BY dueDate ASC")
    fun getAllFollowUps(): Flow<List<FollowUpEntity>>

    @Query("SELECT * FROM follow_ups WHERE clinicId = :clinicId ORDER BY dueDate ASC")
    fun getFollowUpsForClinic(clinicId: String): Flow<List<FollowUpEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowUp(followUp: FollowUpEntity)

    @Update
    suspend fun updateFollowUp(followUp: FollowUpEntity)

    // Review Requests
    @Query("SELECT * FROM review_requests ORDER BY sentAt DESC")
    fun getAllReviewRequests(): Flow<List<ReviewRequestEntity>>

    @Query("SELECT * FROM review_requests WHERE clinicId = :clinicId ORDER BY sentAt DESC")
    fun getReviewRequestsForClinic(clinicId: String): Flow<List<ReviewRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviewRequest(reviewRequest: ReviewRequestEntity)

    @Update
    suspend fun updateReviewRequest(reviewRequest: ReviewRequestEntity)

    // Automation Workflows
    @Query("SELECT * FROM automation_workflows ORDER BY name ASC")
    fun getAllWorkflows(): Flow<List<AutomationWorkflowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkflow(workflow: AutomationWorkflowEntity)

    @Update
    suspend fun updateWorkflow(workflow: AutomationWorkflowEntity)

    // Executions
    @Query("SELECT * FROM automation_executions ORDER BY executedAt DESC LIMIT 50")
    fun getRecentExecutions(): Flow<List<AutomationExecutionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExecution(execution: AutomationExecutionEntity)

    // Message Jobs
    @Query("SELECT * FROM message_jobs ORDER BY sentAt DESC LIMIT 50")
    fun getAllMessageJobs(): Flow<List<MessageJobEntity>>

    @Query("SELECT * FROM message_jobs WHERE clinicId = :clinicId ORDER BY sentAt DESC")
    fun getMessageJobsForClinic(clinicId: String): Flow<List<MessageJobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageJob(job: MessageJobEntity)

    @Update
    suspend fun updateMessageJob(job: MessageJobEntity)

    // Subscriptions
    @Query("SELECT * FROM subscriptions ORDER BY renewalDate ASC")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity)

    @Update
    suspend fun updateSubscription(subscription: SubscriptionEntity)

    // Invoices
    @Query("SELECT * FROM invoices ORDER BY createdAt DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE clinicId = :clinicId ORDER BY createdAt DESC")
    fun getInvoicesForClinic(clinicId: String): Flow<List<InvoiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity)

    @Update
    suspend fun updateInvoice(invoice: InvoiceEntity)

    // Staff Tasks
    @Query("SELECT * FROM staff_tasks ORDER BY dueDate ASC")
    fun getAllStaffTasks(): Flow<List<StaffTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffTask(task: StaffTaskEntity)

    @Update
    suspend fun updateStaffTask(task: StaffTaskEntity)

    // Support Tickets
    @Query("SELECT * FROM support_tickets ORDER BY createdAt DESC")
    fun getAllSupportTickets(): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM support_tickets WHERE clinicId = :clinicId ORDER BY createdAt DESC")
    fun getSupportTicketsForClinic(clinicId: String): Flow<List<SupportTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportTicket(ticket: SupportTicketEntity)

    @Update
    suspend fun updateSupportTicket(ticket: SupportTicketEntity)

    // Integrations
    @Query("SELECT * FROM integrations ORDER BY serviceName ASC")
    fun getAllIntegrations(): Flow<List<IntegrationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntegration(integration: IntegrationEntity)

    @Update
    suspend fun updateIntegration(integration: IntegrationEntity)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    // Brand Settings
    @Query("SELECT * FROM brand_settings WHERE id = 1 LIMIT 1")
    fun getBrandSettings(): Flow<BrandSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrandSettings(settings: BrandSettingsEntity)

    // Database Reset / Clear
    @Query("DELETE FROM clinics")
    suspend fun clearClinics()

    @Query("DELETE FROM leads")
    suspend fun clearLeads()

    @Query("DELETE FROM crm_activities")
    suspend fun clearActivities()

    @Query("DELETE FROM appointments")
    suspend fun clearAppointments()

    @Query("DELETE FROM follow_ups")
    suspend fun clearFollowUps()

    @Query("DELETE FROM review_requests")
    suspend fun clearReviewRequests()

    @Query("DELETE FROM automation_workflows")
    suspend fun clearWorkflows()

    @Query("DELETE FROM automation_executions")
    suspend fun clearExecutions()

    @Query("DELETE FROM message_jobs")
    suspend fun clearMessageJobs()

    @Query("DELETE FROM subscriptions")
    suspend fun clearSubscriptions()

    @Query("DELETE FROM invoices")
    suspend fun clearInvoices()

    @Query("DELETE FROM staff_tasks")
    suspend fun clearStaffTasks()

    @Query("DELETE FROM support_tickets")
    suspend fun clearSupportTickets()

    @Query("DELETE FROM integrations")
    suspend fun clearIntegrations()

    @Query("DELETE FROM audit_logs")
    suspend fun clearAuditLogs()
}
