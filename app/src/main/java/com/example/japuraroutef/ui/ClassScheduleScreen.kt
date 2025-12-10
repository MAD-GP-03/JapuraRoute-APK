package com.example.japuraroutef.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.japuraroutef.model.TimetableEntry
import com.example.japuraroutef.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassScheduleScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val timetableData = viewModel.timetableData.value
    val userFocusArea = viewModel.userFocusArea.value

    // State for selected date and filter
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showAllFocusAreas by remember { mutableStateOf(false) }

    // Get classes for selected day with filtering
    val dayClasses = remember(timetableData, selectedDate, userFocusArea, showAllFocusAreas) {
        val dayName = selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        timetableData?.timetable?.timetable
            ?.filter { it.day.equals(dayName, ignoreCase = true) }
            ?.filter { classEntry ->
                if (showAllFocusAreas) {
                    // Show all classes when filter is enabled
                    true
                } else {
                    // Show only ITC (common) and user's focus area
                    classEntry.matchesFocusArea(userFocusArea)
                }
            }
            ?.sortedBy { it.start_time }
            ?: emptyList()
    }

    Scaffold(
        topBar = {
            ClassScheduleTopBar(
                onNavigateBack = onNavigateBack,
                showAllFocusAreas = showAllFocusAreas,
                onFilterToggle = { showAllFocusAreas = !showAllFocusAreas }
            )
        },
        bottomBar = {
            // Use HomeScreen's bottom navigation bar with Schedule tab selected
            HomeBottomNavigationBar(
                selectedTab = 1, // Schedule/Explore tab
                onHomeClick = onNavigateBack
            )
        },
        containerColor = com.example.japuraroutef.ui.theme.SurfaceDark
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // Extra padding for 3-button navigation
        ) {
            // Week selector
            item {
                WeekSelector(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )
            }

            // Date display
            item {
                DateDisplay(selectedDate = selectedDate)
            }

            // Classes list
            if (dayClasses.isEmpty()) {
                item {
                    NoClassesMessage()
                }
            } else {
                items(dayClasses) { classEntry ->
                    ClassCard(classEntry = classEntry)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClassScheduleTopBar(
    onNavigateBack: () -> Unit,
    showAllFocusAreas: Boolean,
    onFilterToggle: () -> Unit
) {
    Surface(
        color = com.example.japuraroutef.ui.theme.SurfaceDark,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding() // Add status bar padding
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = com.example.japuraroutef.ui.theme.OnSurfaceDark
                    )
                }
                Text(
                    text = "Class Schedule",
                    style = MaterialTheme.typography.headlineSmall,
                    color = com.example.japuraroutef.ui.theme.OnSurfaceDark
                )
            }

            // Filter button
            Surface(
                shape = CircleShape,
                color = if (showAllFocusAreas)
                    com.example.japuraroutef.ui.theme.PrimaryContainerDark
                else
                    com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
                modifier = Modifier.size(40.dp)
            ) {
                IconButton(
                    onClick = onFilterToggle
                ) {
                    Icon(
                        imageVector = if (showAllFocusAreas) Icons.Default.FilterAltOff else Icons.Default.FilterAlt,
                        contentDescription = if (showAllFocusAreas) "Hide other subjects" else "Show all subjects",
                        tint = if (showAllFocusAreas)
                            com.example.japuraroutef.ui.theme.OnPrimaryContainerDark
                        else
                            com.example.japuraroutef.ui.theme.OnSurfaceDark
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekSelector(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // Get start of week (Monday)
    val startOfWeek = selectedDate.with(java.time.DayOfWeek.MONDAY)
    val weekDates = (0..4).map { startOfWeek.plusDays(it.toLong()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous week button
        IconButton(
            onClick = { onDateSelected(selectedDate.minusWeeks(1)) },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous week",
                tint = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
            )
        }

        // Week days
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            weekDates.forEach { date ->
                DaySelector(
                    date = date,
                    isSelected = date == selectedDate,
                    onClick = { onDateSelected(date) }
                )
            }
        }

        // Next week button
        IconButton(
            onClick = { onDateSelected(selectedDate.plusWeeks(1)) },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next week",
                tint = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
            )
        }
    }
}

@Composable
private fun DaySelector(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase()
    val dayOfMonth = date.dayOfMonth

    Surface(
        shape = CircleShape,
        color = if (isSelected) com.example.japuraroutef.ui.theme.PrimaryContainerDark else Color.Transparent,
        modifier = Modifier
            .size(width = 48.dp, height = 64.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayOfWeek,
                fontSize = 12.sp,
                color = if (isSelected)
                    com.example.japuraroutef.ui.theme.OnPrimaryContainerDark
                else
                    com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dayOfMonth.toString(),
                fontSize = 18.sp,
                color = if (isSelected)
                    com.example.japuraroutef.ui.theme.OnPrimaryContainerDark
                else
                    com.example.japuraroutef.ui.theme.OnSurfaceDark,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DateDisplay(selectedDate: LocalDate) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = com.example.japuraroutef.ui.theme.SurfaceContainerHighDark,
            modifier = Modifier.wrapContentWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = com.example.japuraroutef.ui.theme.OnSurfaceDark
                )
            }
        }
    }
}

@Composable
private fun ClassCard(classEntry: TimetableEntry) {
    // Determine card color based on type
    val (backgroundColor, textColor) = when {
        classEntry.module_name.contains("canceled", ignoreCase = true) ->
            Pair(com.example.japuraroutef.ui.theme.ErrorContainerDark, com.example.japuraroutef.ui.theme.OnErrorContainerDark)
        classEntry.type.contains("P", ignoreCase = true) ->
            Pair(com.example.japuraroutef.ui.theme.TertiaryContainerDark, com.example.japuraroutef.ui.theme.OnTertiaryContainerDark)
        classEntry.type.contains("L", ignoreCase = true) ->
            Pair(com.example.japuraroutef.ui.theme.SecondaryContainerDark, com.example.japuraroutef.ui.theme.OnSecondaryContainerDark)
        else ->
            Pair(com.example.japuraroutef.ui.theme.SurfaceContainerHighDark, com.example.japuraroutef.ui.theme.OnSurfaceDark)
    }

    val isCanceled = classEntry.module_name.contains("canceled", ignoreCase = true)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Time column
        Column(
            modifier = Modifier.width(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = classEntry.start_time,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
            )
            Text(
                text = if (classEntry.start_time.split(":")[0].toIntOrNull()?.let { it < 12 } == true) "AM" else "PM",
                fontSize = 12.sp,
                color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
            )
        }

        // Class card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = backgroundColor,
            modifier = Modifier
                .weight(1f)
                .alpha(if (isCanceled) 0.8f else 1f),
            border = if (backgroundColor == com.example.japuraroutef.ui.theme.SurfaceContainerHighDark) {
                androidx.compose.foundation.BorderStroke(1.dp, com.example.japuraroutef.ui.theme.OutlineDark)
            } else null
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = classEntry.module_name,
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    textDecoration = if (isCanceled) TextDecoration.LineThrough else null
                )
                Text(
                    text = if (isCanceled) "CANCELED" else classEntry.location,
                    fontSize = 14.sp,
                    color = if (isCanceled) textColor else textColor.copy(alpha = 0.9f),
                    fontWeight = if (isCanceled) FontWeight.Medium else FontWeight.Normal
                )
                Text(
                    text = classEntry.lecturer,
                    fontSize = 14.sp,
                    color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                    textDecoration = if (isCanceled) TextDecoration.LineThrough else null
                )

                // Show module code and type
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = classEntry.module_code,
                        fontSize = 12.sp,
                        color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
                    )
                    Text(
                        text = classEntry.type,
                        fontSize = 12.sp,
                        color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun NoClassesMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.FreeBreakfast,
            contentDescription = null,
            tint = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = "No classes scheduled",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = com.example.japuraroutef.ui.theme.OnSurfaceDark
        )
        Text(
            text = "Enjoy your free time!",
            fontSize = 14.sp,
            color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
        )
    }
}

// Extension function to check if TimetableEntry matches focus area
private fun TimetableEntry.matchesFocusArea(userFocusArea: com.example.japuraroutef.model.FocusArea?): Boolean {
    // COMMON subjects (ITC) are for everyone
    if (focus_area.contains("ITC")) {
        return true
    }

    // If user has no specific focus area, only show COMMON subjects
    if (userFocusArea == null || userFocusArea == com.example.japuraroutef.model.FocusArea.COMMON) {
        return false
    }

    // Map user's focus area to timetable focus area code
    val userFocusCode = when (userFocusArea) {
        com.example.japuraroutef.model.FocusArea.SOFTWARE -> "ITS"
        com.example.japuraroutef.model.FocusArea.NETWORKING -> "ITN"
        com.example.japuraroutef.model.FocusArea.MULTIMEDIA -> "ITM"
        com.example.japuraroutef.model.FocusArea.COMMON -> "ITC"
    }

    return focus_area.contains(userFocusCode)
}

