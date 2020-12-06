import core.HammingNetwork;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        HammingNetwork network = new HammingNetwork(140 * 160, 10);

        File[] files = new File[10];
        for (int i = 0; i < files.length; i++) {
            files[i] = new File("src/main/resources/images/train/" + i + ".jpg");
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            printMenu();
            try {
                String command = br.readLine();
                if (command.equals("1")) {
                    network.startTrain(files);
                } else if (command.equals("2")) {
                    System.out.print("Enter fileName: ");
                    String fileName = "src/main/resources/images/estimate/" + br.readLine();
                    network.test(new File(fileName));
                } else if (command.equals("3")) {
                    printEstimateDialog(network, br);
                } else if (command.equals("4")) {
                    System.out.print("Enter class: ");
                    command = br.readLine();
                    File etalonFile = new File("src/main/resources/images/train/" + command + ".jpg");
                    File[] noisyEtalonFiles = new File[10];
                    for (int i = 0; i < noisyEtalonFiles.length; i++) {
                        noisyEtalonFiles[i] = new File("src/main/resources/images/estimate/" + command + i + ".jpg");
                    }
                    List<List<Integer>> results = network.estimateClass(etalonFile, noisyEtalonFiles);
                    printEstimateResults(results);
                } else if (command.equals("5")) {

                } else if (command.equals("6")) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n********************************************************************************");
        System.out.println("1: Start Training");
        System.out.println("2: Test");
        System.out.println("3: Average Class for N iterations");
        System.out.println("4: Winner Class for 1 iteration");
        System.out.println("5: Estimate All");
        System.out.println("6: Quit");
        System.out.print("Enter command : ");
    }

    private static void printEstimateMenu() {
        System.out.println("********************************************************************************");
        System.out.println("1: Estimate");
        System.out.println("2: Quit");
    }

    private static void printEstimateDialog(HammingNetwork network, BufferedReader reader) throws IOException {
        System.out.print("Enter etalon fileName: ");
        String etalonFileName = reader.readLine();
        System.out.print("Enter iteration count: ");
        int iterations;
        try {
            iterations = Integer.parseInt(reader.readLine());
        } catch (NumberFormatException e) {
            iterations = 1;
        }
        List<Double> winnerNeuron = network.estimateNoisyInput(etalonFileName, iterations);
        System.out.println("Averages:");
        for (int i = 0; i < winnerNeuron.size(); i++) {
            System.out.printf("%.4f\n", winnerNeuron.get(i));
        }
    }

    private static void printEstimateResults(List<List<Integer>> results) {
        for (List<Integer> item : results) {
            System.out.println(item);
        }
    }
}
