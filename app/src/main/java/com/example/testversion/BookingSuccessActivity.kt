package com.example.testversion

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import android.content.ContentValues
import android.provider.MediaStore
import android.os.Build
import android.graphics.Bitmap
import android.widget.ImageButton

class BookingSuccessActivity : AppCompatActivity() {
    private var bookingNumber: Long = -1L
    private var totalPrice: Double = 0.0
    private var userName: String = ""
    private var userPhone: String = ""
    private var userEmail: String = ""
    private var userIc: String = ""
    private var branchName: String = ""
    private var roomType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_success)

        bookingNumber = intent.getLongExtra("bookingNumber", -1L)
        totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        userName = intent.getStringExtra("userName") ?: ""
        userPhone = intent.getStringExtra("userPhone") ?: ""
        userEmail = intent.getStringExtra("userEmail") ?: ""
        userIc = intent.getStringExtra("userPassport") ?: ""
        branchName = intent.getStringExtra("branchName") ?: ""
        roomType = intent.getStringExtra("roomType") ?: ""

        findViewById<TextView>(R.id.bookingNumberTextView).text = " $bookingNumber"
        findViewById<TextView>(R.id.totalPriceTextView).text = "Total: RM %.2f".format(totalPrice)

        findViewById<Button>(R.id.homeButton).setOnClickListener {
            startActivity(Intent(this, BranchOverview::class.java))
            finish()
        }
        findViewById<ImageButton>(R.id.downloadReceiptButton)?.setOnClickListener {
        generateReceiptPdf()
        }
    }

    private fun generateReceiptPdf() {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val pageInfo = PdfDocument.PageInfo.Builder(400, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas: Canvas = page.canvas
        val centerX = pageInfo.pageWidth / 2

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.v2resit)
        val desiredHeight = 120
        val scaledBitmap = Bitmap.createScaledBitmap(
            bitmap,
            pageInfo.pageWidth,
            desiredHeight,
            false
        )

        canvas.drawBitmap(scaledBitmap, 0f, 0f, null)

        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 22f
        paint.isFakeBoldText = true
        canvas.drawText("Booking Receipt", centerX.toFloat(), 150f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 1f
        canvas.drawLine(40f, 170f, 360f, 170f, paint)


        paint.textSize = 14f
        paint.isFakeBoldText = false

        var y = 200f
        canvas.drawText("Booking Number: $bookingNumber", 50f, y, paint)
        y += 30
        canvas.drawText("Total Price: RM %.2f".format(totalPrice), 50f, y, paint)

        y += 30
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("User Details:", 50f, y, paint)

        paint.isFakeBoldText = false
        y += 25
        canvas.drawText("Name: $userName", 50f, y, paint)
        y += 20
        canvas.drawText("Phone: $userPhone", 50f, y, paint)
        y += 20
        canvas.drawText("Email: $userEmail", 50f, y, paint)
        y += 20
        canvas.drawText("IC Number: $userIc", 50f, y, paint)

        y += 30
        paint.isFakeBoldText = true
        canvas.drawText("Booking Details:", 50f, y, paint)

        paint.isFakeBoldText = false
        y += 25
        canvas.drawText("Branch: $branchName", 50f, y, paint)
        y += 20
        canvas.drawText("Room Type: $roomType", 50f, y, paint)

        paint.textAlign = Paint.Align.CENTER
        y += 80
        paint.textSize = 16f
        paint.isFakeBoldText = true
        canvas.drawText("Thank you for booking with us!", centerX.toFloat(), y, paint)

        pdfDocument.finishPage(page)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, "booking_receipt_$bookingNumber.pdf")
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.RELATIVE_PATH, "Download/")
                }

                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    contentResolver.openOutputStream(it).use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                    Toast.makeText(this, "Receipt saved to Downloads", Toast.LENGTH_LONG).show()
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, "booking_receipt_$bookingNumber.pdf")
                pdfDocument.writeTo(FileOutputStream(file))
                Toast.makeText(this, "Receipt saved to Downloads", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to save PDF: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
    }


}

