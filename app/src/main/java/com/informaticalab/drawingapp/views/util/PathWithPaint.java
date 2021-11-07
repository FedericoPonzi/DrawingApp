package com.informaticalab.drawingapp.views.util;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by FedericoPonzi on 13/06/2016.
 */
public class PathWithPaint extends Path
{

    Paint p;

    public void setPaint(Paint p)
    {
        this.p = p;
    }

    public Paint getPaint()
    {
        return p;
    }

    public PathWithPaint(Paint p)
    {
        this.p = p;
    }
}

