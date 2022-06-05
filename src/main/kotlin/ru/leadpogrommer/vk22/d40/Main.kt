package ru.leadpogrommer.vk22.d40

import dev.kord.core.Kord
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.core.event.channel.ChannelUpdateEvent
import dev.kord.core.event.channel.VoiceChannelUpdateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.event.user.VoiceStateUpdateEvent
import dev.kord.core.on
import dev.kord.gateway.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@OptIn(PrivilegedIntent::class)
suspend fun main(args: Array<String>){
    val parser = ArgParser("sportprog")
    val token by parser.option(ArgType.String).required()
    parser.parse(args)

    val lessons = loadCsv(File("test.csv"))



    val kord = Kord(token)

    kord.on<VoiceStateUpdateEvent> {
        val channel = kord.getChannel(state.channelId?:return@on)
        if(channel !is VoiceChannel) return@on
        println("${state.getMember().fetchUser().tag} joined ${channel.name} at ${Date()}")
        val username = state.getMember().fetchUser().tag

        var ok = false
        for (lesson in lessons){
            if(channel.name != lesson.Channel)continue
            if (username !in lesson.users)continue
            if(LocalDate.now().dayOfWeek !in lesson.days)continue
            val time = LocalDateTime.now().hour *60 + LocalDateTime.now().minute
            if(time !in lesson.start .. lesson.end)continue
            ok = true
            break
        }
        var message = ""
        if(ok){
            message = "$username joined ${channel.name} at time"
        }else{
            message = "It is not time for you to join ${channel.name}, $username"
        }
        channel.getGuild().channels.collect{
            if(it is TextChannel){
                it.createMessage(message)
            }
        }

    }

    kord.login{
        intents += Intent.MessageContent
    }

}