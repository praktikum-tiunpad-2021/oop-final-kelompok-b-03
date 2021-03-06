import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;


import javax.swing.*;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class Sudoku extends JFrame {
	static int grid = Puzzle.grid; // ukuran grid
	int subgrid = Puzzle.subgrid; // ukuran subgrid

	int size = 60;
	int width = size * grid;
	int height = size * grid;

	Color color_blank = Color.WHITE; // warna background untuk cell yang akan diisi user
	Color color_true = Color.BLUE; // warna font ketika jawaban user benar
	Color color_false = Color.RED; // warna font ketika jawaban user salah
	Color color_fill = Color.CYAN; // warna background untuk cell yang berisi angka clue
	Color color_number = Color.BLUE; // warna font angka clue
	Font font_number = new Font("TW Cen MT", Font.BOLD, 20);
	int level; 
	static int closedCellsNum;

	JPanel sudokuboard;
	JPanel PanelBoard;
	JLabel LabelBoard;
	JMenuBar menu;


	JTextField[][] display = new JTextField[grid][grid];

	// puzzle (teka-teki) yang harus diselesaikan dan set elemen yang tidak dimasking (sesuai difficulty level : easy, medium, hard)
	// pada bagian ini, valuenya masih false semua karena akan diset selanjutnya mengikuti difficulty level
	private int[][] sudokupuzzle = Puzzle.getPuzzle();
    private static boolean[][] restgame = {    //temp restart
			{ false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false } };
	private static boolean[][] game = {
			{ false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false },
			{ false, false, false, false, false, false, false, false, false } };

	public Sudoku() {

		Container contentp = getContentPane();
		contentp.setLayout(new BorderLayout());

		//SudokuBoard
		sudokuboard = new JPanel();
		sudokuboard.setLayout(new GridLayout(grid, grid));
		sudokuboard.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.DARK_GRAY));
		contentp.add(sudokuboard, BorderLayout.CENTER);

		//membuatPanel
		PanelBoard = new JPanel();
		PanelBoard.setBorder(new BevelBorder(BevelBorder.RAISED));
		PanelBoard.setPreferredSize(new Dimension(contentp.getWidth(), 25));
		PanelBoard.setLayout(new BoxLayout(PanelBoard, BoxLayout.X_AXIS));
		LabelBoard = new JLabel("Cells remaining: " + closedCellsNum);
		LabelBoard.setHorizontalAlignment(SwingConstants.LEFT);
		PanelBoard.add(LabelBoard);
		contentp.add(PanelBoard, BorderLayout.SOUTH);
		
		menu = new JMenuBar();
		
		//membuat option menu
		JMenu MenuBar = new JMenu("Menu");
		JMenu helpMenu = new JMenu("Help");
		JMenuItem help = new JMenuItem("Game Instructions");
	    help.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,"1. Pada baris harus diisi dengan angka mulai dari 1-9 dan setiap baris tidak boleh berisi angka yang sama. \n2. Pada kolom juga harus diisi dengan angka dari 1-9 dan tidak boleh diisi angka kembar. \n3. Pada setiap bagian kotak area dengan ukuran 3 x 3 yang berisi 9 kotak-kotak kecil. Kotak kecil tersebut harus diisi dengan angka 1-9 dan syaratnya tidak boleh ada angka yang berulang pada setiap kotak area.");
			}
		});
		helpMenu.add(help);
		menu.add(helpMenu);	

		JMenu aboutMenu = new JMenu("About");
		JMenuItem about = new JMenuItem("Sudoku Game");
	    about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,"Sudoku, juga dikenal Number Place atau Nanpure, adalah sejenis teka-teki logika. \nTujuannya adalah untuk mengisikan angka-angka dari 1 sampai 9, \nke dalam jaring-jaring 9 x 9 yang terdiri dari 9 kotak 3 x 3 tanpa ada angka yang berulang di satu baris, kolom atau kotak");
			}
		});
		aboutMenu.add(about);
		menu.add(aboutMenu);

	    JMenuItem restartGame = new JMenuItem("Restart Game");
		MenuBar.add(restartGame);
		JMenu Level = new JMenu("Difficulty level");
	    MenuBar.add(Level);
	    LevelListener dmlistener = new LevelListener();
		JMenuItem easylevel = new JMenuItem("Easy");
	    easylevel.addActionListener(dmlistener);
	    Level.add(easylevel);
	    JMenuItem mediumlevel = new JMenuItem("Medium");
	    mediumlevel.addActionListener(dmlistener);
	    Level.add(mediumlevel);
	    JMenuItem hardlevel = new JMenuItem("Hard");
	    hardlevel.addActionListener(dmlistener);
	    Level.add(hardlevel);
		
		menu.add(MenuBar);
	    setJMenuBar(menu);
			
		JMenuItem resetGameItem = new JMenuItem("Reset game");
		MenuBar.add(resetGameItem);
		JMenuItem exitItem = new JMenuItem("Exit");
		MenuBar.add(exitItem);

	    resetGameItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                InputListener listener = new InputListener();
                for (int row = 0; row < grid; ++row) {
                    for (int col = 0; col < grid; ++col) {
                        if (restgame[row][col]) {
                            display[row][col].setText(""); // set ke string kosong
                            display[row][col].setEditable(true);	// dapat diedit
                            display[row][col].setBackground(color_blank);
                            display[row][col].addActionListener(listener);
						}
						else {
                            display[row][col].setText(sudokupuzzle[row][col] + ""); // sudah berisi angka
                            display[row][col].setEditable(false); // tidak dapat diedit
                            display[row][col].setBackground(color_fill);
                            display[row][col].setForeground(color_number);
                        }
                     }
                }
	
			}
		});

		restartGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				setLevel (5);
				new Sudoku();
				}
			});
	    exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0); //exit
			}
		});
		menu.add(MenuBar);
	
	    // inisialisasi game
		initGame(sudokuboard);
		contentp.setPreferredSize(new Dimension(width, height));
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Sudoku Game");
		setVisible(true);
	}

	private class LevelListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "Easy":
				setLevel(5);
				dispose();
				new Sudoku();
				break;
			case "Medium":
				setLevel (7);
				dispose();
				new Sudoku();
				break;
			case "Hard":
				setLevel (8);
				dispose();
				new Sudoku();
				break;
			default:
				setLevel (5);
				dispose();
				new Sudoku();
				break;
			}
		}
	}
	private void initGame(JPanel sudokuboard) {
		InputListener listener = new InputListener();
        
		for (int row = 0; row < grid; ++row) {
			for (int col = 0; col < grid; ++col) {
				display[row][col] = new JTextField(); 

				if (col == 3 || col == 6 || row == 3 || row == 6) {
					if ((col == 3 || col == 6) && (row == 3 || row == 6)) {
						display[row][col].setBorder(BorderFactory.createMatteBorder(5, 5, 1, 1, Color.black));
					} else if (col != 3 && col != 6) {
						display[row][col].setBorder(BorderFactory.createMatteBorder(5, 1, 1, 1, Color.black));
					} else if (row != 3 && row != 6) {
						display[row][col].setBorder(BorderFactory.createMatteBorder(1, 5, 1, 1, Color.black));
					}
				} else {
					display[row][col].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
				}
				display[row][col].setHorizontalAlignment(JTextField.CENTER);
				display[row][col].setFont(font_number);
				sudokuboard.add(display[row][col]); 

				if (game[row][col]) {
					display[row][col].setText("");
					display[row][col].setEditable(true);
					display[row][col].setBackground(color_blank);
					display[row][col].addActionListener(listener);
				} 
				else {
					display[row][col].setText(sudokupuzzle[row][col] + "");
					display[row][col].setEditable(false);
					display[row][col].setBackground(color_fill);
					display[row][col].setForeground(color_number);
				}
			}
		}
	}

	public static void setLevel (int level) {
		for (int row = 0; row < grid; ++row) {
			for (int col = 0; col < grid; ++col){
				game[row][col] = false;
				restgame[row][col] = false;
			}
		}
		closedCellsNum = level * 9;
        Random random = new Random();
		int randomRow = -1;
		int randomCol = -1;

		for (int i = 0; i < closedCellsNum; i++) {
			randomRow = random.nextInt(grid);
			randomCol = random.nextInt(grid);
			if (!game[randomRow][randomCol]) {
                game[randomRow][randomCol] = true;
                restgame[randomRow][randomCol] = true;
			} else {
				i--;
			}
        }
        
	}
	private class InputListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int rowBoard = -1;
			int colBoard = -1;

			// mengambil objek sumber yang menyebabkan event tsb
			JTextField source = (JTextField) e.getSource();
			// Pindai semua baris dan kolom, dan cocokkan dengan objek sumber
			boolean found = false;
			for (int row = 0; row < grid && !found; row++) {
				for (int col = 0; col < grid && !found; col++) {
					if (display[row][col] == source) {
						rowBoard = row;
						colBoard = col;
						found = true;
					}
				}
			}
			//jika input berupa text / karakter
			int input = -1;
			try {
				input = Integer.parseInt(source.getText());
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, "Input Angka!!");
			}
			// membandingkan antara angka yang diinputkan dan angka yang ada di puzzle
			// jika benar, maka angka yang diinputkan akan berwarna biru
			// namun jika tidak, maka warna angka menjadi merah 
			int firstRow = rowBoard / subgrid * subgrid;
            int firstCol = colBoard / subgrid * subgrid;
        
			if (input == sudokupuzzle[rowBoard][colBoard]) {
				display[rowBoard][colBoard].setForeground(color_true);
				display[rowBoard][colBoard].setEditable(false);
				game[rowBoard][colBoard] = false;
				for (int row = firstRow; row < firstRow + subgrid; row++) {
					for (int col = firstCol; col < firstCol + subgrid; col++) {
						if (!game[row][col]) {
							display[row][col] .setBackground(color_fill);
						}
					}
                }
                
				//mengecek cell yang sudah terisi dan yang belum
				LabelBoard.setText("Cells remaining: " + --closedCellsNum);
			} else {
				display[rowBoard][colBoard]
						.setForeground(color_false);
				// mengubah background angka pada subgrid yang sama dengan inputan
				for (int row = firstRow; row < firstRow
						+ subgrid; row++) {
					for (int col = firstCol; col < firstCol
							+ subgrid; col++) {
						if (sudokupuzzle[row][col] == input && !game[row][col]) {
							display[row][col]
							.setBackground(color_false);
						}
					}
				}
			}
			
			// cek apakah permainan sudah berhasil diselesaikan
			boolean winner = true;
			for (int row = 0; row < grid && winner; row++) {
				for (int col = 0; col < grid && winner; col++) {
					winner = !game[row][col];
				}
			}

			if (winner) {
				JOptionPane.showMessageDialog(null, "Congratulations !!!!");
			}
		}
	}

	public static void main(String[] args) {
		JOptionPane.showMessageDialog(null, "Welcome to Sudoku Game!");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(args.length < 1 || args[0].contains("easy")) {
					JOptionPane.showMessageDialog(null, "Level: Easy");
					setLevel (5);
				}

				else if(args[0].contains("medium")) {
					JOptionPane.showMessageDialog(null, "Level: Medium");
					setLevel (7);
				}

				else if(args[0].contains("hard")) {
					JOptionPane.showMessageDialog(null, "Level: Hard");
					setLevel (8);
				}
				new Sudoku();
			}
		});
	}
}