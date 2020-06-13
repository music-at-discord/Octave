package gg.octave.bot.music.sources.spotify

import com.sedmelluq.discord.lavaplayer.tools.http.HttpContextFilter
import com.sedmelluq.discord.lavaplayer.tools.http.HttpContextRetryCounter
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext

class SpotifyHttpContextFilter(private val accessTokenTracker: SpotifyAccessTokenTracker) : HttpContextFilter {
    private val retryCounter = HttpContextRetryCounter("spotify-token-retry")

    override fun onContextOpen(context: HttpClientContext) {
    }

    override fun onContextClose(context: HttpClientContext) {
    }

    override fun onRequest(context: HttpClientContext, request: HttpUriRequest, isRepetition: Boolean) {
        retryCounter.handleUpdate(context, isRepetition)

        if (accessTokenTracker.isIdFetchContext(context)) {
            return
        }

        request.addHeader("Authorization", "Bearer ${accessTokenTracker.latestAccessToken}")
    }

    override fun onRequestResponse(context: HttpClientContext, request: HttpUriRequest, response: HttpResponse): Boolean {
        return when {
            accessTokenTracker.isIdFetchContext(context) || retryCounter.getRetryCount(context) >= 1 -> false
            response.statusLine.statusCode == HttpStatus.SC_UNAUTHORIZED -> {
                accessTokenTracker.updateAccessToken()
                true
            }
            else -> false
        }
    }

    override fun onRequestException(context: HttpClientContext, request: HttpUriRequest, error: Throwable): Boolean {
        return false
    }
}
