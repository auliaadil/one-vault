package com.adilstudio.project.onevault.data.repository

import com.adilstudio.project.onevault.domain.model.TransactionCategory
import com.adilstudio.project.onevault.domain.model.CategoryType
import com.adilstudio.project.onevault.domain.model.TransactionType

object DefaultTransactionCategoriesHelper {
    fun createDefaultCategories(): List<TransactionCategory> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            // EXPENSE CATEGORIES
            // UTILITIES
            TransactionCategory(id = null, name = "Electricity", icon = "⚡", color = "#795548", type = CategoryType.UTILITIES, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Water", icon = "💧", color = "#2196F3", type = CategoryType.UTILITIES, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Internet", icon = "🌐", color = "#212121", type = CategoryType.UTILITIES, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Gas", icon = "🔥", color = "#FF5722", type = CategoryType.UTILITIES, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Phone", icon = "📱", color = "#9C27B0", type = CategoryType.UTILITIES, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),

            // FOOD_AND_DINING
            TransactionCategory(id = null, name = "Groceries", icon = "🛒", color = "#673AB7", type = CategoryType.FOOD_AND_DINING, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Dining Out", icon = "🍽️", color = "#FF9800", type = CategoryType.FOOD_AND_DINING, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Coffee & Beverages", icon = "☕", color = "#795548", type = CategoryType.FOOD_AND_DINING, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Fast Food", icon = "🍕", color = "#F44336", type = CategoryType.FOOD_AND_DINING, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),

            // SHOPPING
            TransactionCategory(id = null, name = "Clothing", icon = "🛍️", color = "#9C27B0", type = CategoryType.SHOPPING, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Electronics", icon = "💻", color = "#607D8B", type = CategoryType.SHOPPING, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Home & Garden", icon = "🏠", color = "#4CAF50", type = CategoryType.SHOPPING, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Beauty & Personal Care", icon = "💄", color = "#E91E63", type = CategoryType.SHOPPING, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),

            // TRANSPORTATION
            TransactionCategory(id = null, name = "Fuel", icon = "⛽", color = "#FF5722", type = CategoryType.TRANSPORTATION, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Public Transport", icon = "🚇", color = "#2196F3", type = CategoryType.TRANSPORTATION, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Taxi/Ride Share", icon = "🚗", color = "#FFB300", type = CategoryType.TRANSPORTATION, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Car Maintenance", icon = "🔧", color = "#607D8B", type = CategoryType.TRANSPORTATION, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Parking", icon = "🅿️", color = "#795548", type = CategoryType.TRANSPORTATION, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),

            // ENTERTAINMENT
            TransactionCategory(id = null, name = "Movies/Cinema", icon = "🎬", color = "#E91E63", type = CategoryType.ENTERTAINMENT, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Streaming Services", icon = "📺", color = "#673AB7", type = CategoryType.ENTERTAINMENT, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Games", icon = "🎮", color = "#3F51B5", type = CategoryType.ENTERTAINMENT, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Music", icon = "🎵", color = "#F44336", type = CategoryType.ENTERTAINMENT, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Sports & Recreation", icon = "⚽", color = "#4CAF50", type = CategoryType.ENTERTAINMENT, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),

            // HEALTHCARE
            TransactionCategory(id = null, name = "Doctor Visits", icon = "👨‍⚕️", color = "#2196F3", type = CategoryType.HEALTHCARE, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Pharmacy", icon = "💊", color = "#F44336", type = CategoryType.HEALTHCARE, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Dental Care", icon = "🦷", color = "#00BCD4", type = CategoryType.HEALTHCARE, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Insurance", icon = "🛡️", color = "#3F51B5", type = CategoryType.HEALTHCARE, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Gym & Fitness", icon = "🏋️", color = "#FF9800", type = CategoryType.HEALTHCARE, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),

            // EDUCATION
            TransactionCategory(id = null, name = "School Fees", icon = "🎓", color = "#2196F3", type = CategoryType.EDUCATION, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Books & Supplies", icon = "📚", color = "#8BC34A", type = CategoryType.EDUCATION, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Online Courses", icon = "💻", color = "#9C27B0", type = CategoryType.EDUCATION, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Tutoring", icon = "📝", color = "#FFB300", type = CategoryType.EDUCATION, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),

            // OTHERS (EXPENSE)
            TransactionCategory(id = null, name = "Pet Care", icon = "🐕", color = "#4CAF50", type = CategoryType.OTHERS, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Donations/Charity", icon = "❤️", color = "#E91E63", type = CategoryType.OTHERS, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Bank Fees", icon = "🏦", color = "#607D8B", type = CategoryType.OTHERS, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Taxes", icon = "📊", color = "#795548", type = CategoryType.OTHERS, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Travel", icon = "✈️", color = "#00BCD4", type = CategoryType.OTHERS, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Miscellaneous", icon = "📋", color = "#9E9E9E", type = CategoryType.OTHERS, transactionType = TransactionType.EXPENSE, isEditable = false, createdAt = currentTime, updatedAt = currentTime),

            // INCOME CATEGORIES
            TransactionCategory(id = null, name = "Salary", icon = "💰", color = "#4CAF50", type = CategoryType.OTHERS, transactionType = TransactionType.INCOME, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Freelance", icon = "💼", color = "#2196F3", type = CategoryType.OTHERS, transactionType = TransactionType.INCOME, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Business Income", icon = "🏢", color = "#FF9800", type = CategoryType.OTHERS, transactionType = TransactionType.INCOME, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Investment Returns", icon = "📈", color = "#9C27B0", type = CategoryType.OTHERS, transactionType = TransactionType.INCOME, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Dividends", icon = "📊", color = "#673AB7", type = CategoryType.OTHERS, transactionType = TransactionType.INCOME, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Interest", icon = "💳", color = "#00BCD4", type = CategoryType.OTHERS, transactionType = TransactionType.INCOME, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Bonus", icon = "🎁", color = "#E91E63", type = CategoryType.OTHERS, transactionType = TransactionType.INCOME, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Commission", icon = "🤝", color = "#FF5722", type = CategoryType.OTHERS, transactionType = TransactionType.INCOME, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Rental Income", icon = "🏠", color = "#795548", type = CategoryType.OTHERS, transactionType = TransactionType.INCOME, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Gifts", icon = "🎉", color = "#FFEB3B", type = CategoryType.OTHERS, transactionType = TransactionType.INCOME, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Refunds", icon = "↩️", color = "#607D8B", type = CategoryType.OTHERS, transactionType = TransactionType.INCOME, isEditable = false, createdAt = currentTime, updatedAt = currentTime),
            TransactionCategory(id = null, name = "Other Income", icon = "💵", color = "#8BC34A", type = CategoryType.OTHERS, transactionType = TransactionType.INCOME, isEditable = false, createdAt = currentTime, updatedAt = currentTime)
        )
    }
}
