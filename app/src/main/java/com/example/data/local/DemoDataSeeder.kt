package com.example.data.local

import com.example.data.model.*

object DemoDataSeeder {
    suspend fun seedAll(dao: ClinispireDao) {
        // Clear all first for a clean state
        dao.clearClinics()
        dao.clearLeads()
        dao.clearActivities()
        dao.clearAppointments()
        dao.clearFollowUps()
        dao.clearReviewRequests()
        dao.clearWorkflows()
        dao.clearExecutions()
        dao.clearMessageJobs()
        dao.clearSubscriptions()
        dao.clearInvoices()
        dao.clearStaffTasks()
        dao.clearSupportTickets()
        dao.clearIntegrations()
        dao.clearAuditLogs()

        // 1. Clinics
        val clinics = listOf(
            ClinicEntity(
                id = "clinic-001",
                name = "Apex Dental & Orthodontics",
                ownerName = "Dr. Rohan Shenoy",
                email = "dr.rohan@apexdental.in",
                phone = "+91 98450 11223",
                location = "Indiranagar, Bengaluru",
                clinicType = "Dental",
                subscriptionPlan = "Pro",
                status = "Active",
                enabledFeatures = "Appointments, Automations, WhatsApp, Billing, Reviews"
            ),
            ClinicEntity(
                id = "clinic-002",
                name = "LifeCare Multispeciality Clinic",
                ownerName = "Dr. Ananya Rao",
                email = "contact@lifecareclinic.in",
                phone = "+91 97410 44556",
                location = "Koramangala, Bengaluru",
                clinicType = "Multispeciality",
                subscriptionPlan = "Growth",
                status = "Active",
                enabledFeatures = "Appointments, Automations, WhatsApp, Reviews"
            ),
            ClinicEntity(
                id = "clinic-003",
                name = "Nova Aesthetics & Skin Studio",
                ownerName = "Dr. Priya Varma",
                email = "drpriya@novaskin.in",
                phone = "+91 99000 88771",
                location = "HSR Layout, Bengaluru",
                clinicType = "Dermatology",
                subscriptionPlan = "Growth",
                status = "Trial",
                enabledFeatures = "Appointments, WhatsApp, Reviews"
            ),
            ClinicEntity(
                id = "clinic-004",
                name = "MedPlus Family Healthcare",
                ownerName = "Dr. Vikram Malhotra",
                email = "admin@medplusfamily.org",
                phone = "+91 98860 33442",
                location = "Whitefield, Bengaluru",
                clinicType = "General Practice",
                subscriptionPlan = "Starter",
                status = "Active",
                enabledFeatures = "Appointments, WhatsApp"
            ),
            ClinicEntity(
                id = "clinic-005",
                name = "CareFirst Pediatric Center",
                ownerName = "Dr. Meenakshi Sundaram",
                email = "info@carefirstpediatrics.com",
                phone = "+91 94480 66778",
                location = "Jayanagar, Bengaluru",
                clinicType = "Pediatric",
                subscriptionPlan = "Starter",
                status = "Suspended",
                enabledFeatures = "Appointments"
            )
        )
        clinics.forEach { dao.insertClinic(it) }

        // 2. Leads (Sales Pipeline)
        val leads = listOf(
            LeadEntity(
                id = "lead-101",
                clinicName = "SmileCraft Dental Lounge",
                contactPerson = "Dr. Nikhil Hegde",
                phone = "+91 98452 77112",
                email = "nikhil@smilecraft.in",
                location = "Koramangala, Bengaluru",
                clinicType = "Dental",
                leadSource = "Instagram",
                requirements = "Automated WhatsApp appointment confirmations & Google review booster",
                estimatedMonthlyValue = 4999.0,
                status = "Proposal Sent",
                assignedStaff = "Vikram (Sales Exec)",
                nextFollowUpDate = "2026-11-10",
                notes = "Very receptive to the Pro plan with WhatsApp automation. Follow up after proposal review."
            ),
            LeadEntity(
                id = "lead-102",
                clinicName = "Aura Dermatology & Laser Clinic",
                contactPerson = "Dr. Tanvi Kulkarni",
                phone = "+91 97411 99220",
                email = "tanvi@auraskin.com",
                location = "Sadashivanagar, Bengaluru",
                clinicType = "Dermatology",
                leadSource = "Website",
                requirements = "Multi-doctor scheduling, client consent forms, automated post-procedure care",
                estimatedMonthlyValue = 4999.0,
                status = "Demo Scheduled",
                assignedStaff = "Pooja (Business Mgr)",
                nextFollowUpDate = "2026-11-08",
                notes = "Demo scheduled for Saturday at 4 PM via Google Meet."
            ),
            LeadEntity(
                id = "lead-103",
                clinicName = "Pulse Cardiac & Diabetes Care",
                contactPerson = "Dr. K. Srinivas",
                phone = "+91 98801 33221",
                email = "srinivas@pulsecardiocare.in",
                location = "Malleshwaram, Bengaluru",
                clinicType = "Cardiology",
                leadSource = "Referral",
                requirements = "Chronic patient follow-up scheduling, lab report dispatch alerts",
                estimatedMonthlyValue = 2999.0,
                status = "Interested",
                assignedStaff = "Vikram (Sales Exec)",
                nextFollowUpDate = "2026-11-12",
                notes = "Referred by Dr. Rohan Shenoy (Apex Dental). Interested in automated follow-ups."
            ),
            LeadEntity(
                id = "lead-104",
                clinicName = "Zenith Physiotherapy & Rehab",
                contactPerson = "Dr. Rajeshwar Singh",
                phone = "+91 99160 55443",
                email = "contact@zenithrehab.com",
                location = "Electronic City, Bengaluru",
                clinicType = "Physiotherapy",
                leadSource = "Cold Outreach",
                requirements = "Session package tracking, payment links and missed workout reminders",
                estimatedMonthlyValue = 2999.0,
                status = "Negotiation",
                assignedStaff = "Pooja (Business Mgr)",
                nextFollowUpDate = "2026-11-09",
                notes = "Requested 10% discount on annual plan. Approved by Super Admin."
            ),
            LeadEntity(
                id = "lead-105",
                clinicName = "Greenwood Homoeopathic Clinic",
                contactPerson = "Dr. Smita Prabhu",
                phone = "+91 98440 22334",
                email = "dr.smita@greenwoodhealth.org",
                location = "Basavanagudi, Bengaluru",
                clinicType = "Homeopathy",
                leadSource = "Instagram",
                requirements = "Simple appointment scheduling with SMS/WhatsApp",
                estimatedMonthlyValue = 1499.0,
                status = "Contacted",
                assignedStaff = "Arun (Sales Exec)",
                nextFollowUpDate = "2026-11-11",
                notes = "Initial call completed. Sent starter brochure via WhatsApp."
            ),
            LeadEntity(
                id = "lead-106",
                clinicName = "Prana Ayurveda & Wellness",
                contactPerson = "Dr. Harish Nayak",
                phone = "+91 97390 88112",
                email = "harish@pranawellness.in",
                location = "Rajajinagar, Bengaluru",
                clinicType = "Ayurveda",
                leadSource = "Google",
                requirements = "Panchakarma session scheduling & recurring medicine refills",
                estimatedMonthlyValue = 2999.0,
                status = "New",
                assignedStaff = "Arun (Sales Exec)",
                nextFollowUpDate = "2026-11-08",
                notes = "Web inquiry submitted today morning. High potential."
            )
        )
        leads.forEach { dao.insertLead(it) }

        // 3. CRM Activities
        val activities = listOf(
            CrmActivityEntity(
                id = "act-1",
                leadId = "lead-101",
                activityType = "Proposal",
                title = "Sent Clinispire Pro Plan Proposal",
                description = "Customized proposal with WhatsApp automation and Razorpay payment link integration.",
                staffName = "Vikram"
            ),
            CrmActivityEntity(
                id = "act-2",
                leadId = "lead-101",
                activityType = "Demo",
                title = "Platform Demo Walkthrough",
                description = "Demonstrated automated 24-hr appointment reminder and Google review workflow.",
                staffName = "Pooja"
            ),
            CrmActivityEntity(
                id = "act-3",
                leadId = "lead-102",
                activityType = "Meeting",
                title = "Discovery Call",
                description = "Assessed multi-doctor requirements for 3 dermatologists and laser therapy rooms.",
                staffName = "Pooja"
            )
        )
        activities.forEach { dao.insertActivity(it) }

        // 4. Appointments
        val appointments = listOf(
            AppointmentEntity(
                id = "apt-01",
                clinicId = "clinic-001",
                patientName = "Rahul Nair",
                patientPhone = "+91 98451 22334",
                doctorName = "Dr. Rohan Shenoy",
                department = "Orthodontics",
                appointmentDate = "2026-11-08",
                appointmentTime = "10:30 AM",
                status = "Scheduled",
                reminderSent = true,
                notes = "Braces tightening and routine checkup"
            ),
            AppointmentEntity(
                id = "apt-02",
                clinicId = "clinic-001",
                patientName = "Deepa Krishnan",
                patientPhone = "+91 97412 55667",
                doctorName = "Dr. Rohan Shenoy",
                department = "Dental Surgery",
                appointmentDate = "2026-11-08",
                appointmentTime = "11:45 AM",
                status = "Scheduled",
                reminderSent = true,
                notes = "Wisdom tooth extraction follow-up"
            ),
            AppointmentEntity(
                id = "apt-03",
                clinicId = "clinic-001",
                patientName = "Suresh Menon",
                patientPhone = "+91 99002 44331",
                doctorName = "Dr. Rohan Shenoy",
                department = "Cosmetic Dentistry",
                appointmentDate = "2026-11-08",
                appointmentTime = "02:15 PM",
                status = "Scheduled",
                reminderSent = false,
                notes = "Teeth whitening consultation"
            ),
            AppointmentEntity(
                id = "apt-04",
                clinicId = "clinic-002",
                patientName = "Kavita Sharma",
                patientPhone = "+91 98863 11229",
                doctorName = "Dr. Ananya Rao",
                department = "General Medicine",
                appointmentDate = "2026-11-08",
                appointmentTime = "11:00 AM",
                status = "Completed",
                reminderSent = true,
                notes = "Blood sugar review & prescription renewal"
            ),
            AppointmentEntity(
                id = "apt-05",
                clinicId = "clinic-002",
                patientName = "Arjun Patel",
                patientPhone = "+91 94481 99008",
                doctorName = "Dr. Ananya Rao",
                department = "Cardiology",
                appointmentDate = "2026-11-08",
                appointmentTime = "03:30 PM",
                status = "NoShow",
                reminderSent = true,
                notes = "ECG review - missed appointment, triggered recovery workflow"
            ),
            AppointmentEntity(
                id = "apt-06",
                clinicId = "clinic-003",
                patientName = "Pooja Hegde",
                patientPhone = "+91 97415 66778",
                doctorName = "Dr. Priya Varma",
                department = "Dermatology",
                appointmentDate = "2026-11-09",
                appointmentTime = "04:00 PM",
                status = "Scheduled",
                reminderSent = true,
                notes = "HydraFacial maintenance session"
            )
        )
        appointments.forEach { dao.insertAppointment(it) }

        // 5. Follow-ups
        val followUps = listOf(
            FollowUpEntity(
                id = "fup-01",
                clinicId = "clinic-001",
                patientName = "Deepa Krishnan",
                patientPhone = "+91 97412 55667",
                reason = "Post-Extraction Healing & Suture Removal Check",
                dueDate = "2026-11-09",
                status = "Pending",
                notes = "Check for swelling or dry socket signs."
            ),
            FollowUpEntity(
                id = "fup-02",
                clinicId = "clinic-002",
                patientName = "Arjun Patel",
                patientPhone = "+91 94481 99008",
                reason = "Missed ECG Follow-up Rebooking",
                dueDate = "2026-11-08",
                status = "Contacted",
                notes = "WhatsApp automated rebooking link sent."
            ),
            FollowUpEntity(
                id = "fup-03",
                clinicId = "clinic-002",
                patientName = "Kavita Sharma",
                patientPhone = "+91 98863 11229",
                reason = "HbA1c Lab Report Review (3 Months)",
                dueDate = "2026-11-15",
                status = "Pending",
                notes = "Remind patient to get fasting blood draw done."
            )
        )
        followUps.forEach { dao.insertFollowUp(it) }

        // 6. Review Requests
        val reviews = listOf(
            ReviewRequestEntity(
                id = "rev-01",
                clinicId = "clinic-001",
                patientName = "Amitabh Sen",
                patientPhone = "+91 98453 88990",
                channel = "WhatsApp",
                status = "Rated",
                rating = 5,
                feedback = "Very gentle dental cleaning and zero wait time! Loved the WhatsApp reminder.",
                sentAt = System.currentTimeMillis() - 86400000L
            ),
            ReviewRequestEntity(
                id = "rev-02",
                clinicId = "clinic-001",
                patientName = "Sneha Reddy",
                patientPhone = "+91 97418 33221",
                channel = "WhatsApp",
                status = "Rated",
                rating = 5,
                feedback = "Dr. Rohan was fantastic with my aligners checkup.",
                sentAt = System.currentTimeMillis() - 172800000L
            ),
            ReviewRequestEntity(
                id = "rev-03",
                clinicId = "clinic-002",
                patientName = "Kavita Sharma",
                patientPhone = "+91 98863 11229",
                channel = "WhatsApp",
                status = "Delivered",
                rating = 0,
                feedback = "",
                sentAt = System.currentTimeMillis() - 3600000L
            )
        )
        reviews.forEach { dao.insertReviewRequest(it) }

        // 7. Automation Workflows
        val workflows = listOf(
            AutomationWorkflowEntity(
                id = "wf-01",
                name = "24-Hour Pre-Appointment WhatsApp Reminder",
                description = "Sends personalized reminder 24 hours prior with Confirm and Reschedule buttons",
                triggerType = "Appointment Scheduled",
                conditions = "Appointment status == Scheduled && timeRemaining <= 24h",
                actions = "Send WhatsApp Template 'appointment_reminder_v2' to patient",
                status = "Active",
                lastExecution = "2026-11-08 09:00 AM",
                nextScheduled = "2026-11-08 01:00 PM",
                successCount = 142,
                failureCount = 1,
                retryCount = 1
            ),
            AutomationWorkflowEntity(
                id = "wf-02",
                name = "Missed Appointment Recovery Follow-Up",
                description = "Dispatches instant re-engagement message when appointment is marked No-Show",
                triggerType = "Appointment Marked NoShow",
                conditions = "Status == NoShow",
                actions = "Send WhatsApp rebooking link + Create urgent staff follow-up task",
                status = "Active",
                lastExecution = "2026-11-08 03:35 PM",
                nextScheduled = "Event Driven",
                successCount = 28,
                failureCount = 0,
                retryCount = 0
            ),
            AutomationWorkflowEntity(
                id = "wf-03",
                name = "Post-Visit Google Review & Feedback Booster",
                description = "Sends polite review invitation 2 hours after appointment marked Completed",
                triggerType = "Appointment Completed",
                conditions = "Status == Completed && delay = 2 hours",
                actions = "Send review link via WhatsApp with 1-tap rating",
                status = "Active",
                lastExecution = "2026-11-08 01:15 PM",
                nextScheduled = "2026-11-08 05:30 PM",
                successCount = 94,
                failureCount = 2,
                retryCount = 2
            ),
            AutomationWorkflowEntity(
                id = "wf-04",
                name = "Outstanding Invoice Payment Reminder",
                description = "Alerts clinic or patient 3 days before invoice due date with UPI/Razorpay link",
                triggerType = "Invoice Due in 3 Days",
                conditions = "Invoice status == Unpaid && dueDate - now <= 3 days",
                actions = "Send payment link SMS/WhatsApp & notification",
                status = "Active",
                lastExecution = "2026-11-07 10:00 AM",
                nextScheduled = "2026-11-09 10:00 AM",
                successCount = 56,
                failureCount = 0,
                retryCount = 0
            ),
            AutomationWorkflowEntity(
                id = "wf-05",
                name = "SaaS Subscription Renewal Alert",
                description = "Notifies Super Admin and Clinic Admin 7 days before monthly renewal",
                triggerType = "Subscription Expiring",
                conditions = "Subscription renewalDate <= 7 days",
                actions = "Send renewal invoice + WhatsApp alert to clinic owner",
                status = "Active",
                lastExecution = "2026-11-07 08:00 AM",
                nextScheduled = "2026-11-08 08:00 AM",
                successCount = 18,
                failureCount = 0,
                retryCount = 0
            ),
            AutomationWorkflowEntity(
                id = "wf-06",
                name = "Failed Integration Health Check Alert",
                description = "Monitors API token expiry and webhook errors across WhatsApp & Razorpay",
                triggerType = "Webhook Error Detected",
                conditions = "Integration status == Error || consecutiveFailures >= 3",
                actions = "Create urgent ticket + SMS notification to Super Admin",
                status = "Active",
                lastExecution = "2026-11-07 11:30 PM",
                nextScheduled = "Every 1 hour",
                successCount = 312,
                failureCount = 0,
                retryCount = 0
            )
        )
        workflows.forEach { dao.insertWorkflow(it) }

        // 8. Recent Executions
        val executions = listOf(
            AutomationExecutionEntity(
                id = "exec-01",
                workflowId = "wf-01",
                workflowName = "24-Hour Pre-Appointment WhatsApp Reminder",
                clinicId = "clinic-001",
                status = "Success",
                triggerDetail = "Triggered for Rahul Nair (apt-01)",
                executedAt = System.currentTimeMillis() - 7200000L
            ),
            AutomationExecutionEntity(
                id = "exec-02",
                workflowId = "wf-02",
                workflowName = "Missed Appointment Recovery Follow-Up",
                clinicId = "clinic-002",
                status = "Success",
                triggerDetail = "Triggered for Arjun Patel (apt-05 marked NoShow)",
                executedAt = System.currentTimeMillis() - 14400000L
            ),
            AutomationExecutionEntity(
                id = "exec-03",
                workflowId = "wf-03",
                workflowName = "Post-Visit Google Review & Feedback Booster",
                clinicId = "clinic-002",
                status = "Failed",
                triggerDetail = "Triggered for Kavita Sharma (apt-04)",
                executedAt = System.currentTimeMillis() - 28800000L,
                errorMessage = "WhatsApp Cloud API: Rate limit temporarily throttled (Code 130429)"
            ),
            AutomationExecutionEntity(
                id = "exec-04",
                workflowId = "wf-03",
                workflowName = "Post-Visit Google Review & Feedback Booster",
                clinicId = "clinic-002",
                status = "Retried",
                triggerDetail = "Exponential backoff retry #1 succeeded for Kavita Sharma",
                executedAt = System.currentTimeMillis() - 25200000L
            )
        )
        executions.forEach { dao.insertExecution(it) }

        // 9. Message Jobs (WhatsApp Queue)
        val messageJobs = listOf(
            MessageJobEntity(
                id = "msg-01",
                clinicId = "clinic-001",
                recipientName = "Rahul Nair",
                recipientPhone = "+91 98451 22334",
                channel = "WhatsApp",
                templateName = "appointment_reminder_v2",
                content = "Hi Rahul! Your appointment with Dr. Rohan Shenoy at Apex Dental is scheduled for tomorrow at 10:30 AM. Reply 1 to Confirm or 2 to Reschedule.",
                status = "Delivered",
                sentAt = System.currentTimeMillis() - 7200000L
            ),
            MessageJobEntity(
                id = "msg-02",
                clinicId = "clinic-001",
                recipientName = "Deepa Krishnan",
                recipientPhone = "+91 97412 55667",
                channel = "WhatsApp",
                templateName = "appointment_reminder_v2",
                content = "Hi Deepa! Friendly reminder for your appointment with Dr. Rohan Shenoy tomorrow at 11:45 AM. Looking forward to seeing you!",
                status = "Delivered",
                sentAt = System.currentTimeMillis() - 6500000L
            ),
            MessageJobEntity(
                id = "msg-03",
                clinicId = "clinic-002",
                recipientName = "Arjun Patel",
                recipientPhone = "+91 94481 99008",
                channel = "WhatsApp",
                templateName = "missed_rebooking",
                content = "Hello Arjun, we missed you today at LifeCare Clinic. We hope everything is well! Click here to easily pick a new time: clinispire.online/b/lifecare",
                status = "Sent",
                sentAt = System.currentTimeMillis() - 14000000L
            )
        )
        messageJobs.forEach { dao.insertMessageJob(it) }

        // 10. Subscriptions (MRR Data)
        val subscriptions = listOf(
            SubscriptionEntity(
                id = "sub-01",
                clinicId = "clinic-001",
                clinicName = "Apex Dental & Orthodontics",
                planName = "Pro",
                monthlyPrice = 4999.0,
                billingCycle = "Monthly",
                startDate = "2026-01-15",
                renewalDate = "2026-11-15",
                status = "Active"
            ),
            SubscriptionEntity(
                id = "sub-02",
                clinicId = "clinic-002",
                clinicName = "LifeCare Multispeciality Clinic",
                planName = "Growth",
                monthlyPrice = 2999.0,
                billingCycle = "Monthly",
                startDate = "2026-03-01",
                renewalDate = "2026-12-01",
                status = "Active"
            ),
            SubscriptionEntity(
                id = "sub-03",
                clinicId = "clinic-003",
                clinicName = "Nova Aesthetics & Skin Studio",
                planName = "Growth",
                monthlyPrice = 2999.0,
                billingCycle = "Monthly",
                startDate = "2026-10-25",
                renewalDate = "2026-11-09",
                status = "Trial"
            ),
            SubscriptionEntity(
                id = "sub-04",
                clinicId = "clinic-004",
                clinicName = "MedPlus Family Healthcare",
                planName = "Starter",
                monthlyPrice = 1499.0,
                billingCycle = "Monthly",
                startDate = "2026-04-10",
                renewalDate = "2026-11-10",
                status = "Active"
            ),
            SubscriptionEntity(
                id = "sub-05",
                clinicId = "clinic-005",
                clinicName = "CareFirst Pediatric Center",
                planName = "Starter",
                monthlyPrice = 1499.0,
                billingCycle = "Monthly",
                startDate = "2026-02-18",
                renewalDate = "2026-10-18",
                status = "Cancelled"
            )
        )
        subscriptions.forEach { dao.insertSubscription(it) }

        // 11. Invoices
        val invoices = listOf(
            InvoiceEntity(
                id = "inv-101",
                invoiceNumber = "CLIN-2026-092",
                clinicId = "clinic-001",
                clinicName = "Apex Dental & Orthodontics",
                subtotal = 4999.0,
                tax = 899.82,
                totalAmount = 5898.82,
                dueDate = "2026-11-15",
                status = "Paid",
                paidAt = "2026-11-05",
                description = "Clinispire Pro Plan - WhatsApp Automation & Unlimited Appointments"
            ),
            InvoiceEntity(
                id = "inv-102",
                invoiceNumber = "CLIN-2026-093",
                clinicId = "clinic-002",
                clinicName = "LifeCare Multispeciality Clinic",
                subtotal = 2999.0,
                tax = 539.82,
                totalAmount = 3538.82,
                dueDate = "2026-12-01",
                status = "Paid",
                paidAt = "2026-11-01",
                description = "Clinispire Growth Plan - Multi-Doctor & Review Engine"
            ),
            InvoiceEntity(
                id = "inv-103",
                invoiceNumber = "CLIN-2026-094",
                clinicId = "clinic-004",
                clinicName = "MedPlus Family Healthcare",
                subtotal = 1499.0,
                tax = 269.82,
                totalAmount = 1768.82,
                dueDate = "2026-11-10",
                status = "Unpaid",
                paidAt = null,
                description = "Clinispire Starter Plan - Core Scheduling"
            ),
            InvoiceEntity(
                id = "inv-104",
                invoiceNumber = "CLIN-2026-088",
                clinicId = "clinic-005",
                clinicName = "CareFirst Pediatric Center",
                subtotal = 1499.0,
                tax = 269.82,
                totalAmount = 1768.82,
                dueDate = "2026-10-18",
                status = "Overdue",
                paidAt = null,
                description = "Clinispire Starter Plan - October Service"
            )
        )
        invoices.forEach { dao.insertInvoice(it) }

        // 12. Staff Tasks
        val staffTasks = listOf(
            StaffTaskEntity(
                id = "task-01",
                title = "Conduct Aura Dermatology Demo",
                description = "Live demo for Dr. Tanvi on Saturday at 4 PM covering multi-doctor scheduling.",
                assignedToRole = "Business Manager",
                assignedToName = "Pooja Hegde",
                dueDate = "2026-11-08",
                priority = "High",
                status = "In Progress",
                relatedEntityId = "lead-102"
            ),
            StaffTaskEntity(
                id = "task-02",
                title = "Follow-up SmileCraft Proposal",
                description = "Call Dr. Nikhil regarding the ₹4,999 Pro plan proposal sent yesterday.",
                assignedToRole = "Sales Executive",
                assignedToName = "Vikram Shenoy",
                dueDate = "2026-11-10",
                priority = "High",
                status = "Pending",
                relatedEntityId = "lead-101"
            ),
            StaffTaskEntity(
                id = "task-03",
                title = "Configure WhatsApp Webhook for Nova Aesthetics",
                description = "Verify Meta Business ID and link authorized WhatsApp Cloud API phone number.",
                assignedToRole = "Operations Manager",
                assignedToName = "Karthik Raj",
                dueDate = "2026-11-09",
                priority = "Medium",
                status = "Pending",
                relatedEntityId = "clinic-003"
            ),
            StaffTaskEntity(
                id = "task-04",
                title = "Resolve MedPlus Payment Gateway Query",
                description = "Assist MedPlus admin with Razorpay automated payment webhook test.",
                assignedToRole = "Support Agent",
                assignedToName = "Neha Rao",
                dueDate = "2026-11-08",
                priority = "Urgent",
                status = "In Progress",
                relatedEntityId = "clinic-004"
            )
        )
        staffTasks.forEach { dao.insertStaffTask(it) }

        // 13. Support Tickets
        val tickets = listOf(
            SupportTicketEntity(
                id = "tkt-01",
                ticketNumber = "TICK-2026-041",
                clinicId = "clinic-004",
                clinicName = "MedPlus Family Healthcare",
                subject = "Razorpay Webhook signature verification failing in test mode",
                description = "When running test payment webhooks, returning 400 Bad Request error.",
                priority = "High",
                category = "Billing & Payment Gateway",
                status = "In Progress",
                assignedStaff = "Neha (Support Agent)",
                resolutionNotes = "Checking webhook secret configuration in Clinispire Integrations panel."
            ),
            SupportTicketEntity(
                id = "tkt-02",
                ticketNumber = "TICK-2026-039",
                clinicId = "clinic-002",
                clinicName = "LifeCare Multispeciality Clinic",
                subject = "Add Dr. Santosh (Orthopedic) to reception appointment calendar",
                description = "Need to add a 3rd practitioner to the slot availability matrix.",
                priority = "Medium",
                category = "Staff & Doctor Setup",
                status = "Open",
                assignedStaff = "Karthik (Operations)",
                resolutionNotes = ""
            ),
            SupportTicketEntity(
                id = "tkt-03",
                ticketNumber = "TICK-2026-035",
                clinicId = "clinic-001",
                clinicName = "Apex Dental & Orthodontics",
                subject = "Request custom Spanish translation for WhatsApp review template",
                description = "Clinic has international dental tourism patients requesting bilingual reminders.",
                priority = "Low",
                category = "WhatsApp Integration",
                status = "Resolved",
                assignedStaff = "Neha (Support Agent)",
                resolutionNotes = "Added multilingual template to Meta WhatsApp Business account."
            )
        )
        tickets.forEach { dao.insertSupportTicket(it) }

        // 14. Integrations
        val integrations = listOf(
            IntegrationEntity(
                id = "int-01",
                serviceKey = "supabase",
                serviceName = "Supabase PostgreSQL & Auth",
                description = "Cloud database, multi-tenant row-level security (RLS), and secure admin auth.",
                category = "Database & Auth",
                status = "Connected and Verified",
                endpointUrl = "https://clinispire-prod.supabase.co",
                lastVerified = "2026-11-08 00:15",
                notes = "All RLS policies active. Tenant isolation verified."
            ),
            IntegrationEntity(
                id = "int-02",
                serviceKey = "whatsapp_cloud",
                serviceName = "WhatsApp Business Platform (Cloud API)",
                description = "Official Meta Cloud API for patient reminders, confirmations, and review dispatches.",
                category = "Communication",
                status = "Connected and Verified",
                endpointUrl = "https://graph.facebook.com/v21.0",
                lastVerified = "2026-11-08 00:20",
                notes = "Meta Verified Business Manager ID: 48920192841"
            ),
            IntegrationEntity(
                id = "int-03",
                serviceKey = "razorpay",
                serviceName = "Razorpay Payment Gateway",
                description = "Server-verified UPI, credit card, and netbanking subscriptions & patient bills.",
                category = "Payments",
                status = "Connected and Verified",
                endpointUrl = "https://api.razorpay.com/v1",
                lastVerified = "2026-11-08 00:10",
                notes = "Webhook verification enabled. Merchant ID: rzp_live_clinispire"
            ),
            IntegrationEntity(
                id = "int-04",
                serviceKey = "google_calendar",
                serviceName = "Google Calendar Sync",
                description = "Two-way doctor schedule synchronization and room conflict prevention.",
                category = "Calendar",
                status = "Configuration Required",
                endpointUrl = "https://www.googleapis.com/calendar/v3",
                lastVerified = "Pending OAuth token",
                notes = "OAuth Consent Screen awaiting Google verification review."
            ),
            IntegrationEntity(
                id = "int-05",
                serviceKey = "email_resend",
                serviceName = "Email Provider (Resend / AWS SES)",
                description = "Transactional email dispatch for invoices, password resets, and audit alerts.",
                category = "Communication",
                status = "Connected and Verified",
                endpointUrl = "https://api.resend.com",
                lastVerified = "2026-11-07 18:40",
                notes = "Domain verified: clinispire.online (DKIM / SPF pass)"
            ),
            IntegrationEntity(
                id = "int-06",
                serviceKey = "fcm",
                serviceName = "Firebase Cloud Messaging (FCM)",
                description = "Real-time push notifications to doctors and clinic front-desk staff.",
                category = "Notifications",
                status = "Connected and Verified",
                endpointUrl = "https://fcm.googleapis.com",
                lastVerified = "2026-11-07 22:00",
                notes = "Android push notification channel active."
            )
        )
        integrations.forEach { dao.insertIntegration(it) }

        // 15. Audit Logs
        val auditLogs = listOf(
            AuditLogEntity(
                id = "log-01",
                userName = "Super Admin (Raseb)",
                organizationName = "Clinispire HQ",
                action = "Workflow Created",
                recordType = "AutomationWorkflow",
                recordId = "wf-06",
                outcome = "Success",
                timestamp = System.currentTimeMillis() - 1800000L
            ),
            AuditLogEntity(
                id = "log-02",
                userName = "Super Admin (Raseb)",
                organizationName = "Clinispire HQ",
                action = "Invoice Generated",
                recordType = "Invoice",
                recordId = "inv-101",
                outcome = "Success",
                timestamp = System.currentTimeMillis() - 7200000L
            ),
            AuditLogEntity(
                id = "log-03",
                userName = "Pooja (Business Mgr)",
                organizationName = "Clinispire HQ",
                action = "Lead Pipeline Advanced",
                recordType = "Lead",
                recordId = "lead-101",
                outcome = "Success",
                timestamp = System.currentTimeMillis() - 14400000L
            ),
            AuditLogEntity(
                id = "log-04",
                userName = "Dr. Rohan Shenoy",
                organizationName = "Apex Dental",
                action = "Appointment Booked",
                recordType = "Appointment",
                recordId = "apt-01",
                outcome = "Success",
                timestamp = System.currentTimeMillis() - 28800000L
            ),
            AuditLogEntity(
                id = "log-05",
                userName = "System Automation Worker",
                organizationName = "LifeCare Clinic",
                action = "Workflow Execution Retry",
                recordType = "AutomationExecution",
                recordId = "exec-04",
                outcome = "Success",
                timestamp = System.currentTimeMillis() - 25200000L
            )
        )
        auditLogs.forEach { dao.insertAuditLog(it) }

        // 16. Brand Settings
        dao.insertBrandSettings(
            BrandSettingsEntity(
                id = 1,
                companyName = "Clinispire",
                tagline = "Inspire. Connect. Learn. Grow.",
                website = "www.clinispire.online",
                instagram = "@clinispire",
                supportEmail = "support@clinispire.online",
                phone = "+91 98765 43210",
                address = "Bengaluru, Karnataka, India",
                primaryColor = "#2563EB",
                deepNavyColor = "#10233F",
                tealColor = "#14B8A6",
                isDemoMode = true
            )
        )
    }
}
