package org.harry.jesus.jesajautils;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class representing a Tuple response
 * @param <F> the first object class
 * @param <S> the second object class
 */
public class Tuple<F,S> implements Serializable {

    public static final long serialVersionUID = 1378900L;
    /**
     * first element
     */
    private final F first;

    /**
     * second element
     */
    private final S second;

    /**
     * CTOr for creating a tuple
     * @param first first element
     * @param second second element
     */
    public Tuple(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * get the first element
     * @return the object of type F
     */
    public F getFirst() {
        return first;
    }

    /**
     * get the second element
     * @return the object of type S
     */
    public S getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return getFirst().equals(tuple.getFirst()) &&
                getSecond().equals(tuple.getSecond());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirst(), getSecond());
    }
}
