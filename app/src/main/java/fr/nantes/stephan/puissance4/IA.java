package fr.nantes.stephan.puissance4;

/**
 * Created by Ugho on 17/03/14.
 */
public class IA {

    private final static int[][] POSITIONS_VALUES = {{3,4,5,7,5,4,3},{4,6,8,10,8,6,4},{5,8,11,13,11,8,5},{5,8,11,13,11,8,5},{4,6,8,10,8,6,4},{3,4,5,7,5,4,3}};
    private final static int MAX = 100000;
    private final static int DEPTH = 4; // MAX 6
    public final static String COMPUTER = "C";
    public final static String PLAYER = "P";

    public IA() {
        // empty constructor
    }

    /**
     * This method return the best column to play for the computer
     * @param game
     * @return the column IA should play
     */
    public int getColumn(final String[][] game) {
        Node root = new Node();
        root.setRoot(true);

        analyzeFuturePosition(root, DEPTH, game, COMPUTER);

        return getBestColumn(root);
    }

    /**
     * This method say if the player win or not
     * @param game
     * @param player
     * @return TRUE if player is winner and FALSE if not
     */
    public boolean playerWin(final String[][] game, final String player) {

        //vérif  par colonne
        for (int i = 0; i<=6; i++) {
            for (int j = 0; j<=2; j++) {
                if (player.equals(game[i][j])
                        && player.equals(game[i][j + 1])
                        && player.equals(game[i][j + 2])
                        && player.equals(game[i][j + 3])) {
                    return true;
                }
            }
        }

        //vérif  par ligne
        for (int i = 0; i<=3; i++) {
            for (int j = 0; j<=5; j++) {
                if (player.equals(game[i][j])
                        && player.equals(game[i + 1][j])
                        && player.equals(game[i + 2][j])
                        && player.equals(game[i + 3][j])) {
                    return true;
                }
            }
        }

        //vérif  par diagonale vers la droite
        for (int i = 0; i<=3; i++) {
            for (int j = 3; j<=5; j++) {
                if (player.equals(game[i][j])
                        && player.equals(game[i + 1][j - 1])
                        && player.equals(game[i + 2][j - 2])
                        && player.equals(game[i + 3][j - 3])) {
                    return true;
                }
            }
        }

        //vérif  par diagonale vers la gauche
        for (int i = 3; i<=6; i++) {
            for (int j = 3; j<=5; j++) {
                if (player.equals(game[i][j])
                        && player.equals(game[i - 1][j - 1])
                        && player.equals(game[i - 2][j - 2])
                        && player.equals(game[i - 3][j - 3])) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * This method analyse the future position recursively for all the nodes
     * @param node
     * @param depth
     * @param game
     * @param player
     */
    private void analyzeFuturePosition(Node node, final int depth, final String[][] game, final String player) {

        if (playerWin(game, COMPUTER)) {
            node.setEstimation(MAX + MAX);
            node.setDepth(depth);
            return;
        }

        if (playerWin(game, PLAYER)) {
            node.setEstimation(-MAX - MAX);
            node.setDepth(depth);
            return;
        }

        if (depth == 0) {
            node.setEstimation(estimateGame(game));
            return;
        }

        for (int col=0; col<=6; col++) {
            final String[][] saveOfGame = new String[7][6];
            int[] nbPiecesByCol = numberOfPiecesByColumn(game);

            if (nbPiecesByCol[col] < 6) {
                int position = ((5 - nbPiecesByCol[col]) * 7) + col;
                int ligne = (int) Math.floor(position / 7);

                saveOfGame[col][ligne] = player;
                Node no = new Node(col, depth, node);
                node.getMyNodes().add(no);

                analyzeFuturePosition(no, depth - 1, saveOfGame, getFuturePlayer(player));
            }
        }
    }

    /**
     * This method return the value of the estimate game
     * @param game
     * @return the value of the estimate game
     */
    private int estimateGame(final String[][] game) {
        final int computer_estimation = gameValue(game, COMPUTER) + winInOneShot(game, COMPUTER) + winInTwoShots(game, COMPUTER);
        final int human_estimation = gameValue(game, PLAYER) + winInOneShot(game, PLAYER) + winInTwoShots(game, PLAYER);

        return computer_estimation - human_estimation;
    }

    /**
     * This method return the best column to play after analyze all nodes.
     * @param root
     * @return the best column to play
     */
    private int getBestColumn(Node root) {

        int columnToPlay = 0;

        for (int i=0; i<root.getMyNodes().size(); i++) {
            buildTree(root.getMyNodes().get(i));
        }

        int max = -9999999;

        columnToPlay = root.getMyNodes().get(0).getCol();

        for (int i=0; i<root.getMyNodes().size(); i++) {

            final int currentEstimation = root.getMyNodes().get(i).getEstimation();
            final int currentCol = root.getMyNodes().get(i).getCol();

            if (currentEstimation > max
                    || (currentEstimation == max & (currentCol == 2 || currentCol == 3 || currentCol ==4 ))) {

                max = currentEstimation;
                columnToPlay = currentCol;
            }
        }

        return columnToPlay;
    }

    /**
     * This metod build the tree of estimations
     * @param node
     */
    private void buildTree(Node node){
        for (int i=0; i<node.getMyNodes().size(); i++) {
            if (node.getMyNodes().get(i).getEstimation() == 0 & node.getMyNodes().get(i).getMyNodes().size() > 0) {
                buildTree(node.getMyNodes().get(i));
            }
        }
        if (node.getDepth() % 2 == 0) {
            int min=9999999;
            for (int k=0; k<node.getMyNodes().size(); k++) {
                if (node.getMyNodes().get(k).getEstimation() < min) {
                    min=node.getMyNodes().get(k).getEstimation();
                    node.setEstimation(min);
                }
            }
        } else {
            int max=-9999999;
            for (int k=0; k<node.getMyNodes().size(); k++) {
                if (node.getMyNodes().get(k).getEstimation()>max) {
                    max=node.getMyNodes().get(k).getEstimation();
                    node.setEstimation(max);
                }
            }
        }
    }

    /**
     * This method return the future player according to the player in param
     * @param player
     * @return the future player
     */
    private String getFuturePlayer(final String player) {
        return (player.equals(COMPUTER)) ?  PLAYER : COMPUTER;
    }

    /**
     * This method return the number of pieces by column.
     * @param game
     * @return the number of pieces by column
     */
    private int[] numberOfPiecesByColumn(final String[][] game) {
        final int grid[] = new int[7];

        for (int i = 0; i<=6; i++) {
            grid[i] = 0;
        }

        for (int i=0; i<=6; i++) {
            for (int j=0; j<=5; j++) {
                if (game[i][j] != null) {
                    grid[i] += 1;
                }
            }
        }

        return grid;
    }

    /**
     * This method return the value of the game by the array attribute of this class
     * @param game
     * @param player
     * @return value of the game
     */
    private int gameValue(final String[][] game, final String player) {
        int value = 0;
        for (int i=0; i<=6; i++) {
            for (int j=0; j<=5; j++) {
                if (player.equals(game[i][j])) {
                    value += POSITIONS_VALUES[j][i];
                }
            }
        }
        return value;
    }

    /**
     * This method return the value of the game after verified if the player
     * can win after two shots.
     * @param game
     * @param player
     * @return value of the game
     */
    private int winInTwoShots(final String[][] game, final String player) {
        int value = 0;

        //vérif  par colonne
        for (int i = 0; i<=6; i++) {
            for (int j = 0; j<=2; j++) {
                if ((player.equals(game[i][j]) && player.equals(game[i][j + 1]) && game[i][j + 2] == null && game[i][j + 3] == null)
                        || (game[i][j] == null && game[i][j + 1] == null && player.equals(game[i][j + 2]) && player.equals(game[i][j + 3]))
                        || (player.equals(game[i][j]) && game[i][j + 1] == null && game[i][j + 2] == null && player.equals(game[i][j + 3])))
                {
                    value += 300;
                }
            }
        }

        //vérif  par ligne
        for (int i = 0; i<=3; i++) {
            for (int j = 0; j<=5; j++) {
                if ((player.equals(game[i][j]) && player.equals(game[i + 1][j]) && game[i + 2][j] == null && game[i + 3][j] == null)
                        || (game[i][j] == null && game[i + 1][j] == null && player.equals(game[i + 2][j]) && player.equals(game[i + 3][j]))
                        || (player.equals(game[i][j]) && game[i + 1][j] == null && game[i + 2][j] == null && player.equals(game[i + 3][j])))
                {
                    value += 300;
                }
            }
        }

        //vérif  par diagonale vers la droite
        for (int i = 0; i<=3; i++) {
            for (int j = 3; j<=5; j++) {
                if ((player.equals(game[i][j]) && player.equals(game[i + 1][j - 1]) && game[i + 2][j - 2] == null && game[i + 3][j - 3] == null)
                        || (game[i][j] == null && game[i + 1][j - 1] == null && player.equals(game[i + 2][j - 2]) && player.equals(game[i + 3][j - 3]))
                        || (player.equals(game[i][j]) && game[i + 1][j - 1] == null && game[i + 2][j - 2] == null && player.equals(game[i + 3][j - 3])))
                {
                    value += 300;
                }
            }
        }

        //vérif  par diagonale vers la gauche
        for (int i = 3; i<=6; i++) {
            for (int j = 3; j<=5; j++) {
                if ((game[i][j] == null && game[i - 1][j - 1] == null && player.equals(game[i - 2][j - 2]) && player.equals(game[i - 3][j - 3]))
                        || (player.equals(game[i][j]) && player.equals(game[i - 1][j - 1]) && game[i - 2][j - 2] == null && game[i - 3][j - 3] == null)
                        || (player.equals(game[i][j]) && game[i - 1][j - 1] == null && game[i - 2][j - 2] == null && player.equals(game[i - 3][j - 3])))
                {
                    value += 300;
                }
            }
        }

        return value;
    }

    /**
     * This method return the value of the game after verified if the player
     * can win after one shot.
     * @param game
     * @param player
     * @return value of the game
     */
    private int winInOneShot(final String[][] game, final String player) {
        int value = 0;

        //vérif  par colonne
        for (int i = 0; i<=6; i++) {
            for (int j = 0; j<=2; j++) {
                if ((game[i][j] == null && player.equals(game[i][j + 1]) && player.equals(game[i][j + 2]) && player.equals(game[i][j + 3]))
                        || (player.equals(game[i][j]) && game[i][j + 1] == null && player.equals(game[i][j + 2]) && player.equals(game[i][j + 3]))
                        || (player.equals(game[i][j]) && player.equals(game[i][j + 1]) && game[i][j + 2] == null && player.equals(game[i][j + 3]))
                        || (player.equals(game[i][j]) && player.equals(game[i][j + 1]) && player.equals(game[i][j + 2]) && game[i][j + 3] == null))
                {
                    value += 5000;
                }
            }
        }

        //vérif  par ligne
        for (int i = 0; i<=3; i++) {
            for (int j = 0; j<=5; j++) {
                if ((game[i][j] == null && player.equals(game[i + 1][j]) && player.equals(game[i + 2][j]) && player.equals(game[i + 3][j]))
                        || (player.equals(game[i][j]) && game[i + 1][j] == null && player.equals(game[i + 2][j]) && player.equals(game[i + 3][j]))
                        || (player.equals(game[i][j]) && player.equals(game[i + 1][j]) && game[i + 2][j] == null && player.equals(game[i + 3][j]))
                        || (player.equals(game[i][j]) && player.equals(game[i + 1][j]) && player.equals(game[i + 2][j]) && game[i + 3][j] == null))
                {
                    value += 5000;
                }
            }
        }

        //vérif  par diagonale vers la droite
        for (int i = 0; i<=3; i++) {
            for (int j = 3; j<=5; j++) {
                if ((game[i][j] == null && player.equals(game[i + 1][j - 1]) && player.equals(game[i + 2][j - 2]) && player.equals(game[i + 3][j - 3]))
                        || (player.equals(game[i][j]) && game[i + 1][j - 1] == null && player.equals(game[i + 2][j - 2]) && player.equals(game[i + 3][j - 3]))
                        || (player.equals(game[i][j]) && player.equals(game[i + 1][j - 1]) && game[i + 2][j - 2] == null && player.equals(game[i + 3][j - 3]))
                        || (player.equals(game[i][j]) && player.equals(game[i + 1][j - 1]) && player.equals(game[i + 2][j - 2]) && game[i + 3][j - 3] == null))
                {
                    value += 5000;
                }
            }
        }

        //vérif  par diagonale vers la gauche
        for (int i = 3; i<=6; i++) {
            for (int j = 3; j<=5; j++) {
                if ((game[i][j] == null && player.equals(game[i - 1][j - 1]) && player.equals(game[i - 2][j - 2]) && player.equals(game[i - 3][j - 3]))
                        || (player.equals(game[i][j]) && game[i - 1][j - 1] == null && player.equals(game[i - 2][j - 2]) && player.equals(game[i - 3][j - 3]))
                        || (player.equals(game[i][j]) && player.equals(game[i - 1][j - 1]) && game[i - 2][j - 2] == null && player.equals(game[i - 3][j - 3]))
                        || (player.equals(game[i][j]) && player.equals(game[i - 1][j - 1]) && player.equals(game[i - 2][j - 2]) && game[i - 3][j - 3] == null))
                {
                    value += 5000;
                }
            }
        }

        return value;
    }

}