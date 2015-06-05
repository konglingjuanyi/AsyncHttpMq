package org.mqservice.model;

import java.io.Serializable;

public class JsonMessage implements Serializable {

	Long id;

	String uuid;

	String name;

	String message;

	public JsonMessage(Long id, String uuid, String name, String message) {
		super();
		this.id = id;
		this.uuid = uuid;
		this.name = name;
		this.message = message;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "JsonMessage [id=" + id + ", uuid=" + uuid + ", name=" + name
				+ ", message=" + message + "]";
	}

}
