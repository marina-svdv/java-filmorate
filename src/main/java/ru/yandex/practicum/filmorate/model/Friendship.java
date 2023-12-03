package ru.yandex.practicum.filmorate.model;

public class Friendship {
    private final int userId;
    private final int friendId;
    private FriendshipStatus status;

    public Friendship(int userId, int friendId, FriendshipStatus status) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public int getFriendId() {
        return friendId;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }
}
