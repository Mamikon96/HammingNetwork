package core;

import configs.Environment;
import exceptions.WinnerNeuronNotFoundException;
import models.InputLayer;
import models.OutputLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HammingNetwork {

    //    private Logger log = Logger.getLogger(core.HammingNetwork.class.getName());
    private static final Logger log = LoggerFactory.getLogger(HammingNetwork.class);

    private int n;
    private int m;

    private double accuracy = 0.95;

    private InputLayer inputLayer;
    private OutputLayer outputLayer;

    private HammingNetwork() {
    }

    public HammingNetwork(int n, int m) {
        this.n = n;
        this.m = m;
        inputLayer = new InputLayer(n, m);
        outputLayer = new OutputLayer(m, m);
        if (log.isDebugEnabled()) {
            log.debug("Constructor");
        }
    }

    public void startTrain(int[][] inputs) {
        log.info("Start Training...");
        List<List<Integer>> transformedInputs = InputTransformer.transformArrayInput(inputs);
        inputLayer.initWeights(transformedInputs);
        List<Double> outputs = inputLayer.calculateOutputs(transformedInputs);
        outputLayer.initStates(outputs);
        outputLayer.calculateWeights();
    }

    public void startTrain(File[] inputs) throws IOException {
        log.info("Start Training...");
        List<List<Integer>> transformedInputs = InputTransformer.transformFileInput(inputs);
        inputLayer.initWeights(transformedInputs);
        List<Double> outputs = inputLayer.calculateOutputs(transformedInputs);
        outputLayer.initStates(outputs);
        outputLayer.calculateWeights();
        outputLayer.initOutputs(outputs);
    }

    public void test(int[] testSet) {
        log.info("Start Testing...");
        List<Integer> testVector = InputTransformer.transformArrayInput(testSet);
//        log.info("First Layer Output: " + inputLayer.getOutputs(testVector).toString());
        List<Double> inputLayerOutput = inputLayer.getOutputs(testVector);
        outputLayer.initStates(inputLayerOutput);
        outputLayer.initOutputs(inputLayerOutput);
        try {
            int winnerNeuronNumber = outputLayer.findWinner();
            log.info("Winner Neuron: " + winnerNeuronNumber + "\n");
        } catch (WinnerNeuronNotFoundException e) {
            log.error(e.getMessage());
        }
    }

    public int test(File file) throws IOException {
        if (!Environment.isEstimateMode) {
            log.info("Start Testing...");
        }
        List<Integer> testVector = InputTransformer.transformFileInput(file);
//        log.info("First Layer Output: " + inputLayer.getOutputs(testVector).toString());
        List<Double> inputLayerOutput = inputLayer.getOutputs(testVector);
        outputLayer.initStates(inputLayerOutput);
        outputLayer.initOutputs(inputLayerOutput);
        try {
            int winnerNeuronNumber = outputLayer.findWinner();
            if (Environment.isDebugMode) {
                log.info("Winner Neuron: " + winnerNeuronNumber + "\n");
            }
            return winnerNeuronNumber;
        } catch (WinnerNeuronNotFoundException e) {
            log.error(e.getMessage());
            return -1;
        }
    }

    public void estimate(String etalonFileName, String noisyEtalonFileName) throws IOException {
        File etalon = new File("src/main/resources/images/train/" + etalonFileName);
        File noisyEtalon = new File("src/main/resources/images/estimate/" + noisyEtalonFileName);
        log.info("Noise Pixels: " + calculateNoises(etalon, noisyEtalon));
    }

    public List<List<Integer>> estimateClass(File etalonFile, File[] noisyEtalonFiles) throws IOException {
        int winnerNeuron = -1;
        int hammingDist = 0;
        List<List<Integer>> results = new ArrayList<>();
        for (int i = 0; i < noisyEtalonFiles.length; i++) {
            List<Integer> resultItem = new ArrayList<>();
            winnerNeuron = test(noisyEtalonFiles[i]);
            hammingDist = calculateNoises(etalonFile, noisyEtalonFiles[i]);
            resultItem.add(i);
            resultItem.add(winnerNeuron);
            resultItem.add(hammingDist);
            results.add(resultItem);
        }
        return results;
    }

    public List<Double> estimateNoisyInput(String etalonFileName, int n) throws IOException {
        File etalonFile = new File("src/main/resources/images/train/" + etalonFileName + ".jpg");
        List<Double> avgs = new ArrayList<>();
        for (int k = 0; k < 10; k++) {
            File noisyEtalonFile = new File("src/main/resources/images/estimate/" + etalonFileName + k + ".jpg");
            double avgWinnerNeuron = 0;
            int winnerNeuron = -1;
            for (int i = 0; i < n; i++) {
                winnerNeuron = test(noisyEtalonFile);
                avgWinnerNeuron += (double) winnerNeuron / n;
            }
            avgs.add(avgWinnerNeuron);
        }
        return avgs;
    }

    private int calculateNoises(File etalon, File noisyEtalon) throws IOException {
        int[] etalonPixels = ImageUtils.extractPixels(etalon);
        int[] noisyEtalonPixels = ImageUtils.extractPixels(noisyEtalon);
        int noisePixelsCount = 0;
        for (int i = 0; i < etalonPixels.length; i++) {
            if (etalonPixels[i] != noisyEtalonPixels[i]) {
                noisePixelsCount++;
            }
        }
        return noisePixelsCount;
    }
}
