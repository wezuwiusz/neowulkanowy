package io.github.wulkanowy.api;

import java.io.InputStream;
import java.util.Scanner;

public class FixtureHelper {

    public static String getAsString(InputStream inputStream) {
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
