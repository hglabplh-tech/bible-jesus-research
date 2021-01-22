package org.harry.jesus.jesajautils.browse;

import org.fxmisc.richtext.model.NodeSegmentOpsBase;
import org.harry.jesus.jesajautils.browse.EmptyLinkedImage;
import org.harry.jesus.jesajautils.browse.LinkedImage;


/**
 * The type Linked image ops.
 *
 * @param <S> the type parameter
 */
public class LinkedImageOps<S> extends NodeSegmentOpsBase<LinkedImage, S> {

    /**
     * Instantiates a new Linked image ops.
     */
    public LinkedImageOps() {
        super(new EmptyLinkedImage());
    }

    @Override
    public int length(LinkedImage linkedImage) {
        return linkedImage.isReal() ? 1 : 0;
    }

}