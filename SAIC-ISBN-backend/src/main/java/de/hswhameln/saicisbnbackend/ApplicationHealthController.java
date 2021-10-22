package de.hswhameln.saicisbnbackend;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/check")
public class ApplicationHealthController {

    @GetMapping(path = "/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
