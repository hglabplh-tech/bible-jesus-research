package org.harry.jesus.jesajautils.browse;

import javafx.scene.Node;

/**
 * The type Empty linked image.
 */
public class EmptyLinkedImage implements LinkedImage {

    @Override
    public boolean isReal() {
        return false;
    }

    @Override
    public String getImagePath() {
        return "";
    }

    @Override
    public Node createNode() {
        throw new AssertionError("Unreachable code");
    }
}
