package com.adilstudio.project.onevault.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.domain.model.AppLockTimeoutOption

@Composable
fun AppLockTimeoutDialog(
    currentTimeout: Long,
    onTimeoutSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val timeoutOptions = remember {
        listOf(
            AppLockTimeoutOption.FIVE_SECONDS to R.string.timeout_5_seconds,
            AppLockTimeoutOption.THIRTY_SECONDS to R.string.timeout_30_seconds,
            AppLockTimeoutOption.ONE_MINUTE to R.string.timeout_1_minute,
            AppLockTimeoutOption.FIVE_MINUTES to R.string.timeout_5_minutes
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.app_lock_timeout),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = stringResource(R.string.app_lock_timeout_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                timeoutOptions.forEach { (timeoutValue, labelResId) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = currentTimeout == timeoutValue,
                                onClick = { onTimeoutSelected(timeoutValue) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentTimeout == timeoutValue,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(labelResId),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        }
    }
}
