# Welcome to Shmames
The Discord bot that's changing the world.

### What is Shmames?
Shmames is a utility and meme bot, giving your Discord server a little more flavor. Get things done
by creating polls, automating responses, and tracking tallies, or have some fun with playing
hangman, minesweeper, and rolling dice. Shmames can send random GIFs for you, or let you create
shortcuts to your own. It even keeps track of which custom emoji get used the most on your server,
making it easier to know which ones to get rid of.

Shmames is feature-rich, quirky, and a fun new way to interact with your Discord server, and it's
100% free.

### Features
* Create polls with up to 9 different options
* Customize random responses based on keywords
* Track custom tallies
* Create shortcuts to URL links, and send them in a snap
* Send a random GIF, based on search keywords
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

### Getting Started
To add Shmames to your server, simply click on the link to invite him. Server administrators
will be able to change Shmames' settings to customize permissions and behavior to best fit the
server, all from within Discord. If you aren't sure how a command works, simply use `shmames help
<command>` for more details.

### NEW: Server Families
> Introducing Shmames Families: a new way to group your servers together, and share your Shmames
data between them in a secure way. Families currently allow you to use ForumWeapons and custom Emoji
(via the `simonsays` command) from any server in the Family. Tired of duplicating your stuff
on multiple servers? Shmames is here to the rescue! Check out the new command `shmames family`
for more details.

### Commands
Below is a list of Shmames commands, and a short definition of what it does. You can also view command
information in Discord, using `shmames help` or `shmames help <command>`!

**How to read command arguments**

Shmames' commands follow a particular pattern. When a piece of information is required for the command,
it will be marked with `<angle brackets>`. If it is an optional command argument, it will be marked
with `[square brackets]`. Certain commands are more complex than others, so this rule can help you
understand how to use them!

#### Random Response Types
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

#### Responses and Triggers

> ##### addTrigger &lt;type&gt; &lt;word&gt;
Create a new trigger for a Random Response, using the `<type>` type.

> ##### addResponse &lt;type&gt; &lt;text&gt;
Add a new message response to the `<type>` category.

> ##### dropTrigger &lt;word&gt;
Removes the trigger specified, so that it won't be used anymore.

> ##### dropResponse &lt;type&gt; &lt;number&gt;
Removes the random response, so that it won't be sent anymore. `<number>` can be found by listing the
existing responses.

> ##### listResponses &lvt;type&gt;
Get a list of all random responses currently saved for the `<type>` category. 

> ##### listTriggers
Get a list of all triggers currently saved, and their trigger types.

#### Tallies

> ##### addTally &lt;name&gt;
Increments (adds 1) to the `<name>` tally, or sets it to `1` if it didn't already exist.

> ##### dropTally &lt;name&gt;
Decrements (removes 1) from the `<name>` tally, or removes it if the tally is `0`.

> ##### listTallies
Get a list of all the tallies currently saved.

> ##### setTally &lt;name&gt; &lt;value&gt;
Override the count for the `<name>` tally with a new value.

#### Chat Commands

> ##### 8ball &lt;question&gt;
Shake a virtual Magic 8 Ball and have Shmames predict the future!

> ##### blame &lt;thing&gt;
Shmames will randomly blame something on your favorite country, politician, meme, etc.

> ##### choose &lt;option 1&gt; or &lt;option 2&gt;
Can't make a decision? Shmames will pick between one of the options for you!

> ##### cringeThat &lt;^...&gt; [times]
Shmames will rewrite a previous message in a cringy way. Specify the message using `^` symbols. One `^` is
the last message, `^^` was two messages ago, etc. (Up to 15). Optionally specify how many times to rewrite it,
getting worse each time!

> ##### enhance &lt;thing&gt;
Shmames will find a way to make a `<thing>` even better!

> ##### idiotThat &lt;^...&gt;
Shmames will rewrite a previous message with poor grammar. Specify the message using `^` symbols. One `^` is
the last message, `^^` was two messages ago, etc. (Up to 15).

> ##### jinping
Activate Jinping mode, and spam the 🏓 emote for 1 minute in support of the Hong Kong pro-democracy 
protesters.

> ##### nickname &lt;new nickname&gt;
Change Shmames' nickname on your server!

> ##### pinThat &lt;^...&gt;
 Pin a message to the Pin Channel, chosen by an administrator. Specify the message using `^` symbols. One `^` is
the last message, `^^` was two messages ago, etc. (Up to 15).
 
> ##### react &lt;word&gt; &lt;^...&gt;
 React to a message using emojis that spell out a word. Specify the message using `^` symbols. One `^` is
the last message, `^^` was two messages ago, etc. (Up to 15).
 
> ##### simonSays &lt;phrase&gt;
 Have Shmames say whatever you'd like! This can also include emojis.
 
> ##### thoughts &lt;thing&gt;
  Get Shmames' thoughts on something.
  
> ##### what should I do
   Bored? Shmames will give you an idea for what to do! These ideas may not be safe, ethical, or fit
   for human consumption.

### Utility Commands

> ##### closePoll &lt;poll ID&gt;
Need to end a Poll early? Use this command, along with the Poll's ID, to close it.

> ##### family &lt;create|add|view|remove&gt; [family|code] [server]
Manage a Shmames Family server group. Families allow servers to share certain things with each other,
with more features to come. See the command in Discord for more details.

> ##### fw [create|update|remove] &lt;weapon name&gt; [weapon link]
Create, manage, and send Forum Weapons - simple shortcuts to your favorite GIFs, videos, and links. Join a Family
to use these in other servers, too!

> ##### fwlist [all]
View the Forum Weapons currently saved on this server. If you're in a Family, use the `[all]` argument
to view all weapons in the Family as well.

> ##### gif &lt;thing&gt;
Search Tenor for a random GIF of `<thing>`. Content is limited to Tenor's PG-13 rating and below.

> ##### help [command]
View a list of commands Shmames can use, or see more information on a specific command.

> ##### listEmoteStats
See the number of times your custom Emojis have been used in messages, reactions, and `simonSays`.

> ##### modify &lt;setting&gt; [new value]
Modify is the server admin's command for controlling permissions and bot behavior. Settings are here
to keep your server safe, secure, and customized the way you want it.

> ##### newSeed [seed]
Generate a new (pseudo) random seed for the bot. This seed is used in the sending of random messages
and rolling of dice.

> ##### resetEmoteStats
Reset the counts of your custom Emoji to `0`, and start over.

> ##### roll a d&lt;#&gt;[+#]
Roll some dice! For example, `roll a d20`, or `roll 3d6+8`.

> ##### startPoll &lt;time&gt;[d/h/m/s] &lt;question&gt;? &lt;option&gt;; &lt;option&gt;...
Create a new server poll. Specify how long it should last before closing with `<time>` (ex. `3h` for 3 hours,
or `60m` for 60 minutes). Enter your question, and separate your options with a `;`. Example: `startPoll
1h What is your favorite color? Red; Blue; Green; Very Very Dark Gray; Other`

> ##### timer &lt;time&gt; [description]
Start a new timer, with an optional description. Shmames will ping you when the timer completes.

> ##### wiki &lt;thing&gt;
Search WolframAlpha for a short description of something, if available.

#### Games

> ##### hangman &lt;start|guess|list&gt; [dictionary|letter|answer]
Play a game of Hangman! Shmames has a growing list of puzzles for players of all kinds. Currently, there
are puzzles in the following categories: Video Games, Dungeons and Dragons, Anime, and Books/Movies/TV.

> ##### minesweep &lt;grid size&gt;
Play a game of Minesweeper! Shmames will generate a new game for you, and then send the puzzle in chat.
You can pick a grid size between 6 and 11.