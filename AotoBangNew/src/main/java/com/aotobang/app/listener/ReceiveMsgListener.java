package com.aotobang.app.listener;

import com.aotobang.net.entity.AotoNotifiEvent;

public interface ReceiveMsgListener {
      public void onEvent(AotoNotifiEvent event);
}
