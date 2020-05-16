package gg.octave.bot.utils.extensions

import gg.octave.bot.entities.framework.Usages
import me.devoxin.flight.internal.entities.Executable
import net.dv8tion.jda.api.entities.*
import java.time.Duration
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

fun Executable.generateDefaultUsage(): String {
    return buildString {
        for (arg in this@generateDefaultUsage.arguments) {
            val value = when (arg.type) {
                String::class.java -> "\"some text\""
                Int::class, java.lang.Integer::class.java, Long::class.java, java.lang.Long::class.java -> "0"
                Double::class.java, java.lang.Double::class.java -> "0.0"
                Member::class.java, User::class.java -> "@User"
                Role::class.java -> "@DJ"
                TextChannel::class.java -> "#general"
                VoiceChannel::class.java -> "Music"
                Boolean::class.java, java.lang.Boolean::class.java -> "yes"
                Duration::class.java -> "20m"
                else -> {
                    if (arg.type.isEnum) {
                        arg.type.enumConstants.first().toString().toLowerCase()
                    } else {
                        "[Unknown Type, report to devs]"
                    }
                }
            }
            append(value)
            append(" ")
        }
    }.trim()
}

@ExperimentalStdlibApi
fun Executable.generateExampleUsage(commandLayout: String): String {
    return when {
        this.method.hasAnnotation<Usages>() -> this.method.findAnnotation<Usages>()!!.usages.joinToString("\n") { "`$commandLayout $it`" }
        else -> "`" + "$commandLayout ${generateDefaultUsage()}".trim() + "`"
    }
}
