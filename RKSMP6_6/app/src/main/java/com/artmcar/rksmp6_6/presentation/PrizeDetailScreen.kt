package com.artmcar.rksmp6_6.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.artmcar.rksmp6_6.domain.Prize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrizeDetailScreen(prize: Prize, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${prize.awardYear} · ${prize.category}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(prize.fullName, style = MaterialTheme.typography.headlineSmall)
            if (prize.motivation.isNotBlank()) {
                Text(prize.motivation, style = MaterialTheme.typography.bodyMedium)
            }
            HorizontalDivider()
            Text("Лауреаты (${prize.laureates.size})", style = MaterialTheme.typography.titleMedium)
            prize.laureates.forEach { l ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(l.fullName, style = MaterialTheme.typography.titleSmall)
                        if (l.portion.isNotBlank() && l.portion != "1") {
                            Text("Доля: ${l.portion}", style = MaterialTheme.typography.labelSmall)
                        }
                        if (l.motivation.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(l.motivation, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}