package models;

import java.util.ArrayList;
import java.util.List;

public class Neuron<T> {

    private List<T> weights;
    private double output;
    private double state;

    public Neuron() {
        weights = new ArrayList<>();
        output = 0;
        state = 0;
    }

    public Neuron(List<T> weights) {
        this.weights = weights != null ? weights : new ArrayList<>();
        output = 0;
        state = 0;
    }

    public List<T> getWeights() {
        return weights;
    }

    public T getWeightByIndex(int index) {
        if (weights != null && weights.size() != 0) return weights.get(index);
        return null;
//        throw new Exception();
    }

    public double getOutput() {
        return output;
    }

    public double getState() {
        return state;
    }

    public void setWeights(List<T> weights) {
        this.weights = weights;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public void setState(double state) {
        this.state = state;
    }
}
