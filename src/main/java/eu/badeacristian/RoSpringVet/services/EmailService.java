package eu.badeacristian.RoSpringVet.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import eu.badeacristian.RoSpringVet.models.User;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	//email inregistrare reusita user
	public void sendEmailInregistrare(User user) throws MailException{ 
		//trimitere email
		SimpleMailMessage mail = new SimpleMailMessage(); 
		mail.setTo(user.getEmail());
		mail.setFrom("rospringvet@gmail.com");
		mail.setSubject("Înregistrare RoSpringVet");
		mail.setText("Te-ai înregistrat cu succes,"+ user.getFirstname() + " " + user.getLastname() + ". Îți dorim o experiență placută pe site-ul nostru și la clinică ! ");		
		javaMailSender.send(mail);
	}
	
	//email programare confirmata (facuta de angajat)
	public void sendEmailProgramareConfirmata(User user, Date data, String nume) throws MailException{ 
		//trimitere email
		SimpleMailMessage mail = new SimpleMailMessage(); 
		mail.setTo(user.getEmail());
		mail.setFrom("rospringvet@gmail.com");
		mail.setSubject("Programare RoSpringVet CONFIRMATĂ");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		String data_formatata = simpleDateFormat.format(data);
		mail.setText(user.getFirstname() + " " + user.getLastname() + " programarea ta pentru data de " + data_formatata + ", pentru animalul " + nume + ", a fost confirmată, te așteptăm!" );		
		javaMailSender.send(mail);
	}
	
	//email programare neconfirmata (facuta de stapan)
	public void sendEmailProgramareNeconfirmata(User user, Date data, String nume) throws MailException{ 
		//trimitere email
		SimpleMailMessage mail = new SimpleMailMessage(); 
		mail.setTo(user.getEmail());
		mail.setFrom("rospringvet@gmail.com");
		mail.setSubject("Programare RoSpringVet NECONFIRMATĂ");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		String data_formatata = simpleDateFormat.format(data);
		mail.setText(user.getFirstname() + " " + user.getLastname() + ", am înregistrat programarea făcută de tine pentru data de " + data_formatata + ", pentru animalul " + nume + ", ne vom uita peste ea și îți vom spune dacă o confirmăm sau o refuzăm. O zi bună!" );		
		javaMailSender.send(mail);
	}
	
	//email programare refuzata
	public void sendEmailProgramareRefuzata(User user, Date data, String nume) throws MailException{ 
		//trimitere email
		SimpleMailMessage mail = new SimpleMailMessage(); 
		mail.setTo(user.getEmail());
		mail.setFrom("rospringvet@gmail.com");
		mail.setSubject("Programare RoSpringVet REFUZATĂ");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		String data_formatata = simpleDateFormat.format(data);
		
		
		mail.setText(user.getFirstname() + " " + user.getLastname() + " programarea ta pentru data de " + data_formatata + ", pentru animalul " + nume + ", a fost refuzată, propune o altă dată utilizând site-ul sau sună-ne!" );		
		javaMailSender.send(mail);
	}
	
	
	//email programare anulata
		public void sendEmailProgramareAnulata(User user, Date data, String nume) throws MailException{ 
			//trimitere email
			SimpleMailMessage mail = new SimpleMailMessage(); 
			mail.setTo(user.getEmail());
			mail.setFrom("rospringvet@gmail.com");
			mail.setSubject("Programare RoSpringVet ANULATĂ");
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			String data_formatata = simpleDateFormat.format(data);
			
			
			mail.setText(user.getFirstname() + " " + user.getLastname() + " programarea ta pentru data de " + data_formatata + ", pentru animalul " + nume + ", a fost anulată." );		
			javaMailSender.send(mail);
		}
		
	
	
	
	///amintire programare
	///aici cu ceva EVENT 
	public void sendEmailProgramareAmintire(User user, Date data, String nume) throws MailException{ 
		//trimitere email
		SimpleMailMessage mail = new SimpleMailMessage(); 
		mail.setTo(user.getEmail());
		mail.setFrom("rospringvet@gmail.com");
		mail.setSubject("Programare RoSpringVet amintire");
		mail.setText(user.getFirstname() + " " + user.getLastname() + " îți reamintim despre programarea ta pentru data de " + data + ", pentru animalul " + nume + ", sperăm că o a fii aici!" );		
		javaMailSender.send(mail);
	}
	
}
