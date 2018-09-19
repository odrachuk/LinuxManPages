/*******************************************************************************
 * Created by o.drachuk on 12/06/2014. 
 *
 * Copyright Oleksandr Drachuk.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.softsandr.man.util;

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * This class used for...
 */
public class LoggingUtil {

    private LoggingUtil() {
    }

    /**
     * Erase system handlers and register new
     *
     * @param h one or more Handlers that used in app...
     */
    public static void resetRootHandler(Handler... h) {
        Logger logger = LogManager.getLogManager().getLogger("");
        if (logger != null) {
            // clear all default handlers
            Handler[] handlers = logger.getHandlers();
            for (Handler handler : handlers) {
                logger.removeHandler(handler);
            }
            // add all new handlers
            for (Handler handler : h) {
                logger.addHandler(handler);
            }
        }
    }
}
