import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.After;

import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class DuTests {
    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private PrintStream originalOut = System.out;
    private ByteArrayInputStream inContent;

    private Path poliFile = Paths.get("src","main", "resources", "spbstu");

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Before
    public void duStream() {
        String input = "du";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void consoleTest() throws IOException {
        Path dir1 = Paths.get("src","main","resources","spbstu");
        String[] args1 = {"-c", "-h", dir1.toString()};
        DuParse.main(args1);
        String excepted = "Суммарный размер равен 7 KB" + System.lineSeparator();
        String actual = outContent.toString();
        assertEquals(excepted, actual);
    }

    @Test
    public void defaultBase() throws IOException {
        String[] args1 = {"-c", "-h", "Github.jpg"};
        DuParse.main(args1);
        String excepted = "Суммарный размер равен 11 KB" + System.lineSeparator();
        String actual = outContent.toString();
        assertEquals(excepted, actual);
    }

    @Test
    public void noArgument() throws IOException {
        String[] args1 = {"-c", "-h"};
        exit.expectSystemExitWithStatus(1);
        DuParse.main(args1);
    }

    @Test(expected = NullPointerException.class)
    public void inCorrectTest() throws IOException {
        String[] args1 = {"-correct", "-h"};
        DuParse.main(args1);
    }

    @Test
    public void expectedExit() throws IOException {
        String[] args1 = {"-c", "-h", "2.txt", "testDirectory"};
        exit.expectSystemExitWithStatus(1);
        DuParse.main(args1);
    }

    @Test
    public void withNoSummarySize() throws IOException {
        String[] args1 = {"-h", "--si", poliFile.toString()};
        DuParse.main(args1);
        String excepted = "Размер " + poliFile.toString() + " равен 7 KB" + System.lineSeparator();
        String actual = outContent.toString();
        assertEquals(excepted, actual);
    }

    @Test
    public void onlyUseUnit() throws IOException {
        String[] args1 = {"--si", "Github.jpg", poliFile.toString()};
        DuParse.main(args1);
        String excepted = "Размер Github.jpg равен 12" + System.lineSeparator() +
                "Размер " + poliFile.toString() + " равен 7" + System.lineSeparator();
        String actual = outContent.toString();
        assertEquals(excepted, actual);
    }

    @Test
    public void withNoOtherBase() throws IOException {
        String[] args1 = {"-c", "--si", "Github.jpg", poliFile.toString()};
        DuParse.main(args1);
        String excepted = "Суммарный размер равен 19" + System.lineSeparator();
        String actual = outContent.toString();
        assertEquals(excepted, actual);
    }

    @After
    public void restoreStreams(){
        System.setOut(originalOut);
    }
}
