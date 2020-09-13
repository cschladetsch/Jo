import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class App {
    private ArrayList<Repo> gitFolders = new ArrayList<>();

    public static void main(String[] args) {
        //System.getenv("WORK_DIR"));
        try {
            new App(args);
        } catch (IOException e) {
            error(e.toString());
        }
    }

    public static void error(String text) {
        printErr(text);
        execute("exit -1");
    }

    public static void execute(String text) {
        System.out.println(text);
    }

    public static void printErr(String text) {
        System.err.println(text);
    }

    public static void print(String text) {
        execute("echo " + text);
    }

    public App(String[] args) throws IOException {
        File reposDir = getReposRoot();
        if (reposDir == null) {
            error("Couldn't find a repos folder.");
            return;
        }

        getRepos(reposDir);

        run(args);
    }

    private File getReposRoot() {
        String root = System.getenv("WORK2_DIR");
        File repos = Paths.get(root, "repos").toFile();
        if (!repos.isDirectory()) {
            error(String.format("%s is not a directory", repos.toString()));
            return null;
        }
        return repos;
    }

    private void getRepos(File reposDir) throws IOException {
        if (!reposDir.exists()) {
            App.error(String.format("%s is not a directory.", reposDir.toString()));
            return;
        }

        for (File dir : reposDir.listFiles()) {
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

    public void run(String[] args) {
        if (args.length == 0) {
            showRepos();
            return;
        }

        // TODO: add https://picocli.info/
        int repoNum = 0;
        try {
            repoNum = Integer.parseInt(args[0]);
        }
        catch (Exception e) {
            error(e.toString());
            return;
        }

        if (repoNum < 0 || repoNum > gitFolders.size()) {
            error("Invalid repo number");
            return;
        }

        execute("cd " + gitFolders.get(repoNum).getPath().toString());
    }

    private void showRepos() {
        int n = 0;
        for (Repo repo : gitFolders) {
            String text = String.format("%s %s", n++, repo.getName());
            print(text);
        }
    }
}
