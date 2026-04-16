package com.kaam.app.ui.customer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaam.app.ui.components.EmptyState
import com.kaam.app.ui.components.LoadingScreen
import com.kaam.app.ui.components.WorkerCard

@Composable
fun WorkerListScreen(
    viewModel: WorkerListViewModel,
    onWorkerClick: (String) -> Unit,
) {
    val workers by viewModel.workers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        LoadingScreen()
        return
    }

    if (workers.isEmpty()) {
        EmptyState("No workers available yet")
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        item {
            Text(text = "Available Workers", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(workers, key = { it.profile.id }) { worker ->
            WorkerCard(
                worker = worker,
                onClick = { onWorkerClick(worker.profile.id) },
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }
    }
}
