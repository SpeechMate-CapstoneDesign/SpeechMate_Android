package com.speech.mypage.graph.mypage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.common_ui.util.clickable
import com.speech.common_ui.util.combinedClickable
import com.speech.common_ui.util.rememberDebouncedOnClick
import com.speech.common_ui.util.rememberLazyListState
import com.speech.designsystem.R
import com.speech.designsystem.component.CheckCancelDialog
import com.speech.designsystem.component.SMDropDownMenu
import com.speech.designsystem.component.SMDropdownMenuItem
import com.speech.designsystem.theme.Green
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.PrimaryDefault
import com.speech.designsystem.theme.Purple
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.domain.model.speech.Audience
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFeed
import com.speech.domain.model.speech.SpeechFileType
import com.speech.domain.model.speech.SpeechType
import com.speech.domain.model.speech.Venue
import com.speech.mypage.graph.setting.SettingViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun MyPageRoute(
    navigateToSetting: () -> Unit,
    navigateToFeedBack: (Int, String, SpeechFileType, SpeechConfig) -> Unit,
    viewModel: MyPageViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MyPageSideEffect.ShowSnackbar -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(sideEffect.message)
                }
            }

            is MyPageSideEffect.NavigateToSetting -> navigateToSetting()
            is MyPageSideEffect.NavigateToFeedback -> navigateToFeedBack(
                sideEffect.speechId,
                sideEffect.fileUrl,
                sideEffect.speechFileType,
                sideEffect.speechConfig,
            )
        }
    }

    MyPageScreen(
        state = state,
        onSettingClick = { viewModel.onIntent(MyPageIntent.OnSettingClick) },
        onRefresh = viewModel::onRefresh,
        onSpeechClick = { speechId, fileUrl, speechFileType, speechConfig ->
            viewModel.onIntent(
                MyPageIntent.OnSpeechClick(
                    speechId,
                    fileUrl,
                    speechFileType,
                    speechConfig,
                ),
            )
        },
        onDeleteSpeech = { speechId ->
            viewModel.onIntent(
                MyPageIntent.OnDeleteClick(speechId),
            )
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MyPageScreen(
    state: MyPageState,
    onSettingClick: () -> Unit,
    onRefresh: () -> Unit,
    onSpeechClick: (Int, String, SpeechFileType, SpeechConfig) -> Unit,
    onDeleteSpeech: (Int) -> Unit,
) {
    val speechFeeds = state.speechFeeds.collectAsLazyPagingItems()
    val isRefreshing = speechFeeds.loadState.refresh is LoadState.Loading
    val isAppending = speechFeeds.loadState.append is LoadState.Loading
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            speechFeeds.refresh()
            onRefresh()
        },
    )
    val listState = speechFeeds.rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
    ) {
        PullRefreshIndicator(
            refreshing = false,
            state = pullRefreshState,
            contentColor = PrimaryDefault,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 48.dp),
        ) {
            item {
                Text(
                    "나의 스피치",
                    style = SpeechMateTheme.typography.headingMB,
                )

                Spacer(Modifier.height(20.dp))
            }

            items(
                count = speechFeeds.itemCount,
                key = { index -> speechFeeds[index]?.id ?: index },
            ) { index ->
                speechFeeds[index]?.let {
                    SpeechFeed(
                        speechFeed = it, onClick = onSpeechClick, onDelete = onDeleteSpeech,
                        modifier = Modifier.animateItem(
                            fadeInSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
                            placementSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                            fadeOutSpec = tween(durationMillis = 400, easing = FastOutLinearInEasing),
                        ),
                    )
                }

                Spacer(Modifier.height(12.dp))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, end = 10.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.setting_ic),
                contentDescription = "설정",
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.TopEnd)
                    .clickable(
                        onClick = rememberDebouncedOnClick {
                            onSettingClick()
                        },
                    ),
            )
        }

        if (isRefreshing || isAppending) {
            CircularProgressIndicator(
                color = PrimaryDefault,
                modifier = Modifier.align(
                    if (isRefreshing) Alignment.Center else Alignment.BottomCenter,
                ),
            )
        }
    }
}

