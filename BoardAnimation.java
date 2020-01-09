import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.awt.image.*;
import java.util.HashMap;

//1) Have main screen -> options like play, help, settings, quit
//2) want Have splash screen before main screen
//3) 1280 x 720, and has to be one window
//4) Help to have text and graphics maybe a tutorial
//5) Rock solid 60fps
//6) Settings -> change the port number and three other settings. Remember settings between instances
//7) Host game or join game option
//8) While in game, implement chat between players
//9) Gameplay must be animation based
//10) Use mouse or key listener to control animation
//11) Be able to go back and then play again
//12) Want to have some sound
//13) All images need to be made from scratch
//14) Write up req doc with needs but keep track of the wants
public class BoardAnimation extends JPanel {
    private Color darkGrey = new Color(79, 76, 69);

    // Will be used later for networking
    private boolean blnServer, blnTurn;
    private boolean blnClientStarted = false;
    private static Board chessBoard;
    private boolean pressed = false;
    private Piece temp = null;
    // {queen, rook, bishop, knight, pawn}
    // {4,1,3,2,6}
    private JLabel[] serverCaptureLabels = new JLabel[5];
    private JLabel[] clientCaptureLabels = new JLabel[5];
    // used for labelling captures and whether it is promotion time
    public JLabel serverInfoLabel = new JLabel("CAPTURED PIECES");
    public JLabel clientInfoLabel = new JLabel("CAPTURED PIECES");

    private int [] intPieces = {4,1,3,2,6};
    private ArrayList<BufferedImage> whiteCaptureImages = new ArrayList<>();
    private ArrayList<BufferedImage> blackCaptureImages = new ArrayList<>();
    public static HashMap<Integer, BufferedImage> pieceImages = new HashMap<>();

    private static void initImages() {
        String path = "Assets/Pieces/";

        String[] fileNames = { "Rook.png", "Knight.png", "Bishop.png", "Queen.png", "King.png", "Pawn.png",
                "RookBlack.png", "KnightBlack.png", "BishopBlack.png", "QueenBlack.png", "KingBlack.png",
                "PawnBlack.png" };

        for (int i = 0; i < fileNames.length; i++) {
            pieceImages.put(i + 1, Utility.loadImage(path + fileNames[i]));
        }

    }

    private void initCaptureImages() {
        for(int intPiece : intPieces) {
            blackCaptureImages.add(Utility.resizeImage(pieceImages.get(intPiece), 60, 120));
            whiteCaptureImages.add(Utility.resizeImage(pieceImages.get(intPiece + 6), 60, 120));
        }
    }

    public void changeTurn() {
        blnTurn = !blnTurn;
    }

    public void initializeGame() {
        blnClientStarted = true;
        chessBoard = new Board(blnServer);
        addMouseListener(new MyMouseAdaptor());
        addMouseMotionListener(new MyMouseAdaptor());
        initializeCaptureLabels();
    }

    private void initializeCaptureLabels() {
        serverInfoLabel.setSize(200, 20);
        serverInfoLabel.setLocation(900, 5);
        serverInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        serverInfoLabel.setVerticalAlignment(SwingConstants.CENTER);
        Utility.setLabelStyle(serverInfoLabel, 12);
        add(serverInfoLabel);
        for (int i = 0; i < serverCaptureLabels.length; i++) {
            serverCaptureLabels[i] = new JLabel("0");
            Utility.setLabelStyle(serverCaptureLabels[i], 18);
            serverCaptureLabels[i].setSize(60, 20);
            serverCaptureLabels[i].setLocation(800 + (i * 80), 190);
            serverCaptureLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            serverCaptureLabels[i].setVerticalAlignment(SwingConstants.CENTER);
            add(serverCaptureLabels[i]);
        }

        clientInfoLabel.setSize(200, 20);
        clientInfoLabel.setLocation(900, 210);
        clientInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        clientInfoLabel.setVerticalAlignment(SwingConstants.CENTER);
        Utility.setLabelStyle(clientInfoLabel, 12);
        add(clientInfoLabel);
        for (int i = 0; i < clientCaptureLabels.length; i++) {
            clientCaptureLabels[i] = new JLabel("0");
            Utility.setLabelStyle(clientCaptureLabels[i], 18);
            clientCaptureLabels[i].setSize(60, 20);
            clientCaptureLabels[i].setLocation(800 + (i * 80), 390);
            clientCaptureLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            clientCaptureLabels[i].setVerticalAlignment(SwingConstants.CENTER);
            add(clientCaptureLabels[i]);
        }
    }

