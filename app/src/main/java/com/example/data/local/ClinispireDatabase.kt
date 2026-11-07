package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ClinicEntity::class,
        LeadEntity::class,
        CrmActivityEntity::class,
        AppointmentEntity::class,
        FollowUpEntity::class,
        ReviewRequestEntity::class,
        AutomationWorkflowEntity::class,
        AutomationExecutionEntity::class,
        MessageJobEntity::class,
        SubscriptionEntity::class,
        InvoiceEntity::class,
        StaffTaskEntity::class,
        SupportTicketEntity::class,
        IntegrationEntity::class,
        AuditLogEntity::class,
        BrandSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ClinispireDatabase : RoomDatabase() {
    abstract fun clinispireDao(): ClinispireDao

    companion object {
        @Volatile
        private var INSTANCE: ClinispireDatabase? = null

        fun getDatabase(context: Context): ClinispireDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClinispireDatabase::class.java,
                    "clinispire_os.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    DemoDataSeeder.seedAll(database.clinispireDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
