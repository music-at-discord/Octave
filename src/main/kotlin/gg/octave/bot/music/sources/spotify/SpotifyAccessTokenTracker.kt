package gg.octave.bot.music.sources.spotify

import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

class SpotifyAccessTokenTracker(clientId: String, clientSecret: String, private val sourceManager: SpotifyAudioSourceManager) {
    private val encoded = Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())
    private val tokenLock = Any()

    private var _accessToken: String? = null
    private var lastTokenUpdate = 0L
    private var tokenExpiryTime = 0L

    private val isTokenExpired: Boolean
        get() = _accessToken == null || tokenExpiryTime > 0L && System.currentTimeMillis() > tokenExpiryTime

    val latestAccessToken: String?
        get() {
            synchronized(tokenLock) {
                if (_accessToken == null) {
                    updateAccessToken()
                }

                return _accessToken
            }
        }

    fun updateAccessToken(force: Boolean = false) {
        synchronized(tokenLock) {
            val now = System.currentTimeMillis()
            val isEmergencyUpdate = _accessToken.isNullOrEmpty() && now - lastTokenUpdate > EMERGENCY_TOKEN_REFRESH_INTERVAL
            val isNormalUpdate = now - lastTokenUpdate > TOKEN_REFRESH_INTERVAL || force

            if (!isTokenExpired && !isEmergencyUpdate && !isNormalUpdate) {
                log.debug("The access token was updated recently, not updating again immediately")
                return
            }

            lastTokenUpdate = now
            log.info("Updating the Spotify access token")

            try {
                sourceManager.request(
                    HttpPost.METHOD_NAME,
                    "https://accounts.spotify.com/api/token",
                    {
                        context.setAttribute(TOKEN_FETCH_CONTEXT_ATTRIBUTE, true)
                    },
                    {
                        addHeader("Authorization", "Basic $encoded")
                        addHeader("Content-Type", "application/x-www-form-urlencoded")
                        entity = StringEntity("grant_type=client_credentials")
                    }
                ).use {
                    if (it.statusLine.statusCode != HttpStatus.SC_OK) {
                        _accessToken = null
                        log.warn("Spotify access token update failed: code=${it.statusLine.statusCode}")
                        return
                    }

                    val content = EntityUtils.toString(it.entity)
                    val json = JSONObject(content)

                    if (json.has("error")) {
                        _accessToken = null
                        log.error("Spotify access token update failed: ${json.getString("error")}")
                        return
                    }

                    // expires_in = seconds
                    tokenExpiryTime = System.currentTimeMillis() + json.getInt("expires_in").toLong() * 1000
                    _accessToken = json.getString("access_token")
                    log.info("Spotify access token updated successfully")
                }
            } catch (e: Exception) {
                _accessToken = null

            }
        }
    }

    fun isIdFetchContext(context: HttpClientContext): Boolean {
        return context.getAttribute(TOKEN_FETCH_CONTEXT_ATTRIBUTE).let { it is Boolean && it }
    }

    companion object {
        private val log = LoggerFactory.getLogger(SpotifyAccessTokenTracker::class.java)
        private val TOKEN_REFRESH_INTERVAL = TimeUnit.HOURS.toMillis(1)
        private val EMERGENCY_TOKEN_REFRESH_INTERVAL = TimeUnit.MINUTES.toMillis(5)
        private const val TOKEN_FETCH_CONTEXT_ATTRIBUTE = "sp-at"
    }
}
