package eu.badeacristian.RoSpringVet.models;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.JoinColumn;

@NoArgsConstructor
@Getter
@Setter
@Entity
//@Table(name =  "user", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
	
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Size(min=2, message="Introdu un prenume")
	private String firstname;
	
	@NotNull
	@Size(min=2, message="Introdu un nume")
	private String lastname;
	
	@NotNull
	@Column(unique = true, length = 32)
	@Email(message = "Format email nevalid")
	@Size(min=7, message="Introdu un email")
	private String email;
	
	@NotNull
	@Size(min=3, message="Parola prea scurta. Minim 3 caractere")
	private String password;
	
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(
		            name = "user_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(
				            name = "role_id", referencedColumnName = "id"))
	
	private Collection<Role> roles;


	public User(String firstname, String lastname, String email, String password, Collection<Role> roles) {
		super();
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.password = password;
		this.roles = roles;
	}
	
	

	

}
