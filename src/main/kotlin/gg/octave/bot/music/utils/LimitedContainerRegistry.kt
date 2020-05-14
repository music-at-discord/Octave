package gg.octave.bot.music.utils

import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry
import com.sedmelluq.discord.lavaplayer.container.flac.FlacContainerProbe
import com.sedmelluq.discord.lavaplayer.container.matroska.MatroskaContainerProbe
import com.sedmelluq.discord.lavaplayer.container.mp3.Mp3ContainerProbe
import com.sedmelluq.discord.lavaplayer.container.mpeg.MpegContainerProbe
import com.sedmelluq.discord.lavaplayer.container.ogg.OggContainerProbe
import com.sedmelluq.discord.lavaplayer.container.wav.WavContainerProbe

class LimitedContainerRegistry : MediaContainerRegistry(
    listOf(
        FlacContainerProbe(), // FLAC
        MatroskaContainerProbe(), // MKV
        MpegContainerProbe(), // MP4
        Mp3ContainerProbe(), // MP3
        OggContainerProbe(), // OGG
        WavContainerProbe() // WAV
    )
)
