package com.example.demo.security;

import com.example.demo.exceptions.SpringRedditException;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

@Service
public class JwtProvider {
    private KeyStore keyStore;

    @PostConstruct
    public void init() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
            keyStore = keyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/springkeystore.jks");
            keyStore.load(resourceAsStream, "secrect".toCharArray());
    }
    public String generateToken(Authentication authentication) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        User principal = (User) authentication.getPrincipal();
        return Jwts.builder().setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        Key tmpkey =  keyStore.getKey("springreddit", "secrect".toCharArray());
        return (PrivateKey) tmpkey;
    }

    public boolean validateToken(String jwt) {
        Jwts.parser().setSigningKey(getPublickey()).parseClaimsJws(jwt);
        return true;
    }

    private PublicKey getPublickey() {
        try {
            return keyStore.getCertificate("springreddit").getPublicKey();
        } catch (KeyStoreException e) {
            throw new SpringRedditException("Exception occured while retrieving public key from keystore");
        }
    }



    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getPublickey())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

}
