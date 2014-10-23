/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.wm;

import static com.android.server.wm.WindowManagerService.TAG;

import android.util.EventLog;
import android.util.Slog;

class Task {
    TaskStack mStack;
    final AppTokenList mAppTokens = new AppTokenList();
    final int taskId;
    final int mUserId;
    boolean mDeferRemoval = false;

    Task(AppWindowToken wtoken, TaskStack stack, int userId) {
        taskId = wtoken.groupId;
        mAppTokens.add(wtoken);
        mStack = stack;
        mUserId = userId;
    }

    DisplayContent getDisplayContent() {
        return mStack.getDisplayContent();
    }

    void addAppToken(int addPos, AppWindowToken wtoken) {
        final int lastPos = mAppTokens.size();
        for (int pos = 0; pos < lastPos && pos < addPos; ++pos) {
            if (mAppTokens.get(pos).removed) {
                // addPos assumes removed tokens are actually gone.
                ++addPos;
            }
        }
        mAppTokens.add(addPos, wtoken);
        mDeferRemoval = false;
    }

    boolean removeAppToken(AppWindowToken wtoken) {
        boolean removed = mAppTokens.remove(wtoken);
        if (mAppTokens.size() == 0) {
            EventLog.writeEvent(com.android.server.EventLogTags.WM_TASK_REMOVED, taskId,
                    "removeAppToken: last token");
        }
        return removed;
    }

    void setSendingToBottom(boolean toBottom) {
        for (int appTokenNdx = 0; appTokenNdx < mAppTokens.size(); appTokenNdx++) {
            mAppTokens.get(appTokenNdx).sendingToBottom = toBottom;
        }
    }

    @Override
    public String toString() {
        return "{taskId=" + taskId + " appTokens=" + mAppTokens + "}";
    }
}
