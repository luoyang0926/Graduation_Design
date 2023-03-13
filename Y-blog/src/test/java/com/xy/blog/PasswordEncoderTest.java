package com.xy.blog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderTest {


    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        String password = "qwer1234";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(password);
        System.out.println(encode);
        boolean matches = passwordEncoder.matches(password, encode);
        System.out.println("两个密码一致:" + matches);
        boolean b = passwordEncoder.upgradeEncoding(encode);
        System.out.println(b);
    }

    @Test
    public void v() {
        String password = "qwer1234";
        String encode = passwordEncoder.encode(password);
        System.out.println(encode);
    }
}
