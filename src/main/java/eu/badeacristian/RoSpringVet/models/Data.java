package eu.badeacristian.RoSpringVet.models;

import java.util.Date;

import javax.persistence.MappedSuperclass;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class Data {
	
	 @DateTimeFormat(pattern = "MM/dd/yyyy h:mm a")
	 private Date date;
	 
	
}
