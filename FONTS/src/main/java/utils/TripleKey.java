package utils;

import java.util.Objects;

/**
 * 
 * The TripleKey class is a utility designed to be used with containers which
 * need to be of the form {@code <<Key, Key>, Value>}.
 *
 * @param <T> the type of the first key
 * @param <U> the type of the second key
 * @param <V> the type of the third key
 * 
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class TripleKey<T, U, V> {
    private final T key1;
    private final U key2;
    private final V key3;

    /**
     *
     * Constructs a TripleKey with three keys.
     *
     * @param key1 the first key
     * @param key2 the second key
     * @param key3 the third key
     */
    public TripleKey(final T key1, final U key2, final V key3) {
        this.key1 = key1;
        this.key2 = key2;
        this.key3 = key3;
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

    /**
     * @return the third key
     */
    public V getKey3() {
        return key3;
    }

    // Override equals() to compare based on the values of the three keys
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof TripleKey)) return false;
        final TripleKey<?, ?, ?> that = (TripleKey<?, ?, ?>) o;
        return Objects.equals(key1, that.key1) &&
               Objects.equals(key2, that.key2) &&
               Objects.equals(key3, that.key3);
    }

    // Override hashCode() to compute hash based on the three keys
    @Override
    public int hashCode() {
        return Objects.hash(key1, key2, key3);
    }

    // Override toString() for a meaningful string representation
    @Override
    public String toString() {
        return "TripleKey{" + "key1=" + key1 + ", key2=" + key2 + ", key3=" + key3 + '}';
    }
}

