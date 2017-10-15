package me.pjq.rpicar;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 05/08/2017.
 */

public class DataManager {
    public static final long SCHEME_VERSION_0 = 0;
    public static final long SCHEME_VERSION_1 = 1;
    public static final long SCHEME_VERSION_2 = 2;
    public static final long SCHEME_VERSION_3 = 3;


    public static void init(Context context) {
        Realm.init(context);
    }

    public static Realm getRealm() {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .modules(Realm.getDefaultModule())
                .schemaVersion(SCHEME_VERSION_2)
                .name("Realm")
//                .deleteRealmIfMigrationNeeded() // TODO: which migration behavior do we want?
                .migration(new MyRealmMigration())
                .build();

        Realm realm = Realm.getInstance(realmConfig);

        return realm;
    }
}
