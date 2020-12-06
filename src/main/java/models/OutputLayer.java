package models;

import configs.Environment;
import exceptions.WinnerNeuronNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OutputLayer {

    private static final Logger log = LoggerFactory.getLogger(OutputLayer.class);

    private int n;
    private int m;
    private double accuracy = 0.8;
    private int iterationCount = 0;

    private Neuron<Double>[] neurons;

    private OutputLayer() {
    }

    public OutputLayer(int n, int m) {
        this.n = n;
        this.m = m;
        neurons = new Neuron[m];
        for (int i = 0; i < neurons.length; i++) {
            neurons[i] = new Neuron<>();
        }
    }


    public Neuron<Double>[] getNeurons() {
        return neurons;
    }



    public void initStates(List<Double> inputLayerOutputs) {
        for (int i = 0; i < inputLayerOutputs.size(); i++) {
            neurons[i].setState(inputLayerOutputs.get(i));
        }
//        log.info("Init States: " + inputLayerOutputs);
    }

    public void calculateWeights() {
        if (Environment.isDebugMode) {
            log.info("Weights:");
        }
        for (int i = 0; i < neurons.length; i++) {
            neurons[i].setWeights(calculateWeightsForNeuron(i));
            if (Environment.isDebugMode) {
                log.info(i + " : " + neurons[i].getWeights());
            }
        }
    }

    private List<Double> calculateWeightsForNeuron(int neuronNumber) {
        List<Double> weights = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (i == neuronNumber) {
                weights.add(1.0);
            } else {
                weights.add(-((double) 1 / m) + Math.random() / 1000);
//                weights.add(-((double) 1 / m));

            }
        }
        return weights;
    }

    public void initOutputs(List<Double> inputLayerOutputs) {
        for (int i = 0; i < neurons.length; i++) {
            neurons[i].setOutput(inputLayerOutputs.get(i));
        }
    }

    private int search(List<Double> output) throws WinnerNeuronNotFoundException {
        List<Double> outputs = recalculateOutputs();
        for (int i = 0; i < neurons.length; i++) {
            neurons[i].setOutput(activationFunction(outputs.get(i)));
        }
        if (Environment.isDebugMode) {
            log.info("iteration " + iterationCount + ": " + outputs);
        }
        List<Double> results = outputs.stream()
                .filter(item -> item > 0)
                .collect(Collectors.toList());
        if (results.size() > 1) {
            if (results.stream().allMatch(item -> item == results.get(0))) {
                throw new WinnerNeuronNotFoundException("Neuron Not Found!");
            }
            iterationCount++;
            return search(outputs);
        }
        if (results.size() == 1) {
            iterationCount = 0;
            return outputs.indexOf(results.get(0));
        }
        throw new WinnerNeuronNotFoundException("Neuron Not Found!");
    }

    public int findWinner() throws WinnerNeuronNotFoundException {
        List<Double> outputs = getOutputs();
        if (Environment.isDebugMode) {
            log.info("Initial outputs: " + outputs);
        }

        List<Double> results = outputs.stream()
                .filter(output -> output > 0)
                .collect(Collectors.toList());
        if (results.size() > 1) {
            if (results.stream().allMatch(item -> item == results.get(0))) {
                throw new WinnerNeuronNotFoundException("Neuron Not Found!");
            }
            return search(outputs);
        }
        if (results.size() == 1) {
            return outputs.indexOf(results.get(0));
        }
        throw new WinnerNeuronNotFoundException("Neuron Not Found!");
    }

    private List<Double> recalculateOutputs() {
        List<Double> outputs = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            double result = 0;
            for (int j = 0; j < m; j++) {
                double weight = neurons[i].getWeightByIndex(j);
                double output = neurons[j].getOutput();
                result += weight * output;
                int a = 0;
            }
//            neurons[i].setOutput(activationFunction(result));
            outputs.add(activationFunction(result));
        }
        return outputs;
    }

    private void recalculateStates() {
        for (int i = 0; i < m; i++) {
            double result = 0;
            for (int j = 0; j < m; j++) {
                double weight = neurons[i].getWeightByIndex(j);
                double output = neurons[j].getOutput();
                result += weight * output;
            }
            neurons[i].setState(result);
            neurons[i].setOutput(activationFunction(result));
        }
    }

    private List<Double> getOutputs() {
        List<Double> outputs = new ArrayList<>();
        for (int i = 0; i < neurons.length; i++) {
            outputs.add(neurons[i].getOutput());
        }
        return outputs;
    }

    private double activationFunction(double s) {
        double t = (double) m / 2;
        if (s <= 0) return 0;
        return (s <= t) ? s : t;
    }
}
