package cubeservice.src.main.java.com.example;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;

@RestController
public class CubeController {
    private static final int EXPIRATION = 7 * 24 * 60 * 60;
    /*
    *Repository for user sessions
    */
    private HashMap<Integer, Cube> cubeRepository =
     new HashMap<Integer, Cube>();
    
    //Tracks the date that the user started a session
    private java.util.HashMap<Integer, Integer> userIDSession =
    new HashMap<Integer, Integer>();
    /*
    returns the cube's state for a given session in a json body
    */
    @PostMapping("/cube/fetch-data")
    public ResponseEntity<Cube> fetchData(@RequestParam HttpSeverletRequest req ) {
        String id = getUserIdFromCookie(req);
        if (id == null) {
            createAndSetUserCookie(req);
            id = getUserIdFromCookie(req);
        }
        return ResponseEntity.ok(cubeRepository.get(Integer.parseInt(id)));
        };
    
    @PostMapping("/cube/command/")
    public ResponseEntity<Cube> updateCube(@RequestParam String move, @RequestParam int pointer, @RequestParam HttpServerletRequest req) {
        try {
            String id = getUserIdFromCookie(req);
            Cube cube = cubeRepository.get(Integer.parseInt(getUserIdFromCookie(req)));
            switch (move) {
                case 'U':
                    cube.U(pointer);
                break;
                case 'D':
                    cube.D(pointer);
                    break;
                case 'L':
                    cube.L(pointer);
                    break;
                case 'R':
                    cube.R(pointer);
                case "HU":
                    cube.HU(pointer);
                    break;
                case "HD":
                    cube.HD(pointer);
                    break;
                default:
                    break;
            }
            return ResponseEntity.ok(cube);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error performing move: " + e.getMessage());
        }
    }
    /*
     * returns userId from request - used for tracking userID for cubestate
     */
    public String getUserIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("user_id".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    /*
     * Sets cookie from userId - expires 7 days after instatiation
     */
    public void createAndSetUserCookie(HttpServletResponse response) {
        Integer uniqueID = UUID.randomUUID().toString();
        createUser(uniqueID);

        Cookie cookie = new Cookie("user_id", uniqueID.toString());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(EXPIRATION); 
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    /*
     * Adds an entry with the key of the userId
     * Creates a cube object for the id
     */
    private void createUser(int userId) {
        userIdSession.put(userId,  new Date.getTime());
        cubeRepository.put(userId, new Cube());
    }
    /*
     * Clears all entries in the repository that have expired
     * (session was longer than 7 days)
     */
    private void clearRepository() {
    userIDSession.forEach((id, data) -> {
        if (new Date.getTime() - data >= EXPIRATION * 1000) {
            userIDSession.remove(id);
        }
    });
}
    /*
     * Updates the userId session to the current date
     */
    private void updateUser(int userId) {
     userIDSession.remove(userId);
     userIDSession.put(userId, new Date.getTime()); 
    }
}
