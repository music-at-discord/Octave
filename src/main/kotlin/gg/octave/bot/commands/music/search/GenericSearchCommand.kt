package gg.octave.bot.commands.music.search

import com.jagrosh.jdautilities.selector
import gg.octave.bot.Launcher
import gg.octave.bot.commands.music.embedTitle
import gg.octave.bot.commands.music.embedUri
import gg.octave.bot.music.utils.MusicLimitException
import gg.octave.bot.utils.Utils
import gg.octave.bot.utils.extensions.selfMember
import gg.octave.bot.utils.extensions.voiceChannel
import me.devoxin.flight.api.Context
import java.awt.Color

fun GenericSearchCommand(ctx: Context, query: String, searchPrefix: String, provider: String, color: Color, link: String, icon: String, thumbnail: String) {
    Launcher.players.get(ctx.guild).search("$searchPrefix:$query", 5) { results ->
        if (results.isEmpty()) {
            return@search ctx.send("No search results for `$query`.")
        }

        val botChannel = ctx.selfMember!!.voiceState?.channel
        val userChannel = ctx.voiceChannel

        if (userChannel == null || botChannel != null && botChannel != userChannel) {
            return@search ctx.send {
                setColor(Color(141, 20, 0))
                setAuthor("$provider Results", link, icon)
                setThumbnail(thumbnail)
                setDescription(
                    results.joinToString("\n") {
                        "**[${it.info.embedTitle}](${it.info.embedUri})**\n" +
                            "**`${Utils.getTimestamp(it.duration)}`** by **${it.info.author}**\n"
                    }
                )
                setFooter("Want to play one of these music tracks? Join a voice channel and reenter this command.", null)
            }
        }

        Launcher.eventWaiter.selector {
            setColor(color)
            setTitle("$provider Results")
            setDescription("Select one of the following options to play them in your current music channel.")
            setUser(ctx.author)

            for (result in results) {
                addOption("`${Utils.getTimestamp(result.info.length)}` **[${result.info.embedTitle}](${result.info.embedUri})**") {
                    if (ctx.member!!.voiceState!!.inVoiceChannel()) {
                        val manager = try {
                            Launcher.players.get(ctx.guild)
                        } catch (e: MusicLimitException) {
                            return@addOption e.sendToContext(ctx)
                        }

                        val args = query.split(" +".toRegex())
                        Play.smartPlay(ctx, manager, args, true, result.info.uri)
                    } else {
                        ctx.send("You're not in a voice channel anymore!")
                    }
                }
            }

            finally { it?.delete()?.queue() }
        }.display(ctx.textChannel!!)
    }
}
