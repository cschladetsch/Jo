import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class App {
    private ArrayList<Repo> gitFolders = new ArrayList<>();

    public static void main(String[] args) {
        //System.getenv("WORK_DIR"));
        try {
            new App().run();
        } catch (IOException e) {
            e.printStackTrace();
            error(e.toString());
        }
    }

    public static void error(String text) {
        System.err.println(text);
        execute("exit -1");
    }

    public static void execute(String text) {
        System.out.println(text);
    }

    public static void print(String text) {
        execute("echo " + text);
    }

    public App() throws IOException {
        String currentDir = System.getProperty("user.dir");
        Path repos = Paths.get(currentDir, "repos");
        File reposDir = repos.toFile();
        if (!reposDir.isDirectory()) {
            error(String.format("%s is not a directory", repos.toString()));
            return;
        }

        getRepos(reposDir);
    }

    private void getRepos(File reposDir) throws IOException {
        if (!reposDir.exists()) {
            App.error(String.format("%s is not a directory.", reposDir.toString()));
            return;
        }

        for (File dir : reposDir.listFiles())
        {
            if (!dir.isDirectory())
                continue;

            Optional<File> gitFolder = Arrays.stream(dir.listFiles())
                    .filter(g -> g.isDirectory() && g.getName().equals(".git"))
                    .findAny();

            if (gitFolder.isPresent()) {
                gitFolders.add(new Repo(dir.toPath()));
            }
        }
    }

    public void run() {
        //System.out.println(String.format("Git Status: %s", repos.toString()));

        int n = 0;
        for (Repo repo : gitFolders) {
            String text = String.format("%s %s", n++, repo.getName());
            print(text);
        }
    }
}
