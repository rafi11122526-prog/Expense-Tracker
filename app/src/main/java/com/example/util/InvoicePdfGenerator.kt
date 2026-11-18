package com.example.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.core.content.FileProvider
import com.example.data.local.CustomerEntity
import com.example.data.local.InvoiceEntity
import com.example.data.local.InvoiceItemEntity
import com.example.domain.calculator.FinancialCalculator
import com.example.domain.model.BusinessSettings
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InvoicePdfGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN_X = 40f

    fun generateInvoicePdf(
        context: Context,
        invoice: InvoiceEntity,
        customer: CustomerEntity?,
        items: List<InvoiceItemEntity>,
        settings: BusinessSettings
    ): Result<File> = runCatching {
        val outDir = File(context.cacheDir, "invoices").apply { mkdirs() }
        val safeName = invoice.invoiceNumber.replace(Regex("[^A-Za-z0-9_-]"), "_")
        val outFile = File(outDir, "$safeName.pdf")

        val pdf = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdf.startPage(pageInfo)
        drawInvoicePage(
            canvas = page.canvas,
            invoice = invoice,
            customer = customer,
            items = items,
            settings = settings
        )
        pdf.finishPage(page)

        FileOutputStream(outFile).use { stream ->
            pdf.writeTo(stream)
        }
        pdf.close()
        outFile
    }

    fun shareInvoicePdf(
        context: Context,
        pdfFile: File,
        invoiceNumber: String
    ): Result<Unit> = runCatching {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Invoice $invoiceNumber")
            putExtra(Intent.EXTRA_TEXT, "Please find attached invoice $invoiceNumber.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, "Share Invoice $invoiceNumber").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
            throw IllegalStateException("No app available to share PDF files.", e)
        }
    }

    fun printInvoicePdf(
        context: Context,
        pdfFile: File,
        invoiceNumber: String
    ): Result<Unit> = runCatching {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            ?: throw IllegalStateException("Printing service is unavailable on this device.")
        val jobName = "PocketBiz Invoice $invoiceNumber"
        val adapter = object : PrintDocumentAdapter() {
            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback.onLayoutCancelled()
                    return
                }
                val info = PrintDocumentInfo.Builder("$invoiceNumber.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()
                callback.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback
            ) {
                if (destination == null) {
                    callback.onWriteFailed("Invalid print destination.")
                    return
                }
                try {
                    FileInputStream(pdfFile).use { input ->
                        FileOutputStream(destination.fileDescriptor).use { output ->
                            val buffer = ByteArray(8192)
                            var bytesRead: Int
                            while (input.read(buffer).also { bytesRead = it } > 0) {
                                if (cancellationSignal?.isCanceled == true) {
                                    callback.onWriteCancelled()
                                    return
                                }
                                output.write(buffer, 0, bytesRead)
                            }
                        }
                    }
                    callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback.onWriteFailed(e.message ?: "Unable to print PDF.")
                }
            }
        }
        printManager.print(jobName, adapter, PrintAttributes.Builder().build())
    }

    private fun drawInvoicePage(
        canvas: Canvas,
        invoice: InvoiceEntity,
        customer: CustomerEntity?,
        items: List<InvoiceItemEntity>,
        settings: BusinessSettings
    ) {
        val navyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(15, 35, 71)
        }
        val emeraldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(5, 150, 105)
        }
        val softBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(241, 245, 249)
        }
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(226, 232, 240)
            strokeWidth = 1f
        }
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val headerSubPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(203, 213, 225)
            textSize = 10f
        }
        val sectionLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(100, 116, 139)
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(15, 23, 42)
            textSize = 11f
        }
        val bodyBoldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(15, 23, 42)
            textSize = 11.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val rightBodyPaint = Paint(bodyPaint).apply {
            textAlign = Paint.Align.RIGHT
        }
        val rightBoldPaint = Paint(bodyBoldPaint).apply {
            textAlign = Paint.Align.RIGHT
        }

        val currency = settings.currencyCode
        val dateFormatter = runCatching {
            SimpleDateFormat(settings.dateFormatPattern, Locale.getDefault())
        }.getOrElse {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        }

        // Top Navy Header Band
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 110f, navyPaint)
        canvas.drawRect(0f, 110f, PAGE_WIDTH.toFloat(), 114f, emeraldPaint)

        canvas.drawText(settings.businessName.ifBlank { "PocketBiz" }, MARGIN_X, 46f, titlePaint)
        var businessMetaY = 66f
        val businessMeta = listOf(settings.businessPhone, settings.businessEmail, settings.businessAddress)
            .filter { it.isNotBlank() }
            .joinToString("  •  ")
        if (businessMeta.isNotBlank()) {
            canvas.drawText(ellipsize(businessMeta, headerSubPaint, 320f), MARGIN_X, businessMetaY, headerSubPaint)
            businessMetaY += 14f
        }

        val invoiceHeadingPaint = Paint(titlePaint).apply {
            textAlign = Paint.Align.RIGHT
            textSize = 20f
        }
        val invoiceNumPaint = Paint(headerSubPaint).apply {
            textAlign = Paint.Align.RIGHT
            color = Color.WHITE
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("INVOICE", PAGE_WIDTH - MARGIN_X, 44f, invoiceHeadingPaint)
        canvas.drawText(invoice.invoiceNumber, PAGE_WIDTH - MARGIN_X, 64f, invoiceNumPaint)
        canvas.drawText(
            "Status: ${invoice.status.name}",
            PAGE_WIDTH - MARGIN_X,
            82f,
            Paint(headerSubPaint).apply { textAlign = Paint.Align.RIGHT }
        )

        var y = 146f

        // Bill To & Invoice Metadata Columns
        canvas.drawText("BILL TO", MARGIN_X, y, sectionLabelPaint)
        canvas.drawText("INVOICE DETAILS", PAGE_WIDTH - MARGIN_X - 180f, y, sectionLabelPaint)
        y += 18f

        canvas.drawText(customer?.name ?: "Customer", MARGIN_X, y, bodyBoldPaint)
        canvas.drawText("Issue Date:", PAGE_WIDTH - MARGIN_X - 180f, y, bodyPaint)
        canvas.drawText(dateFormatter.format(Date(invoice.issueDateEpochMs)), PAGE_WIDTH - MARGIN_X, y, rightBodyPaint)
        y += 16f

        if (!customer?.phone.isNullOrBlank()) {
            canvas.drawText(customer!!.phone, MARGIN_X, y, bodyPaint)
        }
        canvas.drawText("Due Date:", PAGE_WIDTH - MARGIN_X - 180f, y, bodyPaint)
        canvas.drawText(dateFormatter.format(Date(invoice.dueDateEpochMs)), PAGE_WIDTH - MARGIN_X, y, rightBodyPaint)
        y += 16f

        if (!customer?.email.isNullOrBlank()) {
            canvas.drawText(customer!!.email, MARGIN_X, y, bodyPaint)
            y += 16f
        }
        if (!customer?.address.isNullOrBlank()) {
            canvas.drawText(ellipsize(customer!!.address, bodyPaint, 260f), MARGIN_X, y, bodyPaint)
            y += 16f
        }

        y += 18f

        // Table Header
        val tableRight = PAGE_WIDTH - MARGIN_X
        canvas.drawRoundRect(MARGIN_X, y, tableRight, y + 28f, 6f, 6f, softBgPaint)
        val headerTextY = y + 18f
        canvas.drawText("DESCRIPTION", MARGIN_X + 10f, headerTextY, sectionLabelPaint)
        canvas.drawText("QTY", MARGIN_X + 295f, headerTextY, Paint(sectionLabelPaint).apply { textAlign = Paint.Align.RIGHT })
        canvas.drawText("UNIT PRICE", MARGIN_X + 395f, headerTextY, Paint(sectionLabelPaint).apply { textAlign = Paint.Align.RIGHT })
        canvas.drawText("AMOUNT", tableRight - 10f, headerTextY, Paint(sectionLabelPaint).apply { textAlign = Paint.Align.RIGHT })
        y += 40f

        val maxItemsToDraw = 18
        items.take(maxItemsToDraw).forEach { item ->
            canvas.drawText(ellipsize(item.description, bodyPaint, 235f), MARGIN_X + 10f, y, bodyPaint)
            canvas.drawText(FinancialCalculator.formatQuantity(item.quantityMilli), MARGIN_X + 295f, y, rightBodyPaint)
            canvas.drawText(FinancialCalculator.formatCurrency(item.unitPriceMinor, currency), MARGIN_X + 395f, y, rightBodyPaint)
            canvas.drawText(FinancialCalculator.formatCurrency(item.lineTotalMinor, currency), tableRight - 10f, y, rightBoldPaint)
            y += 10f
            canvas.drawLine(MARGIN_X, y, tableRight, y, linePaint)
            y += 18f
        }

        if (items.size > maxItemsToDraw) {
            canvas.drawText("... +${items.size - maxItemsToDraw} additional item(s)", MARGIN_X + 10f, y, sectionLabelPaint)
            y += 20f
        }

        y += 10f

        // Totals Summary Box
        val summaryLeft = PAGE_WIDTH - MARGIN_X - 230f
        fun drawTotalLine(label: String, value: String, bold: Boolean = false, accentColor: Int? = null) {
            val lp = if (bold) bodyBoldPaint else bodyPaint
            val vp = Paint(if (bold) rightBoldPaint else rightBodyPaint).apply {
                if (accentColor != null) color = accentColor
            }
            canvas.drawText(label, summaryLeft, y, lp)
            canvas.drawText(value, tableRight - 10f, y, vp)
            y += 18f
        }

        drawTotalLine("Subtotal", FinancialCalculator.formatCurrency(invoice.subtotalMinor, currency))
        if (invoice.discountMinor > 0L) {
            drawTotalLine("Discount", "-${FinancialCalculator.formatCurrency(invoice.discountMinor, currency)}")
        }
        if (invoice.taxAmountMinor > 0L || invoice.taxRateBasisPoints > 0) {
            val taxPct = FinancialCalculator.minorUnitsToBigDecimal(invoice.taxRateBasisPoints.toLong()).stripTrailingZeros().toPlainString()
            drawTotalLine("Tax ($taxPct%)", FinancialCalculator.formatCurrency(invoice.taxAmountMinor, currency))
        }
        canvas.drawLine(summaryLeft, y - 6f, tableRight, y - 6f, linePaint)
        y += 8f
        drawTotalLine("Total", FinancialCalculator.formatCurrency(invoice.totalMinor, currency), bold = true)
        drawTotalLine("Paid", FinancialCalculator.formatCurrency(invoice.paidAmountMinor, currency))

        val balance = (invoice.totalMinor - invoice.paidAmountMinor).coerceAtLeast(0L)
        canvas.drawRoundRect(summaryLeft - 10f, y - 6f, tableRight, y + 20f, 6f, 6f, softBgPaint)
        y += 11f
        drawTotalLine(
            "Balance Due",
            FinancialCalculator.formatCurrency(balance, currency),
            bold = true,
            accentColor = if (balance == 0L) Color.rgb(5, 150, 105) else Color.rgb(180, 83, 9)
        )

        if (invoice.notes.isNotBlank() && y < PAGE_HEIGHT - 80f) {
            y += 20f
            canvas.drawText("NOTES", MARGIN_X, y, sectionLabelPaint)
            y += 15f
            canvas.drawText(ellipsize(invoice.notes, bodyPaint, PAGE_WIDTH - 2 * MARGIN_X), MARGIN_X, y, bodyPaint)
        }

        canvas.drawLine(MARGIN_X, PAGE_HEIGHT - 54f, tableRight, PAGE_HEIGHT - 54f, linePaint)
        canvas.drawText(
            "Generated with ${settings.businessName.ifBlank { "PocketBiz" }}",
            MARGIN_X,
            PAGE_HEIGHT - 34f,
            sectionLabelPaint
        )
    }

    private fun ellipsize(text: String, paint: Paint, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        val suffix = "..."
        var end = text.length
        while (end > 0 && paint.measureText(text.substring(0, end) + suffix) > maxWidth) {
            end--
        }
        return if (end <= 0) suffix else text.substring(0, end) + suffix
    }
}
