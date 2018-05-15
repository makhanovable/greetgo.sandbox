package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.mvc.security.SecurityCrypto;
import kz.greetgo.mvc.security.SecurityCryptoBridge;
import kz.greetgo.mvc.security.SecuritySource;
import kz.greetgo.mvc.security.SecuritySourceConfig;
import kz.greetgo.mvc.security.SecuritySourceConfigDefault;
import kz.greetgo.mvc.security.SecuritySourceOnFiles;
import kz.greetgo.mvc.security.SerializeUtil;
import kz.greetgo.mvc.util.Base64Util;
import kz.greetgo.sandbox.controller.register.auth.model.SessionInfo;
import kz.greetgo.sandbox.db.util.App;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Bean
public class TokenRegister implements HasAfterInject {
  File publicKeyFile = new File(App.securityDir() + "/session.public.key");
  File privateKeyFile = new File(App.securityDir() + "/session.private.key");

  @Override
  public void afterInject() throws Exception {

    SecuritySourceConfig securitySourceConfig = new SecuritySourceConfigDefault();

    securitySource = new SecuritySourceOnFiles(privateKeyFile, publicKeyFile, securitySourceConfig) {
      @Override
      protected void performReadingException(Exception e) {
        generateKeys();
        saveKeys();
      }
    };

    securityCrypto = new SecurityCryptoBridge(securitySource);
  }

  private SecuritySource securitySource;
  SecurityCrypto securityCrypto;

  public String createToken(SessionInfo sessionInfo) {
    byte[] bytes = SerializeUtil.serialize(sessionInfo);
    byte[] encryptedBytes = securityCrypto.encrypt(bytes);
    return Base64Util.bytesToBase64(encryptedBytes);
  }

  public SessionInfo decryptToken(String token) {
    byte[] encryptedBytes = Base64Util.base64ToBytes(token);
    byte[] bytes = securityCrypto.decrypt(encryptedBytes);
    try {
      return (SessionInfo) SerializeUtil.deserialize(bytes);
    } catch (ClassCastException e) {
      return null;
    }
  }

  public String encryptPassword(String password) {
    if (password == null) return null;
    byte[] digest = securitySource.getMessageDigest().digest(password.getBytes(StandardCharsets.UTF_8));
    return Base64Util.bytesToBase64(digest);
  }
}
