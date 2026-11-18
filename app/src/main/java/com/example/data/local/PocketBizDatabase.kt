package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        CustomerEntity::class,
        InvoiceEntity::class,
        InvoiceItemEntity::class,
        TransactionEntity::class,
        PaymentEntity::class,
        ReceiptEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PocketBizDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun customerDao(): CustomerDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun receiptDao(): ReceiptDao

    companion object {
        @Volatile
        private var instance: PocketBizDatabase? = null

        fun getInstance(context: Context): PocketBizDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    PocketBizDatabase::class.java,
                    "pocketbiz.db"
                ).build().also { instance = it }
            }
        }
    }
}
