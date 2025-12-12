package com.example.japuraroutef.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.StrokeCap
import com.example.japuraroutef.ui.theme.*
import com.example.japuraroutef.viewmodel.GpaViewModel
import com.example.japuraroutef.repository.GpaRepository
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpaStatisticsScreen(
    viewModel: GpaViewModel,
    onNavigateBack: () -> Unit
) {
    val semesterGpas = viewModel.semesterGpas.toList()
    val cgpa = viewModel.cgpa.value

    // Calculate grade distribution from all subjects across all semesters
    val allSubjects = semesterGpas.flatMap { it.subjects }
    val gradeDistribution = calculateGradeDistribution(allSubjects)

    // Batch average state
    var batchAverage by remember { mutableStateOf<com.example.japuraroutef.model.BatchAverageResponse?>(null) }
    var isLoadingBatch by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = remember { GpaRepository(com.example.japuraroutef.api.ApiService.create(context)) }

    // Load batch average on screen open
    LaunchedEffect(Unit) {
        scope.launch {
            isLoadingBatch = true
            repository.getBatchAverage().onSuccess { data ->
                batchAverage = data
            }.onFailure {
                // Handle error silently
            }
            isLoadingBatch = false
        }
    }

    // Calculate pass rate
    val passRate = calculatePassRate(allSubjects)

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "Module Analytics",
                            style = MaterialTheme.typography.titleLarge,
                            color = OnSurfaceDark
                        )
                        Text(
                            text = "Faculty of Technology",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariantDark
                        )
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundDark)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Cards Row 1
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Current GPA Card
                    if (cgpa != null) {
                        CurrentGpaCard(
                            gpa = cgpa,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Cohort GPA Card
                    CohortGpaCard(
                        cohortGpa = batchAverage?.averageGpa ?: 0f,
                        isLoading = isLoadingBatch,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Stats Cards Row 2
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Pass Rate Card
                    PassRateCard(
                        passRate = passRate,
                        modifier = Modifier.weight(1f)
                    )

                    // At-Risk Card
                    AtRiskCard(
                        atRiskCount = calculateAtRiskSubjects(allSubjects),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // GPA Trend Chart
            item {
                GpaTrendCard(semesterGpas)
            }

            // Grade Distribution Chart
            item {
                GradeDistributionCard(gradeDistribution)
            }
            
        }
    }
}

@Composable
private fun CurrentGpaCard(gpa: Float, modifier: Modifier = Modifier) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(gpa) {
        animatedProgress.animateTo(
            targetValue = gpa / 4.0f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Surface(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(28.dp),
        color = PrimaryContainerDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(90.dp)
            ) {
                // Background circle
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = OnPrimaryContainerDark.copy(alpha = 0.2f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Animated progress circle
                Canvas(modifier = Modifier
                    .fillMaxSize()
                    .rotate(-90f)) {
                    drawArc(
                        color = Primary,
                        startAngle = 0f,
                        sweepAngle = 360f * animatedProgress.value,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = String.format(Locale.US, "%.2f", gpa),
                        style = MaterialTheme.typography.headlineMedium,
                        color = OnPrimaryContainerDark,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "/ 4.0",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnPrimaryContainerDark.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "CURRENT GPA",
                style = MaterialTheme.typography.labelSmall,
                color = OnPrimaryContainerDark.copy(alpha = 0.8f),
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun CohortGpaCard(cohortGpa: Float, isLoading: Boolean, modifier: Modifier = Modifier) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(cohortGpa, isLoading) {
        if (!isLoading) {
            animatedProgress.animateTo(
                targetValue = cohortGpa / 4.0f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            )
        }
    }

    Surface(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(28.dp),
        color = SecondaryContainerDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = OnSecondaryContainerDark,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "COHORT GPA",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSecondaryContainerDark.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(90.dp)
                ) {
                    // Background circle
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = OnSecondaryContainerDark.copy(alpha = 0.2f),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Animated progress circle
                    Canvas(modifier = Modifier
                        .fillMaxSize()
                        .rotate(-90f)) {
                        drawArc(
                            color = Secondary,
                            startAngle = 0f,
                            sweepAngle = 360f * animatedProgress.value,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.2f", cohortGpa),
                            style = MaterialTheme.typography.headlineMedium,
                            color = OnSecondaryContainerDark,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "/ 4.0",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSecondaryContainerDark.copy(alpha = 0.6f),
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "COHORT GPA",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSecondaryContainerDark.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun PassRateCard(passRate: Float, modifier: Modifier = Modifier) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(passRate) {
        animatedProgress.animateTo(
            targetValue = passRate / 100f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Surface(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(28.dp),
        color = TertiaryContainerDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(90.dp)
            ) {
                // Background circle
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = OnTertiaryContainerDark.copy(alpha = 0.2f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Animated progress circle
                Canvas(modifier = Modifier
                    .fillMaxSize()
                    .rotate(-90f)) {
                    drawArc(
                        color = Tertiary,
                        startAngle = 0f,
                        sweepAngle = 360f * animatedProgress.value,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = "${passRate.toInt()}%",
                    style = MaterialTheme.typography.headlineMedium,
                    color = OnTertiaryContainerDark,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "PASS RATE",
                style = MaterialTheme.typography.labelSmall,
                color = OnTertiaryContainerDark.copy(alpha = 0.8f),
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun AtRiskCard(atRiskCount: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(28.dp),
        color = com.example.japuraroutef.ui.theme.ErrorContainerDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "AT-RISK",
                style = MaterialTheme.typography.labelSmall,
                color = com.example.japuraroutef.ui.theme.OnErrorContainerDark.copy(alpha = 0.8f),
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )

            Column {
                Text(
                    text = "$atRiskCount",
                    style = MaterialTheme.typography.displaySmall,
                    color = com.example.japuraroutef.ui.theme.OnErrorContainerDark,
                    fontWeight = FontWeight.Light
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Modules require attention",
                    style = MaterialTheme.typography.labelSmall,
                    color = com.example.japuraroutef.ui.theme.OnErrorContainerDark.copy(alpha = 0.8f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun GradeDistributionCard(gradeDistribution: Map<String, Int>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = SurfaceContainerHighDark
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Grade Distribution",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurfaceDark,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Current Semester",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariantDark
                    )
                }

                // Legend
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Primary)
                        )
                        Text(
                            text = "Pass",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariantDark
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Tertiary)
                        )
                        Text(
                            text = "Fail",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariantDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Chart
            val grades = listOf("F", "D", "C", "B", "A", "A+")
            val maxCount = gradeDistribution.values.maxOrNull() ?: 1

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Y-axis labels
                Column(
                    modifier = Modifier
                        .width(24.dp)
                        .fillMaxHeight()
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$maxCount",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariantDark,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "${maxCount * 4 / 5}",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariantDark,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "${maxCount * 3 / 5}",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariantDark,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "${maxCount * 2 / 5}",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariantDark,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "${maxCount / 5}",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariantDark,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariantDark,
                        fontSize = 10.sp
                    )
                }

                // Bars
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Chart area with bars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        grades.forEach { grade ->
                            val count = gradeDistribution[grade] ?: 0
                            val heightFraction = if (maxCount > 0) count.toFloat() / maxCount else 0f
                            val color = when (grade) {
                                "F", "D" -> Tertiary
                                "C" -> PrimaryContainerDark
                                else -> Primary
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (count > 0) {
                                    Text(
                                        text = "$count",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceDark,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
                                        .fillMaxHeight(heightFraction)
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .background(color)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // X-axis labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        grades.forEach { grade ->
                            Text(
                                text = grade,
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariantDark,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GpaTrendCard(semesters: List<com.example.japuraroutef.model.SemesterGpaResponse>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = SurfaceContainerHighDark
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "GPA Trend",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurfaceDark,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Last ${semesters.size} Semesters",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariantDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (semesters.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No GPA data available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariantDark
                    )
                }
            } else {
                GpaTrendChart(semesters)
            }
        }
    }
}

@Composable
private fun GpaTrendChart(semesters: List<com.example.japuraroutef.model.SemesterGpaResponse>) {
    val sortedSemesters = remember(semesters) {
        semesters.sortedBy { it.semesterId.ordinal }.takeLast(6) // Show last 6 semesters
    }

    if (sortedSemesters.isEmpty()) return

    val maxGpa = 4.0f
    val minGpa = 0f

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val width = size.width
        val height = size.height
        val padding = 40.dp.toPx()
        val chartWidth = width - padding * 2
        val chartHeight = height - padding * 2

        // Draw Y-axis labels and grid lines
        val yLabels = listOf(4.0f, 3.5f, 3.0f, 2.5f, 2.0f, 1.5f, 1.0f, 0.5f, 0f)
        yLabels.forEach { label ->
            val y = padding + chartHeight * (1 - (label - minGpa) / (maxGpa - minGpa))

            // Grid line
            drawLine(
                color = com.example.japuraroutef.ui.theme.OutlineVariantDark,
                start = androidx.compose.ui.geometry.Offset(padding, y),
                end = androidx.compose.ui.geometry.Offset(width - padding, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
            )
        }

        // Calculate points for the line chart
        val points = sortedSemesters.mapIndexed { index, semester ->
            val x = padding + (chartWidth / (sortedSemesters.size - 1).coerceAtLeast(1)) * index
            val y = padding + chartHeight * (1 - (semester.gpa - minGpa) / (maxGpa - minGpa))
            androidx.compose.ui.geometry.Offset(x, y)
        }

        // Draw gradient fill under the line
        if (points.size > 1) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(points.first().x, height - padding)
                points.forEach { point ->
                    lineTo(point.x, point.y)
                }
                lineTo(points.last().x, height - padding)
                close()
            }

            drawPath(
                path = path,
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        com.example.japuraroutef.ui.theme.Primary.copy(alpha = 0.3f),
                        com.example.japuraroutef.ui.theme.Primary.copy(alpha = 0f)
                    ),
                    startY = padding,
                    endY = height - padding
                )
            )
        }

        // Draw the line
        if (points.size > 1) {
            val linePath = androidx.compose.ui.graphics.Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { point ->
                    lineTo(point.x, point.y)
                }
            }

            drawPath(
                path = linePath,
                color = com.example.japuraroutef.ui.theme.Primary,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
            )
        }

        // Draw points
        points.forEachIndexed { index, point ->
            // Outer circle (background)
            drawCircle(
                color = com.example.japuraroutef.ui.theme.BackgroundDark,
                radius = 6.dp.toPx(),
                center = point
            )

            // Inner circle (colored)
            drawCircle(
                color = if (index == points.size - 1) com.example.japuraroutef.ui.theme.Primary else com.example.japuraroutef.ui.theme.Primary,
                radius = 4.dp.toPx(),
                center = point
            )

            // Highlight last point
            if (index == points.size - 1) {
                drawCircle(
                    color = com.example.japuraroutef.ui.theme.Primary,
                    radius = 6.dp.toPx(),
                    center = point,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // X-axis labels (semester names)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        sortedSemesters.forEach { semester ->
            Text(
                text = getSemesterLabel(semester.semesterId),
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariantDark,
                fontSize = 10.sp,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

private fun getSemesterLabel(semesterId: com.example.japuraroutef.model.SemesterId): String {
    return when (semesterId) {
        com.example.japuraroutef.model.SemesterId.FIRST -> "S1"
        com.example.japuraroutef.model.SemesterId.SECOND -> "S2"
        com.example.japuraroutef.model.SemesterId.THIRD -> "S3"
        com.example.japuraroutef.model.SemesterId.FOURTH -> "S4"
        com.example.japuraroutef.model.SemesterId.FIFTH -> "S5"
        com.example.japuraroutef.model.SemesterId.SIXTH -> "S6"
        com.example.japuraroutef.model.SemesterId.SEVENTH -> "S7"
        com.example.japuraroutef.model.SemesterId.EIGHTH -> "S8"
    }
}

private fun calculateGradeDistribution(subjects: List<com.example.japuraroutef.model.Subject>): Map<String, Int> {
    val distribution = mutableMapOf(
        "A+" to 0,
        "A" to 0,
        "A-" to 0,
        "B+" to 0,
        "B" to 0,
        "B-" to 0,
        "C+" to 0,
        "C" to 0,
        "C-" to 0,
        "D+" to 0,
        "D" to 0,
        "E" to 0,
        "F" to 0
    )

    subjects.forEach { subject ->
        val grade = subject.grade
        if (distribution.containsKey(grade)) {
            distribution[grade] = distribution[grade]!! + 1
        }
    }

    // Group into simplified categories for display
    return mapOf(
        "A+" to (distribution["A+"] ?: 0),
        "A" to ((distribution["A"] ?: 0) + (distribution["A-"] ?: 0)),
        "B" to ((distribution["B+"] ?: 0) + (distribution["B"] ?: 0) + (distribution["B-"] ?: 0)),
        "C" to ((distribution["C+"] ?: 0) + (distribution["C"] ?: 0) + (distribution["C-"] ?: 0)),
        "D" to ((distribution["D+"] ?: 0) + (distribution["D"] ?: 0)),
        "F" to ((distribution["E"] ?: 0) + (distribution["F"] ?: 0))
    )
}

private fun calculatePassRate(subjects: List<com.example.japuraroutef.model.Subject>): Float {
    if (subjects.isEmpty()) return 0f

    val passingGrades = setOf("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D")
    val passCount = subjects.count { it.grade in passingGrades }

    return (passCount.toFloat() / subjects.size) * 100f
}

private fun calculateAtRiskSubjects(subjects: List<com.example.japuraroutef.model.Subject>): Int {
    val atRiskGrades = setOf("D+", "D", "E", "F")

    return subjects.count { it.grade in atRiskGrades }
}
