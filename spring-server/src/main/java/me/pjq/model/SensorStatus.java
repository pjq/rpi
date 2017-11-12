package me.pjq.model;

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 16/10/2017.
 */
public class SensorStatus {
    public float distance;

    public boolean relay_on;
    /**
     * obstacles : {"obstacle1":false,"obstacle2":false,"obstacle3":true,"obstacle4":false}
     */

    public Obstacles obstacles;

    public static class Obstacles {
        /**
         * obstacle1 : false
         * obstacle2 : false
         * obstacle3 : true
         * obstacle4 : false
         */

        public boolean obstacle1;
        public boolean obstacle2;
        public boolean obstacle3;
        public boolean obstacle4;

        @Override
        public String toString() {
            return "Obstacles{" +
                    "obstacle1=" + obstacle1 +
                    ", obstacle2=" + obstacle2 +
                    ", obstacle3=" + obstacle3 +
                    ", obstacle4=" + obstacle4 +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SensorStatus{" +
                "distance=" + distance +
                ", obstacles=" + obstacles +
                '}';
    }
}
