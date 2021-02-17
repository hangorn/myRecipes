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
package es.magDevs.myRecipes.dal.be;

public class BasicBean implements Cloneable {

	private Long id;
	
	public BasicBean() {
	}
	
	public BasicBean(Long id) {
		this.id = id;
	}
	
	public BasicBean(BasicBean be) {
		this.id = be.id;
	}

	@Override
	public boolean equals(Object obj) {
		return getId() != null && obj instanceof BasicBean && getId().equals(((BasicBean) obj).getId());
	}
	
	@Override
	public int hashCode() {
		return getId() == null ? 0 : getId().hashCode();
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new BasicBean(this);
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}	
