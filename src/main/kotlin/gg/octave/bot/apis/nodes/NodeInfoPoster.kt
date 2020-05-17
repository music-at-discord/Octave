package gg.octave.bot.apis.nodes

import gg.octave.bot.Launcher
import org.json.JSONObject
import java.lang.management.ManagementFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class NodeInfoPoster(var nodeId: Int) {
    private val scheduler = Executors.newSingleThreadScheduledExecutor()

    fun postEvery(time: Long, unit: TimeUnit) {
        scheduler.scheduleWithFixedDelay({
            val ramUsedBytes = Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() }

            Launcher.database.jedisPool.resource.use {
                it.hset("node-stats", nodeId.toString(),
                        JSONObject()
                                .put("music_players", Launcher.players.size())
                                .put("uptime", ManagementFactory.getRuntimeMXBean().uptime)
                                .put("used_ram", ramUsedBytes)
                                .put("thread_count", Thread.activeCount())
                                .toString()
                )
            }
        }, time, time, unit)
    }

}