import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Duu {

    private final boolean useFormat;
    private final boolean totalSize;
    private final boolean otherBase;


    public Duu(boolean useFormat, boolean totalSize, boolean otherBase) {

        this.useFormat = useFormat;
        this.totalSize = totalSize;
        this.otherBase = otherBase;
    }


    private int fileSize(Path path) throws IOException {
        int size = 0;
        ArrayList<Path> pathlist = new ArrayList<>(Collections.singletonList(path));
        ArrayList<Path> directoryfiles = new ArrayList<>();
        while (!pathlist.isEmpty()) {
            Iterator<Path> iterator = pathlist.iterator();
            while (iterator.hasNext()) {
                Path nextpath = iterator.next();
                if (Files.isDirectory(nextpath)) {
                    DirectoryStream<Path> stream = Files.newDirectoryStream(nextpath);
                    for (Path entry : stream) {
                        directoryfiles.add(entry);
                    }
                    stream.close();
                } else {
                    size += Files.size(nextpath);
                }
                iterator.remove();
            }
            pathlist.addAll(directoryfiles);
            directoryfiles.clear();
        }
        return size;
    }

    private static final String[] FORMATS = {"B", "KB", "MB", "GB"};


    public List<String> result(String[] filePaths) throws IOException {
        if (filePaths == null) {
            System.exit(1);
        }
        ArrayList<String> result = new ArrayList<>();
        int base = otherBase ? 1000 : 1024;
        int totalFileSize = 0;
        for (String filepath : filePaths) {
            Path path = Paths.get(filepath);
            if (Files.notExists(path)) {
                System.exit(1);
            }
            int sizepath = fileSize(path);
            if (totalSize) {
                totalFileSize += fileSize(path);
            } else {
                if (useFormat) {
                    result.add (usableFormat("Размер " + filepath + " равен " , sizepath, base));
                } else {
                    result.add ("Размер " + filepath + " равен " + sizepath / base);
                }
            }
        }
        if (totalSize) {
            if (useFormat) {
                result.add(usableFormat ("Суммарный размер равен ", totalFileSize, base));
            } else {
                result.add ("Суммарный размер равен " + totalFileSize / base);
            }
        }
        return result;
    }


    private String usableFormat(String text, long size, int unit) {
        int form = 0;
        while ((size / unit > 0) && (form < 3)) {
            size /= unit;
            form++;
        }
        return text + size + " " + FORMATS[form];
    }


}