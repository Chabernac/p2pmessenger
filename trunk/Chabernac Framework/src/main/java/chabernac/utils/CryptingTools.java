package chabernac.utils;

public class CryptingTools {

  public static void main(String[] args) {
    //String s = "this is a test - you will see encryption working as never before";
    //String key = "teamspirit";
    System.out.println("Encrypting " + args[1] + " with key " + args[0]);
    String s1 = encrypt(args[1], args[0]);
    System.out.println ("encrypted: " + s1);
    System.out.println ("decrypted: " + decrypt(s1, args[0]));
  }

  private static String decrypt(String str, String key) {
     //
     // To 'decrypt' the string, simply apply the same technique.
     return encrypt(str, key);
  }

  private static String encrypt(String str, String key) {
	  if(key == null || key.equals("")) return str;
	  byte theBytes[] = str.getBytes();
	  encryptBytes(str.getBytes(), key.getBytes());
	  return new String(theBytes);
  }

  public static void decryptBytes(byte[] str, byte[] key) {
	  encryptBytes(str, key);
  }

  public static void encryptBytes(byte[] str, byte[] key) {
	  if(key.length == 0 || str.length ==0) return;
	  for (int i=0;i<str.length;i++){
		  str[i] ^= key[i % key.length];
	  }
  }
}