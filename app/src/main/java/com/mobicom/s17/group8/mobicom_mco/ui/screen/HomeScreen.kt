package com.mobicom.s17.group8.mobicom_mco.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable
import com.mobicom.s17.group8.mobicom_mco.ui.components.AppTopBar
import com.mobicom.s17.group8.mobicom_mco.ui.components.BottomNavBar
import com.mobicom.s17.group8.mobicom_mco.ui.theme.MOBICOM_MCOTheme

@Composable
fun HomeScreen() {
    var selectedIndex by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        topBar = { AppTopBar() },
        bottomBar = {
            BottomNavBar(
                selectedIndex = selectedIndex,
                onItemSelected = { selectedIndex = it }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text("June 13th", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))

            Text("Hello, Kyoka!", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation()
            ) {
                // ahmm
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: navigate to study sets */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Start Studying ➜", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Today’s Tasks", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation()
            ) {
                // task list here
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Study Sets", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation()
            ) {
                // study set cards here
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MOBICOM_MCOTheme {
        HomeScreen()
    }
}
