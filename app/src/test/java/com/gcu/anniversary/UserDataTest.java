package com.gcu.anniversary;

import org.junit.Test;
import static org.junit.Assert.*;
import DTO.UserData;

public class UserDataTest {

    @Test
    public void testEquals_sameObject() {
        UserData userData = new UserData("1", "Park", "url1", "UserName1");
        assertTrue(userData.equals(userData));
    }

    @Test
    public void testEquals_nullObject() {
        UserData userData = new UserData("1", "Jay", "url2", "UserName2");
        assertFalse(userData.equals(null));
    }

    @Test
    public void testEquals_differentClass() {
        UserData userData = new UserData("1", "Son", "url3", "UserName3");
        String otherObject = "I am a String";
        assertFalse(userData.equals(otherObject));
    }

    @Test
    public void testEquals_equalObjects() {
        UserData userData1 = new UserData("1", "Lee", "url4", "UserName4");
        UserData userData2 = new UserData("1", "Lee", "url4", "UserName4");
        assertTrue(userData1.equals(userData2));
    }

    @Test
    public void testEquals_differentUID() {
        UserData userData1 = new UserData("1", "Charlie", "url5", "UserName5");
        UserData userData2 = new UserData("2", "Charlie", "url5", "UserName5");
        assertFalse(userData1.equals(userData2));
    }

    @Test
    public void testEquals_differentNickName() {
        UserData userData1 = new UserData("1", "David", "url6", "UserName6");
        UserData userData2 = new UserData("1", "Eve", "url6", "UserName6");
        assertFalse(userData1.equals(userData2));
    }

    @Test
    public void testEquals_differentImageURL() {
        UserData userData1 = new UserData("1", "Frank", "url7", "UserName7");
        UserData userData2 = new UserData("1", "Frank", "url8", "UserName7");
        assertFalse(userData1.equals(userData2));
    }
}
