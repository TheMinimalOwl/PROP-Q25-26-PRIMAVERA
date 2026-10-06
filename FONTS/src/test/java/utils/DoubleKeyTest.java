package utils;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;

/**
 * Unit test for the class DoubleKey.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class DoubleKeyTest {

    @Test
    public void testConstructorAndGetters() {
        String key1 = "first";
        Integer key2 = 10;

        DoubleKey<String, Integer> doubleKey = new DoubleKey<>(key1, key2);

        assertEquals("Key1 should be 'first'", key1, doubleKey.getKey1());
        assertEquals("Key2 should be 10", key2, doubleKey.getKey2());
    }

    @Test
    public void testEqualsSameKeys() {
        DoubleKey<Integer, Integer> doubleKey1 = new DoubleKey<>(5, 10);
        DoubleKey<Integer, Integer> doubleKey2 = new DoubleKey<>(5, 10);

        assertEquals("DoubleKey objects with the same keys should be equal", doubleKey1, doubleKey2);
    }

    @Test
    public void testEqualsDifferentKeys() {
        DoubleKey<Integer, LocalDateTime> doubleKey1 = new DoubleKey<>(20, LocalDateTime.now());
        DoubleKey<Integer, LocalDateTime> doubleKey2 = new DoubleKey<>(10, LocalDateTime.now());

        assertNotEquals("DoubleKey objects with different keys should not be equal", doubleKey1, doubleKey2);
    }

    @Test
    public void testEqualsNull() {
        DoubleKey<String, Integer> doubleKey = new DoubleKey<>("first", 10);

        assertNotEquals("DoubleKey should not be equal to null", doubleKey, null);
    }

    @Test
    public void testEqualsDifferentType() {
        DoubleKey<String, Integer> doubleKey = new DoubleKey<>("first", 10);
        String nonDoubleKeyObject = "I am not a DoubleKey";

        assertNotEquals("DoubleKey should not be equal to an object of different type", doubleKey, nonDoubleKeyObject);
    }

    @Test
    public void testHashCode() {
        DoubleKey<String, Integer> doubleKey1 = new DoubleKey<>("first", 10);
        DoubleKey<String, Integer> doubleKey2 = new DoubleKey<>("first", 10);

        assertEquals("Hash codes of DoubleKeys with same keys should be equal", doubleKey1.hashCode(), doubleKey2.hashCode());
    }

    @Test
    public void testToString() {
        DoubleKey<String, Integer> doubleKey = new DoubleKey<>("first", 10);

        assertEquals("toString() should return the correct string representation", "DoubleKey{key1=first, key2=10}", doubleKey.toString());
    }
}
