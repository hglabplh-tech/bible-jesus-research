package org.harry.jesus.jesajautils.browse;

import javafx.scene.Node;
import org.fxmisc.richtext.model.Codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The interface Linked image.
 */
public interface LinkedImage {
    /**
     * Codec codec.
     *
     * @param <S> the type parameter
     * @return the codec
     */
    static <S> Codec<LinkedImage> codec() {
        return new Codec<LinkedImage>() {
            @Override
            public String getName() {
                return "LinkedImage";
            }

            @Override
            public void encode(DataOutputStream os, LinkedImage linkedImage) throws IOException {
                if (linkedImage.isReal()) {
                    os.writeBoolean(true);
                    String externalPath = linkedImage.getImagePath().replace("\\", "/");
                    Codec.STRING_CODEC.encode(os, externalPath);
                } else {
                    os.writeBoolean(false);
                }
            }

            @Override
            public LinkedImage decode(DataInputStream is) throws IOException {
                if (is.readBoolean()) {
                    String imagePath = Codec.STRING_CODEC.decode(is);
                    imagePath = imagePath.replace("\\",  "/");
                    return new RealLinkedImage(imagePath);
                } else {
                    return new EmptyLinkedImage();
                }
            }
        };
    }

    /**
     * Is real boolean.
     *
     * @return the boolean
     */
    boolean isReal();

    /**
     * Gets image path.
     *
     * @return The path of the image to render.
     */
    String getImagePath();

    /**
     * Create node node.
     *
     * @return the node
     */
    Node createNode();
}
