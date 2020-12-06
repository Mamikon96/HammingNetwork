package core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputTransformer {

    public static List<List<Integer>> transformArrayInput(int[][] input) {
        List<List<Integer>> transformedInput = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            transformedInput.add(InputTransformer.transformArrayInput(input[i]));
        }
        return transformedInput;
    }

    public static List<Integer> transformArrayInput(int[] input) {
        List<Integer> transformedInput = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            transformedInput.add(input[i]);
        }
        return transformedInput;
    }

    public static List<List<Integer>> transformFileInput(File[] input) throws IOException {
        List<List<Integer>> transformedInput = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            transformedInput.add(InputTransformer.transformFileInput(input[i]));
        }
        return transformedInput;
    }

    public static List<Integer> transformFileInput(File input) throws IOException {
        int[] inputVector = ImageUtils.extractPixels(input);
        return transformArrayInput(inputVector);
    }
}
