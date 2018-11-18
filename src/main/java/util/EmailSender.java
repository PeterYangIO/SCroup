package util;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

// https://www.mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/
public class EmailSender extends Thread{
   	final static String username = "uscscroup@gmail.com";
	final static String password = "scrouppassword";
	
	private String to, header, content;
	
	public EmailSender(String to, String header, String content) {
		this.to = to;
		this.header = header;
		this.content = content;
	}

   public void run() {    
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("uscscroup@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to));
			message.setSubject(header);
			message.setText(content);

			Transport.send(message);

			System.out.println("Email sent");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
   }
}