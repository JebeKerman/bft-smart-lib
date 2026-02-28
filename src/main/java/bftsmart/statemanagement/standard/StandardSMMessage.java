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
package bftsmart.statemanagement.standard;

import bftsmart.reconfiguration.views.View;
import bftsmart.statemanagement.ApplicationState;
import bftsmart.tom.util.TOMUtil;

/**
 * 
 * @author Marcel Santos
 *
 */
public class StandardSMMessage extends StandardSMMessageWire<ApplicationState> {

    public StandardSMMessage(int sender, int cid, int type, int replica, ApplicationState state, View view, int regency, int leader) {
    	super(sender, cid, type, replica, state.getSerializedState(), view, regency, leader, type == TOMUtil.TRIGGER_SM_LOCALLY && sender == -1);
        this.state = state;
    }
	
    public StandardSMMessage() {
    	super();
    }
}
