package com.adilstudio.project.onevault.data.repository

import com.adilstudio.project.onevault.domain.model.BillCategory
import com.adilstudio.project.onevault.domain.model.CategoryType

object DefaultBillCategoriesHelper {
    fun createDefaultCategories(): List<BillCategory> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            // UTILITIES
            BillCategory(id = null, name = "Electricity", icon = "⚡", color = "#795548", type = CategoryType.UTILITIES, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Amber
            BillCategory(id = null, name = "Water", icon = "💧", color = "#2196F3", type = CategoryType.UTILITIES, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Blue
            BillCategory(id = null, name = "Internet", icon = "🌐", color = "#212121", type = CategoryType.UTILITIES, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Green
            BillCategory(id = null, name = "Gas", icon = "🔥", color = "#FF5722", type = CategoryType.UTILITIES, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Orange
            BillCategory(id = null, name = "Phone", icon = "📱", color = "#9C27B0", type = CategoryType.UTILITIES, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Purple

            // FOOD_AND_DINING
            BillCategory(id = null, name = "Groceries", icon = "🛒", color = "#673AB7", type = CategoryType.FOOD_AND_DINING, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Light Green
            BillCategory(id = null, name = "Dining Out", icon = "🍽️", color = "#FF9800", type = CategoryType.FOOD_AND_DINING, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Orange
            BillCategory(id = null, name = "Coffee & Beverages", icon = "☕", color = "#795548", type = CategoryType.FOOD_AND_DINING, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Brown
            BillCategory(id = null, name = "Fast Food", icon = "🍕", color = "#F44336", type = CategoryType.FOOD_AND_DINING, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Red

            // SHOPPING
            BillCategory(id = null, name = "Clothing", icon = "🛍️", color = "#9C27B0", type = CategoryType.SHOPPING, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Purple
            BillCategory(id = null, name = "Electronics", icon = "💻", color = "#607D8B", type = CategoryType.SHOPPING, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Blue Grey
            BillCategory(id = null, name = "Home & Garden", icon = "🏠", color = "#4CAF50", type = CategoryType.SHOPPING, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Green
            BillCategory(id = null, name = "Beauty & Personal Care", icon = "💄", color = "#E91E63", type = CategoryType.SHOPPING, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Pink

            // TRANSPORTATION
            BillCategory(id = null, name = "Fuel", icon = "⛽", color = "#FF5722", type = CategoryType.TRANSPORTATION, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Orange
            BillCategory(id = null, name = "Public Transport", icon = "🚇", color = "#2196F3", type = CategoryType.TRANSPORTATION, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Blue
            BillCategory(id = null, name = "Taxi/Ride Share", icon = "🚗", color = "#FFB300", type = CategoryType.TRANSPORTATION, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Amber
            BillCategory(id = null, name = "Car Maintenance", icon = "🔧", color = "#607D8B", type = CategoryType.TRANSPORTATION, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Blue Grey
            BillCategory(id = null, name = "Parking", icon = "🅿️", color = "#795548", type = CategoryType.TRANSPORTATION, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Brown

            // ENTERTAINMENT
            BillCategory(id = null, name = "Movies/Cinema", icon = "🎬", color = "#E91E63", type = CategoryType.ENTERTAINMENT, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Pink
            BillCategory(id = null, name = "Streaming Services", icon = "📺", color = "#673AB7", type = CategoryType.ENTERTAINMENT, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Deep Purple
            BillCategory(id = null, name = "Games", icon = "🎮", color = "#3F51B5", type = CategoryType.ENTERTAINMENT, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Indigo
            BillCategory(id = null, name = "Music", icon = "🎵", color = "#F44336", type = CategoryType.ENTERTAINMENT, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Red
            BillCategory(id = null, name = "Sports & Recreation", icon = "⚽", color = "#4CAF50", type = CategoryType.ENTERTAINMENT, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Green

            // HEALTHCARE
            BillCategory(id = null, name = "Doctor Visits", icon = "👨‍⚕️", color = "#2196F3", type = CategoryType.HEALTHCARE, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Blue
            BillCategory(id = null, name = "Pharmacy", icon = "💊", color = "#F44336", type = CategoryType.HEALTHCARE, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Red
            BillCategory(id = null, name = "Dental Care", icon = "🦷", color = "#00BCD4", type = CategoryType.HEALTHCARE, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Cyan
            BillCategory(id = null, name = "Insurance", icon = "🛡️", color = "#3F51B5", type = CategoryType.HEALTHCARE, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Indigo
            BillCategory(id = null, name = "Gym & Fitness", icon = "🏋️", color = "#FF9800", type = CategoryType.HEALTHCARE, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Orange

            // EDUCATION
            BillCategory(id = null, name = "School Fees", icon = "🎓", color = "#2196F3", type = CategoryType.EDUCATION, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Blue
            BillCategory(id = null, name = "Books & Supplies", icon = "📚", color = "#8BC34A", type = CategoryType.EDUCATION, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Light Green
            BillCategory(id = null, name = "Online Courses", icon = "💻", color = "#9C27B0", type = CategoryType.EDUCATION, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Purple
            BillCategory(id = null, name = "Tutoring", icon = "📝", color = "#FFB300", type = CategoryType.EDUCATION, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Amber

            // OTHERS
            BillCategory(id = null, name = "Pet Care", icon = "🐕", color = "#4CAF50", type = CategoryType.OTHERS, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Green
            BillCategory(id = null, name = "Donations/Charity", icon = "❤️", color = "#FFFFFF", type = CategoryType.OTHERS, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Red
            BillCategory(id = null, name = "Bank Fees", icon = "🏦", color = "#607D8B", type = CategoryType.OTHERS, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Blue Grey
            BillCategory(id = null, name = "Taxes", icon = "📊", color = "#795548", type = CategoryType.OTHERS, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Brown
            BillCategory(id = null, name = "Travel", icon = "✈️", color = "#00BCD4", type = CategoryType.OTHERS, isEditable = false, createdAt = currentTime, updatedAt = currentTime), // Cyan
            BillCategory(id = null, name = "Miscellaneous", icon = "📋", color = "#9E9E9E", type = CategoryType.OTHERS, isEditable = false, createdAt = currentTime, updatedAt = currentTime) // Grey
        )
    }
}
