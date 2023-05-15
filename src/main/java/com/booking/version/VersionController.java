package main.java.com.booking.version;

import com.jcabi.manifests.Manifests;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version")
public class VersionController {

    @GetMapping
    public String getVersion() {
        return Manifests.read("Booking-Implementation-Version");
    }
}
