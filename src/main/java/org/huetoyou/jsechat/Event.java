package org.huetoyou.jsechat;

/**
 * Event received from the chat network
 *
 * An instance of this class contains all of the meta information for a single
 * event that occurred. Use the accessors to retrieve the event's data.
 */
public class Event {

    public static final int MessagePosted = 1;
    public static final int MessageEdited = 2;
    public static final int UserJoined = 3;
    public static final int UserLeft = 4;
    public static final int RoomNameChanged = 5;
    public static final int MessageStarred = 6;
    public static final int DebugMessage = 7;
    public static final int UserMentioned = 8;
    public static final int MessageFlagged = 9;
    public static final int MessageDeleted = 10;
    public static final int FileAdded = 11;
    public static final int ModeratorFlag = 12;
    public static final int UserSettingsChanged = 13;
    public static final int GlobalNotification = 14;
    public static final int AccessLevelChanged = 15;
    public static final int UserNotification = 16;
    public static final int Invitation = 17;
    public static final int MessageReply = 18;
    public static final int MessageMovedOut = 19;
    public static final int MessageMovedIn = 20;
    public static final int TimeBreak = 21;
    public static final int FeedTicker = 22;
    public static final int UserSuspended = 29;
    public static final int UserMerged = 30;
    public static final int UserNameOrAvatarChanged = 34;
}
