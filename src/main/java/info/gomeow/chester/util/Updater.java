package info.gomeow.chester.util;

import info.gomeow.chester.Chester;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

public class Updater extends Chester {

    public boolean getUpdate(String v) throws IOException {
        JSONObject json;
        try {
            json = getInfo();
            String version = json.getString("dbo_version");
            String link = json.getString("link");
            Chester.LINK = link;
            Chester.NEWVERSION = version;
            if(!version.equalsIgnoreCase(v)) {
                return true;
            }
        } catch(JSONException e) {
            throw new IOException();
        }
        return false;
    }

    public JSONObject getInfo() throws IOException {
        URL url = new URL("http://api.bukget.org/3/plugins/bukkit/chester/latest");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch(UnknownHostException e) {
            throw new IOException();
        }
        JSONObject json;
        try {
            json = new JSONObject(in.readLine()).getJSONArray("versions").getJSONObject(0);
            in.close();
            return json;
        } catch(JSONException e) {
        }
        return null;
    }
}