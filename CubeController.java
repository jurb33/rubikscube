package com.cubeservice.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@RestController
@Configuration
public class CubeController implements WebMvcConfigurer {
    private static final int EXPIRATION = 7 * 24 * 60 * 60; // 7 days in seconds
    private Cube userCube;
    //private HashMap<String, Cube> cubeRepository = new HashMap<>();
    //private HashMap<String, Long> userIDSession = new HashMap<>();

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST")
            .allowCredentials(true)
            .allowedHeaders("*");
    }

    @GetMapping("/cube/fetch-data")
    public ResponseEntity<Cube> fetchData(HttpServletRequest req, HttpServletResponse res) {
        if (userCube == null) {
            userCube = new Cube();
            //userCube.scramble(1);
        }
        return ResponseEntity.ok(userCube);
    }

    @PostMapping("/cube/command/")
    public ResponseEntity<Cube> updateCube(@RequestParam String move, @RequestParam int pointer, 
        HttpServletRequest req) {
        if (userCube == null) {
            return ResponseEntity.badRequest().build();
        }
    
        performCubeOperation(move, pointer);
        return ResponseEntity.ok(userCube);
    }

    private void performCubeOperation(String move, int pointer) {
        switch (move) {
            case "U": userCube.U(pointer); break;
            case "D": userCube.D(pointer); break;
            case "L": userCube.L(pointer); break;
            case "R": userCube.R(pointer); break;
            case "HU": userCube.HD(pointer); break; //Swapped because of formatting
            case "HD": userCube.HU(pointer); break;
            default: break;
        }
        for (int i = 0; i < userCube.CUBE_DIMENSION; i++) {
            for (int j = 0; j < userCube.CUBE_DIMENSION; j++) {
                System.out.println("up face" + userCube.matrixToString(userCube.upFace, 1));
                System.out.println("front face: " +userCube.matrixToString(userCube.frontFace, 1));
            }
    }
}
}
