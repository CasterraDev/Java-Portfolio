import java.util.Scanner;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.*;
import java.util.*;

class Minesweeper {
    public int w,h;
    public Cell[][] cells;
    public int numOfCells;
    public int numOfMines;
    public int numCellsClicked = 0;
    public int MINE = 10;
    public Cell[] neighborsArr = new Cell[8];
    public int score = 0,combo = 0;
    public boolean gameActive = true;
    public JFrame frame = new JFrame();
    public JLabel scoreL = new JLabel();
    public JButton reset;
    private final ActionListener actionListener = e -> {
        Object source = e.getSource();
        if (source == reset){
            resetGame();
        }else{
            clickedCell((Cell)source);
        }
    };
    
    public void makeHeader(){
        JPanel p = new JPanel();
        scoreL.setText("Score: " + Integer.toString(score));
        p.add(scoreL);
        frame.add(p,BorderLayout.NORTH);
    }

    public void updateHeader(){
        scoreL.setText("Score: " + Integer.toString(score));
    }
    //Make the grid of cells
    public void makeButtonGrid(int width,int height){
        Container grid = new Container();
        grid.setLayout(new GridLayout(width, height));
        cells = new Cell[w][h];
        for (int i = 0;i < h;i++){
            for (int j = 0;j < w;j++){
                cells[j][i] = new Cell(j,i,actionListener);
                grid.add(cells[j][i]);
            }
        }
        frame.add(grid,BorderLayout.CENTER);
    }
    //Make the buttons on the bottom
    public void makeBottomButtons(){
        JPanel p = new JPanel();
        reset = new JButton("Reset");
        reset.addActionListener(actionListener);
        p.add(reset);
        frame.add(p,BorderLayout.SOUTH);
    }
    //Makes a standard JFrame window
    public void makeWindow(int width,int height,String title){
        frame.setPreferredSize(new Dimension(1000,900));
        frame.setMinimumSize(new Dimension(500,500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setTitle(title);
        frame.setVisible(true);
    }

    public void placeMines(){
        Random r = new Random();
        Set<Integer> grid1D = new HashSet<Integer>(numOfCells);
        for (int i = 0;i < h;i++){
            for (int j = 0;j < w;j++){
                grid1D.add(j * w + i);
            }
        }
        //Change the num of mines depending on the board's size
        numOfMines = (int)(numOfCells * .3);
        for (int i = 0;i < numOfMines;i++){
            int in = r.nextInt(grid1D.size());
            int x = in/w;
            int y = in%w;
            cells[x][y].value = MINE;
        }
        //Update the count of neighboring mines
        for (int i = 0;i < h;i++){
            for (int j = 0;j < w;j++){
                if (!cells[j][i].isAMine()) {
                    cells[j][i].updateValue();
                }
            }
        }
        System.out.println("Number of Mines: " + numOfMines);
    }

    public void resetGame(){
        gameActive = true;
        //Reset every cell
        for (int i = 0;i < h;i++){
            for (int j = 0;j < w;j++){
                cells[j][i].reset();
            }
        }
        numCellsClicked = 0;
        //Place new mines
        placeMines();
        //Bring score back down to zero
        score = 0;
        updateHeader();
    }
    //When you click on a cell this method is called
    public void clickedCell(Cell c){
        if (c.isAMine()) gameOver();
        score++;
        updateHeader();
        if (c.countNeighborMines() == 0){
            Set<Cell> i = new HashSet<>();
            i.add(c);
            dominoCells(i);
        }else{
            c.reveal();
        }
    }
    //Reveal all cells to end the game
    public void revealAll(){
        for (int i = 0;i<h;i++){
            for (int j =0;j<w;j++){
                if (cells[j][i].isEnabled()) cells[j][i].reveal();
            }
        }
    }
    //If a cell has the value zero then do a domino effect with it's neighbors
    public void dominoCells(Set<Cell> neighbors){
        combo = 0;
        //If the set isn't empty continue the domino effect
        while(!neighbors.isEmpty()){
            //Get one of the neighbors
            Cell c = neighbors.iterator().next();
            neighbors.remove(c);
            score += combo;
            combo++;
            c.reveal();
            neighborsArr = c.giveNeighbors();
            for (Cell currentCell : neighborsArr){
                if (currentCell == null) continue;
                int n = currentCell.countNeighborMines();
                //If the cell has no neighboring mines and is active then continue the domino effect
                if (n == 0 && currentCell.isEnabled()){
                    neighbors.add(currentCell);
                }else{
                    currentCell.reveal();
                }
            }
        }
    }

    public void gameOver(){
        gameActive = false;
        System.out.println("You Lose");
        System.out.println("Score: " + score);
        revealAll();
    }

    public void checkForWin(){
        if (numOfCells - numOfMines == numCellsClicked && gameActive){
            gameActive = false;
            System.out.println("You Win");
            System.out.println("Score: " + score);
            revealAll();
        }
    }

    private class Cell extends JButton{
        int x,y;
        int value;
    
        public Cell(int x,int y,ActionListener al){
            this.x = x;
            this.y = y;
            addActionListener(al);
        }
    
        public boolean isAMine(){
            return this.value == MINE;
        }

        public int countNeighborMines(){
            int count = 0;
            neighborsArr = giveNeighbors();
            for (Cell neighbor : neighborsArr) {
                if (neighbor == null) break;
                if (neighbor.isAMine()) count++;
            }
            return count;
        }

        public Cell[] giveNeighbors(){
            int c = 0;
            Cell[] n = new Cell[8];
            //Make sure the array is empty
            for (int i = 0;i < 8;i++){ //Max of eight neighbors
                n[i] = null;
            }

            for(int i = y-1;i <= y+1;i++){
                for(int j = x-1;j <= x+1;j++){
                    if (i == y && j == x) continue; //Dont count yourself
                    if (i >= h || i < 0 || j >= w || j < 0) continue; //Stay within the board's bounds
                    n[c] = cells[j][i];
                    c++;
                }
            }
            return n;
        }

        public void reveal(){
            numCellsClicked++;
            if (isAMine()){
                setText("M");
                setBackground(Color.RED);
            }else{
                int num = countNeighborMines();
                setText(Integer.toString(num));
            }
            setEnabled(false);
            //Check to see if all non-mine cells have been clicked
            checkForWin();
        }

        public void reset(){
            setBackground(null);
            numCellsClicked--;
            this.value = 0;
            this.setText("");
            setEnabled(true);
        }

        public void updateValue(){
            int c = countNeighborMines();
            this.value = c;
        }
    }

    public Minesweeper(){
        Scanner input = new Scanner(System.in);
        do{
            System.out.println("Width of Board");
            w = input.nextInt();
            System.out.println("Height of Board");
            h = input.nextInt();
            if (w <= 1 || h <= 1){
                System.out.println("Width and Height have to be more than 1");
            }
        }while(w <= 1 || h <= 1);

        input.close();
        numOfCells = w * h;
        makeWindow(w,h,"Minesweeper");
        makeButtonGrid(w,h);
        makeHeader();
        makeBottomButtons();
        placeMines();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Minesweeper ms = new Minesweeper();
    }
}

