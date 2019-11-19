package com.company;

import java.util.Random;

public class GameOfLife {

    private byte[][] _board;
    private byte[][] _calcBoard;
    private int _size;
    private int _step;
    private int _startingPopulation;
    private String _ruleBorn;
    private String _ruleSurvive;

    public GameOfLife(int startingSize, int startingPopulation){
        _ruleBorn = "3";
        _ruleSurvive = "23";
        if(startingPopulation > (startingSize*startingSize*0.9)){
            double sPop = (startingSize * startingSize * 0.9);
            startingPopulation = (int) sPop;
        }
        _startingPopulation = startingPopulation;
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
                    int surroundingPopulation = 0;
//                    surroundingPopulation = GetSurroundingPopulationNormal(r,c);
                    surroundingPopulation = GetSurroundingPopulationCalc(r,c);
//                    surroundingPopulation = GetSurroundingPopulationCalcFast(r,c);

                    CalculateCellWithRules(r, c, surroundingPopulation);
//                    CalculateCellDefault(r, c, surroundingPopulation);
                }
            }
            CopyBoard(_calcBoard,_board);
        }
    }

    private void CalculateCellDefault(int r, int c, int surroundingPopulation) {
        // default logic
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

    private void CalculateCellWithRules(int r, int c, int surroundingPopulation) {
        // default it dies
        _calcBoard[r][c] = 0;
        // if suvive rule fits get old value
        if (_ruleSurvive.contains(Integer.toString(surroundingPopulation))) _calcBoard[r][c] = _board[r][c];
        // if born rule fits set to 1
        if (_ruleBorn.contains(Integer.toString(surroundingPopulation))) _calcBoard[r][c] = 1;
    }

    private int GetSurroundingPopulationNormal(int r, int c) {
        return _board[r-1][c-1]+_board[r-1][c]+_board[r-1][c+1]
              +_board[r][c-1]+  0             +_board[r][c+1]
              +_board[r+1][c-1]+_board[r+1][c]+_board[r+1][c+1];
    }

    private int GetSurroundingPopulationCalc(int r, int c){
        int result = 0;
        for (int i = r-1; i <= r+1; i++) {
            for (int j = c-1; j <= c+1; j++) {
                if (!((i==r) && (j==c))){ // aktuelle Zelle is nicht zu addieren
                    if (((i>0) && (i<_size+1)) && ((j>0) && (j<_size+1))) { // check boundaries if necessary
                        result += _board[i][j];
                    }
                }
            }
        }
        return result;
    }

    private int GetSurroundingPopulationCalcFast(int r, int c){
        int result = 0;
        for (int i = r-1; i <= r+1; i++) {
            for (int j = c-1; j <= c+1; j++) {
                result = result + _board[i][j];
            }
        }
        result -= _board[r][c];
        return result;
    }

    private void CopyBoard(byte[][] fromBoard, byte[][] toBoard) {
        for (int r = 1; r <= _size; r++) {
            for (int c = 1; c <= _size; c++) {
                toBoard[r][c] = fromBoard[r][c];
            }
        }
    }

    public void Clear(){
        ClearBoard(_board);
    }

    public void SetPoint(int r, int c){
        _board[r][c] = 1;
    }

    public String GetBoardAsHTML(){
        String result = "";
        result = "";
        result += "<table>";
        for (int r = 1; r <= _size; r++) {
            result += "<tr>";
            for (int c = 1; c <= _size; c++) {
                String content = "<a href=\"/?action=set&r="+r+"&c="+c+"\">&nbsp;</a>";
                if (_board[r][c] == 1){
                    result += "<td class=\"cell1\">"+content+"</td>";
                } else {
                    result += "<td class=\"cell0\">"+content+"</td>";
                }
            }
            result += "</tr>";
        }
        result += "</table>";
        return result;
    }

    public String GetBoardString(){
        String result = "";
        for (int r = 1; r <= _size; r++) {
            for (int c = 1; c <= _size; c++) {
                result += _board[r][c];
            }
        }
        return result;
    }

    public String GetBoardHexString(){
        String result = "";
        int pos = 0;
        int act = 0;
        for (int r = 1; r <= _size; r++) {
            for (int c = 1; c <= _size; c++) {
                act = act + _board[r][c];
                act = act << 1;
                pos++;
                if (pos == 8) {
                    pos = 0;
                    result += GetHexFromByte((byte) act);
                    act = 0;
                }
            }
        }
        if (pos > 0) result += GetHexFromByte((byte) act);
        return result;
    }

    static final String HEXES = "0123456789ABCDEF";
    public static String GetHexFromByte( byte raw ) {
        String result = "";
         result += HEXES.charAt((raw & 0xF0) >> 4);
         result += HEXES.charAt((raw & 0x0F));
        return result;
    }

    public void SetBoardString(String boardString){
        int pos = 0;
        for (int r = 1; r <= _size; r++) {
            for (int c = 1; c <= _size; c++) {
                String point = "";
                if (pos < boardString.length()) {
                    point = boardString.substring(pos, pos+1);
                }
                switch (point) {
                    case "1":
                        _board[r][c] = 1;
                        break;
                    default:
                        _board[r][c] = 0;
                        break;
                }

                pos++;
            }
        }
    }

    private void ClearBoard(byte[][] board){
        for (int r = 0; r < _size+2; r++) {
            for (int c = 0; c < _size+2; c++) {
                board[r][c] = 0;
            }
        }
    }

    public void SetRules(String ruleSurvive, String ruleBorn){
        _ruleSurvive = ruleSurvive;
        _ruleBorn = ruleBorn;
    }

    public void Restart(){
        ClearBoard(_board);
        InitializeBoard(_board, _startingPopulation);
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
