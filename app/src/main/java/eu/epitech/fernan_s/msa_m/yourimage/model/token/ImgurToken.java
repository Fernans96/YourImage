package eu.epitech.fernan_s.msa_m.yourimage.model.token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by quent on 31/01/2017.
 */

public class ImgurToken implements IToken {
    private String _access_token;
    private String _refresh_token;
    private String _expires_in;
    private String _token_type;
    private String _account_username;

    private ImgurToken() {

    }

    public ImgurToken(Map<String, String> result) {
        _access_token = result.get("access_token");
        _refresh_token = result.get("refresh_token");
        _expires_in = result.get("expires_in");
        _token_type = result.get("token_type");
        _account_username = result.get("account_username");
    }

    public static ImgurToken Parse(JSONObject obj) {
        ImgurToken ret = new ImgurToken();

        try {
            ret._access_token = obj.getString("access_token");
            ret._account_username = obj.getString("account_username");
            ret._expires_in = obj.getString("expires_in");
            ret._refresh_token = obj.getString("refresh_token");
            ret._token_type = obj.getString("token_type");
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
    public JSONObject ToJson() {
        JSONObject ret = new JSONObject();

        try {
            ret.put("access_token", _access_token);
            ret.put("account_username", _account_username);
            ret.put("expires_in", _expires_in);
            ret.put("refresh_token", _refresh_token);
            ret.put("token_type", _token_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
