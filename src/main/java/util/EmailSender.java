package util;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

// https://www.mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/
public class EmailSender {

   public static void sendMessage(String to, String header, String content) {    
	   final String username = "uscscroup@gmail.com";
		final String password = "scrouppassword";

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