package com.example.japuraroutef.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.japuraroutef.model.*
import com.example.japuraroutef.ui.theme.*
import com.example.japuraroutef.utils.ToastManager
import com.example.japuraroutef.viewmodel.GpaViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpaSemesterDetailScreen(
    viewModel: GpaViewModel,
    semesterId: SemesterId,
    onNavigateBack: () -> Unit,
    userFocusArea: FocusArea? = null
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showSemesterPicker by remember { mutableStateOf(false) }
    var showFocusAreaDialog by remember { mutableStateOf(false) }
    var showExitConfirmation by remember { mutableStateOf(false) }
    var hasUnsavedChanges by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val currentSemesterId by viewModel.currentSemesterId
    val subjects by remember { derivedStateOf { viewModel.subjects.toList() } }
    val calculatedGpa by viewModel.calculatedGpa
    val isLoading by viewModel.isLoading
    val needsFocusArea by viewModel.needsFocusAreaSelection

    // Debug: Track subjects changes
    LaunchedEffect(subjects.size) {
        android.util.Log.d("GpaSemesterDetail", "Subjects size changed: ${subjects.size}")
        android.util.Log.d("GpaSemesterDetail", "Subjects: ${subjects.map { it.subjectName }}")
    }

    // Track changes
    LaunchedEffect(subjects.size) {
        if (subjects.isNotEmpty()) {
            hasUnsavedChanges = true
        }
    }

    // Show focus area dialog if needed
    LaunchedEffect(needsFocusArea) {
        if (needsFocusArea) {
            showFocusAreaDialog = true
        }
    }

    // Single LaunchedEffect that handles data loading intelligently
    LaunchedEffect(semesterId, userFocusArea, viewModel.semesterGpas.size) {
        android.util.Log.d("GpaSemesterDetail", "LaunchedEffect triggered - semesterId: $semesterId, cachedSemesters: ${viewModel.semesterGpas.size}")

        // Always try to load semester data
        // If cache is empty, it will pre-populate from modules
        // If cache has data, it will use that
        viewModel.startEditingSemester(semesterId, userFocusArea)
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Semester Details",
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurfaceDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (hasUnsavedChanges) {
                            showExitConfirmation = true
                        } else {
                            viewModel.clearEditingState()
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Back",
                            tint = OnSurfaceDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Save Button with Loading Animation
                if (hasUnsavedChanges && subjects.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = {
                            if (!isSaving) {
                                isSaving = true
                                viewModel.saveCurrentSemester(
                                    onSuccess = {
                                        isSaving = false
                                        ToastManager.showSuccess("GPA saved successfully!")
                                        hasUnsavedChanges = false
                                        onNavigateBack()
                                    },
                                    onError = { error ->
                                        isSaving = false
                                        ToastManager.showError(error)
                                    }
                                )
                            }
                        },
                        containerColor = Primary,
                        contentColor = SurfaceDark,
                        modifier = Modifier.size(56.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = SurfaceDark,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Add Subject Button (disabled during save)
                FloatingActionButton(
                    onClick = {
                        if (!isSaving) {
                            showAddDialog = true
                        }
                    },
                    containerColor = if (isSaving) Secondary.copy(alpha = 0.5f) else Secondary,
                    contentColor = SurfaceDark,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Subject",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundDark),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Semester Header with GPA
                item {
                    SemesterHeaderCard(
                        semesterId = currentSemesterId ?: semesterId,
                        gpa = calculatedGpa,
                        totalCredits = subjects.sumOf { it.credits.toDouble() }.toFloat(),
                        subjectCount = subjects.size,
                        showGpa = subjects.isNotEmpty(),
                        onChangeSemester = { showSemesterPicker = true }
                    )
                }

                // Subjects Section Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Subjects",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurfaceDark,
                            fontWeight = FontWeight.Medium
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "${subjects.size} Subjects",
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Module List
                if (subjects.isEmpty()) {
                    item {
                        EmptySubjectsCard(onAddClick = { showAddDialog = true })
                    }
                } else {
                    itemsIndexed(subjects) { index, subject ->
                        SubjectCard(
                            subject = subject,
                            index = index,
                            onGradeChange = { idx, grade -> viewModel.updateSubjectGrade(idx, grade) },
                            onCreditsChange = { idx, credits -> viewModel.updateSubjectCredits(idx, credits) },
                            onDelete = { viewModel.removeSubject(index) }
                        )
                    }
                }

                // Bottom spacing for FAB
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    // Add Subject Dialog
    if (showAddDialog) {
        AddSubjectDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { subjectName, credits ->
                viewModel.addSubject(subjectName, credits, "A+") // Default grade A+
                showAddDialog = false
            }
        )
    }

    // Focus Area Selection Dialog
    if (showFocusAreaDialog) {
        FocusAreaSelectionDialog(
            onDismiss = {
                showFocusAreaDialog = false
                onNavigateBack()
            },
            onSelect = { focusArea ->
                viewModel.setUserFocusArea(focusArea)
                showFocusAreaDialog = false
                viewModel.startEditingSemester(semesterId, focusArea)
            }
        )
    }

    // Exit Confirmation Dialog
    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = { showExitConfirmation = false },
            title = { Text("Unsaved Changes", color = OnSurfaceDark) },
            text = {
                Text(
                    "You have unsaved changes. Are you sure you want to leave without saving?",
                    color = OnSurfaceVariantDark
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitConfirmation = false
                        viewModel.clearEditingState()
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Tertiary
                    )
                ) {
                    Text("Leave")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirmation = false }) {
                    Text("Stay", color = Primary)
                }
            },
            containerColor = SurfaceContainerDark
        )
    }

    // Semester Picker Dialog
    if (showSemesterPicker) {
        SemesterPickerDialog(
            currentSemesterId = currentSemesterId ?: semesterId,
            onDismiss = { showSemesterPicker = false },
            onSelect = { selectedSemester ->
                viewModel.startEditingSemester(selectedSemester)
                showSemesterPicker = false
            }
        )
    }
}

