package co.ambisafe.etoken.service;

import co.ambisafe.etoken.utils.CryptoUtils;
import com.auth0.jwt.JWTSigner;

import java.util.Date;
import java.util.HashMap;

public class Tenant {

    private String id;
    private String secret;

    public Tenant(String id, String secret) {
        this.id = id;
        this.secret = secret;
    }

    public String getId() {
        return id;
    }

    public String getSecret() {
        return secret;
    }

    public String getJwtToken(String subject) {
        JWTSigner signer = new JWTSigner(secret);

        HashMap<String, Object> map = new HashMap<>();
        map.put("iss", id);
        map.put("sub", subject);
        map.put("exp", new Date().getTime() / 1000 + 3600);
        map.put("jti", CryptoUtils.getUuid());
        map.put("aud", "ambisafe");

        return signer.sign(map);
    }
}
