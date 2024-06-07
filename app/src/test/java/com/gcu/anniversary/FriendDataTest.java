package com.gcu.anniversary;

import org.junit.Test;
import static org.junit.Assert.*;

import DTO.FriendData;

public class FriendDataTest {

    @Test
    public void testEquals_sameObject() {
        FriendData friendData = new FriendData("1", "101", "102", true);
        assertTrue(friendData.equals(friendData));
    }

    @Test
    public void testEquals_nullObject() {
        FriendData friendData = new FriendData("1", "101", "102", true);
        assertFalse(friendData.equals(null));
    }

    @Test
    public void testEquals_differentClass() {
        FriendData friendData = new FriendData("1", "101", "102", true);
        String otherObject = "I am a String";
        assertFalse(friendData.equals(otherObject));
    }

    @Test
    public void testEquals_equalObjects() {
        FriendData friendData1 = new FriendData("1", "101", "102", true);
        FriendData friendData2 = new FriendData("1", "101", "102", true);
        assertTrue(friendData1.equals(friendData2));
    }

    @Test
    public void testEquals_differentFriendListId() {
        FriendData friendData1 = new FriendData("1", "101", "102", true);
        FriendData friendData2 = new FriendData("2", "101", "102", true);
        assertFalse(friendData1.equals(friendData2));
    }

    @Test
    public void testEquals_differentFriendId1() {
        FriendData friendData1 = new FriendData("1", "101", "102", true);
        FriendData friendData2 = new FriendData("1", "103", "102", true);
        assertFalse(friendData1.equals(friendData2));
    }

    @Test
    public void testEquals_differentFriendId2() {
        FriendData friendData1 = new FriendData("1", "101", "102", true);
        FriendData friendData2 = new FriendData("1", "101", "103", true);
        assertFalse(friendData1.equals(friendData2));
    }

    @Test
    public void testEquals_differentIsFavorite() {
        FriendData friendData1 = new FriendData("1", "101", "102", true);
        FriendData friendData2 = new FriendData("1", "101", "102", false);
        assertFalse(friendData1.equals(friendData2));
    }

    @Test
    public void testHashCode_equalObjects() {
        FriendData friendData1 = new FriendData("1", "101", "102", true);
        FriendData friendData2 = new FriendData("1", "101", "102", true);
        assertEquals(friendData1.hashCode(), friendData2.hashCode());
    }

    @Test
    public void testHashCode_differentObjects() {
        FriendData friendData1 = new FriendData("1", "101", "102", true);
        FriendData friendData2 = new FriendData("2", "103", "104", false);
        assertNotEquals(friendData1.hashCode(), friendData2.hashCode());
    }
}
