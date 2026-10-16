package com.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Entry Point
 * 
 * College Mini Project: Simple Banking System
 * Demonstrates Object-Oriented Programming (OOP) in Java
 * with an interactive web user interface.
 */
@SpringBootApplication
public class BankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
        System.out.println("=================================================");
        System.out.println(" Simple Banking System Web Application Started! ");
        System.out.println(" Open your browser and navigate to:              ");
        System.out.println(" http://localhost:8080                           ");
        System.out.println("=================================================");
    }
}
