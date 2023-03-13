package com.xy.blog.utils;

import com.sun.javaws.jnl.RContentDesc;
import com.sun.mail.util.MailSSLSocketFactory;
import org.junit.jupiter.api.Test;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

/**
 * 邮箱发送工具类
 */
public class MailUtils {
    public static void main(String[] args) throws MessagingException, IOException {
        sendMail("");
    }

    /**
     * 发送验证码
     *
     * @param receiveMail
     * @throws Exception
     */
    public static String sendMail(String receiveMail) throws MessagingException, IOException {
        Properties prop = new Properties();
        // 开启debug调试，以便在控制台查看
        prop.setProperty("mail.debug", "true");
        // 设置邮件服务器主机名
        prop.setProperty("mail.host", "smtp.163.com");
        // 发送服务器需要身份验证
        prop.setProperty("mail.smtp.auth", "true");
        // 发送邮件协议名称
        prop.setProperty("mail.transport.protocol", "smtp");
        // 开启SSL加密，否则会失败
        Message message = null;
        String content = null;
        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            prop.put("mail.smtp.ssl.enable", "true");
            prop.put("mail.smtp.ssl.socketFactory", sf);
            // 创建session
            Session session = Session.getInstance(prop);
            // 通过session得到transport对象
            Transport ts = session.getTransport();
            // 连接邮件服务器：邮箱类型，帐号，POP3/SMTP协议授权码 163使用：smtp.163.com，qq使用：smtp.qq.com
            ts.connect("smtp.163.com", "y2012162055@163.com", "TDXUWNKVUGZCWGRU");
            // 创建邮件
            message = createSimpleMail(session, receiveMail);
            content =(String) message.getContent();
            System.out.println("?????????????????"+content);
            // 发送邮件
            ts.sendMessage(message, message.getAllRecipients());
            ts.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }


    /**
     * @Method: createSimpleMail
     * @Description: 创建一封只包含文本的邮件
     */
    public static MimeMessage createSimpleMail(Session session, String receiveMail) throws Exception {
        //  获取6位随机验证码（英文）
        String[] letters = new String[]{
                "q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m",
                "A", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(letters[(int) Math.floor(Math.random() * letters.length)]);
        }

        //获取6位随机验证码（中文），根据项目需要选择中英文
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);

        // 创建邮件对象
        MimeMessage message = new MimeMessage(session);
        // 指明邮件的发件人
        message.setFrom(new InternetAddress("y2012162055@163.com"));
        // 指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiveMail));
        // 邮件的标题
        message.setSubject("验证码");
        // 邮件的文本内容
        message.setContent("您的验证码：" + verifyCode + "，如非本人操作，请忽略！请勿回复此邮箱", "text/html;charset=UTF-8");

        // 返回创建好的邮件对象
        return message;
    }

/*
    @Test
    public void test() throws Exception {
        //验证码发送到对方的邮箱，本地用session存储验证码，以待确认
        String mesg="尊敬的用户:你好!n xxx.验证码为:" + SendMail.achieveCode()+"     (有效期为一分钟)";
        SendMail.sendMail("1114217951@qq.com","激活测试", mesg);
        System.out.println("发送邮件成功");
    }
    */
}
