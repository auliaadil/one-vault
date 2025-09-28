package com.adilstudio.project.onevault.presentation.bill

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.DateUtil
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.presentation.component.DetailField
import com.adilstudio.project.onevault.presentation.component.GenericBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailBottomSheet(
    bill: Bill,
    onDismiss: () -> Unit,
    onEdit: (Bill) -> Unit,
    onDelete: (Long) -> Unit
) {
    val closeString = stringResource(R.string.close)
    val editString = stringResource(R.string.edit)
    val noCategoryString = stringResource(R.string.no_category)

    GenericBottomSheet(
        title = stringResource(R.string.bill_details),
        onEdit = { onEdit(bill) },
        onDelete = {}, // handled by deleteDialogText and onDeleteConfirmed
        deleteDialogText = stringResource(R.string.delete_bill_message, bill.title),
        onDeleteConfirmed = {
            onDelete(bill.id)
            onDismiss()
        },
        onDismiss = onDismiss
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_large))
        ) {
            DetailField(R.string.title, bill.title)
            DetailField(R.string.vendor, bill.vendor)
            DetailField(R.string.amount, RupiahFormatter.formatWithRupiahPrefix(bill.amount.toLong()))
            DetailField(
                labelRes = R.string.bill_date,
                value = DateUtil.isoStringToLocalDate(bill.billDate)?.let { date ->
                    DateUtil.formatDateForDisplay(date)
                } ?: bill.billDate)
            DetailField(R.string.category_optional, bill.category ?: noCategoryString)
            bill.imagePath?.let {
                DetailField(
                    labelRes = R.string.attachment,
                    imagePath = it
                )
            }
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(closeString)
                }
                Button(
                    onClick = { onEdit(bill) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(editString)
                }
            }
        }
    }
}
