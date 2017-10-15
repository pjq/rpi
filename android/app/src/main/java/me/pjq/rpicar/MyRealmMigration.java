package me.pjq.rpicar;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

import static me.pjq.rpicar.DataManager.SCHEME_VERSION_0;
import static me.pjq.rpicar.DataManager.SCHEME_VERSION_1;
import static me.pjq.rpicar.DataManager.SCHEME_VERSION_2;
import static me.pjq.rpicar.DataManager.SCHEME_VERSION_3;

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 13/10/2017.
 */

public class MyRealmMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema realmSchema = realm.getSchema();

        //version 0 -> version 1
        if (oldVersion == SCHEME_VERSION_0) {
            //add String name;
            RealmObjectSchema realmObjectSchema = realmSchema.get("Settings");
            realmObjectSchema.addField("name", String.class);

            oldVersion++;
        }

        if (oldVersion == SCHEME_VERSION_1) {
            //Change String name -> int name;
            final RealmObjectSchema realmObjectSchema = realmSchema.get("Settings");
            realmObjectSchema.addField("name_tmp", int.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(DynamicRealmObject obj) {
                            int value = 0;
                            try {
                                value = Integer.valueOf(obj.getString("name"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            obj.setInt("name_tmp", value);
                        }
                    })
                    .removeField("name")
                    .renameField("name_tmp", "name");

            oldVersion++;
        }

        if (oldVersion == SCHEME_VERSION_2) {

            oldVersion++;
        }

        if (oldVersion == SCHEME_VERSION_3) {

            oldVersion++;
        }
    }

    @Override
    public int hashCode() {
        return MyRealmMigration.class.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        return object instanceof MyRealmMigration;
    }
}
