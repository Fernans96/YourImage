package eu.epitech.fernan_s.msa_m.yourimage.model.token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by quent on 02/02/2017.
 */

public class FlickrToken implements IToken {
    private String _access_token;
    private String _secret_token;
    private String _account_username;

    private FlickrToken() {

    }

    public FlickrToken(Map<String, String> result) {
        _access_token = result.get("oauth_token");
        _secret_token = result.get("oauth_token_secret");
        _account_username = result.get("username");
    }

    public static FlickrToken Parse(JSONObject obj) {
        FlickrToken ret = new FlickrToken();

        try {
            ret._access_token = obj.getString("oauth_token");
            ret._secret_token = obj.getString("oauth_token_secret");
            ret._account_username = obj.getString("username");
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
        return "";
    }

    @Override
    public String getSecret() {
        return _secret_token;
    }

    @Override
    public JSONObject ToJson() {
        JSONObject ret = new JSONObject();
        try {
            ret.put("oauth_token", _access_token);
            ret.put("username", _account_username);
            ret.put("oauth_token_secret", _secret_token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
