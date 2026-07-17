package com.riverheadny.budget.ui.screens.civic.votes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riverheadny.budget.RiverheadApplication
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.MeetingDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MeetingDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val repository = RiverheadApplication.instance.repository
    private val slug: String = checkNotNull(savedStateHandle["slug"])

    private val _state = MutableStateFlow<LoadState<MeetingDetail>>(LoadState.Loading)
    val state: StateFlow<LoadState<MeetingDetail>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = try {
                LoadState.Success(repository.meetingDetail(slug))
            } catch (e: Exception) {
                LoadState.Error(e.message ?: "unknown error")
            }
        }
    }
}
