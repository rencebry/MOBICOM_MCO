package com.mobicom.s17.group8.mobicom_mco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mobicom.s17.group8.mobicom_mco.ui.screen.HomeScreen
import com.mobicom.s17.group8.mobicom_mco.ui.theme.MOBICOM_MCOTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MOBICOM_MCOTheme {
                HomeScreen()
            }
        }
    }
}

//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    MOBICOM_MCOTheme {
//        Greeting("Android")
//    }
//}