package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY transactionDateEpochMs DESC, id DESC")
    fun observeAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY transactionDateEpochMs DESC, id DESC LIMIT :limit")
    fun observeRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT COUNT(*) FROM transactions")
    fun observeTransactionCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun observeAllCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT COUNT(*) FROM customers")
    fun observeCustomerCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    @Query("DELETE FROM customers WHERE id = :id")
    suspend fun deleteCustomerById(id: Long)
}

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices ORDER BY issueDateEpochMs DESC, id DESC")
    fun observeAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT COUNT(*) FROM invoices")
    fun observeInvoiceCount(): Flow<Int>

    @Query(
        """
        SELECT COALESCE(SUM(
            CASE WHEN totalMinor > paidAmountMinor THEN totalMinor - paidAmountMinor ELSE 0 END
        ), 0) FROM invoices
        WHERE status != 'DRAFT'
        """
    )
    fun observeOutstandingBalanceMinor(): Flow<Long>

    @Query("SELECT * FROM invoice_items ORDER BY id ASC")
    fun observeAllInvoiceItems(): Flow<List<InvoiceItemEntity>>

    @Query("SELECT * FROM payments ORDER BY paymentDateEpochMs DESC, id DESC")
    fun observeAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    suspend fun getInvoiceById(id: Long): InvoiceEntity?

    @Query("DELETE FROM invoices WHERE id = :id")
    suspend fun deleteInvoiceById(id: Long)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertInvoice(invoice: InvoiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertInvoiceItems(items: List<InvoiceItemEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Update
    suspend fun updateInvoice(invoice: InvoiceEntity)
}

@Dao
interface ReceiptDao {
    @Query("SELECT * FROM receipts ORDER BY capturedAtEpochMs DESC")
    fun observeAllReceipts(): Flow<List<ReceiptEntity>>

    @Query("SELECT COUNT(*) FROM receipts")
    fun observeReceiptCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertReceipt(receipt: ReceiptEntity): Long
}
