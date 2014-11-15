package com.delphis.keepmyplace.entity;

import java.io.Serializable;
import java.sql.Date;

import com.google.android.gms.maps.model.LatLng;

public class KMLocation implements Cloneable, Serializable {
	
	private Long _id;
	private String _name;
	private LatLng _coordinates;
	private String _address;
	private String _notes;
	private Date _additionDate;
	
	public Long get_id() {
		return _id;
	}
	public void set_id(Long _id) {
		this._id = _id;
	}
	public String get_name() {
		return _name;
	}
	public void set_name(String _name) {
		this._name = _name;
	}
	public LatLng get_coordinates() {
		return _coordinates;
	}
	public void set_coordinates(LatLng _coordinates) {
		this._coordinates = _coordinates;
	}
	public String get_notes() {
		return _notes;
	}
	public void set_notes(String _notes) {
		this._notes = _notes;
	}	
	
	public Date get_additionDate() {
		return _additionDate;
	}
	public void set_additionDate(Date _additionDate) {
		this._additionDate = _additionDate;
	}
	
	public String get_address() {
		return _address;
	}
	public void set_address(String address) {
		this._address = address;
	}
	
	public KMLocation() { 
		
	}; 
	
	public KMLocation(String name, LatLng coordinates, String notes)
	{
		_name=name;
		_coordinates=coordinates;
		_notes=notes;
	}
	
	@Override
    public String toString() 
	{
        return this._id + ". " + this._name;
    }
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
