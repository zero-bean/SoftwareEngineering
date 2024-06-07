package com.gcu.anniversary;

import org.junit.Test;
import static org.junit.Assert.*;

import DTO.GiftData;

public class GiftDataTest {

    @Test
    public void testEquals_sameObject() {
        GiftData giftData = new GiftData("1", "200", "Birthday Gift", "2024-05-28");
        assertTrue(giftData.equals(giftData));
    }

    @Test
    public void testEquals_nullObject() {
        GiftData giftData = new GiftData("1", "201", "Birthday Gift", "2024-05-28");
        assertFalse(giftData.equals(null));
    }

    @Test
    public void testEquals_differentClass() {
        GiftData giftData = new GiftData("1", "202", "Birthday Gift", "2024-05-28");
        String otherObject = "I am a String";
        assertFalse(giftData.equals(otherObject));
    }

    @Test
    public void testEquals_equalObjects() {
        GiftData giftData1 = new GiftData("1", "203", "Birthday Gift", "2024-05-28");
        GiftData giftData2 = new GiftData("1", "203", "Birthday Gift", "2024-05-28");
        assertTrue(giftData1.equals(giftData2));
    }

    @Test
    public void testEquals_differentGiftID() {
        GiftData giftData1 = new GiftData("1", "204", "Birthday Gift", "2024-05-28");
        GiftData giftData2 = new GiftData("2", "204", "Birthday Gift", "2024-05-28");
        assertFalse(giftData1.equals(giftData2));
    }

    @Test
    public void testEquals_differentFriendListID() {
        GiftData giftData1 = new GiftData("1", "205", "Birthday Gift", "2024-05-28");
        GiftData giftData2 = new GiftData("1", "206", "Birthday Gift", "2024-05-28");
        assertFalse(giftData1.equals(giftData2));
    }

    @Test
    public void testEquals_differentGiftName() {
        GiftData giftData1 = new GiftData("1", "207", "Birthday Gift", "2024-05-28");
        GiftData giftData2 = new GiftData("1", "207", "Anniversary Gift", "2024-05-28");
        assertFalse(giftData1.equals(giftData2));
    }

    @Test
    public void testEquals_differentDate() {
        GiftData giftData1 = new GiftData("1", "208", "Birthday Gift", "2024-05-28");
        GiftData giftData2 = new GiftData("1", "208", "Birthday Gift", "2024-05-02");
        assertFalse(giftData1.equals(giftData2));
    }

    @Test
    public void testHashCode_equalObjects() {
        GiftData giftData1 = new GiftData("1", "209", "Birthday Gift", "2024-05-28");
        GiftData giftData2 = new GiftData("1", "209", "Birthday Gift", "2024-05-28");
        assertEquals(giftData1.hashCode(), giftData2.hashCode());
    }

    @Test
    public void testHashCode_differentObjects() {
        GiftData giftData1 = new GiftData("1", "210", "Birthday Gift", "2024-05-28");
        GiftData giftData2 = new GiftData("2", "211", "Anniversary Gift", "2024-05-02");
        assertNotEquals(giftData1.hashCode(), giftData2.hashCode());
    }
}
