/**
Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and the authors indicated in the @author tags

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package bftsmart.statemanagement;

import bftsmart.reconfiguration.views.View;
import bftsmart.tom.util.TOMUtil;

/**
 * This class represents a message used in the state transfer protocol
 * 
 * @author Joao Sousa
 */
public abstract class SMMessage extends SMMessageWire {

    private ApplicationState state; // State log

    /**
     * Constructs a SMMessage
     * @param sender Process Id of the sender
     * @param cid Consensus ID up to which the sender needs to be updated
     * @param type Message type
     * @param replica Replica that should send the state
     * @param state State log
     */
    protected SMMessage(int sender, int cid, int type, ApplicationState state, View view, int regency, int leader) {
        super(sender, cid, type, state, view, regency, leader, type == TOMUtil.TRIGGER_SM_LOCALLY && sender == -1);
        this.state = state;
    }

    protected SMMessage() {
        super();
    }
    /**
     * Retrieves the state log
     * @return The state Log
     */
    public ApplicationState getState() {
        return state;
    }
}
