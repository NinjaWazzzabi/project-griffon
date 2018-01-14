package arduino;

import utils.StreamReader;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: 14/01/2018 Needs better name an description
public class RegexFinder {

    private final StringBuilder input;

    private InputStream inputStream;
    private String regex;

    private boolean streamClosed;

    public RegexFinder(InputStream inputStream, String regex) {
        this.inputStream = inputStream;
        input = new StringBuilder();
        streamClosed = false;
        this.regex = regex;

        StreamReader streamReader = new StreamReader(inputStream);
        streamReader.setOnInputRead(this::appendStringData);
        streamReader.run();
        streamReader.setOnStreamClosed(() -> streamClosed = true);
    }

    public RegexFinder(String regex) {
        this.regex = regex;
        input = new StringBuilder();
        streamClosed = false;
    }

    public void appendStringData(String str) {
        synchronized (input) {
            input.append(str);
        }
    }

    public String startSearch(int occurrences,long timeoutMillis) {
        long startTime = System.currentTimeMillis();
        String result = "";

        while (startTime + timeoutMillis > System.currentTimeMillis()) {
            String currentInput;
            synchronized (input) {
                currentInput = input.toString();
            }

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(currentInput);

            int foundOccurrences = 0;

            while (matcher.find()) {
                foundOccurrences++;
                if (foundOccurrences == occurrences) {
                    result = input.substring(matcher.start(),matcher.end());
                    return result;
                }
            }

            if (streamClosed) break;
            Thread.yield();
        }

        return result;
    }
}
