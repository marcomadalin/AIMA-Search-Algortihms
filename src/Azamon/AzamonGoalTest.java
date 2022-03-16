package Azamon;

import aima.search.framework.GoalTest;

public class AzamonGoalTest implements GoalTest{
    public boolean isGoalState(Object state){
        return ((AzamonState) state).is_goal_state();
    }
}
