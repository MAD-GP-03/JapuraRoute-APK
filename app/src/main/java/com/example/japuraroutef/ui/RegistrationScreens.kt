package com.example.japuraroutef.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.japuraroutef.model.FocusArea
import com.example.japuraroutef.model.UniYear
import com.example.japuraroutef.viewmodel.RegistrationState
import com.example.japuraroutef.viewmodel.RegistrationViewModel

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

    when (currentStep) {
        1 -> RegistrationStep1Screen(
            viewModel = viewModel,
            onBackToLogin = onBackToLogin
        )
        2 -> RegistrationStep2Screen(
            viewModel = viewModel
        )
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = onBackToLogin) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Step 1 of 2: Basic Information",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = username,
                onValueChange = { viewModel.username.value = it },
                label = { Text("Username") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = { viewModel.fullName.value = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.AccountCircle, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.email.value = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.password.value = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("Min 8 chars, uppercase, lowercase, digit, special char")
                }
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { viewModel.nextStep() },
                enabled = viewModel.validateStep1(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Details") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.previousStep() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Step 2 of 2: Student Information",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { viewModel.phoneNumber.value = it },
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = address,
                onValueChange = { viewModel.address.value = it },
                label = { Text("Address") },
                leadingIcon = { Icon(Icons.Default.Home, null) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            ExposedDropdownMenuBox(
                expanded = expandedYear,
                onExpandedChange = { expandedYear = it }
            ) {
                OutlinedTextField(
                    value = uniYear?.name?.replace("_", " ") ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("University Year") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedYear) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedYear,
                    onDismissRequest = { expandedYear = false }
                ) {
                    UniYear.values().forEach { year ->
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

            OutlinedTextField(
                value = regNumber,
                onValueChange = { viewModel.regNumber.value = it },
                label = { Text("Registration Number") },
                leadingIcon = { Icon(Icons.Default.Info, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (viewModel.canSelectFocusArea()) {
                ExposedDropdownMenuBox(
                    expanded = expandedFocus,
                    onExpandedChange = { expandedFocus = it }
                ) {
                    OutlinedTextField(
                        value = selectedFocusArea.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Focus Area") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedFocus) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedFocus,
                        onDismissRequest = { expandedFocus = false }
                    ) {
                        FocusArea.values().forEach { area ->
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

            OutlinedTextField(
                value = nic,
                onValueChange = { viewModel.nic.value = it },
                label = { Text("NIC Number") },
                leadingIcon = { Icon(Icons.Default.AccountBox, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            when (registrationState) {
                is RegistrationState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is RegistrationState.Error -> {
                    Text(
                        text = (registrationState as RegistrationState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                else -> {}
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { viewModel.register() },
                enabled = viewModel.validateStep2() && registrationState !is RegistrationState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Account")
            }
        }
    }
}
