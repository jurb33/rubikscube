package com.cubeservice.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@Configuration
public class CubeController implements WebMvcConfigurer {
    private final long EXPIRATION = 24 * 60 * 60 * 1000 * 7; //7 days in ms
    private HashMap<String, Cube> cubeRepository = new HashMap<>();
    private HashMap<String, Date> userIdSessions = new HashMap<>();
    /*
     * method setting CORS mapping for different domain environments
     * * ignore if running locally
     * @param registry the CorsRegistry object
     */
    @Override
    @NonNull
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
    public ResponseEntity<Cube> fetchData(HttpServletRequest req, HttpServletResponse res, @RequestParam String user_id) {
        if (cubeRepository.get(user_id) == null) {
            cubeRepository.put(user_id, new Cube());
        }
        //update the last fetched data
        userIdSessions.remove(user_id);
        userIdSessions.put(user_id, new Date());
        return ResponseEntity.ok(cubeRepository.get(user_id));
    }
    /*
     * returns if this userId's cube's state is solved
     * @return responseentity<boolean> true solved/ false not
     */
    @PostMapping("/cube/isSolved")
    public ResponseEntity<Boolean> isSolved(HttpServletRequest req, HttpServletResponse res, @RequestParam String user_id) {
        if (cubeRepository.get(user_id) == null) {
            return null;
        }
        System.out.println(cubeRepository.get(user_id).isSolved());
        return ResponseEntity.ok(cubeRepository.get(user_id).isSolved());
    }
    /*
     * updates userID's session cube with the specified command
     * @param pointer index 0-2 of the cube determined by frontend
     */
    @PostMapping("/cube/command/")
    public ResponseEntity<Cube> updateCube(@RequestParam String user_id, @RequestParam String move,
     @RequestParam int pointer, HttpServletRequest req) {
        Cube userCube = cubeRepository.get(user_id);
        if (user_id == null || userCube == null) {
            return ResponseEntity.badRequest().build();
        }
        performCubeOperation(move, pointer, userCube);
        return ResponseEntity.ok(userCube);
    }
    /*
     * performs the command at the index
     * @param move the move (enumeration)
     * @param pointer the index (0-2)
     */
    private Cube performCubeOperation(String move, int pointer, Cube userCube) {
        switch (move) {
            case "U": userCube.U(pointer); break;
            case "D": userCube.D(pointer); break;
            case "L": userCube.L(pointer); break;
            case "R": userCube.R(pointer); break;
            case "HU": userCube.HU(pointer); break;
            case "HD": userCube.HD(pointer); break;
            case "shuffle1": userCube.scramble(1); break;
            case "shuffle2": userCube.scramble(2); break;
            case "shuffle3": userCube.scramble(3); break;
            case "clear": userCube.setCubeSolved(); break;
            default: break;
        }
        return userCube;
}
    
    /*
     * Checks objects of repository and removes expired sessions
     * from both repositories
     */
    @Scheduled(fixedRate = 6048 * 100000)
    public void cleanRepository() {
        long current = new Date().getTime();
        for(Map.Entry<String, Date> id :userIdSessions.entrySet()) {
            if (current - id.getValue().getTime() >= EXPIRATION ) {
                userIdSessions.remove(id.getKey());
                cubeRepository.remove(id.getKey());
            }
        }
    }
}