@Composable
private fun SpeechFeed(
    modifier: Modifier = Modifier,
    speechFeed: SpeechFeed,
    onClick: (Int, String, SpeechFileType, SpeechConfig) -> Unit,
    onDelete: (Int) -> Unit,
) {
    var showDropdownMenu by remember { mutableStateOf(false) }
    var showDeleteDg by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, PrimaryDefault, RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = {
                    onClick(speechFeed.id, speechFeed.fileUrl, speechFeed.speechFileType, speechFeed.speechConfig)
                },
                onLongClick = {
                    showDropdownMenu = true
                },
            )
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = speechFeed.speechConfig.fileName,
                    style = SpeechMateTheme.typography.bodyXMSB,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.clock_ic),
                            contentDescription = "발표 시간",
                            modifier = Modifier.size(12.dp),
                            colorFilter = ColorFilter.tint(Color.Gray),
                        )

                        Text(
                            text = speechFeed.duration,
                            style = SpeechMateTheme.typography.bodyXSM,
                            color = Color.Gray,
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.calendar_ic),
                            contentDescription = "날짜",
                            modifier = Modifier.size(12.dp),
                            colorFilter = ColorFilter.tint(Color.Gray),
                        )

                        Text(
                            text = speechFeed.date,
                            style = SpeechMateTheme.typography.bodyXSM,
                            color = Color.Gray,
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.document_ic),
                        contentDescription = "발표 상황",
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(PrimaryActive),
                    )

                    Text(
                        text = speechFeed.speechConfig.speechType!!.label,
                        style = SpeechMateTheme.typography.bodySM,
                        color = Color.Gray,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.people_ic),
                        contentDescription = "청중",
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(Green),
                    )

                    Text(
                        text = speechFeed.speechConfig.audience!!.label,
                        style = SpeechMateTheme.typography.bodySM,
                        color = Color.Gray,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.location_ic),
                        contentDescription = "장소",
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(Purple),
                    )

                    Text(
                        text = speechFeed.speechConfig.venue!!.label,
                        style = SpeechMateTheme.typography.bodySM,
                        color = Color.Gray,
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .background(Color.Transparent),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            SMDropDownMenu(
                expanded = showDropdownMenu,
                onDismiss = { showDropdownMenu = false },
                width = 160,
                items = listOf(
                    SMDropdownMenuItem(
                        labelRes = R.string.delete,
                        action = { showDeleteDg = true },
                    ),
                ),
            )
        }

        if (showDeleteDg) {
            CheckCancelDialog(
                onCheck = { onDelete(speechFeed.id) },
                onDismiss = { showDeleteDg = false },
                content = "정말로 삭제하시겠습니까? 삭제된 분석 내역은 복구되지 않습니다.",
            )
        }
    }
}

@Preview
@Composable
private fun MyPageScreenPreview() {
    MyPageScreen(
        state = MyPageState(
            speechFeeds = flowOf(
                PagingData.from(
                    listOf(
                        SpeechFeed(
                            id = 1,
                            date = "23.10.27",
                            fileLength = 123456L,
                            fileUrl = "",
                            speechFileType = SpeechFileType.VIDEO,
                            speechConfig = SpeechConfig(
                                fileName = "1분기 실적 발표",
                                speechType = SpeechType.BUSINESS_PRESENTATION,
                                audience = Audience.EXPERT,
                                venue = Venue.CONFERENCE_ROOM,
                            ),
                        ),
                        SpeechFeed(
                            id = 2,
                            date = "23.10.27",
                            fileLength = 234567L,
                            fileUrl = "",
                            speechFileType = SpeechFileType.AUDIO,
                            speechConfig = SpeechConfig(
                                fileName = "신입사원 온보딩",
                                speechType = SpeechType.ACADEMIC_PRESENTATION,
                                audience = Audience.BEGINNER,
                                venue = Venue.EVENT_HALL,
                            ),
                        ),
                        SpeechFeed(
                            id = 3,
                            date = "23.10.27",
                            fileLength = 89012L,
                            fileUrl = "",
                            speechFileType = SpeechFileType.VIDEO,
                            speechConfig = SpeechConfig(
                                fileName = "개발자 컨퍼런스 발표",
                                speechType = SpeechType.BUSINESS_PRESENTATION,
                                audience = Audience.INTERMEDIATE,
                                venue = Venue.LECTURE_HALL,
                            ),
                        ),
                        SpeechFeed(
                            id = 4,
                            date = "23.10.27",
                            fileLength = 345678L,
                            fileUrl = "",
                            speechFileType = SpeechFileType.VIDEO,
                            speechConfig = SpeechConfig(
                                fileName = "투자 유치 발표",
                                speechType = SpeechType.BUSINESS_PRESENTATION,
                                audience = Audience.EXPERT,
                                venue = Venue.CONFERENCE_ROOM,
                            ),
                        ),
                        SpeechFeed(
                            id = 5,
                            date = "23.10.27",
                            fileLength = 500000L,
                            fileUrl = "",
                            speechFileType = SpeechFileType.AUDIO,
                            speechConfig = SpeechConfig(
                                fileName = "팀 회의 발표",
                                speechType = SpeechType.BUSINESS_PRESENTATION,
                                audience = Audience.INTERMEDIATE,
                                venue = Venue.CONFERENCE_ROOM,
                            ),
                        ),
                    ),
                ),
            ),
        ),
        onSettingClick = {},
        onSpeechClick = { _, _, _, _ -> },
        onDeleteSpeech = {},
        onRefresh = {},
    )
}
