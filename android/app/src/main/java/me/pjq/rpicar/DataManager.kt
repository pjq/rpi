package me.pjq.rpicar

import android.content.Context

import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 05/08/2017.
 */

object DataManager {
    val SCHEME_VERSION_0: Long = 0
        get() = field
    val SCHEME_VERSION_1: Long = 1
    val SCHEME_VERSION_2: Long = 2
    val SCHEME_VERSION_3: Long = 3

    // TODO: which migration behavior do we want?
    //                .migration(new MyRealmMigration())
    val realm: Realm
        get() {
            val realmConfig = RealmConfiguration.Builder()
                    .modules(Realm.getDefaultModule())
                    .schemaVersion(SCHEME_VERSION_2)
                    .name("Realm")
                    .deleteRealmIfMigrationNeeded()
                    .build()

            return Realm.getInstance(realmConfig)
        }


    fun init(context: Context) {
        Realm.init(context)
    }
}
