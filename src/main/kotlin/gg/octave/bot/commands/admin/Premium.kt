package gg.octave.bot.commands.admin

import gg.octave.bot.Launcher
import gg.octave.bot.utils.extensions.DEFAULT_SUBCOMMAND
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.SubCommand
import me.devoxin.flight.api.entities.Cog
import me.devoxin.flight.internal.arguments.types.Snowflake

class Premium : Cog {
    @Command(description = "Set a user's premium status", developerOnly = true)
    fun premium(ctx: Context) = DEFAULT_SUBCOMMAND(ctx)

    @SubCommand(aliases = ["+"], description = "Add/edit an override")
    fun add(ctx: Context, user: Snowflake, pledge: Double) {
        val pu = Launcher.db.getPremiumUser(user.resolved.toString())
        pu.isOverride = true
        pu.pledgeAmount = pledge
        pu.save()

        ctx.send {
            setColor(0x9570D3)
            setTitle("Premium Status")
            setDescription("<@${user.resolved}>'s premium status has been overridden and saved.")
        }
    }

    @SubCommand(aliases = ["-"], description = "Remove an override (deletes entry)")
    fun remove(ctx: Context, user: Snowflake, pledge: Double) {
        val pu = Launcher.db.getPremiumUser(user.resolved.toString())
        pu.delete()

        ctx.send {
            setColor(0x9570D3)
            setTitle("Premium Status")
            setDescription("<@${user.resolved}>'s premium status has been revoked.")
        }
    }
}
