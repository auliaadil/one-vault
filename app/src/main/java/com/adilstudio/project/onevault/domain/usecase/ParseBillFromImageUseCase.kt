package com.adilstudio.project.onevault.domain.usecase

import android.net.Uri
import com.adilstudio.project.onevault.domain.model.BillItem

class ParseBillFromImageUseCase {
    suspend operator fun invoke(imageUri: Uri): ParseBillResult {
        // Mock OCR implementation - in real app, this would use ML Kit or similar
        return mockOcrParsing()
    }

    private fun mockOcrParsing(): ParseBillResult {
        val items = listOf(
            BillItem(id = 1, name = "Nasi Goreng", price = 25000.0, quantity = 2),
            BillItem(id = 2, name = "Es Teh Manis", price = 8000.0, quantity = 3),
            BillItem(id = 3, name = "Ayam Bakar", price = 35000.0, quantity = 1),
            BillItem(id = 4, name = "Gado-gado", price = 15000.0, quantity = 1)
        )

        return ParseBillResult(
            items = items,
            taxPercentage = 10.0,
            servicePercentage = 5.0,
            vendor = "Warung Makan Sederhana",
            billDate = java.time.LocalDate.now().toString()
        )
    }
}

data class ParseBillResult(
    val items: List<BillItem>,
    val taxPercentage: Double,
    val servicePercentage: Double,
    val vendor: String,
    val billDate: String
)
