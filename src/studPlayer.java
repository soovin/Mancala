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

public class studPlayer extends Player {


	/*Use IDS search to find the best move. The step starts from 1 and keeps incrementing by step 1 until
	 * interrupted by the time limit. The best move found in each step should be stored in the
	 * protected variable move of class Player.
	 */
	public void move(GameState state)
	{
		// select the initial legal move (to be safe!)
		for (int i=0; i<6; i++)	{
			if (!state.illegalMove(i)) {
				move = i;
				break;
			}
		}

		// initialize maxDepth
		int maxDepth = 1;

		// create copy state
		GameState stateCopy = new GameState(state);

		// keep updating move until timeout
		while (maxDepth<100) {

			// do minmax search(alpha-beta dfs) up to depth maxDepth
			move = maxAction(stateCopy,maxDepth,move);
			//System.out.println(move);

			// increment maxDepth by 1
			maxDepth++;
			//System.out.println("maxDepth: "+maxDepth);
		}


	}

	// Return best move for max player. Note that this is a wrapper function created for ease to use.
	// In this function, you may do one step of search. Thus you can decide the best move by comparing the 
	// sbe values returned by maxSBE. This function should call minAction with 5 parameters.
	public int maxAction(GameState state, int maxDepth, int moveOld){

		int alpha = Integer.MIN_VALUE;;
		int beta = Integer.MAX_VALUE;;

		int sbe = Integer.MIN_VALUE;
		int temp;

		// TODO: here lies the problem!
		// we have to initialize this variable to some value, which may be an illegal move. 
		// If this function is terminated before going to the loop below(which has a illegalMove() check due to time constraint,
		// we return this initialized value of move, which is illegal......
		// we have to find a way to (1) either initialize the value with illegal bin
		// (2) or figure out when is the moment of timeout and adjust our function to stop iterating before that
		// Problem solved by bringing the old action
		int move = moveOld;

		for (int i=0;i<6;i++) {
			if (!state.illegalMove(i)) {

				// save current state array 
				int[] stateArray = state.toArray();

				// suppose we apply move at bin i
				state.applyMove(i);

				// rotate the context
				state.rotate();

				// call minAction
				temp = minAction(state,1,maxDepth,alpha,beta);

				// update best move if sbe is larger
				if (temp>=sbe) {
					move = i;
					sbe = temp;
					//System.out.println("move:"+move+ "  sbe:"+sbe);
				}

				// restore
				state.state = stateArray;

			}
		}

		return move;
	}

	//return sbe value related to the best move for max player
	public int maxAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta){

		// check if at maxDepth
		if (currentDepth == maxDepth) {
			return sbe(state);
		}

		// save current state array 
		int[] stateArray = state.toArray();

		// initialize v
		int v = Integer.MIN_VALUE;
		boolean playAgain;

		for (int i=0;i<6;i++) {
			if (!state.illegalMove(i)) {
				// suppose we apply move at bin i
				playAgain = state.applyMove(i);
				if (playAgain) {
					// max player continues to play again.
					v = Math.max(v,maxAction(state,currentDepth+1,maxDepth,alpha,beta));
				} else {
					// min player takes the turn.

					// rotate the context
					state.rotate();

					// call minAction
					v = Math.max(v, minAction(state,currentDepth+1,maxDepth,alpha,beta));
				}

				// restore the state (undo applyMove(i))
				//state.rotate();
				state.state = stateArray;

				// check for pruning
				if (v>=beta) {
					return v;
				}
				alpha = Math.max(alpha,v);
			}

		}
		return v;
	}
	//return sbe value related to the bset move for min player
	public int minAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta){

		// check if at maxDepth
		if (currentDepth == maxDepth) {
			return -sbe(state);
		}

		// save current state array 
		int[] stateArray = state.toArray();

		// initialize v
		int v = Integer.MIN_VALUE;
		boolean playAgain;

		for (int i=0;i<6;i++) {
			if (!state.illegalMove(i)) {
				// suppose we apply move at bin i
				playAgain = state.applyMove(i);

				if (playAgain) {
					// min player continues to play again.
					v = Math.min(v, minAction(state,currentDepth+1,maxDepth,alpha,beta));
				} else {
					// max player takes the turn.

					// rotate the context
					state.rotate();

					// call maxAction
					v = Math.min(v, maxAction(state,currentDepth+1,maxDepth,alpha,beta));
				}

				// restore the state (undo applyMove(i))
				state.state = stateArray;
				//state.rotate();

				// check for pruning
				if (v<=alpha) {
					return v;
				}  
				beta = Math.min(beta, v);
			}
		}
		return v;
	}

	//the sbe function for game state. Note that in the game state, the bins for current player are always in the bottom row.
	private int sbe(GameState state){

		// simplest SBE
		return state.stoneCount(6) - state.stoneCount(13);
	}
}

