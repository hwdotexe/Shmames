[![Build](https://github.com/hwdotexe/Shmames/actions/workflows/maven.yml/badge.svg)](https://github.com/hwdotexe/Shmames/actions/workflows/maven.yml)

# Welcome to Shmames
The Discord bot that's changing the world.

## What is Shmames?
Shmames is a multi-purpose Discord chatbot that takes nothing seriously, giving your Discord server a little more flavor.
Get things done by creating polls, automating responses, and tracking tallies, or have some fun with playing
hangman, minesweeper, and rolling dice. Shmames can send random GIFs for you, or let you create
shortcuts to your own. It even keeps track of which custom emoji get used the most on your server,
making it easier to know which ones you can get rid of.

Shmames is feature-rich, quirky, and a fun new way to interact with your Discord server, and it's 100% free.

## Features
* Create polls with up to 9 different options
* Customize random responses based on keywords
* Track custom tallies
* Create shortcuts to URL links, and send them in a snap
* Send a random GIF based on search keywords
* Play a game of Hangman or Minesweeper
* View usage stats on your custom Emoji
* Let your users pin messages to a specialized channel
* Add reactions to messages that spell out words
* Roll dice, up to a d999
* Have the bot repeat after you
* Start timers
* Use fun (sometimes ridiculous) chat commands
* Create families of servers to share data
* ... and much more

## Usage
To add the official, hosted version of Shmames to your Discord server, [click this link](https://discord.com/api/oauth2/authorize?client_id=377639048573091860&permissions=70642752&redirect_uri=https%3A%2F%2Fdiscordapp.com%2Fapi%2Foauth2%2Fauthorize&scope=bot).

If you'd like to build and run your _own_ version of Shmames, keep reading.

### Commands
Shmames comes with a lot of chat commands, a list too long to keep here. To view a list of these commands and how to use
them, be sure to run `shmames help [command]` on your Discord server.

#### How to read command arguments
Shmames' commands follow a particular pattern. When a piece of information is required for the command,
it will be marked with `<angle brackets>`. If it is an optional command argument, it will be marked
with `[square brackets]`. Certain commands are more complex than others, so this rule can help you
understand how to use them!

The default command to summon Shmames is `shmames`, or whichever name you've given your custom implementation if you
are hosting Shmames yourself.

### Random Response Types
Shmames can send a random response based on a **message trigger**. Valid triggers are:

> `Love`: For when Shmames should say something nice.
> 
> `Hate`: For when Shmames isn't too happy about something.
> 
> `Hello`: For when Shmames wants to say hi.
> 
> `Goodbye`: For when Shmames wants to say goodbye.
> 
> `Random`: For any other type of response. Shmames might occasionally send one of these messages, if
he's feeling like it.
> 
> `React`: Shmames will add a reaction to the message.
> 
> `Command`: Use this trigger to customize how you summon the bot.

## Custom Shmames Installation
To build and run Shmames yourself, use the following steps. If you'd like to use a pre-assembled binary from the [Releases](https://github.com/hwdotexe/Shmames/releases)
page, skip to **Step 4**.

#### Step 1
Clone the repository by using `git clone https://github.com/hwdotexe/Shmames.git`

#### Step 2
Import Shmames as a Maven project in your favorite IDE.

#### Step 3
Build Shmames by using `mvn clean package assembly:single`

#### Step 4
Create a `.bat`/`.sh` file to launch the Shmames binary `.jar`:
 
 ```shell script
java -jar name_of_shmames_file.jar
```
 
After launching, the application will generate some first-time setup files.

#### Step 5
You'll need to create a new Discord application by visiting the [Discord Developer Portal](https://discord.com/developers/applications/). Choose
a name for your custom Shmames, and follow Discord's online prompts. When finished, you can find the bot's Token (needed later) by clicking on
your app, going to "Bot" on the side, and clicking `Click to Reveal Token`.

#### Step 6
Where you launched Shmames, there is a new file in `/brains` called `motherBrain.json`. Open this file, and replace the value
for `botAPIKey` with the token you created in the last step. You may notice that there is another option, `botAPIKeySecondary`. This is
used for when you launch the bot in **debug mode**, in case you'd like to debug with a different Discord application. You can ignore
this item for now.

#### Step 7
Launch the bot again, and it should finish the startup process and be ready to roll. You can now invite your bot to your Discord server
using the Discord Developer Portal - simply create an OAuth2 authorization URL for your bot, and use it to join the bot to a server
you have permission to change.

#### Step 8
Once the bot joins your server, it should send a welcome message if possible. Should this not happen, you can still attempt to summon
the bot by using its name - for example, `shmames help`. Have fun!
