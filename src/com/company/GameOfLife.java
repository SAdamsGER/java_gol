package com.company;

import java.util.Random;

public class GameOfLife {

    private byte[][] _board;
    private byte[][] _calcBoard;
    private int _size;
    private int _step;

    public GameOfLife(int startingSize, int startingPopulation){
        if(startingPopulation > (startingSize*startingSize*0.9)){
            double sPop = (startingSize * startingSize * 0.9);
            startingPopulation = (int) sPop;
        }
        _size = startingSize;
        startingSize = startingSize + 2;
        _board = new byte[startingSize][startingSize];
        _calcBoard =  new byte[startingSize][startingSize];
        ClearBoard(_board);
        ClearBoard(_calcBoard);
        InitializeBoard(_board,startingPopulation);
        _step = -1;
    }

    public void Grow(){
        _step++;
        if (_step > 0){
            ClearBoard(_calcBoard);
            for (int r = 1; r <= _size; r++) {
                for (int c = 1; c <= _size; c++) {
                    int surroundingPopulation = _board[r-1][c-1]+_board[r-1][c]+_board[r-1][c+1]
                            +_board[r][c-1]+0+_board[r][c+1]
                            +_board[r+1][c-1]+_board[r+1][c]+_board[r+1][c+1];
                    switch (surroundingPopulation){
                        case 2:
                            _calcBoard[r][c] = _board[r][c];
                            break;
                        case 3:
                            _calcBoard[r][c] = 1;
                            break;
                        default:
                            _calcBoard[r][c] = 0;
                            break;
                    }

                }
            }
            CopyBoard(_calcBoard,_board);
        }
    }

    private void CopyBoard(byte[][] fromBoard, byte[][] toBoard) {
        for (int r = 1; r <= _size; r++) {
            for (int c = 1; c <= _size; c++) {
                toBoard[r][c] = fromBoard[r][c];
            }
        }
    }

    public String GetBoardAsHTML(){
        String result = "";
        result = "<table border=1>";
        for (int r = 1; r <= _size; r++) {
            result += "<tr>";
            for (int c = 1; c <= _size; c++) {
                result += "<td width=16>";
                if (_board[r][c] == 1){
                    result += "@";
                } else {
                    result += "&nbsp;";
                }
                result += "</td>";
            }
            result += "</tr>";
        }
        result += "</table>";
        return result;
    }

    private void ClearBoard(byte[][] board){
        for (int r = 0; r < _size; r++) {
            for (int c = 0; c < _size; c++) {
                board[r][c] = 0;
            }
        }
    }

    private void InitializeBoard(byte[][] board, int startingPopulation){
        Random gen = new Random();

        while(startingPopulation > 0){
            int r = gen.nextInt(_size) + 1;
            int c = gen.nextInt(_size) + 1;
            if(board[r][c] == 0){
                board[r][c] = 1;
                startingPopulation--;
            }
        }
    }
}
