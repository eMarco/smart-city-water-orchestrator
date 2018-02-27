/*
 * Copyright (C) 2018 aleskandro - eMarco - cursedLondor
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.unict.ing.iot.ejb;

import com.sun.mail.smtp.SMTPSSLTransport;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author aleskandro - eMarco - cursedLondor
 */
@Stateless
public class AlertSessionBean implements AlertSessionBeanLocal {
    @Override
    public void SendMail(String recipientEmail, String subject, String message)  {
        try {
            // Get a Properties object
            Properties props = System.getProperties();
            props.setProperty("mail.smtps.host", "madfarm.it");
            //props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.port", "465");
            props.setProperty("mail.smtp.socketFactory.port", "465");
            props.setProperty("mail.smtps.auth", "true");
            
            /*
            If set to false, the QUIT command is sent and the connection is immediately closed. If set
            to true (the default), causes the transport to wait for the response to the QUIT command.
            
            ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
            http://forum.java.sun.com/thread.jspa?threadID=5205249
            smtpsend.java - demo program from javamail
            */
            props.put("mail.smtps.quitwait", "false");
            
            Session session = Session.getInstance(props, null);
            
            // -- Create a new message --
            final MimeMessage msg = new MimeMessage(session);
            
            String sender = "iottester@mannaggia.sh";
            // -- Set the FROM and TO fields --
            msg.setFrom(new InternetAddress(sender));
            msg.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(recipientEmail, false));
            msg.setRecipients(Message.RecipientType.CC, "alessandro.distefano1@gmail.com, markgrak@gmail.com, musluca.lock@gmail.com");
            msg.setSubject(subject);
            msg.setText(message, "utf-8");
            msg.setSentDate(new Date());
            
            SMTPSSLTransport t = (SMTPSSLTransport)session.getTransport("smtps");
            
            t.connect("madfarm.it", sender, "Su4CJR9T4D");
            t.sendMessage(msg, msg.getAllRecipients());
            t.close();
        } catch (MessagingException ex) {
            Logger.getLogger(AlertSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
