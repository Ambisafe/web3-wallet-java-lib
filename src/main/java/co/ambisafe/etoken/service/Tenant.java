package co.ambisafe.etoken.service;

import co.ambisafe.etoken.exceptions.CryptoException;
import co.ambisafe.etoken.utils.CryptoUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.UnsupportedEncodingException;
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
        HashMap<String, Object> map = new HashMap<>();
        map.put("iss", id);
        map.put("sub", subject);
        map.put("exp", new Date().getTime() / 1000 + 3600);
        map.put("jti", CryptoUtils.getUuid());
        map.put("aud", "ambisafe");

        try {
            return Jwts.builder().setClaims(map)
                    .setHeaderParam("typ", "JWT")
                    .signWith(SignatureAlgorithm.HS256, secret.getBytes("UTF-8"))
                    .compact();
        } catch (UnsupportedEncodingException e) {
            throw new CryptoException(e);
        }
    }
}
