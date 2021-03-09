package de.xstampp.service.auth.util;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Hashing {
	private final static int PBKDF2_ITERATIONS = 1_000;

	public static final SecureRandom RND;

	private static final Logger LOGGER = LoggerFactory.getLogger(Hashing.class);

	static {
		SecureRandom secrnd;
		try {
			secrnd = SecureRandom.getInstance("NativePRNGNonBlocking");
		} catch (GeneralSecurityException e) {
			try {
				secrnd = SecureRandom.getInstanceStrong();
			} catch (GeneralSecurityException e1) {
				throw new HashingException("was not able to initialize secure random number generator", e1);
			}
		}
		RND = secrnd;
	}

	private Hashing() {
		throw new IllegalAccessError("Utility class");
	}

	// TODO: Complete Documentation @Rico
	/**
	 * algo$iterCount$salt$hash, example:
	 * 
	 * PBKDF2WithHmacSHA512$10000$B0CFB650D15864AF23BF4681385D3F53$1887792B98230DA2DED0B5E9B6155DDAF26CB9BD0CDD0FE5F91FDBEC374F1149
	 */
	public static String createPbkdf2hash(String password) {
		byte[] newSalt = new byte[16];
		RND.nextBytes(newSalt);
		return "PBKDF2WithHmacSHA512$" + PBKDF2_ITERATIONS + "$" + Hex.encodeHexString(newSalt) + "$"
				+ Hex.encodeHexString(innerPBKDF2Hash(newSalt, password, PBKDF2_ITERATIONS));
	}

	public static boolean checkPasswordAgainstPbkdf2Hash(String password, String hash) {
		try {
			String[] hashParts = hash.split("\\$", 4);
			String iterCount = hashParts[1];
			String salt = hashParts[2];
			String realHashHex = hashParts[3];

			byte[] realHash = Hex.decodeHex(realHashHex);
			byte[] pwHash = innerPBKDF2Hash(Hex.decodeHex(salt), password, Integer.parseInt(iterCount));

			return Arrays.equals(realHash, pwHash);
		} catch (DecoderException | ArrayIndexOutOfBoundsException e) {
			LOGGER.error("Failed checking password hash", e);
			return false;

		}
	}

	private static byte[] innerPBKDF2Hash(byte[] salt, String password, int pbkdf2Iterations) {
		try {
			int hashLength = 512; // deliberate choice: 512 bit hash
			SecretKey key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
					.generateSecret(new PBEKeySpec(password.toCharArray(), salt, pbkdf2Iterations, hashLength));
			return key.getEncoded();
		} catch (GeneralSecurityException e) {
			throw new HashingException("Failed creating PBKDF2 hash", e);
		}
	}

	private static class HashingException extends RuntimeException {
		private static final long serialVersionUID = -2709061138500592934L;

		public HashingException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
