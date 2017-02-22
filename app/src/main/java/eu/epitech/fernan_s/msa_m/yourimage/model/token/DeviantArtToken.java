package eu.epitech.fernan_s.msa_m.yourimage.model.token;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by quent on 21/02/2017.
 */

public class DeviantArtToken implements IToken {
    String _access_token;
    String _refresh_token;
    String _type;



    public static DeviantArtToken Parse(JSONObject obj) {
        DeviantArtToken ret = new DeviantArtToken();
        try {
            ret._access_token = obj.getString("access_token");
            ret._refresh_token = obj.getString("refresh_token");
            ret._type = obj.getString("token_type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public String getUserName() {
        return null;
    }

    @Override
    public String getToken() {
        return _access_token;
    }

    @Override
    public String getAuthType() {
        return _type;
    }

    @Override
    public String getSecret() {
        return _refresh_token;
    }

    @Override
    public JSONObject ToJson() {
        try {
            return new JSONObject()
                    .put("access_token", _access_token)
                    .put("refresh_token", _refresh_token)
                    .put("token_type", _type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
