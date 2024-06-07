package com.gcu.anniversary;

import org.junit.Test;
import static org.junit.Assert.*;

import DTO.AnniversaryData;

public class AnniversaryDataTest {

    @Test
    public void testEquals_sameObject() {
        AnniversaryData anniversaryData = new AnniversaryData("1", "Jay", "2024-05-28", "Happy Anniversary");
        assertTrue(anniversaryData.equals(anniversaryData));
    }

    @Test
    public void testEquals_nullObject() {
        AnniversaryData anniversaryData = new AnniversaryData("1", "Park", "2024-05-28", "Congrats");
        assertFalse(anniversaryData.equals(null));
    }

    @Test
    public void testEquals_differentClass() {
        AnniversaryData anniversaryData = new AnniversaryData("1", "Son", "2024-05-28", "Well Done");
        String otherObject = "I am a String";
        assertFalse(anniversaryData.equals(otherObject));
    }

    @Test
    public void testEquals_equalObjects() {
        AnniversaryData anniversaryData1 = new AnniversaryData("1", "Lee", "2024-05-28", "Great Job");
        AnniversaryData anniversaryData2 = new AnniversaryData("1", "Lee", "2024-05-28", "Great Job");
        assertTrue(anniversaryData1.equals(anniversaryData2));
    }

    @Test
    public void testEquals_differentId() {
        AnniversaryData anniversaryData1 = new AnniversaryData("1", "Eve", "2024-05-28", "Excellent");
        AnniversaryData anniversaryData2 = new AnniversaryData("2", "Eve", "2024-05-28", "Excellent");
        assertFalse(anniversaryData1.equals(anniversaryData2));
    }

    @Test
    public void testEquals_differentUserId() {
        AnniversaryData anniversaryData1 = new AnniversaryData("1", "Frank", "2024-05-28", "Good Job");
        AnniversaryData anniversaryData2 = new AnniversaryData("1", "Grace", "2024-05-28", "Good Job");
        assertFalse(anniversaryData1.equals(anniversaryData2));
    }

    @Test
    public void testEquals_differentDate() {
        AnniversaryData anniversaryData1 = new AnniversaryData("1", "Hank", "2024-05-28", "Nice Work");
        AnniversaryData anniversaryData2 = new AnniversaryData("1", "Hank", "2024-06-28", "Nice Work");
        assertFalse(anniversaryData1.equals(anniversaryData2));
    }

    @Test
    public void testEquals_differentComment() {
        AnniversaryData anniversaryData1 = new AnniversaryData("1", "Ivy", "2024-05-28", "Fantastic");
        AnniversaryData anniversaryData2 = new AnniversaryData("1", "Ivy", "2024-05-28", "Awesome");
        assertFalse(anniversaryData1.equals(anniversaryData2));
    }

    @Test
    public void testHashCode_equalObjects() {
        AnniversaryData anniversaryData1 = new AnniversaryData("1", "Jack", "2024-05-28", "Terrific");
        AnniversaryData anniversaryData2 = new AnniversaryData("1", "Jack", "2024-05-28", "Terrific");
        assertEquals(anniversaryData1.hashCode(), anniversaryData2.hashCode());
    }

    @Test
    public void testHashCode_differentObjects() {
        AnniversaryData anniversaryData1 = new AnniversaryData("1", "Kara", "2024-05-28", "Superb");
        AnniversaryData anniversaryData2 = new AnniversaryData("2", "Leo", "2024-06-28", "Outstanding");
        assertNotEquals(anniversaryData1.hashCode(), anniversaryData2.hashCode());
    }

    @Test
    public void testConstructorWithoutId() {
        AnniversaryData anniversaryData = new AnniversaryData("Mia", "2024-05-28", "Marvelous");
        assertEquals("Mia", anniversaryData.getUserId());
        assertEquals("2024-05-28", anniversaryData.getDate());
        assertEquals("Marvelous", anniversaryData.getComment());
        assertNull(anniversaryData.getId());
    }
}
