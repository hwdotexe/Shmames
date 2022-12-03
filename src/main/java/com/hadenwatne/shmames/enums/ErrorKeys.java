package com.hadenwatne.shmames.enums;

import com.google.gson.annotations.SerializedName;

public enum ErrorKeys {
	ALREADY_EXISTS,
	BOT_ERROR,
	CANNOT_DELETE,
	CHANNEL_NOT_FOUND,
	COMMAND_NOT_FOUND,
	FAMILY_ALREADY_EXISTS,
	FAMILY_ALREADY_JOINED,
	FAMILY_INVALID_DETAIL,
	@SerializedName(value = "FAMILY_LIST_EMPTY", alternate = "SERVER_FAMILY_LIST_EMPTY")
	FAMILY_LIST_EMPTY,
	FAMILY_MEMBER_MAXIMUM_REACHED,
	FAMILY_MAXIMUM_REACHED,
	FAMILY_NOT_JOINED,
	FAMILY_SERVER_LIST_EMPTY,
	FORUM_WEAPON_MAXIMUM_REACHED,
	FORUM_WEAPON_OWNED_OTHER,
	GACHA_NO_COINS,
	HANGMAN_ALREADY_GUESSED,
	HANGMAN_ALREADY_STARTED,
	HANGMAN_NOT_STARTED,
	HEY_THERE,
	INCORRECT_ITEM_COUNT,
	ITEMS_NOT_FOUND,
	MUSIC_NOT_IN_CHANNEL,
	MUSIC_PLAYLIST_ALREADY_EXISTS,
	MUSIC_PLAYLIST_DOESNT_EXIST,
	MUSIC_PLAYLIST_EMPTY,
	MUSIC_PLAYLIST_LIST_EMPTY,
	MUSIC_PLAYLIST_NAME_INVALID,
	MUSIC_PLAYLIST_NAME_MISSING,
	MUSIC_PLAYLIST_PAGE_EMPTY,
	MUSIC_PLAYLIST_TRACK_MAXIMUM_REACHED,
	MUSIC_QUEUE_EMPTY,
	MUSIC_QUEUE_PAGE_EMPTY,
	MUSIC_WRONG_INPUT,
	NO_PERMISSION_BOT,
	NO_PERMISSION_USER,
	NOT_FOUND,
	PAGE_NOT_FOUND,
	RESERVED_WORD,
	SETTING_VALUE_INVALID,
	TIME_VALUE_INCORRECT,
	TIMER_LENGTH_INCORRECT,
	TRACK_NOT_PLAYING,
	WRONG_USAGE
}