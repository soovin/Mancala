/****************************************************************
 * studPlayer.java
 * Implements MiniMax search with A-B pruning and iterative deepening search (IDS). The static board
 * evaluator (SBE) function is simple: the # of stones in studPlayer's
 * mancala minue the # in opponent's mancala.
 * -----------------------------------------------------------------------------------------------------------------
 * Licensing Information: You are free to use or extend these projects for educational purposes provided that
 * (1) you do not distribute or publish solutions, (2) you retain the notice, and (3) you provide clear attribution to UW-Madison
 *
 * Attribute Information: The Mancala Game was developed at UW-Madison.
 *
 * The initial project was developed by Chuck Dyer(dyer@cs.wisc.edu) and his TAs.
 *
 * Current Version with GUI was developed by Fengan Li(fengan@cs.wisc.edu).
 * Some GUI componets are from Mancala Project in Google code.
 */




//################################################################
// studPlayer class
//################################################################

    // http://stackoverflow.com/questions/27527090/finding-the-best-move-using-minmax-with-alpha-beta-pruning
    // http://will.thimbleby.net/algorithms/doku.php?id=minimax_search_with_alpha-beta_pruning

public class studPlayer extends Player {


    /*Use IDS search to find the best move. The step starts from 1 and keeps incrementing by step 1 until
	 * interrupted by the time limit. The best move found in each step should be stored in the
	 * protected variable move of class Player.
     */
    public void move(GameState state)
    {
        GameState stateCopy = new GameState(state);

         for(int i = 0; i < 6; i++) {
             if(!state.illegalMove(i)) {
                 move = i;
                 break;
             }
         }

        int maxDepth = 1;
        while(maxDepth <= 1000) {
            move = maxAction(stateCopy, maxDepth)[0];
            maxDepth++;
        }

    }

    // Return best move for max player. Note that this is a wrapper function created for ease to use.
	// In this function, you may do one step of search. Thus you can decide the best move by comparing the 
	// sbe values returned by maxSBE. This function should call minAction with 5 parameters.
    public int[] maxAction(GameState state, int maxDepth)
    {
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        int[] pair = maxAction(state, 0, maxDepth, alpha, beta);
        return pair;
    }

	//return sbe value related to the best move for max player
    public int[] maxAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
    {
        int[] pair = new int[2];

        if (currentDepth == maxDepth) {
            pair[1] = sbe(state);
            return pair;
        }

        int[] stateArray = state.toArray();

        int v = Integer.MIN_VALUE;
        for(int i = 0; i < 6; i++) {
            if(!state.illegalMove(i)) {
                state.applyMove(i);
                state.rotate();
                int[] vpArray = minAction(state, currentDepth + 1, maxDepth, alpha, beta);
                int vp = vpArray[1];
                if (vp > alpha) {
                    alpha = vp;
                }
                if (vp > v) {
                    pair[0] = i;
                    v = vp;
                }
                if (vp >= beta) {
                    break;
                }
            }
            state.state = stateArray;
        }

        pair[1] = v;
        return pair;
    }

    //return sbe value related to the bset move for min player
    public int[] minAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
    {
        int[] pair = new int[2];

        if (currentDepth == maxDepth) {
            pair[1] = -sbe(state);
            return pair;
        }

        int[] stateArray = state.toArray();

        int v = Integer.MAX_VALUE;
        for(int i = 0; i < 6; i++) {
            if(!state.illegalMove(i)) {
                state.applyMove(i);
                state.rotate();
                int[] vpArray = maxAction(state, currentDepth + 1, maxDepth, alpha, beta);
                int vp = vpArray[1];
                if (vp < beta) {
                    beta = vp;
                }
                if (vp < v) {
                    pair[0] = i;
                    v = vp;
                }
                if (vp <= alpha) {
                    break;
                }
            }
            state.state = stateArray;
        }

        pair[1] = v;
        return pair;
    }

    //the sbe function for game state. Note that in the game state, the bins for current player are always in the bottom row.
    private int sbe(GameState state)
    {
        return state.stoneCount(6) - state.stoneCount(13);
    }
}

