package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.model.InvoiceStatus
import com.example.domain.model.SyncStatus
import com.example.domain.model.TransactionType
import java.util.UUID

@Entity(
    tableName = "customers",
    indices = [
        Index(value = ["clientUuid"], unique = true),
        Index(value = ["name"])
    ]
)
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val clientUuid: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val notes: String = "",
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.LOCAL_ONLY,
    val remoteId: String? = null
)

@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["clientUuid"], unique = true),
        Index(value = ["invoiceNumber"], unique = true),
        Index(value = ["customerId"])
    ]
)
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val clientUuid: String = UUID.randomUUID().toString(),
    val invoiceNumber: String,
    val customerId: Long,
    val issueDateEpochMs: Long,
    val dueDateEpochMs: Long,
    val subtotalMinor: Long,
    val discountMinor: Long = 0L,
    val taxRateBasisPoints: Int = 0,
    val taxAmountMinor: Long = 0L,
    val totalMinor: Long,
    val paidAmountMinor: Long = 0L,
    val status: InvoiceStatus = InvoiceStatus.DRAFT,
    val notes: String = "",
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.LOCAL_ONLY,
    val remoteId: String? = null
)

@Entity(
    tableName = "invoice_items",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["invoiceId"])]
)
data class InvoiceItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val invoiceId: Long,
    val description: String,
    val quantityMilli: Long,
    val unitPriceMinor: Long,
    val lineTotalMinor: Long
)

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["clientUuid"], unique = true),
        Index(value = ["transactionDateEpochMs"]),
        Index(value = ["customerId"]),
        Index(value = ["invoiceId"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val clientUuid: String = UUID.randomUUID().toString(),
    val type: TransactionType,
    val category: String,
    val amountMinor: Long,
    val currencyCode: String,
    val description: String = "",
    val transactionDateEpochMs: Long = System.currentTimeMillis(),
    val customerId: Long? = null,
    val invoiceId: Long? = null,
    val receiptUri: String? = null,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val updatedAtEpochMs: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.LOCAL_ONLY,
    val remoteId: String? = null
)

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["clientUuid"], unique = true),
        Index(value = ["invoiceId"]),
        Index(value = ["customerId"])
    ]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val clientUuid: String = UUID.randomUUID().toString(),
    val invoiceId: Long,
    val customerId: Long,
    val amountMinor: Long,
    val paymentDateEpochMs: Long = System.currentTimeMillis(),
    val method: String = "Cash",
    val reference: String = "",
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.LOCAL_ONLY,
    val remoteId: String? = null
)

@Entity(
    tableName = "receipts",
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["clientUuid"], unique = true),
        Index(value = ["transactionId"])
    ]
)
data class ReceiptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val clientUuid: String = UUID.randomUUID().toString(),
    val transactionId: Long? = null,
    val imagePath: String,
    val title: String = "",
    val capturedAtEpochMs: Long = System.currentTimeMillis(),
    val notes: String = ""
)
