package com.adilstudio.project.onevault.domain.manager

import android.content.Context
import android.net.Uri
import com.adilstudio.project.onevault.domain.model.LineItem
import com.adilstudio.project.onevault.domain.model.OcrResult
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.io.IOException
import androidx.core.net.toUri

class OcrManager(private val context: Context) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun extractTextFromImage(imageUri: String): OcrResult {
        return try {
            val uri = imageUri.toUri()
            val image = InputImage.fromFilePath(context, uri)
            val visionText = recognizer.process(image).await()

            val extractedText = visionText.text
            parseReceiptText(extractedText)

        } catch (e: IOException) {
            throw Exception("Failed to load image: ${e.message}")
        } catch (e: Exception) {
            throw Exception("OCR processing failed: ${e.message}")
        }
    }

    private fun parseReceiptText(text: String): OcrResult {
        val lines = text.split("\n").filter { it.isNotBlank() }

        // Extract merchant name (usually first line or prominent text)
        val merchant = extractMerchant(lines)

        // Extract line items with prices
        val lineItems = extractLineItems(lines)

        // Extract total amount
        val totalAmount = extractTotal(lines)

        // Extract tax
        val tax = extractTax(lines)

        // Extract service fee
        val serviceFee = extractServiceFee(lines)

        // Generate title
        val title = merchant?.let { "$it Receipt" } ?: "Receipt"

        return OcrResult(
            merchant = merchant,
            title = title,
            lineItems = lineItems,
            tax = tax,
            serviceFee = serviceFee,
            totalAmount = totalAmount,
            rawText = text
        )
    }

    private fun extractMerchant(lines: List<String>): String? {
        // Look for merchant name in first few lines, avoiding common receipt words
        val excludeWords = setOf("receipt", "invoice", "bill", "order", "date", "time", "tel", "phone")

        return lines.take(5).find { line ->
            line.length > 3 &&
            !line.matches(Regex(".*\\d{2}[/.-]\\d{2}[/.-]\\d{2,4}.*")) && // Not a date
            !line.matches(Regex(".*\\d{1,2}:\\d{2}.*")) && // Not a time
            excludeWords.none { word -> line.lowercase().contains(word) }
        }?.trim()
    }

    private fun extractLineItems(lines: List<String>): List<LineItem> {
        val items = mutableListOf<LineItem>()
        // More flexible price patterns to catch various formats
        val pricePatterns = listOf(
            Regex("\\b\\d+[.,]\\d{2}\\b"), // Standard: 12.99, 12,99
            Regex("\\b\\d+[.,]\\d{1}\\b"), // Single decimal: 12.9, 12,9
            Regex("\\b\\d+\\b(?=\\s*$)"),  // Whole numbers at end of line: 12
            Regex("\\$\\s*\\d+[.,]?\\d*"), // Dollar format: $12.99, $12
            Regex("\\d+[.,]\\d*\\s*(?:USD|IDR|RP|Rp)"), // With currency: 12.99 USD, 12000 IDR
        )

        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.length < 3) continue // Skip very short lines

            var foundPrice = 0.0
            var priceMatch: MatchResult? = null

            // Try each price pattern
            for (pattern in pricePatterns) {
                val matches = pattern.findAll(trimmedLine).toList()
                if (matches.isNotEmpty()) {
                    priceMatch = matches.last() // Use the last price found
                    val priceText = priceMatch.value
                        .replace(",", ".")
                        .replace("$", "")
                        .replace(Regex("[^\\d.]"), "")
                    foundPrice = priceText.toDoubleOrNull() ?: 0.0
                    break
                }
            }

            if (foundPrice > 0 && priceMatch != null) {
                // Extract description by removing price and cleaning up
                val description = trimmedLine
                    .replace(priceMatch.value, "")
                    .replace(Regex("\\$|USD|IDR|RP|Rp"), "") // Remove currency symbols
                    .replace(Regex("\\s+"), " ") // Normalize whitespace
                    .trim()

                // More lenient filtering - only exclude obvious non-items
                if (description.isNotBlank() &&
                    description.length >= 2 &&
                    !isObviousNonItem(description) &&
                    foundPrice < 1000000) { // Reasonable price limit

                    items.add(LineItem(
                        description = description,
                        price = foundPrice,
                        isExtraCharge = false
                    ))
                }
            }
        }

        // If no items found with strict parsing, try a more relaxed approach
        if (items.isEmpty()) {
            items.addAll(extractLineItemsRelaxed(lines))
        }

        return items.distinctBy { it.description.lowercase() } // Remove duplicates
    }

    private fun extractLineItemsRelaxed(lines: List<String>): List<LineItem> {
        val items = mutableListOf<LineItem>()

        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.length < 3) continue

            // Look for any number that could be a price
            val numbers = Regex("\\d+[.,]?\\d*").findAll(trimmedLine).toList()

            if (numbers.isNotEmpty()) {
                val lastNumber = numbers.last()
                val price = lastNumber.value.replace(",", ".").toDoubleOrNull() ?: 0.0

                if (price > 0.1 && price < 100000) { // Reasonable price range
                    val description = trimmedLine
                        .replace(lastNumber.value, "")
                        .replace(Regex("[^a-zA-Z0-9\\s]"), " ")
                        .replace(Regex("\\s+"), " ")
                        .trim()

                    if (description.isNotBlank() &&
                        description.length >= 2 &&
                        !isObviousNonItem(description)) {

                        items.add(LineItem(
                            description = description,
                            price = price,
                            isExtraCharge = false
                        ))
                    }
                }
            }
        }

        return items.take(20) // Limit to 20 items to avoid spam
    }

    private fun isObviousNonItem(description: String): Boolean {
        val nonItemKeywords = listOf(
            "total", "subtotal", "tax", "vat", "ppn", "service", "tip", "gratuity",
            "amount", "change", "cash", "card", "payment", "receipt", "invoice",
            "date", "time", "table", "order", "no", "number", "tel", "phone",
            "address", "thank", "visit", "again", "www", "http"
        )
        val lowerDesc = description.lowercase()
        return nonItemKeywords.any { keyword -> lowerDesc.contains(keyword) } ||
               description.matches(Regex("\\d+")) || // Pure numbers
               description.length > 50 // Very long descriptions are likely not item names
    }

    private fun extractTotal(lines: List<String>): Double {
        val totalPattern = Regex("(?i)\\btotal\\b.*?(\\d+[.,]\\d{2})")

        for (line in lines) {
            val match = totalPattern.find(line)
            if (match != null) {
                return match.groupValues[1].replace(",", ".").toDoubleOrNull() ?: 0.0
            }
        }

        // Fallback: look for largest amount in the text
        val pricePattern = Regex("\\b\\d+[.,]\\d{2}\\b")
        return lines.flatMap { line ->
            pricePattern.findAll(line).map {
                it.value.replace(",", ".").toDoubleOrNull() ?: 0.0
            }
        }.maxOrNull() ?: 0.0
    }

    private fun extractTax(lines: List<String>): Double {
        val taxPatterns = listOf(
            Regex("(?i)\\btax\\b.*?(\\d+[.,]\\d{2})"),
            Regex("(?i)\\bvat\\b.*?(\\d+[.,]\\d{2})"),
            Regex("(?i)\\bppn\\b.*?(\\d+[.,]\\d{2})") // Indonesian tax
        )

        for (line in lines) {
            for (pattern in taxPatterns) {
                val match = pattern.find(line)
                if (match != null) {
                    return match.groupValues[1].replace(",", ".").toDoubleOrNull() ?: 0.0
                }
            }
        }

        return 0.0
    }

    private fun extractServiceFee(lines: List<String>): Double {
        val servicePatterns = listOf(
            Regex("(?i)\\bservice\\b.*?(\\d+[.,]\\d{2})"),
            Regex("(?i)\\btip\\b.*?(\\d+[.,]\\d{2})"),
            Regex("(?i)\\bgratuity\\b.*?(\\d+[.,]\\d{2})")
        )

        for (line in lines) {
            for (pattern in servicePatterns) {
                val match = pattern.find(line)
                if (match != null) {
                    return match.groupValues[1].replace(",", ".").toDoubleOrNull() ?: 0.0
                }
            }
        }

        return 0.0
    }

    private fun isExtraCharge(description: String): Boolean {
        val extraChargeKeywords = listOf("tax", "vat", "ppn", "service", "tip", "gratuity", "total", "subtotal")
        return extraChargeKeywords.any { keyword ->
            description.lowercase().contains(keyword)
        }
    }

    private fun isTotalLine(description: String): Boolean {
        val totalKeywords = listOf("total", "subtotal", "grand total", "amount due")
        return totalKeywords.any { keyword ->
            description.lowercase().contains(keyword)
        }
    }
}
