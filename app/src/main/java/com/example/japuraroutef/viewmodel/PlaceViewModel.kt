package com.example.japuraroutef.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.japuraroutef.api.ApiService
import com.example.japuraroutef.model.PlaceCategory
import com.example.japuraroutef.model.PlaceResponseDto
import com.example.japuraroutef.repository.PlaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "PlaceViewModel"

data class PlacesUiState(
    val places: List<PlaceResponseDto> = emptyList(),
    val filteredPlaces: List<PlaceResponseDto> = emptyList(),
    val selectedCategory: PlaceCategory = PlaceCategory.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class PlaceDetailUiState(
    val place: PlaceResponseDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class PlaceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PlaceRepository = PlaceRepository(
        ApiService.create(application)
    )

    private val _placesUiState = MutableStateFlow(PlacesUiState())
    val placesUiState: StateFlow<PlacesUiState> = _placesUiState.asStateFlow()

    private val _placeDetailUiState = MutableStateFlow(PlaceDetailUiState())
    val placeDetailUiState: StateFlow<PlaceDetailUiState> = _placeDetailUiState.asStateFlow()

    init {
        loadPlaces()
    }

    fun loadPlaces() {
        viewModelScope.launch {
            Log.d(TAG, "loadPlaces: Starting to load places")
            _placesUiState.update { it.copy(isLoading = true, error = null) }

            repository.getAllPlaces()
                .onSuccess { places ->
                    Log.d(TAG, "loadPlaces: Successfully loaded ${places.size} places")
                    places.forEachIndexed { index, place ->
                        Log.d(TAG, "Place $index: ${place.name}, images: ${place.images.size}, tags: ${place.tags}")
                    }
                    _placesUiState.update { state ->
                        val filtered = filterPlaces(places, state.selectedCategory, state.searchQuery)
                        Log.d(TAG, "loadPlaces: Filtered to ${filtered.size} places")
                        state.copy(
                            places = places,
                            filteredPlaces = filtered,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { exception ->
                    Log.e(TAG, "loadPlaces: Failed to load places", exception)
                    _placesUiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load places"
                        )
                    }
                }
        }
    }

    fun loadPlaceById(placeId: String) {
        viewModelScope.launch {
            _placeDetailUiState.update { it.copy(isLoading = true, error = null) }

            repository.getPlaceById(placeId)
                .onSuccess { place ->
                    _placeDetailUiState.update {
                        it.copy(place = place, isLoading = false, error = null)
                    }
                }
                .onFailure { exception ->
                    _placeDetailUiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load place details"
                        )
                    }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _placesUiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredPlaces = filterPlaces(state.places, state.selectedCategory, query)
            )
        }
    }

    fun updateSelectedCategory(category: PlaceCategory) {
        _placesUiState.update { state ->
            state.copy(
                selectedCategory = category,
                filteredPlaces = filterPlaces(state.places, category, state.searchQuery)
            )
        }
    }

    private fun filterPlaces(
        places: List<PlaceResponseDto>,
        category: PlaceCategory,
        searchQuery: String
    ): List<PlaceResponseDto> {
        return places.filter { place ->
            val matchesCategory = category == PlaceCategory.ALL ||
                place.tags.any { it.equals(category.tag, ignoreCase = true) }

            val matchesSearch = searchQuery.isEmpty() ||
                place.name.contains(searchQuery, ignoreCase = true) ||
                place.shortDescription?.contains(searchQuery, ignoreCase = true) == true ||
                place.description?.contains(searchQuery, ignoreCase = true) == true ||
                place.tags.any { it.contains(searchQuery, ignoreCase = true) }

            matchesCategory && matchesSearch
        }
    }

    fun clearError() {
        _placesUiState.update { it.copy(error = null) }
    }

    fun clearPlaceDetail() {
        _placeDetailUiState.update { PlaceDetailUiState() }
    }
}

