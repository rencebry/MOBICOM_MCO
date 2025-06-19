package com.mobicom.s17.group8.mobicom_mco.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobicom.s17.group8.mobicom_mco.R

data class NavItem(val label: String, val iconRes: Int)

val bottomNavItems = listOf(
    NavItem("Home", R.drawable.person),
    NavItem("To-do", R.drawable.check_box),
    NavItem("Pomo", R.drawable.schedule),
    NavItem("Music", R.drawable.music_note),
    NavItem("Study", R.drawable.folder)
)

@Composable
fun BottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar {
        bottomNavItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
    var selectedIndex by remember { mutableStateOf(2) }

    BottomNavBar(
        selectedIndex = selectedIndex,
        onItemSelected = { selectedIndex = it }
    )
}

