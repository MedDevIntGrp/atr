package org.hl7.davinci.atr.server.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;
import org.hl7.davinci.atr.server.configuration.JSONObjectUserType;

@Entity
@Table(name="coverage")
@TypeDefs({@TypeDef(name= "StringJsonObject", typeClass = JSONObjectUserType.class)})
public class DafCoverage {
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	 
	@Column(name="data")
	@Type(type = "StringJsonObject")
	private String data;
		
	@Column(name="last_updated_ts")
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date timestamp;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
