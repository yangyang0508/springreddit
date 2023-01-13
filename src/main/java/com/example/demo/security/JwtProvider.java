package com.example.demo.security;

import com.example.demo.exceptions.SpringRedditException;
import com.example.demo.model.User;
import org.springframework.security.core.Authentication;
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
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(resourceAsStream, "secrect".toCharArray());
    }
    public String generateToken(Authentication authentication) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        User principal = (User) authentication.getPrincipal();
        return Jwts.builder().setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
    }
}
