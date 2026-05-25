package com.artmcar.rksmp6_2.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.artmcar.rksmp6_2.domain.model.LaureateItem
import com.artmcar.rksmp6_2.domain.model.NobelCategory
import com.artmcar.rksmp6_2.domain.usecase.GetLaureatesUseCase
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface ListUiState {
    data object Loading : ListUiState
    data class Success(val items: List<LaureateItem>) : ListUiState
    data class Error(val message: String) : ListUiState
}

class ListViewModel(private val getLaureates: GetLaureatesUseCase) : ViewModel() {
    var state by mutableStateOf<ListUiState>(ListUiState.Loading)
        private set
    var year by mutableStateOf<Int?>(null)
        private set
    var category by mutableStateOf<NobelCategory?>(null)
        private set

    init { reload() }

    fun updateYear(y: Int?) {
        year = y
        reload() }
    fun updateCategory(c: NobelCategory?) {
        category = c
        reload() }

    fun reload() {
        state = ListUiState.Loading
        viewModelScope.launch {
            state = try {
                ListUiState.Success(getLaureates(year, category?.apiValue))
            } catch (t: Throwable) {
                ListUiState.Error(t.message ?: "Unknown error")
            }
        }
    }

    companion object {
        fun factory(getLaureates: GetLaureatesUseCase) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ListViewModel(getLaureates) as T
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaureatesListScreen(
    getLaureates: GetLaureatesUseCase,
    onItemClick: (String) -> Unit
) {
    val vm: ListViewModel = viewModel(factory = ListViewModel.factory(getLaureates))
    Scaffold(
        topBar = { TopAppBar(title = { Text("Нобелевские премии") }) }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            FilterBar(
                year = vm.year,
                category = vm.category,
                onYearChange = vm::updateYear,
                onCategoryChange = vm::updateCategory
            )
            HorizontalDivider()
            Box(Modifier.fillMaxSize()) {
                when (val s = vm.state) {
                    ListUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    is ListUiState.Error -> Column(
                        Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Ошибка: ${s.message}")
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { vm.reload() }) { Text("Повторить") }
                    }
                    is ListUiState.Success -> {
                        if (s.items.isEmpty()) {
                            Text("Ничего не найдено",
                                modifier = Modifier.align(Alignment.Center))
                        } else LazyColumn(
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(s.items, key = { it.laureateId + it.awardYear + it.category }) {
                                LaureateRow(it) { onItemClick(it.laureateId) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LaureateRow(item: LaureateItem, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("${item.awardYear} - ${item.category}",
                style = MaterialTheme.typography.labelLarge)
            Text(item.fullName, style = MaterialTheme.typography.titleMedium)
            if (item.motivation.isNotBlank())
                Text(item.motivationShort, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun FilterBar(
    year: Int?,
    category: NobelCategory?,
    onYearChange: (Int?) -> Unit,
    onCategoryChange: (NobelCategory?) -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        YearDropdown(year, onYearChange, Modifier.weight(1f))
        CategoryDropdown(category, onCategoryChange, Modifier.weight(1f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YearDropdown(year: Int?, onChange: (Int?) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val current = LocalDate.now().year
    val years = (current downTo 1901).toList()
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded },
        modifier = modifier) {
        OutlinedTextField(
            readOnly = true,
            value = year?.toString() ?: "Все годы",
            onValueChange = {},
            label = { Text("Год") },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Все годы") },
                onClick = { onChange(null); expanded = false })
            years.forEach { y ->
                DropdownMenuItem(text = { Text(y.toString()) },
                    onClick = { onChange(y); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    category: NobelCategory?,
    onChange: (NobelCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded },
        modifier = modifier) {
        OutlinedTextField(
            readOnly = true,
            value = category?.title ?: "Все категории",
            onValueChange = {},
            label = { Text("Категория") },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Все категории") },
                onClick = { onChange(null); expanded = false })
            NobelCategory.entries.forEach { c ->
                DropdownMenuItem(text = { Text(c.title) },
                    onClick = { onChange(c); expanded = false })
            }
        }
    }
}