package pini.mattia.shopfullytest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import pini.mattia.shopfullytest.ui.flyer.FlyerListPage
import pini.mattia.shopfullytest.ui.theme.ShopFullyTestTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShopFullyTestTheme {
                FlyerListPage()
            }
        }
    }
}