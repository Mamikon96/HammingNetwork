package models;

import configs.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class InputLayer {

    private static final Logger log = LoggerFactory.getLogger(InputLayer.class);

    private int n;
    private int m;

    private Neuron<Integer>[] neurons;

    private InputLayer() {
    }

    public InputLayer(int n, int m) {
        this.n = n;
        this.m = m;
        neurons = new Neuron[m];
        for (int i = 0; i < neurons.length; i++) {
            neurons[i] = new Neuron<>();
        }
    }


    public Neuron<Integer>[] getNeurons() {
        return neurons;
    }


    public void initWeights(List<List<Integer>> etalons) {
        for (int i = 0; i < etalons.size(); i++) {
            List<Integer> nextEtalons = etalons.get(i);
            neurons[i].setWeights(nextEtalons);
        }
    }

    public void initWeights(int[][] etalons) {
        for (int i = 0; i < etalons.length; i++) {
            List<Integer> nextEtalons = new ArrayList<>(etalons[i].length);
            for (int j = 0; j < etalons[i].length; j++) {
                nextEtalons.set(j, etalons[i][j]);
            }
            neurons[i].setWeights(nextEtalons);
        }
    }

    private double calculateStateForNeuron(Neuron neuron, List<Integer> inputs) {
        double t = (double) n / 2;
        double state = 0;
        for (int i = 0; i < inputs.size(); i++) {
            state += (Integer) neuron.getWeightByIndex(i) * inputs.get(i);
        }
        state += t;
        return state;
    }

    public List<Double> calculateOutputs(List<List<Integer>> inputs) {
        List<Double> outputs = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            Double output = getOutputForNeuron(neurons[i], inputs.get(i));
            outputs.add(output);
            neurons[i].setOutput(output);
        }
        return outputs;
    }

    public List<Double> getOutputs(List<Integer> inputs) {
        List<Double> outputs = new ArrayList<>();
        for (int i = 0; i < neurons.length; i++) {
            outputs.add(1 - (double) hammingDistance(i, neurons[i].getWeights(), inputs) / inputs.size());
        }
        return outputs;
    }

    private double getOutputForNeuron(Neuron<Integer> neuron, List<Integer> inputs) {
        return 1 - (double) hammingDistance(neuron.getWeights().indexOf(1.0), neuron.getWeights(), inputs) / inputs.size();
    }

    private int hammingDistance(int neuronNumber, List<Integer> weights, List<Integer> inputs) {
        int dist = 0;
        for (int i = 0; i < inputs.size(); i++) {
            if (weights.get(i) != inputs.get(i)) {
                dist++;
            }
        }
        if (Environment.isDebugMode) {
            log.info("d" + neuronNumber + ": " + dist / 2);
        }
        return dist / 2;
    }
}
