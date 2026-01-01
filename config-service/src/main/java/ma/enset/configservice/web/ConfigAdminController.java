package ma.enset.configservice.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/configs")
@CrossOrigin(origins = "*")
public class ConfigAdminController {

    @Value("${spring.cloud.config.server.native.search-locations}")
    private String searchLocations;

    private Path getRepoPath() {
        // Supprime le préfixe "file:///"
        String path = searchLocations.replace("file:///", "");
        return Paths.get(path);
    }

    @GetMapping
    public List<String> listConfigFiles() throws IOException {
        return Files.list(getRepoPath())
                .map(p -> p.getFileName().toString())
                .filter(name -> name.endsWith(".properties"))
                .collect(Collectors.toList());
    }

    @GetMapping("/{fileName}")
    public Map<String, String> getConfigFile(@PathVariable String fileName) throws IOException {
        Path path = getRepoPath().resolve(fileName);
        List<String> lines = Files.readAllLines(path);
        Map<String, String> properties = new HashMap<>();
        for (String line : lines) {
            if (line.contains("=") && !line.trim().startsWith("#")) {
                String[] parts = line.split("=", 2);
                properties.put(parts[0].trim(), parts[1].trim());
            }
        }
        return properties;
    }

    @PostMapping("/{fileName}")
    public void updateConfigFile(@PathVariable String fileName, @RequestBody Map<String, String> properties)
            throws IOException {
        Path path = getRepoPath().resolve(fileName);
        List<String> lines = properties.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.toList());
        Files.write(path, lines);
    }
}
