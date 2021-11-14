package edu.chnu.lab1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lab1_Wave {

    public static long[] array;
    public static int endPos;

    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        System.out.print("Введіть розмір масиву: ");
        int size = in.nextInt();
        array = new long[size];
        for (int i = 0; i < size; i++) {
            array[i] = in.nextLong();
        }
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int delta = Math.max((int) Math.ceil(Math.log(size) / Math.log(availableProcessors)), 1);
        List<WaveThread> threadList = new ArrayList<>();
        int waveNumber = (int) Math.ceil(Math.log(size) / Math.log(2));
        int midPos = size / 2 + size % 2;
        endPos = size;
        for (int w = 0; w < waveNumber; w++) {
            System.out.println("Хвиля " + w);
            threadList.clear();
            List<Integer> indices = getListOfIndices(endPos, delta);
            for (int i = 0; i < indices.size() - 1; i++) {
                threadList.add(new WaveThread(indices.get(i), indices.get(i + 1)));
            }
            for (WaveThread thread : threadList) {
                thread.start();
            }
            for (WaveThread thread : threadList) {
                thread.waitForCalc();
            }
            endPos = midPos;
            midPos = endPos / 2 + endPos % 2;
            delta = Math.max((int) Math.ceil(Math.log(endPos) / Math.log(availableProcessors)), 1);
            System.out.println();
        }
        System.out.println("Сума: " + array[0]);
    }

    private static List<Integer> getListOfIndices(int size, int delta) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < size; i += delta) {
            indices.add(i);
        }
        indices.add(size);
        return indices;
    }
}

class WaveThread extends Thread {

    private int startIndex, endIndex;

    private boolean endFlag;

    public WaveThread(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        endFlag = false;
    }

    @Override
    public synchronized void run() {
        calculateSum();
        System.out.println("Потік " + this.getName() + " завершив роботу.");
        notifyAll();
    }

    private void calculateSum() {
        for (int i = startIndex; i < endIndex; i++) {
            synchronized (Lab1_Wave.array) {
                if (i < Lab1_Wave.endPos - i - 1) {
                    Lab1_Wave.array[i] += Lab1_Wave.array[Lab1_Wave.endPos - i - 1];
                }
            }
        }
        endFlag = true;
    }

    public synchronized void waitForCalc() throws InterruptedException {
        while (!endFlag) {
            wait();
        }
    }
}
