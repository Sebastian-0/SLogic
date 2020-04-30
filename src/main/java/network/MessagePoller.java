/*
* Copyright (C) 2016 Sebastian Hjelm
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*/

package network;

import snet.Network;
import sutilities.Debugger;

public class MessagePoller {
  
  private volatile Network network;
  
  private volatile Thread thread;
  private volatile boolean wasStopped;
  
  public MessagePoller(Network network) {
    this.network = network;
  }

  public void start() {
    wasStopped = false;
    thread = new Thread(runnable, "Message poller");
    thread.start();
  }
  
  public void stop() {
    wasStopped = true;
    thread.interrupt();
  }
  
  
  
  private Runnable runnable = new Runnable() {
    
    @Override
    public void run() {
      while (!wasStopped) {
        try {
          network.pollMessages(true);
        } catch (InterruptedException e) {
          if (!wasStopped)
            Debugger.warning("MessagePoller: run()", "Was interrupted unexpectedly", e);
        }
      }
    }
  };
}
