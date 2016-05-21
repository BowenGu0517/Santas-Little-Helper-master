package me.zhenning;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import javax.activation.FileDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.BodyPart;

/**
 * Created by Zhenning Jiang on 2016/3/27.
 */
public class EmailSender extends javax.mail.Authenticator {
    private String mailhost = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;
    private final static String Tag = "EmailSender";
    private Multipart _multipart;

    static {
        Security.addProvider(new me.zhenning.JSSEProvider());
    }

    public EmailSender(String user, String password) {
        // use accountManager.getAccountsByType("com.Santa");
        // to get all the account related to the Santa little helper
        // and then use Accounts.name and AccountManager.get(mContext).getAccounts();
        // to get the usrename and password to pass into this function.
        this.user = user;
        this.password = password;
        Log.d(Tag, this.user);
        Log.d(Tag, "EmailSender");

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        Log.d(Tag, "getPasswordAuthentication");
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients) throws Exception {
        try{
            final MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setFrom(new InternetAddress(sender));
            message.setSubject(subject);
            message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            Thread EmailThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Transport.send(message);
                    }catch (Exception e){
                        Log.e("Transport Error", e.getMessage(), e);
                    }
                }
            });
            EmailThread.start();

        }catch(Exception e){
            Log.e("SendMail", e.getMessage(), e);
        }

    }

    public synchronized void sendMailWithAttachment(
            String subject, String body,
            String sender, String recipients, String filename) throws Exception {
        try{
            final MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setFrom(new InternetAddress(sender));
            message.setSubject(subject);
            message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));

            if (!"".equals(filename)) {
                Multipart _multipart = new MimeMultipart();
                BodyPart messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filename);

                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filename);

                _multipart.addBodyPart(messageBodyPart);
                message.setContent(_multipart);
            }

            Thread EmailThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Transport.send(message);
                    }catch (Exception e){
                        Log.e("Transport Error", e.getMessage(), e);
                    }
                }
            });
            EmailThread.start();

        }catch(Exception e){
            Log.e("SendMail", e.getMessage(), e);
        }

    }
    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}
