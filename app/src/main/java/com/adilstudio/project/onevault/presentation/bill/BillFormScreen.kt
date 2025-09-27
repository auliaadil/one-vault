package com.adilstudio.project.onevault.presentation.bill

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.DateUtil
import com.adilstudio.project.onevault.core.util.ImageUtil
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.presentation.bill.account.AccountViewModel
import com.adilstudio.project.onevault.presentation.bill.category.BillCategoryViewModel
import com.adilstudio.project.onevault.presentation.component.GenericScreen
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillFormScreen(
    bill: Bill? = null, // null for add, non-null for edit
    scannedImageUri: Uri? = null, // Scanned image to process and attach
    onSave: (Bill) -> Unit,
    onDelete: ((Long) -> Unit)? = null,
    onNavigateBack: () -> Unit,
    onCancel: () -> Unit = {},
    categoryViewModel: BillCategoryViewModel = koinViewModel(),
    accountViewModel: AccountViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val categories by categoryViewModel.categories.collectAsState()
    val accounts by accountViewModel.accounts.collectAsState()
    val isEditing = bill != null

    // ML Kit scanning state for scanned image
    var scannedTexts by remember { mutableStateOf<List<String>>(emptyList()) }
    var showTextSelectionDialog by remember { mutableStateOf(false) }
    var scanningProgress by remember { mutableStateOf(false) }

    // Initialize form state from existing bill or defaults
    var title by remember { mutableStateOf(bill?.title ?: "") }
    var selectedCategory by remember {
        mutableStateOf(categories.find { it.name == bill?.category })
    }
    var selectedAccount by remember {
        mutableStateOf(accounts.find { it.id == bill?.accountId })
    }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showAccountDropdown by remember { mutableStateOf(false) }
    var amountValue by remember { mutableStateOf(bill?.amount?.toLong() ?: 0L) }
    var amountDisplay by remember {
        mutableStateOf(
            if (bill?.amount != null)
                RupiahFormatter.formatRupiahDisplay(bill.amount.toLong())
            else ""
        )
    }
    var vendor by remember { mutableStateOf(bill?.vendor ?: "") }

    // Date picker state - using Calendar for API 24 compatibility
    var selectedDate by remember {
        mutableStateOf(
            bill?.billDate?.let { DateUtil.isoStringToLocalDate(it) } ?: DateUtil.getCurrentDate()
        )
    }
    var showDatePicker by remember { mutableStateOf(false) }

    // Convert LocalDate to milliseconds for DatePicker (API 24 compatible)
    val selectedDateMillis = remember(selectedDate) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedDate.year)
            set(Calendar.MONTH, selectedDate.monthValue - 1) // Calendar months are 0-based
            set(Calendar.DAY_OF_MONTH, selectedDate.dayOfMonth)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        calendar.timeInMillis
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis
    )

    // Image handling state - auto-attach scanned image
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var savedImagePath by remember {
        mutableStateOf(
            bill?.imagePath ?: scannedImageUri?.let {
                ImageUtil.saveImageToInternalStorage(context, it)
            }
        )
    }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Process scanned image with ML Kit when provided
    LaunchedEffect(scannedImageUri) {
        scannedImageUri?.let { uri ->
            scanningProgress = true
            try {
                val image = InputImage.fromFilePath(context, uri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val texts = visionText.textBlocks
                            .flatMap { it.lines }
                            .map { it.text.trim() }
                            .filter { it.isNotEmpty() }

                        scannedTexts = texts
                        scanningProgress = false

                        if (texts.isNotEmpty()) {
                            showTextSelectionDialog = true
                        }
                    }
                    .addOnFailureListener {
                        scanningProgress = false
                    }
            } catch (e: Exception) {
                scanningProgress = false
            }
        }
    }

    // Update selectedCategory when categories are loaded
    LaunchedEffect(categories, bill) {
        if (bill != null && selectedCategory == null) {
            selectedCategory = categories.find { it.name == bill.category }
        }
    }

    // Update selectedCategory when categories are loaded
    LaunchedEffect(accounts, bill) {
        if (bill != null && selectedAccount == null) {
            selectedAccount = accounts.find { it.id == bill.accountId }
        }
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Save image to internal storage
            val imagePath = ImageUtil.saveImageToInternalStorage(context, it)
            savedImagePath = imagePath
        }
    }

    GenericScreen(
        title = if (isEditing) stringResource(R.string.edit_bill) else stringResource(R.string.add_bill),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            if (isEditing && onDelete != null) {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_large))
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.title_placeholder)) }
            )

            // Category selection
            ExposedDropdownMenuBox(
                expanded = showCategoryDropdown,
                onExpandedChange = { showCategoryDropdown = !showCategoryDropdown }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "",
                    onValueChange = {},
                    label = { Text(stringResource(R.string.category_optional)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown)
                    },
                    placeholder = { Text(stringResource(R.string.no_category_selected)) }
                )

                ExposedDropdownMenu(
                    expanded = showCategoryDropdown,
                    onDismissRequest = { showCategoryDropdown = false }
                ) {
                    // Add option to clear selection
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(R.string.no_category),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        onClick = {
                            selectedCategory = null
                            showCategoryDropdown = false
                        }
                    )

                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = category.icon,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(category.name)
                                }
                            },
                            onClick = {
                                selectedCategory = category
                                showCategoryDropdown = false
                            }
                        )
                    }
                }
            }

            // Account selection
            ExposedDropdownMenuBox(
                expanded = showAccountDropdown,
                onExpandedChange = { showAccountDropdown = !showAccountDropdown }
            ) {
                OutlinedTextField(
                    value = selectedAccount?.name ?: "",
                    onValueChange = {},
                    label = { Text(stringResource(R.string.account_optional)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showAccountDropdown)
                    },
                    placeholder = { Text(stringResource(R.string.no_account_selected)) }
                )

                ExposedDropdownMenu(
                    expanded = showAccountDropdown,
                    onDismissRequest = { showAccountDropdown = false }
                ) {
                    // Add option to clear selection
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(R.string.no_account),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        onClick = {
                            selectedAccount = null
                            showAccountDropdown = false
                        }
                    )

                    accounts.forEach { account ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(account.name)
                                    account.description?.let { desc ->
                                        if (desc.isNotBlank()) {
                                            Text(
                                                text = desc,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            },
                            onClick = {
                                selectedAccount = account
                                showAccountDropdown = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = amountDisplay,
                onValueChange = { newValue ->
                    // Only allow digits and format as Indonesian Rupiah
                    val digitsOnly = newValue.replace(Regex("[^0-9]"), "")
                    if (digitsOnly.length <= 15) { // Reasonable limit for amount
                        val longValue = digitsOnly.toLongOrNull() ?: 0L
                        amountValue = longValue
                        amountDisplay = RupiahFormatter.formatRupiahDisplay(longValue)
                    }
                },
                label = { Text(stringResource(R.string.amount)) },
                leadingIcon = {
                    Text(
                        text = stringResource(R.string.rupiah_prefix),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.zero)) }
            )

            OutlinedTextField(
                value = vendor,
                onValueChange = { vendor = it },
                label = { Text(stringResource(R.string.vendor)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.vendor_placeholder)) }
            )

            // Date picker field
            OutlinedTextField(
                value = DateUtil.formatDateForDisplay(selectedDate),
                onValueChange = { },
                label = { Text(stringResource(R.string.bill_date)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = stringResource(R.string.select_date)
                        )
                    }
                }
            )

            // Image upload section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large))) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.attachment_optional),
                            style = MaterialTheme.typography.labelLarge
                        )

                        if (selectedImageUri != null || !savedImagePath.isNullOrEmpty()) {
                            IconButton(
                                onClick = {
                                    selectedImageUri = null
                                    savedImagePath?.let {
                                        ImageUtil.deleteImageFromInternalStorage(it)
                                    }
                                    savedImagePath = null
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.remove_image)
                                )
                            }
                        }
                    }

                    // Show existing image or selected new image
                    val imageToShow = selectedImageUri ?: savedImagePath?.let {
                        ImageUtil.getImageFileUri(context, it)
                    }

                    imageToShow?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = "Bill Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_radius_small))),
                            contentScale = ContentScale.Crop
                        )
                    }

                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = null)
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                        Text(stringResource(R.string.choose_image))
                    }
                }
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isEditing) Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small)) else Arrangement.Center
            ) {
                if (isEditing) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }

                Button(
                    onClick = {
                        val billToSave = Bill(
                            id = bill?.id ?: System.currentTimeMillis(),
                            title = title,
                            category = selectedCategory?.name,
                            amount = amountValue.toDouble(),
                            vendor = vendor,
                            billDate = DateUtil.localDateToIsoString(selectedDate),
                            imagePath = savedImagePath,
                            accountId = selectedAccount?.id
                        )
                        onSave(billToSave)
                    },
                    modifier = if (isEditing) Modifier.weight(1f) else Modifier.fillMaxWidth(),
                    enabled = title.isNotBlank() && amountValue > 0
                ) {
                    Text(
                        if (isEditing) stringResource(R.string.save_changes)
                        else stringResource(R.string.save_bill)
                    )
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            // Convert milliseconds back to LocalDate (API 24 compatible)
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = millis
                            }
                            selectedDate = LocalDate.of(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH) + 1, // Calendar months are 0-based
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_bill)) },
            text = {
                Text(stringResource(R.string.delete_bill_message, title))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        bill?.let { onDelete?.invoke(it.id) }
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Text selection dialog for scanned data
    if (showTextSelectionDialog) {
        TextSelectionDialog(
            scannedTexts = scannedTexts,
            onTextSelected = { selectedTitle, selectedAmount, selectedVendor ->
                // Apply selected text to form fields
                if (selectedTitle.isNotBlank()) title = selectedTitle
                if (selectedVendor.isNotBlank()) vendor = selectedVendor
                if (selectedAmount.isNotBlank()) {
                    val extractedAmount = BillFormUtils.extractAmountFromText(selectedAmount)
                    if (extractedAmount > 0) {
                        amountValue = extractedAmount.toLong()
                        amountDisplay = BillFormUtils.getFormattedAmountDisplay(extractedAmount)
                    }
                }
                showTextSelectionDialog = false
            },
            onDismiss = { showTextSelectionDialog = false }
        )
    }

    // Scanning progress indicator
    if (scanningProgress) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.scanning)) },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(dimensionResource(R.dimen.scanner_dialog_progress_size)))
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.scanner_dialog_progress_spacing)))
                    Text(stringResource(R.string.processing_image))
                }
            },
            confirmButton = {}
        )
    }
}
