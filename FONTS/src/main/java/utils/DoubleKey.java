package utils;

import java.util.Objects;

/**
 * 
 * The DoubleKey class is a utility designed to be used with containers which
 * need to be of the form {@code <<Key, Key>, Value>}.
 * 
 * @param <T> the type of the first key
 * @param <U> the type of the second key
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class DoubleKey<T, U> {
    private final T key1;
    private final U key2;

    /**
     *
     * Constructs a DoubleKey with two keys.
     *
     * @param key1 the first key
     * @param key2 the second key
     */
    public DoubleKey(final T key1, final U key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    /**
     * @return the first key
     */
    public T getKey1() {
        return key1;
    }

    /**
     * @return the second key
     */
    public U getKey2() {
        return key2;
    }

    // Override equals() to compare based on the values of the two keys
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof DoubleKey)) return false;
        final DoubleKey<?, ?> that = (DoubleKey<?, ?>) o;
        return Objects.equals(key1, that.key1) &&
               Objects.equals(key2, that.key2);
    }

    // Override hashCode() to compute hash based on the two keys
    @Override
    public int hashCode() {
        return Objects.hash(key1, key2);
    }

    // Override toString() for a meaningful string representation
    @Override
    public String toString() {
        return "DoubleKey{" + "key1=" + key1 + ", key2=" + key2 + '}';
    }
}
