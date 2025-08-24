package com.adilstudio.project.onevault.presentation.bill

import android.Manifest
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.DateUtil
import com.adilstudio.project.onevault.core.util.ImageUtil
import com.adilstudio.project.onevault.core.util.PermissionUtil
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.presentation.account.AccountViewModel
import com.adilstudio.project.onevault.presentation.bill.category.BillCategoryViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.time.LocalDate
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBillScreen(
    bill: Bill? = null, // null for add, non-null for edit
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

    // Image handling state
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var savedImagePath by remember { mutableStateOf(bill?.imagePath) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // MLKit scanning state
    var scannedTexts by remember { mutableStateOf<List<String>>(emptyList()) }
    var showTextSelectionDialog by remember { mutableStateOf(false) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var isScanning by remember { mutableStateOf(false) }

    // Permission state
    var showPermissionDialog by remember { mutableStateOf(false) }
    var hasCameraPermission by remember {
        mutableStateOf(PermissionUtil.isCameraPermissionGranted(context))
    }

    // Update selectedCategory when categories are loaded
    LaunchedEffect(categories, bill) {
        if (bill != null && selectedCategory == null) {
            selectedCategory = categories.find { it.name == bill.category }
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

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            isScanning = true
            val image = InputImage.fromFilePath(context, cameraImageUri!!)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val texts = visionText.textBlocks
                        .flatMap { it.lines }
                        .map { it.text.trim() }
                        .filter { it.isNotEmpty() }

                    scannedTexts = texts
                    isScanning = false

                    if (texts.isNotEmpty()) {
                        showTextSelectionDialog = true
                    }
                }
                .addOnFailureListener {
                    isScanning = false
                }
        }
    }

    // Function to launch camera
    fun launchCamera() {
        val imageFile = File(context.cacheDir, "camera_image_${System.currentTimeMillis()}.jpg")
        cameraImageUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        cameraImageUri?.let { uri ->
            cameraLauncher.launch(uri)
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            launchCamera()
        } else {
            showPermissionDialog = true
        }
    }

    // Function to handle scan button click
    fun handleScanButtonClick() {
        if (hasCameraPermission) {
            launchCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) stringResource(R.string.edit_bill)
                               else stringResource(R.string.add_bill)
                    )
                },
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
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(dimensionResource(R.dimen.spacing_large))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_large))
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

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
                        .menuAnchor(),
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

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

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
                        .menuAnchor(),
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

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

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
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

            OutlinedTextField(
                value = vendor,
                onValueChange = { vendor = it },
                label = { Text(stringResource(R.string.vendor)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

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
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

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
                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
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

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))

            // Scan Bill Button
            Button(
                onClick = { handleScanButtonClick() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isScanning
            ) {
                if (isScanning) {
                    CircularProgressIndicator(modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small)))
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                    Text(stringResource(R.string.scanning))
                } else {
                    Text(stringResource(R.string.scan_bill))
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

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
                            accountId = selectedAccount?.id // Include account ID
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

    // Permission explanation dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(stringResource(R.string.camera_permission_required)) },
            text = {
                Text(stringResource(R.string.camera_permission_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text(stringResource(R.string.grant_permission))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Text Selection Dialog
    if (showTextSelectionDialog) {
        TextSelectionDialog(
            scannedTexts = scannedTexts,
            onTextSelected = { selectedTitle, selectedAmount, selectedVendor ->
                if (selectedTitle.isNotEmpty()) title = selectedTitle
                if (selectedAmount.isNotEmpty()) {
                    val extractedAmount =
                        RupiahFormatter.extractNumberFromRupiahText(selectedAmount)
                    amountValue = extractedAmount
                    amountDisplay = RupiahFormatter.formatRupiahDisplay(extractedAmount)
                }
                if (selectedVendor.isNotEmpty()) vendor = selectedVendor
                showTextSelectionDialog = false
            },
            onDismiss = { showTextSelectionDialog = false }
        )
    }
}

@Composable
fun TextSelectionDialog(
    scannedTexts: List<String>,
    onTextSelected: (title: String, amount: String, vendor: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTitle by remember { mutableStateOf("") }
    var selectedAmount by remember { mutableStateOf("") }
    var selectedVendor by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_scanned_text)) },
        text = {
            Column {
                Text(
                    stringResource(R.string.tap_text_assign),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

                // Show current selections
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(dimensionResource(R.dimen.spacing_small))) {
                        Text(
                            stringResource(R.string.selected),
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            stringResource(
                                R.string.title_field,
                                selectedTitle.ifEmpty { stringResource(R.string.none) }),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            stringResource(
                                R.string.amount_field,
                                selectedAmount.ifEmpty { stringResource(R.string.none) }),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            stringResource(
                                R.string.vendor_field,
                                selectedVendor.ifEmpty { stringResource(R.string.none) }),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

                LazyColumn(modifier = Modifier.height(200.dp)) {
                    itemsIndexed(scannedTexts) { index, text ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = dimensionResource(R.dimen.spacing_xs)),
                            colors = CardDefaults.cardColors(
                                containerColor = when (text) {
                                    selectedTitle -> MaterialTheme.colorScheme.primaryContainer
                                    selectedAmount -> MaterialTheme.colorScheme.secondaryContainer
                                    selectedVendor -> MaterialTheme.colorScheme.tertiaryContainer
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text, style = MaterialTheme.typography.bodyMedium)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Button(
                                        onClick = { selectedTitle = text },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedTitle == text)
                                                MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.outline
                                        )
                                    ) {
                                        Text(
                                            stringResource(R.string.title),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }

                                    Button(
                                        onClick = { selectedAmount = text },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedAmount == text)
                                                MaterialTheme.colorScheme.secondary
                                            else MaterialTheme.colorScheme.outline
                                        )
                                    ) {
                                        Text(
                                            stringResource(R.string.amount),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }

                                    Button(
                                        onClick = { selectedVendor = text },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedVendor == text)
                                                MaterialTheme.colorScheme.tertiary
                                            else MaterialTheme.colorScheme.outline
                                        )
                                    ) {
                                        Text(
                                            stringResource(R.string.vendor),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onTextSelected(selectedTitle, selectedAmount, selectedVendor)
                }
            ) {
                Text(stringResource(R.string.apply))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
