package com.adilstudio.project.onevault.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.presentation.component.GenericScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    GenericScreen(
        title = stringResource(R.string.privacy_policy),
        showNavIcon = true,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(bottom = dimensionResource(R.dimen.spacing_large))
                .verticalScroll(rememberScrollState())
        ) {
            // Introduction
            PrivacySection(
                title = stringResource(R.string.privacy_introduction_title),
                content = stringResource(R.string.privacy_introduction_content)
            )

            // Information We Collect
            PrivacySection(
                title = stringResource(R.string.privacy_information_collect_title),
                content = stringResource(R.string.privacy_information_collect_content)
            )

            // How We Use Information
            PrivacySection(
                title = stringResource(R.string.privacy_how_we_use_title),
                content = stringResource(R.string.privacy_how_we_use_content)
            )

            // Data Security
            PrivacySection(
                title = stringResource(R.string.privacy_data_security_title),
                content = stringResource(R.string.privacy_data_security_content)
            )

            // Data Storage
            PrivacySection(
                title = stringResource(R.string.privacy_data_storage_title),
                content = stringResource(R.string.privacy_data_storage_content)
            )

            // Third-Party Services
            PrivacySection(
                title = stringResource(R.string.privacy_third_party_title),
                content = stringResource(R.string.privacy_third_party_content)
            )

            // Your Rights
            PrivacySection(
                title = stringResource(R.string.privacy_your_rights_title),
                content = stringResource(R.string.privacy_your_rights_content)
            )

            // Contact Information
            PrivacySection(
                title = stringResource(R.string.privacy_contact_title),
                content = stringResource(R.string.privacy_contact_content)
            )

            // Last Updated
            Text(
                text = stringResource(R.string.privacy_last_updated),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}

@Composable
private fun PrivacySection(
    title: String,
    content: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )
        }
    }
}
