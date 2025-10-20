package com.adilstudio.project.onevault.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.adilstudio.project.onevault.BuildConfig
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.presentation.component.BaseScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen() {
    BaseScreen(
        title = stringResource(R.string.about_us),
        showNavIcon = true,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Icon
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(dimensionResource(R.dimen.app_icon_size))
            )

            // App Name
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))

            // Version
            Text(
                text = stringResource(
                    R.string.version_format,
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxxl)))

            // Description
            Text(
                text = stringResource(R.string.app_description),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxxl)))

            // Features
            Card(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = dimensionResource(R.dimen.spacing_xs))
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large))
                ) {
                    Text(
                        text = stringResource(R.string.key_features),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

                    FeatureItem(stringResource(R.string.feature_transaction_tracker))
                    FeatureItem(stringResource(R.string.feature_password_manager))
                    FeatureItem(stringResource(R.string.feature_split_bill))
                    FeatureItem(stringResource(R.string.feature_password_generator))
                    FeatureItem(stringResource(R.string.feature_biometric_security))
                    FeatureItem(stringResource(R.string.feature_data_encryption))
                }
            }

            // Developer Info
            Card(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = dimensionResource(R.dimen.spacing_xs))
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large))
                ) {
                    Text(
                        text = stringResource(R.string.developer_info),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

                    Text(
                        text = stringResource(R.string.developer_name),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = stringResource(R.string.developer_email),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))
        }
    }
}

@Composable
private fun FeatureItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.spacing_xs)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.bullet_point),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = dimensionResource(R.dimen.spacing_small))
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
