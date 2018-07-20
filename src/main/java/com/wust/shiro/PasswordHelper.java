package com.wust.shiro;

import com.wust.entity.User;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class PasswordHelper {

	private String algorithmName = "md5";
	private int hashIterations = 2;

	public void encryptPassword(User user) {
		//String salt=randomNumberGenerator.nextBytes().toHex();
		String newPassword = new SimpleHash(algorithmName, user.getPwd(),  ByteSource.Util.bytes(user.getUsername()), hashIterations).toHex();
		//String newPassword = new SimpleHash(algorithmName, user.getPassword()).toHex();
		user.setPwd(newPassword);

	}

	public static void main(String[] args) {
		PasswordHelper passwordHelper = new PasswordHelper();
		User user = new User();
		user.setUsername("admin");
		user.setPwd("156");
		passwordHelper.encryptPassword(user);
		System.out.println(user);
	}

}
