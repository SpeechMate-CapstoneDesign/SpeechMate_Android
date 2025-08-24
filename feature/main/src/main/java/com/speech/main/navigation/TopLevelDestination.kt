package com.speech.main.navigation

import androidx.annotation.DrawableRes
import com.speech.designsystem.R
import com.speech.navigation.MyPageBaseRoute
import com.speech.navigation.PracticeBaseRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val route : KClass<*>,
    @DrawableRes val icon : Int,
    val contentDescription: String,
    val title : String
) {
    Practice(
        route = PracticeBaseRoute::class,
        icon = R.drawable.bottom_practice,
        contentDescription = "연습",
        title = "연습"
    ),
    MYPage(
        route = MyPageBaseRoute::class,
        icon = R.drawable.bottom_my_page,
        contentDescription = "마이페이지",
        title = "마이페이지"
    ),
}