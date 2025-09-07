package com.speech.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.speech.domain.model.speech.SpeechFeed
import com.speech.network.model.cursor.Cursor
import com.speech.network.source.speech.SpeechDataSource

class SpeechFeedPagingSource(
    private val speechDataSource: SpeechDataSource,
    private val pageSize: Int = 20
) : PagingSource<Cursor<Int>, SpeechFeed>() {
    override suspend fun load(params: LoadParams<Cursor<Int>>): LoadResult<Cursor<Int>, SpeechFeed> {
        return try {
            val cursor = params.key

            val response = speechDataSource.getSpeechFeeds(
                lastSpeechId = cursor?.id ?: 0,
                limit = pageSize,
            )

            val speechFeeds = response.toDomain()

            val nextCursor = if (response.hasNext) {
                val responseCursor = response.cursor
                    ?: throw IllegalStateException("Cursor must be present when hasNext is true")
                Cursor(
                    id = responseCursor.id,
                    dateTime = responseCursor.dateTime
                )
            } else {
                null
            }

            val safeNextCursor = if (nextCursor == params.key) null else nextCursor

            LoadResult.Page(
                data = speechFeeds,
                prevKey = null,
                nextKey = safeNextCursor
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Cursor<Int>, SpeechFeed>): Cursor<Int>? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}
