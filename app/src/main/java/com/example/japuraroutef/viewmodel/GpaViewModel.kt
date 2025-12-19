package com.example.japuraroutef.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.japuraroutef.model.*
import com.example.japuraroutef.repository.GpaRepository
import kotlinx.coroutines.launch

class GpaViewModel(private val repository: GpaRepository) : ViewModel() {

    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    // All semester GPAs
    val semesterGpas = mutableStateListOf<SemesterGpaResponse>()

    // Overall CGPA
    val cgpa = mutableStateOf<Float?>(null)
    val totalCredits = mutableStateOf<Float?>(null)

    // Current editing state
    val currentSemesterId = mutableStateOf<SemesterId?>(null)
    val currentSemesterName = mutableStateOf("")
    val subjects = mutableStateListOf<Subject>()

    // Calculated GPA (local)
    val calculatedGpa = mutableStateOf(0f)

    // All modules loaded from API
    val allModules = mutableStateListOf<Module>()
    val modulesLoaded = mutableStateOf(false)

    // User's focus area (from registration or selection)
    val userFocusArea = mutableStateOf<FocusArea?>(null)
    val needsFocusAreaSelection = mutableStateOf(false)

    init {
        Log.d("GpaViewModel", "ViewModel initialized - hashCode: ${this.hashCode()}")
    }

    fun fetchAllData() {
        viewModelScope.launch {
            loadAllSemesterGpas()
        }
    }

    fun loadAllModules() {
        // Only call this once from GpaOverviewScreen
        if (modulesLoaded.value) {
            Log.d("GpaViewModel", "Modules already loaded, skipping")
            return
        }

        viewModelScope.launch {
            try {
                val result = repository.getAllModules()
                result.onSuccess { modules ->
                    allModules.clear()
                    allModules.addAll(modules)
                    modulesLoaded.value = true
                    Log.d("GpaViewModel", "Loaded ${modules.size} modules")
                }.onFailure { e ->
                    Log.e("GpaViewModel", "Error loading modules", e)
                }
            } catch (e: Exception) {
                Log.e("GpaViewModel", "Exception loading modules", e)
            }
        }
    }

    fun setUserFocusArea(focusArea: FocusArea) {
        userFocusArea.value = focusArea
        needsFocusAreaSelection.value = false
    }

