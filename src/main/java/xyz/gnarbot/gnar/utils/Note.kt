package xyz.gnarbot.gnar.utils

import net.dv8tion.jda.core.entities.Message
import xyz.gnarbot.gnar.handlers.members.Member
import xyz.gnarbot.gnar.handlers.servers.Host

/**
 * Gnar's wrapper class for JDA's [Message].
 *
 * @see Message
 */
class Note(val host : Host, private val message : Message) : Message by message
{
    /**
     * The author of this Message as a [Member] instance.
     *
     * @return Message author as Member.
     */
    override fun getAuthor() : Member = host.memberHandler.asMember(message.author)
    
    /**
     * Get mentioned users of this Message as [Member] instances.
     *
     * @return Immutable list of mentioned [Member] instances.
     */
    override fun getMentionedUsers() : List<Member> = mentionedUsers.map { host.memberHandler.asMember(it) }
    
    /**
     * Stylized quick-reply to a message.
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    fun reply(msg : String) = Note(host, channel.sendMessage("__**${message.author.name}**__ \u279c $msg").block())
    
    /**
     * Quick-reply to a message.
     *
     * @param msg The text to send.
     * @return The Message created by this function.
     */
    fun replyRaw(msg : String) = Note(host, channel.sendMessage(msg).block())
    
    /**
     * @return String representation of the note.
     */
    override fun toString() = "Note(id=$id, author=${author.name}, content=\"$content\")"
}
