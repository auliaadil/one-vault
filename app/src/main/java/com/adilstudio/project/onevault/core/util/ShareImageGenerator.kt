package com.adilstudio.project.onevault.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import com.adilstudio.project.onevault.domain.model.Currency
import com.adilstudio.project.onevault.domain.model.SplitItem
import com.adilstudio.project.onevault.domain.model.SplitParticipant
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShareImageGenerator(private val context: Context) {

    private val backgroundColor = Color.WHITE
    private val primaryColor = "#BF9000".toColorInt()
    private val textColor = "#222222".toColorInt()
    private val subtextColor = "#757575".toColorInt()
    private val dividerColor = "#E0E0E0".toColorInt()
    private val currency: Currency = Currency.current

    /**
     * Generate complete split bill image with all participants' shares
     */
    fun generateCompleteSplitBillImage(
        billTitle: String,
        merchant: String,
        date: String,
        items: List<SplitItem>,
        participants: List<SplitParticipant>,
        taxPercent: Double,
        serviceFeePercent: Double,
        totalAmount: Double
    ): android.net.Uri? {
        try {
            val width = 800
            val height = calculateCompleteBillImageHeight(items, participants)

            val bitmap = createBitmap(width, height)
            val canvas = Canvas(bitmap)

            // Fill background
            canvas.drawColor(backgroundColor)

            var yPosition = 60f
            val margin = 40f
            val contentWidth = width - (margin * 2)

            // Header
            yPosition = drawCompleteBillHeader(canvas, billTitle, margin, yPosition, contentWidth)
            yPosition += 20f

            // Bill info
            yPosition = drawBillInfo(canvas, billTitle, merchant, date, margin, yPosition)
            yPosition += 30f

            // Bill summary
            yPosition = drawBillSummary(
                canvas,
                items,
                taxPercent,
                serviceFeePercent,
                totalAmount,
                margin,
                yPosition,
                contentWidth
            )
            yPosition += 30f

            // All items
            yPosition = drawAllItems(canvas, items, margin, yPosition, contentWidth)
            yPosition += 30f

            // All participants shares
            yPosition =
                drawAllParticipantsShares(canvas, participants, margin, yPosition, contentWidth)
            yPosition += 40f

            // Watermark
            drawWatermark(canvas, width, height)

            // Save to file and return URI
            return saveBitmapAndGetUri(bitmap, "split_bill_complete")

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun calculateCompleteBillImageHeight(
        items: List<SplitItem>,
        participants: List<SplitParticipant>
    ): Int {
        val baseHeight = 800
        val itemsHeight = items.size * 80
        val participantsHeight = participants.size * 70
        return baseHeight + itemsHeight + participantsHeight
    }

    private fun drawCompleteBillHeader(
        canvas: Canvas,
        billTitle: String,
        margin: Float,
        yPos: Float,
        contentWidth: Float
    ): Float {
        var yPosition = yPos

        // Title
        val titlePaint = Paint().apply {
            color = primaryColor
            textSize = 32f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("Split Bill Summary", margin, yPosition, titlePaint)
        yPosition += 50f

        // Bill title
        val namePaint = Paint().apply {
            color = textColor
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText(billTitle, margin, yPosition, namePaint)
        yPosition += 30f

        // Divider
        val dividerPaint = Paint().apply { color = dividerColor }
        canvas.drawRect(margin, yPosition, margin + contentWidth, yPosition + 2f, dividerPaint)

        return yPosition + 20f
    }

    private fun drawBillSummary(
        canvas: Canvas,
        items: List<SplitItem>,
        taxPercent: Double,
        serviceFeePercent: Double,
        totalAmount: Double,
        margin: Float,
        yPos: Float,
        contentWidth: Float
    ): Float {
        var yPosition = yPos

        // Section title
        val sectionPaint = Paint().apply {
            color = primaryColor
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("Bill Summary", margin, yPosition, sectionPaint)
        yPosition += 35f

        val labelPaint = Paint().apply {
            color = textColor
            textSize = 18f
            isAntiAlias = true
        }

        val valuePaint = Paint().apply {
            color = textColor
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        // Calculate totals
        val baseAmount = items.sumOf { item ->
            val totalQuantity = item.assignedQuantities.values.sum()
            item.price * totalQuantity
        }
        val taxAmount = baseAmount * (taxPercent / 100.0)
        val serviceFeeAmount = baseAmount * (serviceFeePercent / 100.0)

        // Items total
        canvas.drawText("Subtotal:", margin, yPosition, labelPaint)
        val subtotalText = CurrencyFormatter.formatWithPrefix(baseAmount.toLong(), currency)
        val subtotalWidth = valuePaint.measureText(subtotalText)
        canvas.drawText(subtotalText, margin + contentWidth - subtotalWidth, yPosition, valuePaint)
        yPosition += 30f

        // Tax
        canvas.drawText("Tax ($taxPercent%):", margin, yPosition, labelPaint)
        val taxText = CurrencyFormatter.formatWithPrefix(taxAmount.toLong(), currency)
        val taxWidth = valuePaint.measureText(taxText)
        canvas.drawText(taxText, margin + contentWidth - taxWidth, yPosition, valuePaint)
        yPosition += 30f

        // Service fee
        canvas.drawText("Service Fee ($serviceFeePercent%):", margin, yPosition, labelPaint)
        val serviceFeeText = CurrencyFormatter.formatWithPrefix(serviceFeeAmount.toLong(), currency)
        val serviceFeeWidth = valuePaint.measureText(serviceFeeText)
        canvas.drawText(
            serviceFeeText,
            margin + contentWidth - serviceFeeWidth,
            yPosition,
            valuePaint
        )
        yPosition += 40f

        // Divider
        val dividerPaint = Paint().apply { color = dividerColor }
        canvas.drawRect(margin, yPosition, margin + contentWidth, yPosition + 2f, dividerPaint)
        yPosition += 30f

        // Total
        val totalPaint = Paint().apply {
            color = primaryColor
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("Total Amount:", margin, yPosition, totalPaint)
        val totalText = CurrencyFormatter.formatWithPrefix(totalAmount.toLong(), currency)
        val totalWidth = totalPaint.measureText(totalText)
        canvas.drawText(totalText, margin + contentWidth - totalWidth, yPosition, totalPaint)

        return yPosition + 40f
    }

    private fun drawAllItems(
        canvas: Canvas,
        items: List<SplitItem>,
        margin: Float,
        yPos: Float,
        contentWidth: Float
    ): Float {
        var yPosition = yPos

        // Section title
        val sectionPaint = Paint().apply {
            color = primaryColor
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("Items", margin, yPosition, sectionPaint)
        yPosition += 35f

        val itemPaint = Paint().apply {
            color = textColor
            textSize = 18f
            isAntiAlias = true
        }

        val pricePaint = Paint().apply {
            color = primaryColor
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        val detailPaint = Paint().apply {
            color = subtextColor
            textSize = 16f
            isAntiAlias = true
        }

        items.forEach { item ->
            val totalQuantity = item.assignedQuantities.values.sum()
            val itemTotal = item.price * totalQuantity

            // Item name
            canvas.drawText(item.description, margin, yPosition, itemPaint)

            // Price (right aligned)
            val priceText = CurrencyFormatter.formatWithPrefix(itemTotal.toLong(), currency)
            val priceWidth = pricePaint.measureText(priceText)
            canvas.drawText(priceText, margin + contentWidth - priceWidth, yPosition, pricePaint)
            yPosition += 25f

            // Quantity and unit price
            val detailText =
                "Qty: $totalQuantity × ${
                    CurrencyFormatter.formatWithPrefix(
                        item.price.toLong(),
                        currency
                    )
                }"
            canvas.drawText(detailText, margin + 20f, yPosition, detailPaint)
            yPosition += 35f
        }

        return yPosition
    }

    private fun drawAllParticipantsShares(
        canvas: Canvas,
        participants: List<SplitParticipant>,
        margin: Float,
        yPos: Float,
        contentWidth: Float
    ): Float {
        var yPosition = yPos

        // Section title
        val sectionPaint = Paint().apply {
            color = primaryColor
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("Participant Shares", margin, yPosition, sectionPaint)
        yPosition += 35f

        val namePaint = Paint().apply {
            color = textColor
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        val amountPaint = Paint().apply {
            color = primaryColor
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        val notePaint = Paint().apply {
            color = subtextColor
            textSize = 16f
            isAntiAlias = true
        }

        participants.forEach { participant ->
            // Participant name
            canvas.drawText(participant.name, margin, yPosition, namePaint)

            // Share amount (right aligned)
            val shareText =
                CurrencyFormatter.formatWithPrefix(participant.shareAmount.toLong(), currency)
            val shareWidth = amountPaint.measureText(shareText)
            canvas.drawText(shareText, margin + contentWidth - shareWidth, yPosition, amountPaint)
            yPosition += 25f

            // Note if available
            if (!participant.note.isNullOrBlank()) {
                canvas.drawText("Note: ${participant.note}", margin + 20f, yPosition, notePaint)
                yPosition += 25f
            }

            yPosition += 20f
        }

        return yPosition
    }

    fun generateParticipantShareImage(
        participant: SplitParticipant,
        assignedItems: List<Pair<SplitItem, Int>>, // Pair of (item, quantity)
        taxPercent: Double,
        serviceFeePercent: Double,
        billTitle: String,
        merchant: String,
        date: String
    ): android.net.Uri? {
        try {
            val width = 800
            val height = calculateImageHeight(assignedItems)

            val bitmap = createBitmap(width, height)
            val canvas = Canvas(bitmap)

            // Fill background
            canvas.drawColor(backgroundColor)

            var yPosition = 60f
            val margin = 40f
            val contentWidth = width - (margin * 2)

            // Header
            yPosition = drawHeader(canvas, participant.name, margin, yPosition, contentWidth)
            yPosition += 20f

            // Bill info
            yPosition =
                drawBillInfo(canvas, billTitle, merchant, date, margin, yPosition)
            yPosition += 30f

            // Share summary
            yPosition = drawShareSummary(canvas, participant, margin, yPosition, contentWidth)
            yPosition += 30f

            // Assigned items
            yPosition = drawAssignedItems(canvas, assignedItems, margin, yPosition, contentWidth)
            yPosition += 30f

            // Breakdown
            yPosition = drawBreakdown(
                canvas,
                assignedItems,
                participant,
                taxPercent,
                serviceFeePercent,
                margin,
                yPosition,
                contentWidth
            )
            yPosition += 40f

            // Watermark
            drawWatermark(canvas, width, height)

            // Save to file and return URI
            return saveBitmapAndGetUri(bitmap)

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun calculateImageHeight(assignedItems: List<Pair<SplitItem, Int>>): Int {
        val baseHeight = 640
        val itemHeight = assignedItems.size * 80
        return baseHeight + itemHeight
    }

    private fun drawHeader(
        canvas: Canvas,
        participantName: String,
        margin: Float,
        yPos: Float,
        contentWidth: Float
    ): Float {
        var yPosition = yPos

        // Title
        val titlePaint = Paint().apply {
            color = primaryColor
            textSize = 32f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("Split Bill Share", margin, yPosition, titlePaint)
        yPosition += 50f

        // Participant name
        val namePaint = Paint().apply {
            color = textColor
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText(participantName, margin, yPosition, namePaint)
        yPosition += 30f

        // Divider
        val dividerPaint = Paint().apply { color = dividerColor }
        canvas.drawRect(margin, yPosition, margin + contentWidth, yPosition + 2f, dividerPaint)

        return yPosition + 20f
    }

    private fun drawBillInfo(
        canvas: Canvas,
        title: String,
        merchant: String,
        date: String,
        margin: Float,
        yPos: Float
    ): Float {
        var yPosition = yPos

        val labelPaint = Paint().apply {
            color = subtextColor
            textSize = 18f
            isAntiAlias = true
        }

        val valuePaint = Paint().apply {
            color = textColor
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        // Bill title
        canvas.drawText("Title:", margin, yPosition, labelPaint)
        canvas.drawText(title, margin + 100f, yPosition, valuePaint)
        yPosition += 25f

        // Merchant
        canvas.drawText("Merchant:", margin, yPosition, labelPaint)
        canvas.drawText(merchant.ifEmpty { "Not specified" }, margin + 100f, yPosition, valuePaint)
        yPosition += 25f

        // Date
        canvas.drawText("Date:", margin, yPosition, labelPaint)
        val displayDate = DateUtil.isoStringToLocalDate(date)?.let {
            DateUtil.formatDateForDisplay(it)
        } ?: date
        canvas.drawText(displayDate, margin + 100f, yPosition, valuePaint)

        return yPosition + 25f
    }

    private fun drawShareSummary(
        canvas: Canvas,
        participant: SplitParticipant,
        margin: Float,
        yPos: Float,
        contentWidth: Float
    ): Float {
        var yPosition = yPos

        // Section title
        val sectionPaint = Paint().apply {
            color = primaryColor
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("Your Share", margin, yPosition, sectionPaint)
        yPosition += 35f

        // Share amount
        val amountPaint = Paint().apply {
            color = primaryColor
            textSize = 28f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        val shareText =
            CurrencyFormatter.formatWithPrefix(participant.shareAmount.toLong(), currency)
        canvas.drawText(shareText, margin, yPosition, amountPaint)

        return yPosition + 35f
    }

    private fun drawAssignedItems(
        canvas: Canvas,
        assignedItems: List<Pair<SplitItem, Int>>,
        margin: Float,
        yPos: Float,
        contentWidth: Float
    ): Float {
        var yPosition = yPos

        // Section title
        val sectionPaint = Paint().apply {
            color = primaryColor
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("Your Items", margin, yPosition, sectionPaint)
        yPosition += 35f

        val itemPaint = Paint().apply {
            color = textColor
            textSize = 18f
            isAntiAlias = true
        }

        val pricePaint = Paint().apply {
            color = primaryColor
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        val detailPaint = Paint().apply {
            color = subtextColor
            textSize = 16f
            isAntiAlias = true
        }

        assignedItems.forEach { (item, quantity) ->
            val itemTotal = item.price * quantity

            // Item name
            canvas.drawText(item.description, margin, yPosition, itemPaint)

            // Price (right aligned)
            val priceText = CurrencyFormatter.formatWithPrefix(itemTotal.toLong(), currency)
            val priceWidth = pricePaint.measureText(priceText)
            canvas.drawText(priceText, margin + contentWidth - priceWidth, yPosition, pricePaint)
            yPosition += 25f

            // Quantity and unit price
            val detailText =
                "Qty: $quantity × ${
                    CurrencyFormatter.formatWithPrefix(
                        item.price.toLong(),
                        currency
                    )
                }"
            canvas.drawText(detailText, margin + 20f, yPosition, detailPaint)
            yPosition += 35f
        }

        return yPosition
    }

    private fun drawBreakdown(
        canvas: Canvas,
        assignedItems: List<Pair<SplitItem, Int>>,
        participant: SplitParticipant,
        taxPercent: Double,
        serviceFeePercent: Double,
        margin: Float,
        yPos: Float,
        contentWidth: Float
    ): Float {
        var yPosition = yPos

        // Section title
        val sectionPaint = Paint().apply {
            color = primaryColor
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("Breakdown", margin, yPosition, sectionPaint)
        yPosition += 35f

        val labelPaint = Paint().apply {
            color = textColor
            textSize = 18f
            isAntiAlias = true
        }

        val valuePaint = Paint().apply {
            color = textColor
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        val totalPaint = Paint().apply {
            color = primaryColor
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        // Calculate amounts
        val baseAmount = assignedItems.sumOf { (item, quantity) -> item.price * quantity }
        val participantTax = baseAmount * (taxPercent / 100.0)
        val participantServiceFee = baseAmount * (serviceFeePercent / 100.0)

        // Items total
        canvas.drawText("Items Total:", margin, yPosition, labelPaint)
        val itemsTotalText = CurrencyFormatter.formatWithPrefix(baseAmount.toLong(), currency)
        val itemsTotalWidth = valuePaint.measureText(itemsTotalText)
        canvas.drawText(
            itemsTotalText,
            margin + contentWidth - itemsTotalWidth,
            yPosition,
            valuePaint
        )
        yPosition += 30f

        // Tax
        canvas.drawText("Tax ($taxPercent%):", margin, yPosition, labelPaint)
        val taxText = CurrencyFormatter.formatWithPrefix(participantTax.toLong(), currency)
        val taxWidth = valuePaint.measureText(taxText)
        canvas.drawText(taxText, margin + contentWidth - taxWidth, yPosition, valuePaint)
        yPosition += 30f

        // Service fee
        canvas.drawText("Service Fee ($serviceFeePercent%):", margin, yPosition, labelPaint)
        val serviceFeeText =
            CurrencyFormatter.formatWithPrefix(participantServiceFee.toLong(), currency)
        val serviceFeeWidth = valuePaint.measureText(serviceFeeText)
        canvas.drawText(
            serviceFeeText,
            margin + contentWidth - serviceFeeWidth,
            yPosition,
            valuePaint
        )
        yPosition += 40f

        // Divider
        val dividerPaint = Paint().apply { color = dividerColor }
        canvas.drawRect(margin, yPosition, margin + contentWidth, yPosition + 2f, dividerPaint)
        yPosition += 30f

        // Total
        canvas.drawText("Total Share:", margin, yPosition, totalPaint)
        val totalText =
            CurrencyFormatter.formatWithPrefix(participant.shareAmount.toLong(), currency)
        val totalWidth = totalPaint.measureText(totalText)
        canvas.drawText(totalText, margin + contentWidth - totalWidth, yPosition, totalPaint)

        return yPosition + 40f
    }

    private fun drawWatermark(canvas: Canvas, width: Int, height: Int) {
        val watermarkPaint = Paint().apply {
            color = "#80757575".toColorInt() // Semi-transparent gray
            textSize = 16f
            isAntiAlias = true
        }

        val watermarkText = "Generated by OneVault"
        val textWidth = watermarkPaint.measureText(watermarkText)
        val x = (width - textWidth) / 2f
        val y = height - 30f

        canvas.drawText(watermarkText, x, y, watermarkPaint)
    }

    private fun saveBitmapAndGetUri(
        bitmap: Bitmap,
        filenamePrefix: String = "split_bill_share"
    ): android.net.Uri? {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val filename = "${filenamePrefix}_$timestamp.png"

            val imagesDir = File(context.cacheDir, "images")
            imagesDir.mkdirs()

            val imageFile = File(imagesDir, filename)
            val fos = FileOutputStream(imageFile)

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
