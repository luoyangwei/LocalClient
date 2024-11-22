package com.luoyangwei.localclient.utils;

import android.graphics.Matrix;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MatrixUtil {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Axis {
        private float scaleY;
        private float scaleX;
        private float translateX;
        private float translateY;
        private float rotate;
    }

    public Axis getAxis(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        return new Axis(values[Matrix.MSCALE_Y], values[Matrix.MSCALE_X], values[Matrix.MTRANS_X], values[Matrix.MTRANS_Y], values[Matrix.MSKEW_X]);
    }


    public float getScaleY(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        return values[Matrix.MSCALE_Y];
    }

    public float getScaleX(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    public float getTranslateX(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        return values[Matrix.MTRANS_X];
    }

    public float getTranslateY(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        return values[Matrix.MTRANS_Y];
    }

    public float getRotateX(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        return values[Matrix.MSKEW_X];
    }

    public float getRotateY(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        return values[Matrix.MSKEW_Y];
    }
}
