package Azamon;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class AzamonSuccesorHCFunction2 implements SuccessorFunction{

    public List getSuccessors(Object state) {

        ArrayList<Successor> retval = new ArrayList<Successor>();
        AzamonState actual_State = (AzamonState) state;



        for (int i = 0; i < actual_State.packets.size(); ++i) {
            //Creació de tots els possibles nous estats amb la operació swap_offers
            for (int j = i + 1; j < actual_State.packets.size(); ++j) {
                AzamonState new_State = new AzamonState(actual_State);
                if (new_State.swap_offers(i, j)) retval.add(new Successor("package " + i + " swapped with " + j, new_State));
            }
        }
        return retval;
    }
}