    fun loadAllSemesterGpas() {
        Log.d("GpaViewModel", "loadAllSemesterGpas called - current size: ${semesterGpas.size}")
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                val result = repository.getAllSemesterGpas()
                result.onSuccess { gpas ->
                    Log.d("GpaViewModel", "API returned ${gpas.size} semesters")
                    semesterGpas.clear()
                    Log.d("GpaViewModel", "Cleared semesterGpas, size now: ${semesterGpas.size}")
                    semesterGpas.addAll(gpas.sortedByDescending { it.semesterId.ordinal })
                    Log.d("GpaViewModel", "Added ${gpas.size} semesters, size now: ${semesterGpas.size}")

                    // Calculate CGPA on frontend
                    calculateCgpaFromSemesters()
                }.onFailure { e ->
                    error.value = e.message ?: "Failed to load semester GPAs"
                    Log.e("GpaViewModel", "Error loading semester GPAs", e)
                }
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun calculateCgpaFromSemesters() {
        if (semesterGpas.isEmpty()) {
            cgpa.value = null
            totalCredits.value = null
            return
        }

        var totalWeightedGpa = 0.0
        var totalCreds = 0.0

        semesterGpas.forEach { semester ->
            totalWeightedGpa += semester.gpa * semester.totalCredits
            totalCreds += semester.totalCredits
        }

        if (totalCreds > 0) {
            cgpa.value = (totalWeightedGpa / totalCreds).toFloat()
            totalCredits.value = totalCreds.toFloat()
            Log.d("GpaViewModel", "Calculated CGPA: ${cgpa.value} from $totalCreds credits")
        }
    }

    fun startEditingSemester(semesterId: SemesterId, storedFocusArea: FocusArea? = null) {
        Log.d("GpaViewModel", "startEditingSemester called for: $semesterId")
        Log.d("GpaViewModel", "Current semesterId: ${currentSemesterId.value}")
        Log.d("GpaViewModel", "Available semesters in cache: ${semesterGpas.size}")

        // Only clear if switching to a DIFFERENT semester
        val switchingSemesters = currentSemesterId.value != null && currentSemesterId.value != semesterId
        if (switchingSemesters) {
            Log.d("GpaViewModel", "Switching semesters, clearing subjects")
            subjects.clear()
            calculatedGpa.value = 0f
        }

        currentSemesterId.value = semesterId
        currentSemesterName.value = semesterId.getDisplayName()

        // Check if we need to ask for focus area
        if (semesterId.requiresFocusArea() && storedFocusArea == null && userFocusArea.value == null) {
            needsFocusAreaSelection.value = true
            return
        } else if (storedFocusArea != null) {
            userFocusArea.value = storedFocusArea
        }

        // Check if semester already exists in loaded data (NO API CALL!)
        val existingSemester = semesterGpas.find { it.semesterId == semesterId }

        if (existingSemester != null) {
            // Semester already exists, use cached data
            Log.d("GpaViewModel", "Found existing semester with ${existingSemester.subjects.size} subjects")
            subjects.clear()
            subjects.addAll(existingSemester.subjects)
            calculateLocalGpa()
            Log.d("GpaViewModel", "Loaded semester from cache: ${subjects.size} subjects now in list")
            Log.d("GpaViewModel", "Subjects: ${subjects.map { it.subjectName }}")
        } else {
            // Semester doesn't exist yet, pre-populate from modules
            Log.d("GpaViewModel", "Semester not found in cache, pre-populating from modules")
            subjects.clear()
            prePopulateModules(semesterId)
            calculateLocalGpa()
            Log.d("GpaViewModel", "Pre-populated new semester with ${subjects.size} modules")
        }
    }

    private fun prePopulateModules(semesterId: SemesterId) {
        val relevantModules = allModules.filter { module ->
            module.semesterId == semesterId && isModuleRelevant(module)
        }.sortedBy { it.moduleCode }

        subjects.addAll(relevantModules.map { it.toSubject() })
        Log.d("GpaViewModel", "Pre-populated ${subjects.size} modules for $semesterId")
    }

    private fun isModuleRelevant(module: Module): Boolean {
        // ITC modules are common for all students
        if (module.focusArea.contains("ITC")) {
            return true
        }

        // Check user's focus area
        val focus = userFocusArea.value ?: return false
        val focusCode = when (focus) {
            FocusArea.SOFTWARE -> "ITS"
            FocusArea.NETWORKING -> "ITN"
            FocusArea.MULTIMEDIA -> "ITM"
            FocusArea.COMMON -> "ITC"
        }

        return module.focusArea.contains(focusCode)
    }

    fun addSubject(subjectName: String, credits: Float, grade: String) {
        subjects.add(Subject(subjectName, credits, grade))
        calculateLocalGpa()
    }

    fun updateSubject(index: Int, subjectName: String, credits: Float, grade: String) {
        if (index in subjects.indices) {
            subjects[index] = Subject(subjectName, credits, grade)
            calculateLocalGpa()
        }
    }

    fun updateSubjectGrade(index: Int, grade: String) {
        if (index in subjects.indices) {
            val subject = subjects[index]
            subjects[index] = subject.copy(grade = grade)
            calculateLocalGpa()
        }
    }

    fun updateSubjectCredits(index: Int, credits: Float) {
        if (index in subjects.indices) {
            val subject = subjects[index]
            subjects[index] = subject.copy(credits = credits)
            calculateLocalGpa()
        }
    }

    fun removeSubject(index: Int) {
        if (index in subjects.indices) {
            subjects.removeAt(index)
            calculateLocalGpa()
        }
    }

    fun calculateLocalGpa() {
        calculatedGpa.value = repository.calculateGpa(subjects.toList())
    }

    fun saveCurrentSemester(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val semesterId = currentSemesterId.value
        if (semesterId == null) {
            onError("No semester selected")
            return
        }

        if (subjects.isEmpty()) {
            onError("Please add at least one subject")
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                val request = CreateSemesterGpaRequest(
                    semesterId = semesterId,
                    semesterName = currentSemesterName.value,
                    subjects = subjects.toList()
                )

                val result = repository.createOrUpdateSemesterGpa(request)
                result.onSuccess { response ->
                    // Refresh the list
                    loadAllSemesterGpas() // This will also recalculate CGPA

                    // Clear editing state
                    clearEditingState()

                    onSuccess()
                }.onFailure { e ->
                    val errorMsg = e.message ?: "Failed to save semester GPA"
                    error.value = errorMsg
                    onError(errorMsg)
                    Log.e("GpaViewModel", "Error saving semester GPA", e)
                }
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteSemester(semesterId: SemesterId, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                val result = repository.deleteSemesterGpa(semesterId)
                result.onSuccess {
                    loadAllSemesterGpas() // This will also recalculate CGPA
                    onSuccess()
                }.onFailure { e ->
                    val errorMsg = e.message ?: "Failed to delete semester"
                    error.value = errorMsg
                    onError(errorMsg)
                    Log.e("GpaViewModel", "Error deleting semester", e)
                }
            } finally {
                isLoading.value = false
            }
        }
    }

    fun clearEditingState() {
        currentSemesterId.value = null
        currentSemesterName.value = ""
        subjects.clear()
        calculatedGpa.value = 0f
        error.value = null
    }
}

