/**
 * Copyright (c) 2014-2020, Javier Vaquero
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * required by applicable law or agreed to in writing, software
 * under the License is distributed on an "AS IS" BASIS,
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * the License for the specific language governing permissions and
 * under the License.
 */
package es.magDevs.myRecipes.ui.auth;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

/**
 * Listener que se ejecuta para cada UI. Se utiliza para saber si el usuario esta autenticado, y si no redirigirlo
 * a la pantalla. Esta clase hay que registrarla en el fichero:
 *  META-INF/services/com.vaadin.flow.server.VaadinServiceInitListener
 *
 */
public class AuthInitListener implements VaadinServiceInitListener {
	
    @Override
    public void serviceInit(ServiceInitEvent initEvent) {
        initEvent.getSource().addUIInitListener(uiInitEvent -> {
            uiInitEvent.getUI().addBeforeEnterListener(enterEvent -> {
            });
        });
    }
}
