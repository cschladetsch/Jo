import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Repo {
    private Path path;
    private String currentLocation;
    private Git git;

    public Repo(Path path) throws IOException {
        this.path = path;
        this.git = Git.open(path.toFile());

        getCurrent(path);
    }

    private void getCurrent(Path path) throws IOException {
        Path current = Paths.get(path.toString(), ".current");
        File file = current.toFile();
        if (file.exists() && file.canRead()) {
            List<String> contents = Files.readAllLines(current, StandardCharsets.UTF_8);
            if (contents.size() == 1) {
                currentLocation = contents.get(0);
            }
        }
    }

    public Path getPath() {
        return path;
    }

    public String getName() {
        return path.getFileName().toString();
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public Boolean isModified() {
        StatusCommand status = git.status();
        try {
            Status call = status.call();
            return call.isClean();
        } catch (GitAPIException e) {
            App.error(e.getMessage());
        }

        return false;
    }
}
