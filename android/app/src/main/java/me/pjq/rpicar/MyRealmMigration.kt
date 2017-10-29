package me.pjq.rpicar

import io.realm.DynamicRealm
import io.realm.DynamicRealmObject
import io.realm.RealmMigration
import io.realm.RealmObjectSchema
import io.realm.RealmSchema

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 13/10/2017.
 */

class MyRealmMigration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var oldVersion = oldVersion
        val realmSchema = realm.schema

        //version 0 -> version 1
        if (oldVersion == DataManager.SCHEME_VERSION_0) {
            //add String name;
            val realmObjectSchema = realmSchema.get("Settings")
            realmObjectSchema!!.addField("name", String::class.java)

            oldVersion++
        }

        if (oldVersion == DataManager.SCHEME_VERSION_1) {
            //Change String name -> int name;
            val realmObjectSchema = realmSchema.get("Settings")
            realmObjectSchema!!.addField("name_tmp", Int::class.javaPrimitiveType)
                    .transform { obj ->
                        var value = 0
                        try {
                            value = Integer.valueOf(obj.getString("name"))!!
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        obj.setInt("name_tmp", value)
                    }
                    .removeField("name")
                    .renameField("name_tmp", "name")

            oldVersion++
        }

        if (oldVersion == DataManager.SCHEME_VERSION_2) {

            oldVersion++
        }

        if (oldVersion == DataManager.SCHEME_VERSION_3) {

            oldVersion++
        }
    }

    override fun hashCode(): Int {
        return MyRealmMigration::class.java.hashCode()
    }

    override fun equals(`object`: Any?): Boolean {
        return if (`object` == null) {
            false
        } else `object` is MyRealmMigration

    }
}
