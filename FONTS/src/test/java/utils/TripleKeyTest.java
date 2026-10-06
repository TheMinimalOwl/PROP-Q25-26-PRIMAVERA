package utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for the class TripleKey.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class TripleKeyTest {

    @Test
    public void testConstructorAndGetters() {
        String key1 = "first";
        Integer key2 = 10;
        Double key3 = 20.5;

        TripleKey<String, Integer, Double> tripleKey = new TripleKey<>(key1, key2, key3);

        assertEquals("Key1 should be 'first'", key1, tripleKey.getKey1());
        assertEquals("Key2 should be 10", key2, tripleKey.getKey2());
        assertEquals("Key3 should be 20.5", key3, tripleKey.getKey3());
    }

    @Test
    public void testConstructorAndGettersOnlyIntegers() {
        Integer key1 = 5;
        Integer key2 = 10;
        Integer key3 = 20;

        TripleKey<Integer, Integer, Integer> tripleKey = new TripleKey<>(key1, key2, key3);

        assertEquals("Key1 should be 5", key1, tripleKey.getKey1());
        assertEquals("Key2 should be 10", key2, tripleKey.getKey2());
        assertEquals("Key3 should be 20", key3, tripleKey.getKey3());
    }

    @Test
    public void testEqualsSameKeys() {
        TripleKey<String, Integer, Double> tripleKey1 = new TripleKey<>("first", 10, 20.5);
        TripleKey<String, Integer, Double> tripleKey2 = new TripleKey<>("first", 10, 20.5);

        assertEquals("TripleKey objects with the same keys should be equal", tripleKey1, tripleKey2);
    }

    @Test
    public void testEqualsDifferentKeys() {
        // Arrange
        TripleKey<String, Integer, Double> tripleKey1 = new TripleKey<>("first", 10, 20.5);
        TripleKey<String, Integer, Double> tripleKey2 = new TripleKey<>("second", 20, 30.5);

        assertNotEquals("TripleKey objects with different keys should not be equal", tripleKey1, tripleKey2);
    }

    @Test
    public void testEqualsNull() {
        TripleKey<String, Integer, Double> tripleKey = new TripleKey<>("first", 10, 20.5);

        assertNotEquals("TripleKey should not be equal to null", tripleKey, null);
    }

    @Test
    public void testEqualsDifferentType() {
        TripleKey<String, Integer, Double> tripleKey = new TripleKey<>("first", 10, 20.5);
        String nonTripleKeyObject = "I am not a TripleKey";

        assertNotEquals("TripleKey should not be equal to an object of different type", tripleKey, nonTripleKeyObject);
    }

    @Test
    public void testHashCode() {
        TripleKey<String, Integer, Double> tripleKey1 = new TripleKey<>("first", 10, 20.5);
        TripleKey<String, Integer, Double> tripleKey2 = new TripleKey<>("first", 10, 20.5);

        assertEquals("Hash codes of TripleKeys with same keys should be equal", tripleKey1.hashCode(), tripleKey2.hashCode());
    }

    @Test
    public void testToString() {
        TripleKey<String, Integer, Double> tripleKey = new TripleKey<>("first", 10, 20.5);

        assertEquals("toString() should return the correct string representation", 
                     "TripleKey{key1=first, key2=10, key3=20.5}", tripleKey.toString());
    }
}