@Composable
private fun SemesterHeaderCard(
    semesterId: SemesterId,
    gpa: Float,
    totalCredits: Float,
    subjectCount: Int,
    showGpa: Boolean,
    onChangeSemester: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = SecondaryContainerDark
    ) {
        Box {
            // Background decoration
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .offset(x = 200.dp, y = (-80).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = OnSecondaryContainerDark.copy(alpha = 0.1f),
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = OnSecondaryContainerDark,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "CURRENT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSecondaryContainerDark,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = semesterId.getDisplayName(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = OnSecondaryContainerDark,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Information Technology",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSecondaryContainerDark.copy(alpha = 0.8f)
                        )
                    }

                    // GPA Display (when subjects exist)
                    if (showGpa) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "GPA",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSecondaryContainerDark.copy(alpha = 0.7f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = String.format(Locale.US, "%.2f", gpa),
                                style = MaterialTheme.typography.displaySmall,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Primary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "${totalCredits.toInt()} Credits",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSecondaryContainerDark.copy(alpha = 0.8f)
                                )
                            }
                            Text(
                                text = "$subjectCount Subjects",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSecondaryContainerDark.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectCard(
    subject: Subject,
    index: Int,
    onGradeChange: (Int, String) -> Unit,
    onCreditsChange: (Int, Float) -> Unit,
    onDelete: () -> Unit
) {
    var expandedGrade by remember { mutableStateOf(false) }
    var expandedCredits by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceContainerDark,
        border = BorderStroke(1.dp, OutlineDark.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Tertiary.copy(alpha = 0.2f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = null,
                                tint = Tertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = subject.subjectName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceDark,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Tertiary.copy(alpha = 0.6f)
                    )
                }
            }

            // Grade and Credits (Editable)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Grade Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedGrade,
                    onExpandedChange = { expandedGrade = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = if (subject.grade.isNotEmpty()) subject.grade else "Select",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Grade", style = MaterialTheme.typography.labelSmall) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGrade) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OutlineDark,
                            focusedLabelColor = Primary,
                            unfocusedLabelColor = OnSurfaceVariantDark,
                            focusedTextColor = OnSurfaceDark,
                            unfocusedTextColor = OnSurfaceDark
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                    )

                    ExposedDropdownMenu(
                        expanded = expandedGrade,
                        onDismissRequest = { expandedGrade = false },
                        modifier = Modifier.background(SurfaceContainerDark)
                    ) {
                        Grade.entries.forEach { grade ->
                            DropdownMenuItem(
                                text = { Text(grade.value, color = OnSurfaceDark) },
                                onClick = {
                                    onGradeChange(index, grade.value)
                                    expandedGrade = false
                                }
                            )
                        }
                    }
                }

                // Credits Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedCredits,
                    onExpandedChange = { expandedCredits = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = if (subject.credits > 0) subject.credits.toString() else "Credits",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Credits", style = MaterialTheme.typography.labelSmall) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCredits) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OutlineDark,
                            focusedLabelColor = Primary,
                            unfocusedLabelColor = OnSurfaceVariantDark,
                            focusedTextColor = OnSurfaceDark,
                            unfocusedTextColor = OnSurfaceDark
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCredits,
                        onDismissRequest = { expandedCredits = false },
                        modifier = Modifier.background(SurfaceContainerDark)
                    ) {
                        (1..10).forEach { credit ->
                            DropdownMenuItem(
                                text = { Text(credit.toString(), color = OnSurfaceDark) },
                                onClick = {
                                    onCreditsChange(index, credit.toFloat())
                                    expandedCredits = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySubjectsCard(onAddClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = null,
                tint = OnSurfaceVariantDark,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "No subjects added",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariantDark
            )
            Text(
                text = "Add your first subject to calculate GPA",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariantDark.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Float) -> Unit
) {
    var subjectName by remember { mutableStateOf("") }
    var credits by remember { mutableStateOf("") }
    var expandedCredits by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = SurfaceContainerDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Add Custom Subject",
                    style = MaterialTheme.typography.headlineSmall,
                    color = OnSurfaceDark
                )

                Text(
                    text = "You can add grade later by clicking on the subject card",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariantDark
                )

                OutlinedTextField(
                    value = subjectName,
                    onValueChange = { subjectName = it },
                    label = { Text("Subject Name") },
                    placeholder = { Text("e.g., CS101 - Data Structures") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = OutlineDark,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = OnSurfaceVariantDark,
                        focusedTextColor = OnSurfaceDark,
                        unfocusedTextColor = OnSurfaceDark
                    )
                )

                ExposedDropdownMenuBox(
                    expanded = expandedCredits,
                    onExpandedChange = { expandedCredits = it }
                ) {
                    OutlinedTextField(
                        value = credits.ifEmpty { "Select Credits" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Credits") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCredits) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OutlineDark,
                            focusedLabelColor = Primary,
                            unfocusedLabelColor = OnSurfaceVariantDark,
                            focusedTextColor = OnSurfaceDark,
                            unfocusedTextColor = OnSurfaceDark
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCredits,
                        onDismissRequest = { expandedCredits = false },
                        modifier = Modifier.background(SurfaceContainerDark)
                    ) {
                        (1..10).forEach { credit ->
                            DropdownMenuItem(
                                text = { Text(credit.toString(), color = OnSurfaceDark) },
                                onClick = {
                                    credits = credit.toString()
                                    expandedCredits = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = OnSurfaceVariantDark)
                    }
                    Button(
                        onClick = {
                            if (subjectName.isNotBlank() && credits.isNotBlank()) {
                                onAdd(subjectName, credits.toFloat())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = SurfaceDark
                        ),
                        enabled = subjectName.isNotBlank() && credits.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
private fun SemesterPickerDialog(
    currentSemesterId: SemesterId,
    onDismiss: () -> Unit,
    onSelect: (SemesterId) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = SurfaceContainerDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Select Semester",
                    style = MaterialTheme.typography.headlineSmall,
                    color = OnSurfaceDark,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SemesterId.entries.size) { index ->
                        val semester = SemesterId.entries[index]
                        Surface(
                            onClick = { onSelect(semester) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (semester == currentSemesterId)
                                Primary.copy(alpha = 0.2f)
                            else
                                SurfaceContainerHighestDark
                        ) {
                            Text(
                                text = semester.getDisplayName(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (semester == currentSemesterId) Primary else OnSurfaceDark,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FocusAreaSelectionDialog(
    onDismiss: () -> Unit,
    onSelect: (FocusArea) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = SurfaceContainerDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Select Your Focus Area",
                    style = MaterialTheme.typography.headlineSmall,
                    color = OnSurfaceDark,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Starting from Year 3, you need to select a specialization area. This will determine which modules are shown for this and future semesters.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariantDark,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FocusAreaButton("Software Engineering", FocusArea.SOFTWARE) { onSelect(it) }
                    FocusAreaButton("Networking", FocusArea.NETWORKING) { onSelect(it) }
                    FocusAreaButton("Multimedia", FocusArea.MULTIMEDIA) { onSelect(it) }
                }
            }
        }
    }
}

@Composable
private fun FocusAreaButton(
    title: String,
    focusArea: FocusArea,
    onSelect: (FocusArea) -> Unit
) {
    Button(
        onClick = { onSelect(focusArea) },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = SurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

