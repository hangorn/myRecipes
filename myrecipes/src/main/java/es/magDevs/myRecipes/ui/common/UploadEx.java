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
package es.magDevs.myRecipes.ui.common;

import java.util.Arrays;

import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;

public class UploadEx extends Upload {

	public UploadEx() {
		super();
		init();
	}

	public UploadEx(Receiver receiver) {
		super(receiver);
		init();
	}

	private void init() {
		UploadI18N i18n = new UploadI18N();
		i18n.setDropFiles(
		        new UploadI18N.DropFiles().setOne("Arrastre un fichero aquí")
		                .setMany("Arrastre ficheros aquí"))
		        .setAddFiles(new UploadI18N.AddFiles()
		                .setOne("Añadir fichero").setMany("Añadir ficheros"))
		        .setCancel("Cancelar")
		        .setError(new UploadI18N.Error()
		                .setTooManyFiles("Demasiados ficheros.")
		                .setFileIsTooBig("Fichero demasiado grande.")
		                .setIncorrectFileType("Tipo de fichero incorrecto."))
		        .setUploading(new UploadI18N.Uploading()
		                .setStatus(new UploadI18N.Uploading.Status()
		                        .setConnecting("Conectando...")
		                        .setStalled("Parado.")
		                        .setProcessing("Procesando..."))
		                .setRemainingTime(
		                        new UploadI18N.Uploading.RemainingTime()
		                                .setPrefix("Tiempo restante: ")
		                                .setUnknown(
		                                        "Tiempo restante desconocido."))
		                .setError(new UploadI18N.Uploading.Error()
		                        .setServerUnavailable("Servidor no disponible")
		                        .setUnexpectedServerError(
		                                "Error inesperado")
		                        .setForbidden("Prohibido")))
		        .setUnits(Arrays.asList("B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"));
		setI18n(i18n);
	}
}
