package com.adilstudio.project.onevault.presentation.bill.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.domain.model.BillCategory
import com.adilstudio.project.onevault.domain.model.CategoryType
import java.util.UUID

@Composable
fun createDefaultCategories(): List<BillCategory> {
    val currentTime = System.currentTimeMillis()

    return listOf(
        // UTILITIES Categories
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_electricity),
            icon = "⚡",
            color = "#FFB300",
            type = CategoryType.UTILITIES,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_water),
            icon = "💧",
            color = "#2196F3",
            type = CategoryType.UTILITIES,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_internet),
            icon = "🌐",
            color = "#4CAF50",
            type = CategoryType.UTILITIES,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_gas),
            icon = "🔥",
            color = "#FF5722",
            type = CategoryType.UTILITIES,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_phone),
            icon = "📱",
            color = "#9C27B0",
            type = CategoryType.UTILITIES,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),

        // FOOD_AND_DINING Categories
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_groceries),
            icon = "🛒",
            color = "#8BC34A",
            type = CategoryType.FOOD_AND_DINING,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_dining_out),
            icon = "🍽️",
            color = "#FF9800",
            type = CategoryType.FOOD_AND_DINING,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_coffee_beverages),
            icon = "☕",
            color = "#8D6E63",
            type = CategoryType.FOOD_AND_DINING,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_fast_food),
            icon = "🍕",
            color = "#FF5722",
            type = CategoryType.FOOD_AND_DINING,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),

        // SHOPPING Categories
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_clothing),
            icon = "🛍️",
            color = "#9C27B0",
            type = CategoryType.SHOPPING,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_electronics),
            icon = "💻",
            color = "#607D8B",
            type = CategoryType.SHOPPING,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_home_garden),
            icon = "🏠",
            color = "#4CAF50",
            type = CategoryType.SHOPPING,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_beauty_personal_care),
            icon = "💄",
            color = "#E91E63",
            type = CategoryType.SHOPPING,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),

        // TRANSPORTATION Categories
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_fuel),
            icon = "⛽",
            color = "#FF5722",
            type = CategoryType.TRANSPORTATION,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_public_transport),
            icon = "🚇",
            color = "#2196F3",
            type = CategoryType.TRANSPORTATION,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_taxi_ride_share),
            icon = "🚗",
            color = "#FFB300",
            type = CategoryType.TRANSPORTATION,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_car_maintenance),
            icon = "🔧",
            color = "#607D8B",
            type = CategoryType.TRANSPORTATION,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_parking),
            icon = "🅿️",
            color = "#795548",
            type = CategoryType.TRANSPORTATION,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),

        // ENTERTAINMENT Categories
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_movies_cinema),
            icon = "🎬",
            color = "#E91E63",
            type = CategoryType.ENTERTAINMENT,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_streaming_services),
            icon = "📺",
            color = "#673AB7",
            type = CategoryType.ENTERTAINMENT,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_games),
            icon = "🎮",
            color = "#3F51B5",
            type = CategoryType.ENTERTAINMENT,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_music),
            icon = "🎵",
            color = "#E91E63",
            type = CategoryType.ENTERTAINMENT,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_sports_recreation),
            icon = "⚽",
            color = "#4CAF50",
            type = CategoryType.ENTERTAINMENT,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),

        // HEALTHCARE Categories
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_doctor_visits),
            icon = "👨‍⚕️",
            color = "#2196F3",
            type = CategoryType.HEALTHCARE,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_pharmacy),
            icon = "💊",
            color = "#F44336",
            type = CategoryType.HEALTHCARE,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_dental_care),
            icon = "🦷",
            color = "#00BCD4",
            type = CategoryType.HEALTHCARE,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_insurance),
            icon = "🛡️",
            color = "#3F51B5",
            type = CategoryType.HEALTHCARE,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_gym_fitness),
            icon = "🏋️",
            color = "#FF5722",
            type = CategoryType.HEALTHCARE,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),

        // EDUCATION Categories
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_school_fees),
            icon = "🎓",
            color = "#2196F3",
            type = CategoryType.EDUCATION,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_books_supplies),
            icon = "📚",
            color = "#8BC34A",
            type = CategoryType.EDUCATION,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_online_courses),
            icon = "💻",
            color = "#9C27B0",
            type = CategoryType.EDUCATION,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_tutoring),
            icon = "📝",
            color = "#FF9800",
            type = CategoryType.EDUCATION,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),

        // OTHER Categories
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_pet_care),
            icon = "🐕",
            color = "#4CAF50",
            type = CategoryType.OTHER,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_donations_charity),
            icon = "❤️",
            color = "#F44336",
            type = CategoryType.OTHER,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_bank_fees),
            icon = "🏦",
            color = "#607D8B",
            type = CategoryType.OTHER,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_taxes),
            icon = "📊",
            color = "#795548",
            type = CategoryType.OTHER,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_travel),
            icon = "✈️",
            color = "#00BCD4",
            type = CategoryType.OTHER,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        BillCategory(
            id = UUID.randomUUID().toString(),
            name = stringResource(R.string.category_miscellaneous),
            icon = "📋",
            color = "#9E9E9E",
            type = CategoryType.OTHER,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        )
    )
}
