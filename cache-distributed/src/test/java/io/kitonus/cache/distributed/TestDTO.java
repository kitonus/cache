package io.kitonus.cache.distributed;

import java.util.Date;
import java.util.UUID;

public class TestDTO{

	private String id = UUID.randomUUID().toString();
	private String name;
	private Date dateOfBirth;
	private java.sql.Date dateOfBirth2;
	private java.sql.Timestamp dateOfBirth3;
	
	public TestDTO(){}
	
	public TestDTO(String name, Date dateOfBirth){
		this.setName(name);
		this.setDateOfBirth(dateOfBirth);
	}
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
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
		this.dateOfBirth2 = new java.sql.Date(dateOfBirth.getTime());
		this.dateOfBirth3 = new java.sql.Timestamp(dateOfBirth.getTime());
	}
	public java.sql.Date getDateOfBirth2() {
		return dateOfBirth2;
	}
	public java.sql.Timestamp getDateOfBirth3() {
		return dateOfBirth3;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestDTO other = (TestDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "TestDTO [id=" + id + ", name=" + name + ", dateOfBirth=" + dateOfBirth + ", dateOfBirth2="
				+ dateOfBirth2 + ", dateOfBirth3=" + dateOfBirth3 + "]";
	}
}
