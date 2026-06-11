package com.slpolice.trafficfines;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGenerator {
    public static void main(String[] args) {
        System.out.println("HASH_OUTPUT_START");
        System.out.println(new BCryptPasswordEncoder().encode("password123"));
        System.out.println("HASH_OUTPUT_END");
    }
}