    public void updateCaptures() {
        int[] intPieces = { 4, 1, 3, 2, 6 };
        for (int i = 0; i < 5; i++) {
            serverCaptureLabels[i].setText(chessBoard.capturedPieceCount(true, intPieces[i]) + "");
            clientCaptureLabels[i].setText(chessBoard.capturedPieceCount(false, intPieces[i]) + "");
        }
    }

    public static Board getBoard() {
        return chessBoard;
    }

    private void drawBoard(Graphics g) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                g.setColor(((i % 2 == 0) == (j % 2 == 0)) ? Color.WHITE : darkGrey);
                g.fillRect(j * 90, i * 90, 90, 90);
            }
        }
    }

    private void drawPieces(Graphics g) {
        // System.out.println(chessBoard.pieces);
        for (Piece p : chessBoard.pieces) {
            p.update(g);
        }
    }

    // TODO: draw the images later
    private void drawCapturedPieces(Graphics g) {
        g.setColor(Color.BLUE);

        //white captures
        for (int i = 0; i < 5; i++) {
            //g.fillRect(800 + (i * 80), 50, 60, 120);
            g.drawImage(whiteCaptureImages.get(i), 800 + (i*80), 50, null);
            g.drawImage(blackCaptureImages.get(i), 800 + (i*80), 250, null);
        }

        g.setColor(Color.PINK);

        //black captures
        //for (int i = 0; i < 5; i++) {
            //g.fillRect(800 + (i * 80), 250, 60, 120);
        //}
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (blnClientStarted) {
            drawBoard(g);
            drawPieces(g);
            g.setColor(Color.WHITE);
            g.drawLine(720, 0, 720, 720);
            drawCapturedPieces(g);
        }
    }

    // for testing
    public BoardAnimation() {
        super();
        blnServer = true;
    }

    public BoardAnimation(boolean blnIsServer) {
        super();
        setBackground(darkGrey);
        this.blnServer = blnIsServer;
        blnTurn = blnIsServer;
        initImages();
        if(pieceImages != null) {
            initCaptureImages();
        }
    }

    private class MyMouseAdaptor extends MouseAdapter {
        private boolean blnWrongColor, blnMouseError;

        @Override
        public void mouseClicked(MouseEvent evt) {
            if (blnTurn && chessBoard.promotionInProgress()) {
                ArrayList<Piece> promotionChoices = chessBoard.getPromotionChoices(blnServer);
                for (Piece piece : promotionChoices) {
                    if ((evt.getX() <= piece.intXPos + 60 && evt.getX() >= piece.intXPos)
                            && (evt.getY() >= piece.intYPos && evt.getY() <= piece.intYPos + 120)) {
                        chessBoard.promotePiece(piece);
                        serverInfoLabel.setText("CAPTURED PIECES");
                        clientInfoLabel.setText("CAPTURED PIECES");
                        changeTurn();
                        repaint();
                        break;
                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            blnWrongColor = false;
            blnMouseError = false;
            int intXPos = chessBoard.roundDown(evt.getX(), 90);
            int intYPos = chessBoard.roundDown(evt.getY(), 90);
            int intXIndex = blnServer ? intXPos / 90 : 7 - (intXPos / 90);
            int intYIndex = blnServer ? intYPos / 90 : 7 - (intYPos / 90);
            boolean blnInBounds = intXIndex >= 0 && intXIndex < 8 && intYIndex >= 0 && intYIndex < 8;
            boolean blnCorrectColor = blnInBounds
                    ? (chessBoard.isWhite(intXIndex, intYIndex) && blnServer)
                            || (!chessBoard.isWhite(intXIndex, intYIndex) && !blnServer)
                    : false;

            if (blnTurn && blnInBounds && !chessBoard.promotionInProgress()
                    && chessBoard.getPiece(intXIndex, intYIndex) != 0 && blnCorrectColor) {
                for (Piece piece : chessBoard.pieces) {
                    if ((evt.getX() <= piece.intXPos + 90 && evt.getX() >= piece.intXPos)
                            && (evt.getY() >= piece.intYPos && evt.getY() <= piece.intYPos + 90) && pressed == false
                            && piece.blnColor == blnServer) {
                        pressed = true;
                        temp = piece;
                        temp.setPreviousPosition(temp.intXPos, temp.intYPos);
                        break;
                    }
                }
            } else if (blnTurn && blnInBounds && !chessBoard.promotionInProgress()
                    && chessBoard.getPiece(intXIndex, intYIndex) != 0 && !blnCorrectColor) {
                // user clicks on the other side's piece
                Timer labelTimer = null;
                if (blnServer) {
                    serverInfoLabel.setText("You can only move your own pieces");
                    labelTimer = new Timer(3000, event -> serverInfoLabel.setText("CAPTURED PIECES"));
                } else {
                    clientInfoLabel.setText("You can only move your own pieces");
                    labelTimer = new Timer(3000, event -> clientInfoLabel.setText("CAPTURED PIECES"));
                }

                blnWrongColor = true;

                labelTimer.setRepeats(false);
                labelTimer.start();
            } else if (!blnTurn && blnInBounds && !chessBoard.promotionInProgress()
                    && chessBoard.getPiece(intXIndex, intYIndex) != 0 && blnCorrectColor) {
                Timer labelTimer = null;
                if (blnServer) {
                    serverInfoLabel.setText("It is not your turn");
                    labelTimer = new Timer(3000, event -> serverInfoLabel.setText("CAPTURED PIECES"));
                } else {
                    clientInfoLabel.setText("It is not your turn");
                    labelTimer = new Timer(3000, event -> clientInfoLabel.setText("CAPTURED PIECES"));
                }

                labelTimer.setRepeats(false);
                labelTimer.start();
            } else {
                System.out.println("mouse error");
                blnMouseError = true;
            }
        }

        @Override
        public void mouseDragged(MouseEvent evt) {
            if (pressed && temp != null && blnTurn && !chessBoard.promotionInProgress() && !blnMouseError) {
                temp.setPosition(evt.getX(), evt.getY());
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            int intXPos = chessBoard.roundDown(evt.getX(), 90);
            int intYPos = chessBoard.roundDown(evt.getY(), 90);
            pressed = false;
            boolean blnInBounds = intXPos / 90 >= 0 && intXPos / 90 < 8 && intYPos / 90 >= 0 && intYPos / 90 < 8;
            if (!blnTurn || blnWrongColor || blnMouseError) {
                return;
            } else if (temp != null && blnTurn && !blnInBounds && !chessBoard.promotionInProgress()) {
                // placing outside the board
                temp.goBack();
            } else if (temp != null && blnTurn && blnInBounds && !chessBoard.promotionInProgress()) {
                if (chessBoard.executeMove(temp, intXPos, intYPos)) {
                    updateCaptures();
                    if (chessBoard.promotionInProgress()) {
                        if (chessBoard.getPieceToPromote().blnColor) {
                            serverInfoLabel.setText("PROMOTION, CHOOSE A PIECE");
                        } else {
                            clientInfoLabel.setText("PROMOTION, CHOOSE A PIECE");
                        }
                    } else {
                        changeTurn();
                    }
                }
            }

            repaint();
        }
    }
}
