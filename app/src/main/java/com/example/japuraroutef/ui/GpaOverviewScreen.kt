package com.example.japuraroutef.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.japuraroutef.model.SemesterId
import com.example.japuraroutef.ui.theme.*
import com.example.japuraroutef.viewmodel.GpaViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpaOverviewScreen(
    viewModel: GpaViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (SemesterId) -> Unit,
    onNavigateToStatistics: () -> Unit = {}
) {
    var showAddSemesterDialog by remember { mutableStateOf(false) }
    var semesterToDelete by remember { mutableStateOf<com.example.japuraroutef.model.SemesterGpaResponse?>(null) }
    var isDeleting by remember { mutableStateOf(false) }

    val semesterGpas by remember { derivedStateOf { viewModel.semesterGpas.toList() } }
    val cgpa by viewModel.cgpa
    val totalCredits by viewModel.totalCredits
    val isLoading by viewModel.isLoading

    // Load modules once when screen opens (for pre-population in detail screen)
    LaunchedEffect(Unit) {
        viewModel.loadAllModules()
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "GPA Overview",
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurfaceDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Back",
                            tint = OnSurfaceDark
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = CircleShape,
                        color = PrimaryContainerDark,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        IconButton(onClick = onNavigateToStatistics) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = "Statistics",
                                tint = Primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSemesterDialog = true },
                containerColor = Primary,
                contentColor = SurfaceDark,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Semester",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Add Semester",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
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
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // CGPA Card
                item {
                    CgpaCard(cgpa = cgpa ?: 0f, totalCredits = totalCredits ?: 0f)
                }

                // Section Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Academic History",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurfaceDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Semester List
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Primary)
                        }
                    }
                } else if (semesterGpas.isEmpty()) {
                    item {
                        EmptyStateCard()
                    }
                } else {
                    itemsIndexed(semesterGpas) { index, semester ->
                        SemesterCard(
                            semester = semester,
                            onClick = { onNavigateToDetail(semester.semesterId) },
                            onDelete = { semesterToDelete = semester }
                        )
                    }
                }

                // Bottom padding for FAB and navigation
                item {
                    Spacer(modifier = Modifier.height(180.dp))
                }
            }
        }
    }

    // Add Semester Dialog
    if (showAddSemesterDialog) {
        AddSemesterDialog(
            existingSemesters = semesterGpas.map { it.semesterId }.toSet(),
            onDismiss = { showAddSemesterDialog = false },
            onSelect = { semesterId ->
                showAddSemesterDialog = false
                onNavigateToDetail(semesterId)
            }
        )
    }

    // Delete Confirmation Dialog
    semesterToDelete?.let { semester ->
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) {
                    semesterToDelete = null
                }
            },
            title = { Text("Delete Semester?", color = OnSurfaceDark) },
            text = {
                Text(
                    "Are you sure you want to delete ${semester.semesterId.getDisplayName()}? This action cannot be undone.",
                    color = OnSurfaceVariantDark
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isDeleting) {
                            isDeleting = true
                            viewModel.deleteSemester(
                                semester.semesterId,
                                onSuccess = {
                                    isDeleting = false
                                    com.example.japuraroutef.utils.ToastManager.showSuccess("Semester deleted successfully")
                                    semesterToDelete = null
                                },
                                onError = { error ->
                                    isDeleting = false
                                    com.example.japuraroutef.utils.ToastManager.showError(error)
                                    semesterToDelete = null
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Tertiary
                    ),
                    enabled = !isDeleting
                ) {
                    if (isDeleting) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = OnSurfaceDark,
                                strokeWidth = 2.dp
                            )
                            Text("Deleting...")
                        }
                    } else {
                        Text("Delete")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (!isDeleting) {
                            semesterToDelete = null
                        }
                    },
                    enabled = !isDeleting
                ) {
                    Text("Cancel", color = OnSurfaceVariantDark)
                }
            },
            containerColor = SurfaceContainerDark
        )
    }
}

@Composable
private fun CgpaCard(cgpa: Float, totalCredits: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(SurfaceContainerDark)
            .border(1.dp, OutlineDark.copy(alpha = 0.1f), RoundedCornerShape(32.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "CUMULATIVE GPA",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariantDark,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = String.format(Locale.US, "%.2f", cgpa),
                style = MaterialTheme.typography.displayLarge,
                color = Primary,
                fontWeight = FontWeight.Medium,
                fontSize = 72.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceContainerHighestDark,
                    border = BorderStroke(1.dp, OutlineDark.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${totalCredits.toInt()} Credits",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariantDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SemesterCard(
    semester: com.example.japuraroutef.model.SemesterGpaResponse,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceContainerDark,
        border = BorderStroke(1.dp, Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = SecondaryContainerDark,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = OnSecondaryContainerDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = semester.semesterId.getDisplayName(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurfaceDark,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${semester.totalCredits} Credits • ${semester.subjects.size} Courses",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariantDark
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format(Locale.US, "%.2f", semester.gpa),
                    style = MaterialTheme.typography.titleLarge,
                    color = Primary,
                    fontWeight = FontWeight.Medium
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Tertiary.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard() {
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
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = null,
                tint = OnSurfaceVariantDark,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "No semesters yet",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariantDark
            )
            Text(
                text = "Tap the + button to add your first semester",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariantDark.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun AddSemesterDialog(
    existingSemesters: Set<SemesterId>,
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Semester",
                        style = MaterialTheme.typography.headlineSmall,
                        color = OnSurfaceDark
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = OnSurfaceVariantDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Select which semester you want to add",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariantDark,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(SemesterId.entries.size) { index ->
                        val semester = SemesterId.entries[index]
                        val isAlreadyAdded = existingSemesters.contains(semester)

                        Surface(
                            onClick = {
                                if (!isAlreadyAdded) {
                                    onSelect(semester)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isAlreadyAdded)
                                SurfaceContainerHighestDark.copy(alpha = 0.5f)
                            else
                                SurfaceContainerHighestDark,
                            enabled = !isAlreadyAdded
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = semester.getDisplayName(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (isAlreadyAdded)
                                            OnSurfaceDark.copy(alpha = 0.5f)
                                        else
                                            OnSurfaceDark,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (semester.requiresFocusArea()) {
                                        Text(
                                            text = "Focus area required",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Primary.copy(alpha = if (isAlreadyAdded) 0.5f else 1f)
                                        )
                                    }
                                }

                                if (isAlreadyAdded) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Primary.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "Added",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = Primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
