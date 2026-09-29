package com.smartfarmer.procurement.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.smartfarmer.procurement.data.models.Receipt
import java.io.File
import java.io.FileOutputStream

object PdfReceiptGenerator {

    /**
     * Generates a structured PDF document for the given procurement receipt.
     */
    fun generateReceiptPdf(context: Context, receipt: Receipt): File? {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 standard dimensions
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val titlePaint = Paint().apply {
                color = Color.rgb(46, 125, 50)
                textSize = 20f
                isFakeBoldText = true
            }

            val subtitlePaint = Paint().apply {
                color = Color.DKGRAY
                textSize = 12f
            }

            val headerPaint = Paint().apply {
                color = Color.BLACK
                textSize = 14f
                isFakeBoldText = true
            }

            val bodyPaint = Paint().apply {
                color = Color.rgb(33, 33, 33)
                textSize = 12f
            }

            val linePaint = Paint().apply {
                color = Color.LTGRAY
                strokeWidth = 1.5f
            }

            var y = 50f

            // Header
            canvas.drawText("SMART FARMER PROCUREMENT SYSTEM", 40f, y, titlePaint)
            y += 20f
            canvas.drawText("Government of India / Agriculture & Food Civil Supplies", 40f, y, subtitlePaint)
            y += 15f
            canvas.drawLine(40f, y, 555f, y, linePaint)
            y += 30f

            // Receipt Info
            canvas.drawText("DIGITAL PROCUREMENT RECEIPT", 40f, y, headerPaint)
            y += 25f
            canvas.drawText("Receipt No: ${receipt.receiptNumber}", 40f, y, bodyPaint)
            canvas.drawText("Date: ${receipt.dateFormatted}", 350f, y, bodyPaint)
            y += 20f
            canvas.drawText("Booking ID: ${receipt.bookingId}", 40f, y, bodyPaint)
            canvas.drawText("Token No: ${receipt.tokenNumber}", 350f, y, bodyPaint)
            y += 25f
            canvas.drawLine(40f, y, 555f, y, linePaint)
            y += 30f

            // Farmer & Centre Details
            canvas.drawText("Farmer Details", 40f, y, headerPaint)
            y += 20f
            canvas.drawText("Name: ${receipt.farmerName}", 40f, y, bodyPaint)
            canvas.drawText("Farmer ID: ${receipt.farmerId}", 350f, y, bodyPaint)
            y += 20f
            canvas.drawText("Centre: ${receipt.centerName}", 40f, y, bodyPaint)
            y += 25f
            canvas.drawLine(40f, y, 555f, y, linePaint)
            y += 30f

            // Procurement Breakdown
            canvas.drawText("Produce Particulars", 40f, y, headerPaint)
            y += 25f
            canvas.drawText("Crop: ${receipt.cropName}", 40f, y, bodyPaint)
            y += 20f
            canvas.drawText("Quality Grade: ${receipt.qualityGrade}", 40f, y, bodyPaint)
            y += 20f
            canvas.drawText("Approved Net Quantity: ${receipt.netQuantityQuintals} Quintals", 40f, y, bodyPaint)
            y += 20f
            canvas.drawText("MSP Rate / Quintal: ₹${receipt.ratePerQuintal}", 40f, y, bodyPaint)
            y += 25f
            canvas.drawLine(40f, y, 555f, y, linePaint)
            y += 35f

            // Total Amount
            val amountPaint = Paint().apply {
                color = Color.rgb(27, 94, 32)
                textSize = 16f
                isFakeBoldText = true
            }
            canvas.drawText("TOTAL PAYABLE AMOUNT: ₹${receipt.totalAmount}", 40f, y, amountPaint)
            y += 25f
            canvas.drawText("Payment Status: ${receipt.paymentStatus.name}", 40f, y, bodyPaint)
            y += 20f
            canvas.drawText("Transaction Reference: ${receipt.transactionRef}", 40f, y, bodyPaint)
            y += 40f

            // Footer note
            val footerPaint = Paint().apply {
                color = Color.GRAY
                textSize = 10f
            }
            canvas.drawText("This is an electronically generated official procurement receipt.", 40f, y, footerPaint)
            y += 15f
            canvas.drawText("Direct Benefit Transfer (DBT) will be credited within 24-48 working hours.", 40f, y, footerPaint)

            pdfDocument.finishPage(page)

            val dir = File(context.cacheDir, "receipts").apply { if (!exists()) mkdirs() }
            val file = File(dir, "${receipt.receiptNumber}.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Shares the PDF receipt via Android share intent.
     */
    fun shareReceipt(context: Context, pdfFile: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Receipt"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
