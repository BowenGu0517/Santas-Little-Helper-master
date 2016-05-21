package me.chayut.SantaHelperLogic;

import org.json.JSONObject;

/**
 * Created by chayut on 2/04/16.
 */
public class SantaTask {

    public String uuid;
    public SantaAction mAction;


    public JSONObject toJSONObject (){return null;}

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public SantaAction getAction() {
        return mAction;
    }

    public void setAction(SantaAction mAction) {
        this.mAction = mAction;
    }

}
