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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.DateUtil
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.domain.model.Account
import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.domain.model.BillCategory
import com.adilstudio.project.onevault.presentation.component.DetailField
import com.adilstudio.project.onevault.presentation.component.GenericBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailBottomSheet(
    bill: Bill,
    categories: List<BillCategory>,
    accounts: List<Account>,
    onDismiss: () -> Unit,
    onEdit: (Bill) -> Unit,
    onDelete: (Long) -> Unit
) {
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
            DetailField(
                R.string.vendor_optional,
                if (bill.vendor.isBlank()) stringResource(R.string.no_vendor) else bill.vendor
            )
            DetailField(
                R.string.amount,
                RupiahFormatter.formatWithRupiahPrefix(bill.amount.toLong())
            )
            DetailField(
                labelRes = R.string.bill_date,
                value = DateUtil.isoStringToLocalDate(bill.billDate)?.let { date ->
                    DateUtil.formatDateForDisplay(date)
                } ?: bill.billDate)
            val category = bill.categoryId?.let { id ->
                categories.find { it.id == id }
            }
            val categoryName = category?.name ?: stringResource(R.string.no_category)
            DetailField(R.string.category_optional, category?.icon?.let {
                "$it $categoryName"
            } ?: categoryName)

            val account = bill.accountId?.let { id ->
                accounts.find { it.id == id }
            }
            DetailField(
                R.string.account_optional,
                if (bill.accountId == null) stringResource(R.string.no_account_selected) else account?.name
            )
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
                    Text(stringResource(R.string.close))
                }
                Button(
                    onClick = { onEdit(bill) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.edit))
                }
            }
        }
    }
}
