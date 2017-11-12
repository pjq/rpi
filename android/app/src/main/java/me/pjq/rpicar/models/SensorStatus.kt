package me.pjq.rpicar.models

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 16/10/2017.
 */

class SensorStatus {
    var distance: Float = 0.toFloat()
    /**
     * obstacles : {"obstacle1":false,"obstacle2":false,"obstacle3":true,"obstacle4":false}
     */

    var relay_on: Boolean = false

    var obstacles: Obstacles? = null

    class Obstacles {
        /**
         * obstacle1 : false
         * obstacle2 : false
         * obstacle3 : true
         * obstacle4 : false
         */

        var obstacle1: Boolean = false
        var obstacle2: Boolean = false
        var obstacle3: Boolean = false
        var obstacle4: Boolean = false

        override fun toString(): String {
            return "obstacle1: " + obstacle1 +
                    " obstacle2: " + obstacle2 +
                    " obstacle3: " + obstacle3 +
                    " obstacle4: " + obstacle4
        }
    }

    override fun toString(): String {
        return "SensorStatus{" +
                "distance=" + distance +
                ", obstacles=" + obstacles +
                '}'
    }
}
