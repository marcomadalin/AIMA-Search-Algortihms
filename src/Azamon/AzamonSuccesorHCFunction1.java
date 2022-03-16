package Azamon;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class AzamonSuccesorHCFunction1 implements SuccessorFunction{

    public List getSuccessors(Object state) {

        ArrayList<Successor> retval = new ArrayList<Successor>();
        AzamonState actual_State = (AzamonState) state;

        for (int i = 0; i < actual_State.packets.size(); ++i) {
            //Creació de tots els possibles nous estats amb la operació move_package
            for (int k = 0; k < actual_State.shipping.size(); ++k) {
                AzamonState new_State = new AzamonState(actual_State);
                if (new_State.move_package(i, k)) retval.add(new Successor("package " + i + " moved to offer " + k, new_State));
            }
        }
        return retval;
    }
}
