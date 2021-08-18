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
				return Running;
			}
			else
			{
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
				return GameOver;
			}
			else
			{
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
				return Running;
			}
			else
			{
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
	
	
	
	
	
	
	public float getCurrentTime()
	{
		return 1.2f;
	};
	public abstract StateMachine nextState(boolean flag, boolean flag2);
}

	
