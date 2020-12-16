package org.harry.jesus.jesajautils.browse;

import org.fxmisc.richtext.model.NodeSegmentOpsBase;
import org.harry.jesus.jesajautils.browse.EmptyLinkedImage;
import org.harry.jesus.jesajautils.browse.LinkedImage;


public class LinkedImageOps<S> extends NodeSegmentOpsBase<LinkedImage, S> {

    public LinkedImageOps() {
        super(new EmptyLinkedImage());
    }

    @Override
    public int length(LinkedImage linkedImage) {
        return linkedImage.isReal() ? 1 : 0;
    }

}