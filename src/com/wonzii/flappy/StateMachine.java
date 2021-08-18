package com.wonzii.flappy;

public enum StateMachine {
	StartScreen{

		@Override
		public StateMachine nextState(boolean started, boolean aborted) {
			// Check Abort flag first and then Started flag
			if(aborted)
			{
				return Aborted;
			}
			else if(started)
			{
				setStateJustChanged(true);
				return Running;
			}
			else
			{
				setStateJustChanged(false);
				return StartScreen;		
			}
		}

		
	}, 
	Running{
		@Override
		public StateMachine nextState(boolean gameover, boolean aborted) {
			if(aborted)
			{
				return Aborted;
			}
			else if(gameover)
			{
				setStateJustChanged(true);
				return GameOver;
			}
			else
			{
				setStateJustChanged(false);
				return Running;		
			}
		}
	},
	GameOver{
		@Override
		public StateMachine nextState(boolean gameover, boolean aborted) {
			if(aborted)
			{
				return Aborted;
			}
			else if(!gameover)
			{
				setStateJustChanged(true);
				return Running;
			}
			else
			{
				setStateJustChanged(false);
				return GameOver;		
			}
		}
	},
	
	Aborted{
		@Override
		public StateMachine nextState(boolean flag1, boolean flag2) {
			return Aborted;
		}
	};
	
	
	
	private static boolean stateJustChanged = false;
	
	public void setStateJustChanged(boolean flag) {
		stateJustChanged = flag;
	}	
	public boolean getStateJustChanged() {
		return stateJustChanged;
	}
	
	public abstract StateMachine nextState(boolean flag, boolean flag2);
}

	
