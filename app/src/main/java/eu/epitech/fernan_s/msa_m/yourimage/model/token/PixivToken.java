package eu.epitech.fernan_s.msa_m.yourimage.model.token;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by quent on 13/02/2017.
 */

public class PixivToken implements IToken {
    String _username;
    String _Token;
    String _Refresh;
    String _AuthType;

    public static PixivToken Parse(JSONObject jobj) {
        try {
            PixivToken ret = new PixivToken();
            ret._username = jobj.getString("_username");
            ret._Token = jobj.getString("_Token");
            ret._Refresh = jobj.getString("_Refresh");
            ret._AuthType = jobj.getString("_AuthType");
            return ret;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PixivToken() {

    }

    public PixivToken(String username, String Token, String Refresh, String AuthType) {
        _username = username;
        _Token = Token;
        _Refresh = Refresh;
        _AuthType = AuthType;
    }

    @Override
    public String getUserName() {
        return _username;
    }

    @Override
    public String getToken() {
        return _Token;
    }

    @Override
    public String getAuthType() {
        return _AuthType;
    }

    @Override
    public String getSecret() {
        return _Refresh;
    }

    @Override
    public JSONObject ToJson() {
        try {
            return new JSONObject()
                    .put("_username", _username)
                    .put("_Token", _Token)
                    .put("_Refresh",_Refresh)
                    .put("_AuthType",_AuthType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
