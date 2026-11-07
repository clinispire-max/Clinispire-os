package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clinics")
data class ClinicEntity(
    @PrimaryKey val id: String,
    val name: String,
    val ownerName: String,
    val email: String,
    val phone: String,
    val location: String,
    val clinicType: String, // Dental, Multispeciality, Dermatology, Pediatric, General
    val subscriptionPlan: String, // Starter, Growth, Pro, Enterprise
    val status: String, // Active, Trial, Suspended, Inactive
    val enabledFeatures: String, // Comma separated: Appointments, Automations, WhatsApp, Billing, Reviews
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey val id: String,
    val clinicName: String,
    val contactPerson: String,
    val phone: String,
    val email: String,
    val location: String,
    val clinicType: String,
    val leadSource: String, // Instagram, Website, Referral, Cold Outreach, Google
    val requirements: String,
    val estimatedMonthlyValue: Double,
    val status: String, // New, Contacted, Interested, Demo Scheduled, Demo Completed, Proposal Sent, Negotiation, Converted, Lost
    val assignedStaff: String,
    val nextFollowUpDate: String,
    val notes: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "crm_activities")
data class CrmActivityEntity(
    @PrimaryKey val id: String,
    val leadId: String,
    val activityType: String, // Call, Meeting, Email, Demo, Proposal, Follow-up, Note
    val title: String,
    val description: String,
    val staffName: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey val id: String,
    val clinicId: String,
    val patientName: String,
    val patientPhone: String,
    val doctorName: String,
    val department: String,
    val appointmentDate: String, // YYYY-MM-DD
    val appointmentTime: String, // HH:MM
    val status: String, // Scheduled, Completed, Cancelled, Rescheduled, NoShow
    val reminderSent: Boolean = false,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "follow_ups")
data class FollowUpEntity(
    @PrimaryKey val id: String,
    val clinicId: String,
    val patientName: String,
    val patientPhone: String,
    val reason: String,
    val dueDate: String, // YYYY-MM-DD
    val status: String, // Pending, Contacted, Completed, Overdue
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "review_requests")
data class ReviewRequestEntity(
    @PrimaryKey val id: String,
    val clinicId: String,
    val patientName: String,
    val patientPhone: String,
    val channel: String, // WhatsApp, SMS, Email
    val status: String, // Sent, Delivered, Rated, OptedOut
    val rating: Int = 0, // 1 to 5, 0 if not rated yet
    val feedback: String = "",
    val sentAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "automation_workflows")
data class AutomationWorkflowEntity(
    @PrimaryKey val id: String,
    val clinicId: String? = null, // null for global/super-admin templates
    val name: String,
    val description: String,
    val triggerType: String,
    val conditions: String,
    val actions: String,
    val status: String, // Active, Paused
    val lastExecution: String? = null,
    val nextScheduled: String? = null,
    val successCount: Int = 0,
    val failureCount: Int = 0,
    val retryCount: Int = 0
)

@Entity(tableName = "automation_executions")
data class AutomationExecutionEntity(
    @PrimaryKey val id: String,
    val workflowId: String,
    val workflowName: String,
    val clinicId: String? = null,
    val status: String, // Success, Failed, Retried
    val triggerDetail: String,
    val executedAt: Long = System.currentTimeMillis(),
    val errorMessage: String? = null
)

@Entity(tableName = "message_jobs")
data class MessageJobEntity(
    @PrimaryKey val id: String,
    val clinicId: String,
    val recipientName: String,
    val recipientPhone: String,
    val channel: String, // WhatsApp, Email, SMS
    val templateName: String,
    val content: String,
    val status: String, // Pending, Sent, Delivered, Failed, OptedOut
    val sentAt: Long = System.currentTimeMillis(),
    val errorReason: String? = null
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val id: String,
    val clinicId: String,
    val clinicName: String,
    val planName: String, // Starter, Growth, Pro
    val monthlyPrice: Double,
    val billingCycle: String, // Monthly, Annual
    val startDate: String,
    val renewalDate: String,
    val status: String // Active, Trial, Expiring, Cancelled
)

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey val id: String,
    val invoiceNumber: String,
    val clinicId: String,
    val clinicName: String,
    val subtotal: Double,
    val tax: Double,
    val totalAmount: Double,
    val dueDate: String,
    val status: String, // Paid, Unpaid, Overdue
    val paidAt: String? = null,
    val description: String = "Monthly SaaS Subscription & Automation Suite",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "staff_tasks")
data class StaffTaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val assignedToRole: String, // Super Admin, Business Manager, Sales Executive, Support Agent, Operations Manager
    val assignedToName: String,
    val dueDate: String,
    val priority: String, // Low, Medium, High, Urgent
    val status: String, // Pending, In Progress, Completed
    val relatedEntityId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey val id: String,
    val ticketNumber: String,
    val clinicId: String,
    val clinicName: String,
    val subject: String,
    val description: String,
    val priority: String, // Low, Medium, High, Urgent
    val category: String, // WhatsApp Integration, Billing, Appointment Bug, Onboarding, Feature Request
    val status: String, // Open, In Progress, Waiting for Customer, Resolved, Closed
    val assignedStaff: String,
    val resolutionNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "integrations")
data class IntegrationEntity(
    @PrimaryKey val id: String,
    val serviceKey: String, // supabase, whatsapp_cloud, razorpay, google_calendar, fcm, email_resend
    val serviceName: String,
    val description: String,
    val category: String, // Database, Communication, Payments, Calendar, Notifications
    val status: String, // Connected and Verified, Configuration Required, Not Configured, Disabled
    val endpointUrl: String = "",
    val lastVerified: String = "Not verified",
    val notes: String = ""
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val userName: String,
    val organizationName: String,
    val action: String,
    val recordType: String,
    val recordId: String,
    val outcome: String, // Success, Warning, Failed
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "brand_settings")
data class BrandSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val companyName: String = "Clinispire",
    val tagline: String = "Inspire. Connect. Learn. Grow.",
    val website: String = "www.clinispire.online",
    val instagram: String = "@clinispire",
    val supportEmail: String = "support@clinispire.online",
    val phone: String = "+91 98765 43210",
    val address: String = "Bengaluru, Karnataka, India",
    val primaryColor: String = "#2563EB",
    val deepNavyColor: String = "#10233F",
    val tealColor: String = "#14B8A6",
    val isDemoMode: Boolean = true
)
