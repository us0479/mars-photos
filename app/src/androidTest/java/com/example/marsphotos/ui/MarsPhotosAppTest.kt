package com.example.marsphotos.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.marsphotos.data.MarsPhoto
import com.example.marsphotos.ui.theme.MarsPhotosTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MarsPhotosAppTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loading_displaysLoadingMessage() {
        composeRule.setContent {
            MarsPhotosTheme {
                MarsPhotosApp(
                    uiState = MarsUiState.Loading,
                    onRetry = {},
                )
            }
        }

        composeRule
            .onNodeWithText("화성 사진 목록을 불러오는 중입니다.")
            .assertIsDisplayed()
    }

    @Test
    fun error_displaysMessage_andRetryInvokesCallback() {
        var retryCount = 0

        composeRule.setContent {
            MarsPhotosTheme {
                MarsPhotosApp(
                    uiState = MarsUiState.Error,
                    onRetry = { retryCount++ },
                )
            }
        }

        composeRule
            .onNodeWithText("사진 목록을 불러오지 못했습니다.")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("다시 시도")
            .assertIsDisplayed()
            .performClick()

        composeRule.runOnIdle {
            assertEquals(1, retryCount)
        }
    }

    @Test
    fun success_displaysPhotoCount_andPhotoCard() {
        composeRule.setContent {
            MarsPhotosTheme {
                MarsPhotosApp(
                    uiState = MarsUiState.Success(listOf(MarsPhoto("42", ""))),
                    onRetry = {},
                )
            }
        }

        composeRule.onNodeWithText("사진 1장").assertIsDisplayed()
        composeRule.onNodeWithText("ID 42").assertIsDisplayed()
    }
}