import org.apache.flink.api.java.utils.ParameterTool;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class KeywordsTest {
    public static void main(String args[]) {
        try {
            // usage --keywords input_file.txt
            final ParameterTool params = ParameterTool.fromArgs(args);
            String input_file = params.get("keywords");
            System.out.println(input_file);
            Scanner scanner = new Scanner(new File(input_file));
            List<String> keywords = new ArrayList<>();
            while (scanner.hasNextLine()) {
                keywords.add(scanner.nextLine());
            }
            String[] keys = keywords.toArray(new String[0]);
            System.out.println(Arrays.toString(keys));

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
