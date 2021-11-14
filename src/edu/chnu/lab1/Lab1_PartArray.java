package edu.chnu.lab1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lab1_PartArray {

    public static int[] array;

    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        System.out.print("Введіть розмір масиву: ");
        int size = in.nextInt();
        array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = in.nextInt();
        }
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        List<PartArrayThread> threadList = new ArrayList<>();
        int delta = Math.max((int) Math.ceil(Math.log(size) / Math.log(availableProcessors)), 1);
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < size; i += delta) {
            indices.add(i);
        }
        indices.add(size);
        for (int i = 0; i < indices.size() - 1; i++) {
            threadList.add(new PartArrayThread(indices.get(i), indices.get(i + 1)));
        }
        for (Thread thread : threadList) {
            thread.start();
        }
        long sum = 0L;
        for (PartArrayThread thread : threadList) {
            sum += thread.getPartialSum();
        }
        System.out.println("Сума: " + sum);
    }
}

class PartArrayThread extends Thread {

    private int startIndex, endIndex;

    private long partialSum;

    private boolean endFlag;

    public PartArrayThread(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        partialSum = 0L;
        endFlag = false;
    }

    @Override
    public synchronized void run() {
        for (int i = startIndex; i < endIndex; i++) {
            partialSum += Lab1_PartArray.array[i];
        }
        endFlag = true;
        System.out.println("Потік " + this.getName() + " завершив роботу.");
        notifyAll();
    }

    public synchronized long getPartialSum() throws InterruptedException {
        while (!endFlag) {
            wait();
        }
        return partialSum;
    }
}
