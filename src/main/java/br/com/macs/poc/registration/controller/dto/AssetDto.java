package br.com.macs.poc.registration.controller.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.URL;

public class AssetDto {

	private String id;
	
	@NotNull
	private String name;
	
	@NotNull @URL
	private String address;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		return "[id=\"" + id + "\", name=\"" + name + "\", address=\"" + address + "\"]";
	}
	
}
