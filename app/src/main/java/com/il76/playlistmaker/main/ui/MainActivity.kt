package com.il76.playlistmaker.main.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.il76.playlistmaker.R
import com.il76.playlistmaker.media.ui.MediaScreen
import com.il76.playlistmaker.media.ui.PlaylistAddScreen
import com.il76.playlistmaker.player.ui.PlayerScreen
import com.il76.playlistmaker.search.domain.models.Track
import com.il76.playlistmaker.search.ui.SearchScreen
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
    object PlaylistInfo : Screen("playlistinfo", R.string.new_playlist)

    // Экраны без нижней навигации
    object Player : Screen("player/{trackJson}", R.string.button_media)
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
        Screen.PlaylistInfo.route -> ScreenUIConfig(title = stringResource(R.string.new_playlist))
        Screen.Player.route -> ScreenUIConfig(title = "Плеер", showBottomBar = false)
        else -> ScreenUIConfig()
    }

    Scaffold(
        topBar = {
            if (screenConfig.showTopBar) {
                TopAppBar(
                    title = { Text(text = screenConfig.title ?: "") },
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    navigationIcon = {
                        if (!screenConfig.showBottomBar) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
//                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
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
            startDestination = Screen.Search.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Search.route) { Surface {SearchScreen(navController) }}
            composable(Screen.Media.route) { Surface {MediaScreen(navController)} }
            composable(Screen.Settings.route) { Surface {SettingsScreen() }}
            composable(
                route = Screen.Player.route,
                arguments = listOf(navArgument("trackJson") {
                    type = NavType.StringType
                    defaultValue = ""
                })
            ) { backStackEntry ->
                //Log.d("pls", "route_player")
                val trackJson = backStackEntry.arguments?.getString("trackJson") ?: ""
                val track = remember(trackJson) {
                    Gson().fromJson(trackJson, Track::class.java)
                }
                Surface {
                    PlayerScreen(navController, track = track)
                }
            }
            composable(
                route = Screen.PlaylistInfo.route,
                arguments = listOf(navArgument("playlistId") {
                    type = NavType.IntType
                    defaultValue = 0
                })
            ) { backStackEntry ->
                Log.d("pls", "route_playlistinfo")
                val playlistId = backStackEntry.arguments?.getInt("playlistId") ?: 0
                Surface {
                    PlaylistAddScreen(navController, playlistId = playlistId)
                }
            }
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

    BottomAppBar (containerColor = MaterialTheme.colorScheme.background) {
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
                label = {
                    Text(
                        text = stringResource(id = screen.titleResId),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
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