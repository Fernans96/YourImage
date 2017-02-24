package eu.epitech.fernan_s.msa_m.yourimage.model.token;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Zacka on 2/19/2017.
 */

public class PX500Token implements IToken {
    private String _access_token;
    private String _access_token_secret;
    private String _account_username = "gysco";
    private String _token_type = "";

    private PX500Token() {

    }

    public PX500Token(Map<String, String> result) {
        _access_token = result.get("oauth_token");
        _access_token_secret = result.get("oauth_token_secret");
    }

    public static PX500Token Parse(JSONObject obj) {
        PX500Token ret = new PX500Token();

        try {
            ret._access_token = obj.getString("access_token");
            ret._access_token_secret = obj.getString("access_token_secret");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }

    @Override
    public String getUserName() {
        return _account_username;
    }

    @Override
    public String getToken() {
        return _access_token;
    }

    @Override
    public String getAuthType() {
        return _token_type;
    }

    @Override
    public String getSecret() {
        return _access_token_secret;
    }

    @Override
    public JSONObject ToJson() {
        JSONObject ret = new JSONObject();

        try {
            ret.put("access_token", _access_token);
            ret.put("access_token_secret", _access_token_secret);
            ret.put("account_username", _account_username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
