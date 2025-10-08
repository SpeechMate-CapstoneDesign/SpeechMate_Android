package com.speech.mypage.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType
import com.speech.mypage.graph.mypage.MyPageRoute
import com.speech.mypage.graph.setting.SettingRoute
import com.speech.mypage.graph.webview.WebViewRoute
import com.speech.navigation.MyPageBaseRoute
import com.speech.navigation.MyPageGraph

fun NavController.navigateToMyPage(navOptions: NavOptions? = null) {
    navigate(MyPageGraph.MyPageRoute, navOptions)
}

fun NavController.navigateToSetting(navOptions: NavOptions? = null) {
    navigate(MyPageGraph.SettingRoute, navOptions)
}

fun NavController.navigateToWebView(url: String, navOptions: NavOptions? = null) {
    navigate(MyPageGraph.WebViewRoute(url), navOptions)
}

fun NavGraphBuilder.myPageNavGraph(
    navigateBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToSetting: () -> Unit,
    navigateToFeedBack: (Int, String, SpeechFileType, SpeechConfig) -> Unit,
    navigateToWebView : (String) -> Unit,
) {
    navigation<MyPageBaseRoute>(startDestination = MyPageGraph.MyPageRoute) {
        composable<MyPageGraph.MyPageRoute> {
            MyPageRoute(
                navigateToSetting = navigateToSetting,
                navigateToFeedBack = navigateToFeedBack,
            )
        }

        composable<MyPageGraph.SettingRoute> {
            SettingRoute(
                navigateToBack = navigateBack,
                navigateToLogin = navigateToLogin,
                navigateToWebView = navigateToWebView
            )
        }

        composable<MyPageGraph.WebViewRoute> { backStackEntry ->
            val webView = backStackEntry.toRoute<MyPageGraph.WebViewRoute>()
            WebViewRoute(url = webView.url)
        }
    }
}
