package se.greatbrain.sats.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Instructor extends RealmObject {

    @PrimaryKey
    private String id;


    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
}
