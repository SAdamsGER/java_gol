package com.company;

import java.util.Random;

public class GameOfLife {

    private byte[][] _population;
    private int _size;

    public GameOfLife(int startingSize, int startingPopulation){
        if(startingPopulation > (startingSize*startingSize*0.9)){
            double sPop = (startingSize * startingSize * 0.9);
            startingPopulation = (int) sPop;
        }
        _size = startingSize;
        startingSize = startingSize + 2;
        _population = new byte[startingSize][startingSize];
        for (int i = 0; i < startingSize; i++) {
            for (int j = 0; j < startingSize; j++) {
                _population[i][j] = 0;
            }
        }
    }

    private void Initialize(int startingPopulation){
        Random gen = new Random();

        while(startingPopulation > 0){
            int r = gen.nextInt(_size) + 1;
            int c = gen.nextInt(_size) + 1;
            if(_population[r][c] == 0){
                _population[r][c] = 1;
                startingPopulation -=;
            }
        }
    }
}
