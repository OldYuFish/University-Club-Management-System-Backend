package com.wust.ucms.pojo;

import com.wust.ucms.utils.RSAUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@Component
@ConfigurationProperties(prefix = "rsa.key")
public class RSAKeyProperties {

    private String publicKeyFile;
    private String privateKeyFile;

    private PublicKey publicKey;
    private PrivateKey privateKey;
    private String secret;

    @PostConstruct
    public void createRSAKey() throws Exception {
        RSAUtil.generateKey(publicKeyFile, privateKeyFile, secret, 0);
        this.publicKey = RSAUtil.getPublicKey(publicKeyFile);
        this.privateKey = RSAUtil.getPrivateKey(privateKeyFile);
    }
}
