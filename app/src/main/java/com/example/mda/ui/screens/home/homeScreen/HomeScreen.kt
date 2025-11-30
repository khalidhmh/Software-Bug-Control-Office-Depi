@file:Suppress("DEPRECATION")

package com.example.mda.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mda.data.repository.mappers.toMovie
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.ui.screens.auth.AuthViewModel
import com.example.mda.ui.screens.favorites.FavoritesViewModel
import com.example.mda.ui.screens.home.HomeViewModel
import com.example.mda.ui.screens.home.homeScreen.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    onTopBarStateChange: (TopBarState) -> Unit,
    favoritesViewModel: FavoritesViewModel,
    authViewModel: AuthViewModel
) {
    // 1️⃣ تجميع البيانات من الـ ViewModel
    val trendingEntities by viewModel.trendingMedia.collectAsState()
    val mixedEntities by viewModel.popularMixed.collectAsState()

    // ✅ استخدام القوائم المفلترة الجديدة
    val recMoviesEntities by viewModel.recommendedMovies.collectAsState()
    val recTvEntities by viewModel.recommendedTvShows.collectAsState()

    // 2️⃣ تحويل MediaEntity إلى Movie
    val trendingList = remember(trendingEntities) { trendingEntities.map { it.toMovie() } }
    val bannerList = remember(mixedEntities) { mixedEntities.map { it.toMovie() } }

    val recommendedMoviesList = remember(recMoviesEntities) { recMoviesEntities.map { it.toMovie() } }
    val recommendedTvShowsList = remember(recTvEntities) { recTvEntities.map { it.toMovie() } }

    val scrollState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    var refreshing by remember { mutableStateOf(false) }
    val refreshState = rememberSwipeRefreshState(isRefreshing = refreshing)
    val coroutineScope = rememberCoroutineScope()
    val authUiState by authViewModel.uiState.collectAsState()

    // 🚀 التعديل هنا: منع التحميل المتكرر
    LaunchedEffect(Unit) {
        // لو القوائم فاضية (أول مرة نفتح)، حمل الداتا
        if (trendingEntities.isEmpty() || mixedEntities.isEmpty()) {
            viewModel.onUserActivityDetected(forceRefresh = true)
        } else {
            // لو الداتا موجودة، بس حدث التوصيات في الخلفية من غير ما تعمل Loading Spinner
            // (اختياري: ممكن تخليها false لو مش عايز تحدث خالص)
            viewModel.onUserActivityDetected(forceRefresh = false)
        }
    }

    // 🔹 منطق الترحيب الذكي حسب الوقت + الترجمة
    val greetingKey = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> LocalizationKeys.HOME_GREETING_MORNING
            in 12..16 -> LocalizationKeys.HOME_GREETING_AFTERNOON
            in 17..20 -> LocalizationKeys.HOME_GREETING_EVENING
            else -> LocalizationKeys.HOME_GREETING_MORNING
        }
    }
    
    val titleText = localizedString(greetingKey)
    val subtitleText = localizedString(LocalizationKeys.HOME_SUBTITLE)

    // ده لازم يفضل موجود عشان يرجع العنوان لما نرجع من صفحة تانية
    LaunchedEffect(titleText, subtitleText) {
        onTopBarStateChange(
            TopBarState(
                title = titleText,
                subtitle = subtitleText
            )
        )
    }

    SwipeRefresh(
        state = refreshState,
        onRefresh = {
            refreshing = true
            coroutineScope.launch {
                viewModel.loadTrending("day")
                viewModel.loadPopularData()
                viewModel.loadTopRated()
                // هنا بنجبر التحديث عشان المستخدم سحب الشاشة بنفسه
                viewModel.onUserActivityDetected(forceRefresh = true)
                delay(1500)
                refreshing = false
            }
        },
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 106.dp),
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ---------------- Banner ----------------
            item {
                AnimatedVisibility(
                    visible = bannerList.isNotEmpty(),
                    enter = fadeIn() + slideInVertically()
                ) {
                    BannerSection(movies = bannerList)
                }
            }

            // ---------------- For You Section ----------------
            item {
                val showRecommendations = recommendedMoviesList.isNotEmpty() || recommendedTvShowsList.isNotEmpty()

                AnimatedVisibility(
                    visible = showRecommendations,
                    enter = fadeIn()
                ) {
                    ForYouSection(
                        recommendedMovies = recommendedMoviesList,
                        recommendedTvShows = recommendedTvShowsList,
                        onMovieClick = { m ->
                            navController.navigate("detail/${m.mediaType}/${m.id}")
                        },
                        favoritesViewModel = favoritesViewModel,
                        navController = navController,
                        isAuthenticated = authUiState.isAuthenticated
                    )
                }
            }

            // ---------------- Trending ----------------
            item {
                TrendingSection(
                    trendingMovies = trendingList,
                    selectedWindow = viewModel.selectedTimeWindow,
                    onTimeWindowChange = viewModel::loadTrending,
                    onMovieClick = { m ->
                        navController.navigate("detail/${m.mediaType}/${m.id}")
                    },
                    favoritesViewModel = favoritesViewModel,
                    navController = navController,
                    isAuthenticated = authUiState.isAuthenticated
                )
            }

            // ---------------- Popular ----------------
            item {
                PopularSection(
                    popularMovies = bannerList,
                    onMovieClick = { m ->
                        navController.navigate("detail/${m.mediaType}/${m.id}")
                    },
                    onViewMoreClick = {
                        navController.navigate("popular_movies")
                    },
                    favoritesViewModel = favoritesViewModel,
                    navController = navController,
                    isAuthenticated = authUiState.isAuthenticated
                )
            }
        }
    }
}