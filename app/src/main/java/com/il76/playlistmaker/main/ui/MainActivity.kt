package com.il76.playlistmaker.main.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.il76.playlistmaker.R
import com.il76.playlistmaker.media.ui.MediaFragment
import com.il76.playlistmaker.search.ui.SearchFragment
import com.il76.playlistmaker.settings.ui.SettingsScreen
import com.il76.playlistmaker.ui.theme.PlaylistMakerTheme

//ComponentActivity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.BLACK))

        setContent {
            PlaylistMakerTheme {
                AppNavigation()
            }
        }




//        ViewCompat.setOnApplyWindowInsetsListener(binding.mainActivity) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            // Android 15 fix
//            WindowInsetsCompat.CONSUMED
//        }
//
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment
//        val navController = navHostFragment.navController
//        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
//            if (nd.label == "fragment_player" || nd.label == "fragment_playlistadd" || nd.label == "fragment_playlist") { //прячем нижнее меню на некоторых экранах
//                binding.bottomNavigationView.isVisible = false
//                binding.bottomNavigationViewBorder.isVisible = false
//            } else {
//                binding.bottomNavigationView.isVisible = true
//                binding.bottomNavigationViewBorder.isVisible = true
//            }
//        }
//        binding.bottomNavigationView.setupWithNavController(navController)
    }
}

sealed class Screen(val route: String, val titleResId: Int) {
    object Search : Screen("search", R.string.button_search)
    object Media : Screen("media", R.string.button_media)
    object Settings : Screen("settings", R.string.button_settings)

    // Экраны без нижней навигации
    object Player : Screen("details", R.string.button_media)
}

data class ScreenUIConfig(
    val showTopBar: Boolean = true,
    val showBottomBar: Boolean = true,
    val title: String? = null
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentScreen = currentBackStack?.destination?.route

    val screenConfig = when (currentScreen) {
        Screen.Search.route -> ScreenUIConfig(title = "Поиск")
        Screen.Media.route -> ScreenUIConfig(title = "Медиа")
        Screen.Settings.route -> ScreenUIConfig(title = "Настройки")
        Screen.Player.route -> ScreenUIConfig(title = "Медиа")
        else -> ScreenUIConfig()
    }

    Scaffold(
        topBar = {
            if (screenConfig.showTopBar) {
                TopAppBar(
                    title = { Text(text = screenConfig.title ?: "") },
//                    navigationIcon = {
//                        if (navController.previousBackStackEntry != null) {
//                            IconButton(onClick = { navController.popBackStack() }) {
//                                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
//                            }
//                        }
//                    }
                )
            }
        },
        bottomBar = {
            if (screenConfig.showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Settings.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            //composable(Screen.Search.route) { SearchScreen(navController) }
            composable(Screen.Search.route) { LegacyFragmentContainer(SearchFragment::class.java) }

            composable(Screen.Media.route) { MediaScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen(navController) }
            //composable(Screen.Settings.route) { SettingsScreen(navController) }
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Search,
        Screen.Media,
        Screen.Settings
    )

    NavigationBar {
        val currentRoute = currentRoute(navController)
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = when (screen) {
                            Screen.Search -> R.drawable.icon_search
                            Screen.Media -> R.drawable.icon_media
                            Screen.Settings -> R.drawable.icon_settings
                            else -> R.drawable.icon_arrow_right
                        }),
                        contentDescription = stringResource(id = screen.titleResId!!)
                    )
                },
                label = { Text(stringResource(id = screen.titleResId)) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(Screen.Search.route) {
                            saveState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}


@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MainScreen() {
//    var selectedTab by remember { mutableIntStateOf(0) }
//
//    // Tabs data
//    val tabs = listOf(
//        BottomNavItem.Search,
//        BottomNavItem.Media,
//        BottomNavItem.Settings
//    )
//
//    Scaffold(
//        bottomBar = {
//            NavigationBar {
//                tabs.forEachIndexed { index, item ->
//                    NavigationBarItem(
//                        icon = {
//                            Icon(
//                                painter = painterResource(id = item.iconRes),
//                                contentDescription = stringResource(id = item.title)
//                            )
//                        },
//                        label = { Text(stringResource(id = item.title)) },
//                        selected = selectedTab == index,
//                        onClick = { selectedTab = index }
//                    )
//                }
//            }
//        }
//    ) { innerPadding ->
//        Box(modifier = Modifier.padding(innerPadding)) {
//            when (tabs[selectedTab]) {
//                is BottomNavItem.Search -> LegacyFragmentContainer(SearchFragment::class.java)
//                is BottomNavItem.Media -> LegacyFragmentContainer(MediaFragment::class.java)
//                is BottomNavItem.Settings -> SettingsScreen()
//            }
//        }
//    }
//}
//
//// Модель для пунктов навигации
//sealed class BottomNavItem(
//    @DrawableRes val iconRes: Int,  // ID ресурса иконки
//    @StringRes val title: Int       // ID строки заголовка
//) {
//    object Search : BottomNavItem(R.drawable.icon_search, R.string.button_search)
//    object Media : BottomNavItem(R.drawable.icon_media, R.string.button_media)
//    object Settings : BottomNavItem(R.drawable.icon_settings, R.string.button_settings)
//}

@Composable
fun SearchScreen(navController: NavController) { /* ... */ }
@Composable
fun MediaScreen(navController: NavController) { /* ... */ }

@Composable
fun LegacyFragmentContainer(fragmentClass: Class<out Fragment>) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var fragmentContainer by remember { mutableStateOf<View?>(null) }

    AndroidView(
        factory = { ctx ->
            FrameLayout(ctx).apply {
                fragmentContainer = this
                id = View.generateViewId()
            }
        },
        update = { view ->
            if (view is FrameLayout) {
                val fragmentManager = (context as FragmentActivity).supportFragmentManager
                val existingFragment = fragmentManager.findFragmentById(view.id)

                if (existingFragment == null) {
                    fragmentManager.commit {
                        setReorderingAllowed(true)
                        add(view.id, fragmentClass, null)
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}