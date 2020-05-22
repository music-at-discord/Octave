/*
 * MIT License
 *
 * Copyright (c) 2020 Melms Media LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package gg.octave.bot.db.music;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import gg.octave.bot.Launcher;
import gg.octave.bot.db.ManagedObject;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicPlaylist extends ManagedObject {
    @ConstructorProperties("id")
    public MusicPlaylist(String id) {
        super(id, "savedplaylists");
    }

    private String name;
    private List<String> encodedTracks = new ArrayList<>();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getEncodedTracks() {
        return encodedTracks;
    }

    @JsonIgnore
    public BasicAudioPlaylist toLavaPlaylist() {
        return Launcher.INSTANCE.getPlayers().getPlayerManager().decodePlaylist(encodedTracks, name);
    }

    @JsonIgnore
    public void replacePlaylist(BasicAudioPlaylist playlist) {
        encodedTracks = Launcher.INSTANCE.getPlayers().getPlayerManager().encodePlaylist(playlist);
    }

    @JsonIgnore
    public void appendTrack(AudioTrack track) throws IOException {
        encodedTracks.add(Launcher.INSTANCE.getPlayers().getPlayerManager().encodeAudioTrack(track));
    }

    @JsonIgnore
    public void appendTracks(List<AudioTrack> tracks) {
        tracks.forEach(Launcher.INSTANCE.getPlayers().getPlayerManager()::encodeAudioTrack);
    }
}
