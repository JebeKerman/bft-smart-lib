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
package bftsmart.tom.leaderchange;

import bftsmart.tom.util.TOMUtil;

/**
 * Message used during leader change and synchronization
 * @author Joao Sousa
 */
public class LCMessage extends LCMessageWire {

    /**
     * Empty constructor
     */
    public LCMessage(){
        super();
    }

    /**
     * Constructor
     * @param from replica that creates this message
     * @param type type of the message (STOP, SYNC, CATCH-UP)
     * @param ts timestamp of leader change and synchronization
     * @param payload dada that comes with the message
     */
    public LCMessage(int from, int type, int ts, byte[] payload) {
        super(from, type, ts, payload, type == TOMUtil.TRIGGER_LC_LOCALLY && from == -1);
    }
}
