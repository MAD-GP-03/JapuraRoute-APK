package com.example.japuraroutef.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import com.example.japuraroutef.model.FocusArea
import com.example.japuraroutef.model.UniYear
import com.example.japuraroutef.viewmodel.RegistrationState
import com.example.japuraroutef.viewmodel.RegistrationViewModel
import com.example.japuraroutef.utils.ToastHost

@Composable
fun RegistrationScreen(
    onRegistrationSuccess: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    val viewModel: RegistrationViewModel = viewModel()
    val currentStep by viewModel.currentStep.collectAsState()
    val registrationState by viewModel.registrationState.collectAsState()

    LaunchedEffect(registrationState) {
        if (registrationState is RegistrationState.Success) {
            val token = (registrationState as RegistrationState.Success).response.token
            onRegistrationSuccess(token)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentStep) {
            1 -> RegistrationStep1Screen(
                viewModel = viewModel,
                onBackToLogin = onBackToLogin
            )
            2 -> RegistrationStep2Screen(
                viewModel = viewModel
            )
        }

        // Position ToastHost at the bottom for proper snackbar display
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            ToastHost()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationStep1Screen(
    viewModel: RegistrationViewModel,
    onBackToLogin: () -> Unit
) {
    val username by viewModel.username.collectAsState()
    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.japuraroutef.ui.theme.BackgroundDark)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4F378B),
                            Color(0xFF141218)
                        )
                    )
                )
        ) {
            // Blur effects
            Box(
                modifier = Modifier
                    .offset(x = (-160).dp, y = (-160).dp)
                    .size(320.dp)
                    .background(
                        color = com.example.japuraroutef.ui.theme.Primary.copy(alpha = 0.05f),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 160.dp, y = 160.dp)
                    .size(320.dp)
                    .background(
                        color = Color(0xFFEADDFF).copy(alpha = 0.05f),
                        shape = CircleShape
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .navigationBarsPadding()
        ) {
            // Back button
            IconButton(
                onClick = onBackToLogin,
                modifier = Modifier.padding(top = 18.dp, bottom = 8.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Back",
                    tint = com.example.japuraroutef.ui.theme.OnSurfaceDark
                )
            }

            Spacer(Modifier.height(16.dp))

            // Icon and Title section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = com.example.japuraroutef.ui.theme.SurfaceContainerHighestDark,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            tint = com.example.japuraroutef.ui.theme.Primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineLarge,
                    color = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                    fontWeight = FontWeight.Normal
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Step 1 of 2: Basic Information",
                    style = MaterialTheme.typography.bodyMedium,
                    color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
                )
            }

            Spacer(Modifier.height(40.dp))

            // Scrollable form fields - with weight to take remaining space
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Username - Icon on left
                InputFieldWithIcon(
                    value = username,
                    onValueChange = { viewModel.username.value = it },
                    label = "Username",
                    icon = Icons.Default.PersonOutline,
                    errorMessage = if (username.isNotBlank()) viewModel.validateUsername() else null
                )

                // Full Name
                InputFieldWithIcon(
                    value = fullName,
                    onValueChange = { viewModel.fullName.value = it },
                    label = "Full Name",
                    icon = Icons.Default.Badge,
                    errorMessage = if (fullName.isNotBlank()) viewModel.validateFullName() else null
                )

                // Email
                InputFieldWithIcon(
                    value = email,
                    onValueChange = { viewModel.email.value = it },
                    label = "Email Address",
                    icon = Icons.Default.AlternateEmail,
                    keyboardType = KeyboardType.Email,
                    errorMessage = if (email.isNotBlank()) viewModel.validateEmail() else null
                )

                // Password
                InputFieldWithIcon(
                    value = password,
                    onValueChange = { viewModel.password.value = it },
                    label = "Password",
                    icon = Icons.Default.Password,
                    isPassword = true,
                    errorMessage = if (password.isNotBlank()) viewModel.validatePassword() else null
                )

                // Confirm Password
                InputFieldWithIcon(
                    value = confirmPassword,
                    onValueChange = { viewModel.confirmPassword.value = it },
                    label = "Confirm Password",
                    icon = Icons.Default.LockReset,
                    isPassword = true,
                    errorMessage = if (confirmPassword.isNotBlank()) viewModel.validateConfirmPassword() else null
                )
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { viewModel.nextStep() },
                    enabled = viewModel.validateStep1(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.validateStep1())
                            com.example.japuraroutef.ui.theme.Primary
                        else
                            com.example.japuraroutef.ui.theme.Primary.copy(alpha = 0.3f),
                        contentColor = if (viewModel.validateStep1())
                            Color.Black
                        else
                            Color.Black.copy(alpha = 0.6f),
                        disabledContainerColor = com.example.japuraroutef.ui.theme.Primary.copy(alpha = 0.3f),
                        disabledContentColor = Color.Black.copy(alpha = 0.6f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = if (viewModel.validateStep1()) 6.dp else 2.dp,
                        pressedElevation = if (viewModel.validateStep1()) 8.dp else 4.dp,
                        disabledElevation = 1.dp
                    )
                ) {
                    Text(
                        "Continue",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Sign in link
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have an account? ",
                        color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Sign In",
                        color = com.example.japuraroutef.ui.theme.Primary,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onBackToLogin() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputFieldWithIcon(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon on the left
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isError) Color(0xFFFF6B6B) else com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                modifier = Modifier
                    .size(30.dp)
                    .padding(top = 1.dp)
            )

            // Outlined TextField
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                isError = isError,
                visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF111111),
                    unfocusedContainerColor = Color(0xFF111111),
                    focusedBorderColor = if (isError) Color(0xFFFF6B6B) else com.example.japuraroutef.ui.theme.Primary,
                    unfocusedBorderColor = if (isError) Color(0xFFFF6B6B) else com.example.japuraroutef.ui.theme.OutlineDark,
                    focusedLabelColor = if (isError) Color(0xFFFF6B6B) else com.example.japuraroutef.ui.theme.Primary,
                    unfocusedLabelColor = if (isError) Color(0xFFFF6B6B) else com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                    focusedTextColor = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                    unfocusedTextColor = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                    cursorColor = com.example.japuraroutef.ui.theme.Primary,
                    errorBorderColor = Color(0xFFFF6B6B),
                    errorLabelColor = Color(0xFFFF6B6B),
                    errorCursorColor = Color(0xFFFF6B6B)
                )
            )
        }

        // Error message
        if (errorMessage != null) {
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Spacer to align with text field (accounting for icon)
                Spacer(Modifier.size(30.dp))
                Text(
                    text = errorMessage,
                    color = Color(0xFFFF6B6B),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationStep2Screen(
    viewModel: RegistrationViewModel
) {
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val address by viewModel.address.collectAsState()
    val uniYear by viewModel.uniYear.collectAsState()
    val regNumber by viewModel.regNumber.collectAsState()
    val nic by viewModel.nic.collectAsState()
    val selectedFocusArea by viewModel.selectedFocusArea.collectAsState()
    val registrationState by viewModel.registrationState.collectAsState()

    var expandedYear by remember { mutableStateOf(false) }
    var expandedFocus by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.japuraroutef.ui.theme.BackgroundDark)
    ) {
        // Top gradient background matching HTML design
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4F378B),
                            Color(0xFF141218)
                        )
                    )
                )
        ) {
            // Blur effects
            Box(
                modifier = Modifier
                    .offset(x = (-160).dp, y = (-160).dp)
                    .size(320.dp)
                    .background(
                        color = com.example.japuraroutef.ui.theme.Primary.copy(alpha = 0.05f),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 160.dp, y = 160.dp)
                    .size(320.dp)
                    .background(
                        color = Color(0xFFEADDFF).copy(alpha = 0.05f),
                        shape = CircleShape
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .navigationBarsPadding()
        ) {
            // Back button
            IconButton(
                onClick = { viewModel.previousStep() },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Back",
                    tint = com.example.japuraroutef.ui.theme.OnSurfaceDark
                )
            }

            Spacer(Modifier.height(16.dp))

            // Icon and Title section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = com.example.japuraroutef.ui.theme.SurfaceContainerHighestDark,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            tint = com.example.japuraroutef.ui.theme.Primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineLarge,
                    color = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                    fontWeight = FontWeight.Normal
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Step 2 of 2: Student Information",
                    style = MaterialTheme.typography.bodyMedium,
                    color = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark
                )
            }

            Spacer(Modifier.height(24.dp))

            // Scrollable form fields - with weight to take remaining space
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Phone Number
                InputFieldWithIcon(
                    value = phoneNumber,
                    onValueChange = { viewModel.phoneNumber.value = it },
                    label = "Phone Number",
                    icon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    errorMessage = if (phoneNumber.isNotBlank()) viewModel.validatePhoneNumber() else null
                )

                // Address
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = if (address.isNotBlank() && viewModel.validateAddress() != null)
                                Color(0xFFFF6B6B) else com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                            modifier = Modifier
                                .size(30.dp)
                                .padding(top = 1.dp)
                        )
                        OutlinedTextField(
                            value = address,
                            onValueChange = { viewModel.address.value = it },
                            label = { Text("Address") },
                            modifier = Modifier
                                .weight(1f),
                            minLines = 3,
                            maxLines = 3,
                            isError = address.isNotBlank() && viewModel.validateAddress() != null,
                            shape = RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF111111),
                                unfocusedContainerColor = Color(0xFF111111),
                                focusedBorderColor = if (address.isNotBlank() && viewModel.validateAddress() != null)
                                    Color(0xFFFF6B6B) else com.example.japuraroutef.ui.theme.Primary,
                                unfocusedBorderColor = if (address.isNotBlank() && viewModel.validateAddress() != null)
                                    Color(0xFFFF6B6B) else com.example.japuraroutef.ui.theme.OutlineDark,
                                focusedLabelColor = if (address.isNotBlank() && viewModel.validateAddress() != null)
                                    Color(0xFFFF6B6B) else com.example.japuraroutef.ui.theme.Primary,
                                unfocusedLabelColor = if (address.isNotBlank() && viewModel.validateAddress() != null)
                                    Color(0xFFFF6B6B) else com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                                focusedTextColor = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                                unfocusedTextColor = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                                cursorColor = com.example.japuraroutef.ui.theme.Primary,
                                errorBorderColor = Color(0xFFFF6B6B),
                                errorLabelColor = Color(0xFFFF6B6B),
                                errorCursorColor = Color(0xFFFF6B6B)
                            )
                        )
                    }

                    // Address error message
                    if (address.isNotBlank()) {
                        val addressError = viewModel.validateAddress()
                        if (addressError != null) {
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Spacer(Modifier.size(24.dp))
                                Text(
                                    text = addressError,
                                    color = Color(0xFFFF6B6B),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // University Year Dropdown
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = if (viewModel.validateUniYear() != null)
                                Color(0xFFFF6B6B) else com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                            modifier = Modifier
                                .size(30.dp)
                                .padding(top = 1.dp)
                        )
                        ExposedDropdownMenuBox(
                            expanded = expandedYear,
                            onExpandedChange = { expandedYear = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = uniYear?.name?.replace("_", " ") ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("University Year") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedYear) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                                isError = viewModel.validateUniYear() != null,
                                shape = RoundedCornerShape(4.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF111111),
                                    unfocusedContainerColor = Color(0xFF111111),
                                    focusedBorderColor = if (viewModel.validateUniYear() != null)
                                        Color(0xFFFF6B6B) else com.example.japuraroutef.ui.theme.Primary,
                                    unfocusedBorderColor = if (viewModel.validateUniYear() != null)
                                        Color(0xFFFF6B6B) else com.example.japuraroutef.ui.theme.OutlineDark,
                                    focusedLabelColor = if (viewModel.validateUniYear() != null)
                                        Color(0xFFFF6B6B) else com.example.japuraroutef.ui.theme.Primary,
                                    unfocusedLabelColor = if (viewModel.validateUniYear() != null)
                                        Color(0xFFFF6B6B) else com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                                    focusedTextColor = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                                    unfocusedTextColor = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                                    errorBorderColor = Color(0xFFFF6B6B),
                                    errorLabelColor = Color(0xFFFF6B6B)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedYear,
                                onDismissRequest = { expandedYear = false }
                            ) {
                                UniYear.entries.forEach { year ->
                                    DropdownMenuItem(
                                        text = { Text(year.name.replace("_", " ")) },
                                        onClick = {
                                            viewModel.uniYear.value = year
                                            expandedYear = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // University Year error message
                    val yearError = viewModel.validateUniYear()
                    if (yearError != null) {
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Spacer(Modifier.size(24.dp))
                            Text(
                                text = yearError,
                                color = Color(0xFFFF6B6B),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Focus Area (conditional)
                if (viewModel.canSelectFocusArea()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                            modifier = Modifier
                                .size(30.dp)
                                .padding(top = 1.dp)
                        )
                        ExposedDropdownMenuBox(
                            expanded = expandedFocus,
                            onExpandedChange = { expandedFocus = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedFocusArea.name,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Focus Area") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedFocus) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                                shape = RoundedCornerShape(4.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF111111),
                                    unfocusedContainerColor = Color(0xFF111111),
                                    focusedBorderColor = com.example.japuraroutef.ui.theme.Primary,
                                    unfocusedBorderColor = com.example.japuraroutef.ui.theme.OutlineDark,
                                    focusedLabelColor = com.example.japuraroutef.ui.theme.Primary,
                                    unfocusedLabelColor = com.example.japuraroutef.ui.theme.OnSurfaceVariantDark,
                                    focusedTextColor = com.example.japuraroutef.ui.theme.OnSurfaceDark,
                                    unfocusedTextColor = com.example.japuraroutef.ui.theme.OnSurfaceDark
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedFocus,
                                onDismissRequest = { expandedFocus = false }
                            ) {
                                FocusArea.entries.filter { it != FocusArea.COMMON }.forEach { area ->
                                    DropdownMenuItem(
                                        text = { Text(area.name) },
                                        onClick = {
                                            viewModel.selectedFocusArea.value = area
                                            expandedFocus = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }


                // Registration Number
                InputFieldWithIcon(
                    value = regNumber,
                    onValueChange = { viewModel.regNumber.value = it },
                    label = "Registration Number",
                    icon = Icons.Default.Info,
                    errorMessage = if (regNumber.isNotBlank()) viewModel.validateRegNumber() else null
                )


                // NIC Number
                InputFieldWithIcon(
                    value = nic,
                    onValueChange = { viewModel.nic.value = it },
                    label = "NIC Number",
                    icon = Icons.Default.AccountBox,
                    errorMessage = if (nic.isNotBlank()) viewModel.validateNic() else null
                )
            }

            // Fixed bottom section - Create Account button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Create Account button
                Button(
                    onClick = { viewModel.register() },
                    enabled = viewModel.validateStep2() && registrationState !is RegistrationState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.validateStep2() && registrationState !is RegistrationState.Loading)
                            com.example.japuraroutef.ui.theme.Primary
                        else
                            com.example.japuraroutef.ui.theme.Primary.copy(alpha = 0.3f),
                        contentColor = if (viewModel.validateStep2() && registrationState !is RegistrationState.Loading)
                            Color.Black
                        else
                            Color.Black.copy(alpha = 0.6f),
                        disabledContainerColor = com.example.japuraroutef.ui.theme.Primary.copy(alpha = 0.3f),
                        disabledContentColor = Color.Black.copy(alpha = 0.6f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = if (viewModel.validateStep2() && registrationState !is RegistrationState.Loading) 6.dp else 2.dp,
                        pressedElevation = if (viewModel.validateStep2() && registrationState !is RegistrationState.Loading) 8.dp else 4.dp,
                        disabledElevation = 1.dp
                    )
                ) {
                    if (registrationState is RegistrationState.Loading) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Black.copy(alpha = 0.7f),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Creating Account...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        Text(
                            "Create Account",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
