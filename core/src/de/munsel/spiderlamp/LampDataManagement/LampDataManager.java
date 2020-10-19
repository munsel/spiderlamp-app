package de.munsel.spiderlamp.LampDataManagement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.io.FileNotFoundException;

/**
 * Created by munsel on 21.03.16.
 */
public class LampDataManager {
    private static final String SAVE_STATE_FILE_NAME = "lampData.json";

    public static void SaveLampData(LampData state){
        Json json = new Json();
        FileHandle handle = Gdx.files.local(SAVE_STATE_FILE_NAME);
        handle.writeString(json.toJson(state), false);
    }

    public static LampData getSavedLampData(){
        FileHandle handle = Gdx.files.local(SAVE_STATE_FILE_NAME);
        Json json = new Json();
        // System.out.println(json.prettyPrint(state));
        if (handle.exists()) {
            String fileContent = handle.readString();
            return json.fromJson(LampData.class, fileContent);
        }
        return null;
    }



}
