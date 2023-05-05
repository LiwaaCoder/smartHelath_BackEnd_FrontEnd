package superapp.Boundary.User;

import org.springframework.beans.factory.annotation.Value;

public class UserId {
	private String superapp;
	private String email;
	
	public UserId() {
		
	}

	public UserId(@Value("${spring.application.name}") String superapp,String email) {
		super();
		this.email = email;
		this.superapp = superapp;
	}


	public String getSuperapp() {
		return superapp;
	}
	
	public void setSuperapp(String superapp) {
		this.superapp = superapp;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public boolean equals(Object obj){
		UserId other = (UserId) obj;
		return this.email.equals(other.email) && this.superapp.equals(other.superapp);
	}
}
