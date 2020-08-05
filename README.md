<img align="right" src="https://octave.gg/assets/img/logo.png" height="250" width="250">

# Octave [![License](https://img.shields.io/github/license/mashape/apistatus.svg?style=flat-square)](LICENSE) [![Chat](https://img.shields.io/badge/chat-discord-blue.svg?style=flat-square)](https://discord.gg/musicbot)
**Octave** is an open-source Discord bot written in Java and Kotlin, using JDA and Lavaplayer.
Octave provides a premium music experience.

## Links
[Click here](https://discordapp.com/oauth2/authorize?client_id=201492375653056512&scope=bot&permissions=8) to add Octave to a server, provided that you have the `Manage Server` permission.

[Click here](https://octave.gg/) to head to Octave's website.

## Self-hosting Octave
- Though we do not disallow user-hosted versions of Octave, we will not provide **any** support for it.
- We are not responsible for anything that this project does to you or your server and can not be held liable 
    for anything pertaining to it. 

## Music@Discord selfhosting how-to (incomplete)

Prerequisites:
- Git
- JDK 11
- RethinkDB (A database called 'bot' and the tables: `guilds_v2` `premiumguilds` `premiumusers`)
- Redis

Setup:
- Clone the respository using: `git clone https://github.com/music-at-discord/Octave`
- Switch directory to the bot: `cd Octave`
- Rename `credentials.conf.example` to `credentials.conf`
- Open `credentials.conf` in your favourite text editor
- Set 'token' to your bot's token, make sure to include ""
- Set the 'console webhook' to a webhook for logging, again be sure to include ""
- run `./gradlew run` to compile and run the bot