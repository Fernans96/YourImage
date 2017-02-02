package eu.epitech.fernan_s.msa_m.yourimage.model.token;

import org.json.JSONObject;

/**
 * Created by quent on 31/01/2017.
 */

public interface IToken {
    public String getUserName();
    public String getToken();
    public String getAuthType();
    public String getSecret();
    public JSONObject ToJson();
}
