//package com.market.BuyFromHome.config;
//
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class DotenvConfig {
//
//    static {
//        Dotenv dotenv = Dotenv.configure()
//                .ignoreIfMissing()
//                .load();
//
//        System.setProperty("SPRING_DATASOURCE_URL", dotenv.get("DB_URL"));
//        System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("DB_USERNAME"));
//        System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("DB_PASSWORD"));
//
//        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
//        System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));
//        System.setProperty("GOOGLE_CLIENT_ID", dotenv.get("GOOGLE_CLIENT_ID"));
//    }
//}
