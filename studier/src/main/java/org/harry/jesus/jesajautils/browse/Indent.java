package org.harry.jesus.jesajautils.browse;

/**
 * The type Indent.
 */
public class Indent
{
    /**
     * The Width.
     */
    double width = 15;
    /**
     * The Level.
     */
    int level = 1;

    /**
     * Instantiates a new Indent.
     */
    Indent() {}

    /**
     * Instantiates a new Indent.
     *
     * @param level the level
     */
    Indent( int level )
    {
        if ( level > 0 ) this.level = level;
    }

    /**
     * Increase indent.
     *
     * @return the indent
     */
    Indent increase()
    {
        return new Indent( level+1 );
    }

    /**
     * Decrease indent.
     *
     * @return the indent
     */
    Indent decrease()
    {
        return new Indent( level-1 );
    }
}
