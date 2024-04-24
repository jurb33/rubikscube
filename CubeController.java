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

    /*
     * method setting CORS mapping for different domain environments
     * * ignore if running locally
     * @param registry the CorsRegistry object
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST")
            .allowCredentials(true)
            .allowedHeaders("*");
    }
    /*
     * returns instance of userID session's cube
     * creates a cube if the userID's is null
     */
    @GetMapping("/cube/fetch-data")
    public ResponseEntity<Cube> fetchData(HttpServletRequest req, HttpServletResponse res) {
        if (userCube == null) {
            userCube = new Cube();
            //userCube.scramble(1);
        }
        return ResponseEntity.ok(userCube);
    }
    /*
     * updates userID's session cube with the specified command
     * @param pointer index 0-2 of the cube determined by frontend
     */
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
            case "HU": userCube.HU(pointer); break;
            case "HD": userCube.HD(pointer); break;
            default: break;
        }
}
}
