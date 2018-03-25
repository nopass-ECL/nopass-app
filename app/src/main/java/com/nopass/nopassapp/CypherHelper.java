package com.nopass.nopassapp;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by hazegard on 22/03/18.
 */

public class CypherHelper {

  private KeyStore keyStore;

  CypherHelper() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
    keyStore = KeyStore.getInstance("AndroidKeyStore");
    keyStore.load(null);
  }

  String encryptString(String message, PublicKey publicKey) {
    try {
      Cipher input = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding"/*"RSA/ECB/PKCS1Padding"*/);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, input);

      input.init(Cipher.ENCRYPT_MODE, publicKey);
      cipherOutputStream.write(message.getBytes());
      cipherOutputStream.close();

      byte[] vals = outputStream.toByteArray();
      return Base64.encodeToString(vals, Base64.DEFAULT).replace("\n", "");

    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  PrivateKey getPrivKey(String keyName) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
    KeyStore.Entry entry = keyStore.getEntry(keyName, null);
    PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
//    Log.d("PrivKey","**************************");
//    Log.d("PrivKey",privateKey.getEncoded().toString());
    return privateKey;
  }

  PublicKey getPubKey(String keyName) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
//    KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyName, null);
    PublicKey publicKey = keyStore.getCertificate(keyName).getPublicKey();
    return publicKey;
  }

  String getPubKeyPem(String keyName) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
//    getPrivKey(keyName);
    PublicKey pubKey = getPubKey(keyName);
    StringBuilder sb = new StringBuilder();
    sb.append("-----BEGIN PUBLIC KEY-----\n");
    sb.append(new String(Base64.encode(pubKey.getEncoded(), Base64.DEFAULT)).trim());
    sb.append("\n-----END PUBLIC KEY-----");
    return sb.toString();

  }

  private byte[] getKeyBytes(final byte[] key) throws Exception {
    byte[] keyBytes = new byte[16];
    System.arraycopy(key, 0, keyBytes, 0, Math.min(key.length, keyBytes.length));
    return keyBytes;
  }

  public Cipher getCipherDecrypt(byte[] key) throws Exception {
    byte[] keyBytes = getKeyBytes(key);
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS5Padding");
    SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
    IvParameterSpec ivParameterSpec = new IvParameterSpec(keyBytes);
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
    return cipher;
  }

  String decryptString(String message, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, NoSuchProviderException {
    Cipher output = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding"/*"RSA/ECB/OAEPWithSHA-256AndMGF1Padding"*//*"RSA/ECB/OAEPWithSHA-1AndMGF1Padding"/*"RSA/ECB/OAEPWithSHA-1AndMGF1Padding"*//*"RSA/ECB/PKCS1Padding"*/);
    output.init(Cipher.PRIVATE_KEY, privateKey);
    byte[] decode0 = Base64.decode(message, Base64.DEFAULT);
//    byte[] decode1 = Arrays.copyOfRange(decode0, 0, decode0.length/2);
    CipherInputStream cipherInputStream = new CipherInputStream(
      new ByteArrayInputStream(decode0), output);
    ArrayList<Byte> values = new ArrayList<>();
    int nextByte;
    while ((nextByte = cipherInputStream.read()) != -1) {
      values.add((byte) nextByte);
    }

    byte[] bytes = new byte[values.size()];
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = values.get(i);
    }

    String finaltext = new String(bytes, 0, bytes.length, "UTF-8");
    finaltext = new String(output.doFinal(decode0), "UTF-8");
    return finaltext;
  }

  PublicKey createPublicKey(String encodedPubKey) throws NoSuchAlgorithmException, InvalidKeySpecException {

    Log.d("encodedPubKey", encodedPubKey);
    byte[] publicBytes = Base64.decode(encodedPubKey, Base64.DEFAULT);
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
    return pubKey;
  }


  void createNewKey(String keyName) throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    Log.d("88888888888888888888", "99999999999999999999999");
    if (!keyStore.containsAlias(keyName)) {
//      keyStore.deleteEntry(keyName);
      final long now = java.lang.System.currentTimeMillis();
      KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
      KeyGenParameterSpec kps = new KeyGenParameterSpec.Builder(keyName, KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
        .setDigests(KeyProperties.DIGEST_SHA1, KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
        .build();
      kpg.initialize(kps);
      KeyPair keyPair = kpg.generateKeyPair();
      Log.d("pub", "****************************" + new String(Base64.encode(keyPair.getPublic().getEncoded(), Base64.DEFAULT)).trim());
    }
  }


//  void createNewKey(String keyname) {
//    if (!keyStore.containsAlias(keyname)) {
//      // Generate a key pair for encryption
//      Calendar start = Calendar.getInstance();
//      Calendar end = Calendar.getInstance();
//      end.add(Calendar.YEAR, 30);
//
//      KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(keyname, KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
//        .setSubject(new X500Principal("CN=" + keyname))
//        .setSerialNumber(BigInteger.TEN)
//        .setStartDate(start.getTime())
//        .setEndDate(end.getTime())
//        .build();
//      KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, AndroidKeyStore);
//      kpg.initialize(spec);
//      kpg.generateKeyPair();
//    }
//  }
}