import jdk.nashorn.internal.runtime.options.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class App {
    private ArrayList<Repo> gitFolders = new ArrayList<>();
    private String WORK_ROOT_ENV_VAR = "WORK2_ROOT";

    public static void main(String[] args) {
        try {
            new App().run(args);
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

    public App() throws IOException {
        Optional<Path> repos = getReposRoot();
        if (!repos.isPresent()) {
            return;
        }

        getRepos(repos.get());
    }

    private Optional<Path> getReposRoot() {
        String rootName = System.getenv(WORK_ROOT_ENV_VAR);
        Path root = Paths.get(rootName);
        if (!Files.exists(root)) {
            error(WORK_ROOT_ENV_VAR + "='" + root.toString() + ": folder doesn't exist");
            return Optional.empty();
        }

        Path repos = Paths.get(root.toString(), "repos");
        if (!Files.exists(repos)) {
            error("Repos folder + " + repos.toString() + " doesn't exist");
            return Optional.empty();
        }

        if (!Files.isDirectory(repos)) {
            error(repos.toString() + " is not a directory");
            return null;
        }

        return Optional.of(repos);
    }

    private void getRepos(Path reposDir) throws IOException {
        for (File dir : reposDir.toFile().listFiles()) {
            if (!dir.isDirectory())
                continue;

            Optional<File> gitFolder = Arrays.stream(dir.listFiles())
                    .filter(g -> g.isDirectory() && g.getName().equals(".git"))
                    .findFirst();

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

        // should add https://picocli.info/ when we start adding arguments
        int repoNum = 0;
        try {
            repoNum = Integer.parseInt(args[0]);
        } catch (Exception e) {
            error(e.toString());
            return;
        }

        if (repoNum < 0 || repoNum > gitFolders.size()) {
            error("Invalid repo number");
            return;
        }

        moveToRepo(repoNum);
    }

    String getCurrentDir() {
        return System.getProperty("user.dir");
    }

    private void moveToRepo(int targetNum) {
        optionalExec(getCurrentDir(), ".leave");
        String targetPath = gitFolders.get(targetNum).getPath().toString();
        execute("cd " + targetPath);
        optionalExec(targetPath, ".enter");
    }

    private void optionalExec(String dir, String fileName) {
        String script = findAbove(dir, fileName);
        if (script != null)
            execute("source " + script);
    }

    private String getWorkDir() {
        return System.getenv(WORK_ROOT_ENV_VAR);
    }

    private String findAbove(String dir, String fileName) {
        if (dir == null || dir.equals(getWorkDir()))
            return null;

        File path = Paths.get(dir).toFile();
        if (!path.isDirectory())
            return null;

        Optional<File> found = Arrays.stream(path.listFiles())
                .filter(g -> g.isFile() && g.getName().equals(fileName))
                .findFirst();

        return found.isPresent() ? found.get().toString() : findAbove(path.getParent(), fileName);
    }

    private void showRepos() {
        int n = 0;
        for (Repo repo : gitFolders) {
            String mod = repo.isModified() ? "*" : " ";
            String text = String.format("%s%s %s", mod, n++, repo.getName());
            print(text);
        }
    }
}
